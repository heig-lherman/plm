{-
    Parser.hs - StandardQL Parser
    Authors: Loïc Herman
-}
{-# LANGUAGE OverloadedStrings #-}

module StandardQL.Parser
  ( -- * Main types
    Parser,
    ParseError (..),

    -- * Parsing functions
    parseProgram,
    parseStatement,
    parseExpression,
  )
where

import Control.Monad.Combinators.Expr
import Data.Functor
import Data.Text (Text)
import Data.Text qualified as T
import StandardQL.Elaboration (elaborateCondition)
import StandardQL.Tree
import Text.Megaparsec hiding (ParseError)
import Text.Megaparsec.Char
import Text.Megaparsec.Char.Lexer qualified as L

-- | Parse errors
data ParseError
  = ReservedKeywordError Text
  | GenericParseError Text
  deriving (Eq, Show, Ord)

instance ShowErrorComponent ParseError where
  showErrorComponent (ReservedKeywordError kw) = T.unpack $ "Reserved keyword: " <> kw <> " cannot be used as identifier"
  showErrorComponent (GenericParseError msg) = T.unpack $ "Parse error: " <> msg

-- | Parser type for StandardQL
type Parser = Parsec ParseError Text

-- | Bundle of parse errors
type ErrorBundle = ParseErrorBundle Text ParseError

-- | Parse a complete StandardQL program
parseProgram :: Text -> Either ErrorBundle Statement
parseProgram = parse program "StandardQL"

-- * Lexer components

-- | Space consumer
sc :: Parser ()
sc =
  L.space
    space1
    (L.skipLineComment "--")
    (L.skipBlockComment "{-" "-}")

-- | Lexeme parser
lexeme :: Parser a -> Parser a
lexeme = L.lexeme sc

-- | Symbol parser
symbol :: Text -> Parser Text
symbol = L.symbol sc

-- ** Reserved keywords

-- | Reserved keywords in StandardQL
reservedWords :: [Text]
reservedWords = ["OR", "AND", "IS", "NOT", "NULL", "BETWEEN", "IN", "MATCHES", "WHERE"]

-- | Parse a reserved keyword
reserved :: Text -> Parser ()
reserved w = (lexeme . try) (string' w *> notFollowedBy alphaNumChar)

-- ** Identifiers and literals

-- | Parse an identifier (variable name)
identifier :: Parser Identifier
identifier = lexeme $ do
  first <- letterChar
  rest <- many (alphaNumChar <|> char '_')
  let name = T.pack (first : rest)
  -- Check if it's not a reserved word
  if name `elem` reservedWords
    then customFailure $ ReservedKeywordError name
    else return $ Identifier name

-- | Parse a numeric literal (integer or float)
number :: Parser Double
number = lexeme $ try float <|> fromIntegral <$> integer
  where
    float = L.float
    integer = L.decimal :: Parser Integer

-- | Parse a string literal
stringLiteral :: Parser Text
stringLiteral = lexeme $ char '"' *> manyTill L.charLiteral (char '"') <&> T.pack

-- | Parse a value
value :: Parser Value
value =
  choice
    [ NullValue <$ reserved "NULL",
      NumValue <$> number,
      StrValue <$> stringLiteral
    ]

-- ** Syntax helpers

-- | Parse a parenthesized expression
parens :: Parser a -> Parser a
parens = between (symbol "(") (symbol ")")

-- * Main parsers

-- | Parse a complete StandardQL program
program :: Parser Statement
program = sc *> statement <* eof

-- | Parse a statement, which can be simple or filtered
statement :: Parser Statement
statement = do
  expr <- expression
  whereClause <- optional (reserved "WHERE" *> expression)
  return $ case whereClause of
    Just filterExpr -> FilteredStatement expr filterExpr
    Nothing -> SimpleStatement expr

-- ** Simple expressions

-- | Parse an expression using operator precedence
expression :: Parser Expression
expression = makeExprParser term operators
  where
    operators =
      [ [ InfixL (BinaryExpr OpMul <$ symbol "*"),
          InfixL (BinaryExpr OpDiv <$ symbol "/")
        ],
        [ InfixL (BinaryExpr OpAdd <$ symbol "+"),
          InfixL (BinaryExpr OpSub <$ symbol "-")
        ],
        [ InfixN (BinaryExpr OpEq <$ symbol "="),
          InfixN (BinaryExpr OpNeq <$ (symbol "!=" <|> symbol "<>")),
          InfixN (BinaryExpr OpLte <$ symbol "<="),
          InfixN (BinaryExpr OpGte <$ symbol ">="),
          InfixN (BinaryExpr OpLt <$ symbol "<"),
          InfixN (BinaryExpr OpGt <$ symbol ">")
        ],
        [ InfixL (BinaryExpr OpAnd <$ reserved "AND")
        ],
        [ InfixL (BinaryExpr OpOr <$ reserved "OR")
        ]
      ]

-- | Parse a term (expression without binary operators)
term :: Parser Expression
term =
  choice
    [ try functionCall,
      try conditionExpr,
      try $ parens expression,
      ValueExpr <$> value,
      IdentExpr <$> identifier
    ]

-- | Parse a function call
functionCall :: Parser Expression
functionCall = do
  name <- identifier
  args <- parens (sepBy argument (symbol ","))
  return $ FunctionExpr $ FunctionCall name args

-- | Parse an argument
argument :: Parser Argument
argument =
  choice
    [ ArgFunction <$> try functionCallArg, -- Function call as argument
      ArgIdent <$> identifier,
      ArgValue <$> value
    ]
  where
    functionCallArg = do
      name <- identifier
      args <- parens (sepBy argument (symbol ","))
      return $ FunctionCall name args

-- ** Condition expressions

-- | Parse a condition expression (identifier followed by conditions)
conditionExpr :: Parser Expression
conditionExpr =
  do
    ident <- identifier
    elaborateCondition ident <$> conditionOr
  where
    -- Parse OR-level conditions
    conditionOr = do
      left <- conditionAnd
      rest <- many $ try $ do
        reserved "OR"
        lookAhead conditionKeywords
        conditionAnd
      return $ foldl CondOr left rest

    -- Parse AND-level conditions
    conditionAnd = do
      left <- conditionTerm
      rest <- many $ try $ do
        reserved "AND"
        lookAhead conditionKeywords
        conditionTerm
      return $ foldl CondAnd left rest

    conditionKeywords =
      choice
        [ void $ optional (reserved "IS") *> reserved "NULL",
          void $ optional (reserved "IS") *> reserved "NOT" *> reserved "NULL",
          void $ reserved "BETWEEN",
          void $ reserved "IN",
          void $ reserved "NOT" *> reserved "IN",
          void $ optional (reserved "MATCHES") *> symbol "(",
          void $ choice [symbol "=", symbol "!=", symbol "<>", symbol "<=", symbol ">=", symbol "<", symbol ">"] *> value
        ]

-- | Parse a condition term
conditionTerm :: Parser Condition
conditionTerm =
  choice
    [ try isNull,
      try isNotNull,
      try betweenCondition,
      try inCondition,
      try notInCondition,
      try matches,
      comparison
    ]

-- | Parse comparison conditions
comparison :: Parser Condition
comparison =
  choice
    [ CondEq <$> (symbol "=" *> value),
      CondNeq <$> ((symbol "!=" <|> symbol "<>") *> value),
      CondLte <$> (symbol "<=" *> value),
      CondGte <$> (symbol ">=" *> value),
      CondLt <$> (symbol "<" *> value),
      CondGt <$> (symbol ">" *> value)
    ]

-- | Parse IS NULL
isNull :: Parser Condition
isNull = CondIsNull <$ (optional (reserved "IS") *> reserved "NULL")

-- | Parse IS NOT NULL
isNotNull :: Parser Condition
isNotNull = CondIsNotNull <$ (optional (reserved "IS") *> reserved "NOT" *> reserved "NULL")

-- | Parse BETWEEN condition
betweenCondition :: Parser Condition
betweenCondition = do
  reserved "BETWEEN"
  v1 <- value
  reserved "AND"
  CondBetween v1 <$> value

-- | Parse IN condition
inCondition :: Parser Condition
inCondition = do
  reserved "IN"
  CondIn <$> parens valueList

-- | Parse NOT IN condition
notInCondition :: Parser Condition
notInCondition = do
  reserved "NOT"
  reserved "IN"
  CondNotIn <$> parens valueList

-- | Parse MATCHES condition
matches :: Parser Condition
matches = do
  _ <- optional (reserved "MATCHES")
  expr <- parens expression
  return $ CondMatches expr

-- | Parse a value list (for IN conditions)
valueList :: Parser [ValueRange]
valueList = sepBy1 valueRange (symbol "|")

-- | Parse a value range
valueRange :: Parser ValueRange
valueRange = do
  v1 <- value
  mv2 <- optional (symbol "-" *> value)
  return $ case mv2 of
    Nothing -> SingleValue v1
    Just v2 -> RangeValue v1 v2

-- * Export convenience functions

-- | Parse a statement directly
parseStatement :: Text -> Either ErrorBundle Statement
parseStatement = parse (sc *> statement <* eof) "StandardQL"

-- | Parse an expression directly
parseExpression :: Text -> Either ErrorBundle Expression
parseExpression = parse (sc *> expression <* eof) "StandardQL"
