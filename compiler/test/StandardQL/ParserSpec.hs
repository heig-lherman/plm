{-# LANGUAGE OverloadedStrings #-}

-- | Test suite for StandardQL parser
module StandardQL.ParserSpec
  ( spec,
  )
where

import StandardQL
import Test.Hspec
import Test.Hspec.Megaparsec

-- | Main test specification
spec :: Spec
spec = do
  describe "StandardQL Parser" $ do
    valueSpec
    identifierSpec
    expressionSpec
    conditionSpec
    statementSpec
    programSpec
    realWorldExamplesSpec

-- | Tests for value parsing
valueSpec :: Spec
valueSpec = describe "value parser" $ do
  it "parses integers" $ do
    parseExpression "42" `shouldParse` ValueExpr (NumValue 42)
    parseExpression "0" `shouldParse` ValueExpr (NumValue 0)
    parseExpression "1007" `shouldParse` ValueExpr (NumValue 1007)

  it "parses floats" $ do
    parseExpression "3.14" `shouldParse` ValueExpr (NumValue 3.14)
    parseExpression "0.0" `shouldParse` ValueExpr (NumValue 0.0)

  it "parses strings" $ do
    parseExpression "\"hello\"" `shouldParse` ValueExpr (StrValue "hello")
    parseExpression "\"hello world\"" `shouldParse` ValueExpr (StrValue "hello world")
    parseExpression "\"\"" `shouldParse` ValueExpr (StrValue "")

identifierSpec :: Spec
identifierSpec = describe "identifier handling" $ do
  it "parses valid identifiers" $ do
    parseExpression "x" `shouldParse` IdentExpr (Identifier "x")
    parseExpression "My_V4R" `shouldParse` IdentExpr (Identifier "My_V4R")
    parseExpression "GABBJ" `shouldParse` IdentExpr (Identifier "GABBJ")

  it "rejects reserved keywords as identifiers" $ do
    parseExpression `shouldFailOn` "AND"
    parseExpression `shouldFailOn` "OR"
    parseExpression `shouldFailOn` "IS"

-- | Tests for expression parsing
expressionSpec :: Spec
expressionSpec = describe "expression parser" $ do
  it "parses binary expressions" $ do
    parseExpression "1 + 2"
      `shouldParse` BinaryExpr OpAdd (ValueExpr (NumValue 1)) (ValueExpr (NumValue 2))

    parseExpression "x = 5"
      `shouldParse` BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 5))

  it "parses operator precedence correctly" $ do
    parseExpression "1 + 2 * 3"
      `shouldParse` BinaryExpr
        OpAdd
        (ValueExpr (NumValue 1))
        (BinaryExpr OpMul (ValueExpr (NumValue 2)) (ValueExpr (NumValue 3)))

    parseExpression "x = 1 AND y = 2"
      `shouldParse` BinaryExpr
        OpAnd
        (BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 1)))
        (BinaryExpr OpEq (IdentExpr (Identifier "y")) (ValueExpr (NumValue 2)))

  it "parses parenthesized expressions" $ do
    parseExpression "(1 + 2) * 3"
      `shouldParse` BinaryExpr
        OpMul
        (BinaryExpr OpAdd (ValueExpr (NumValue 1)) (ValueExpr (NumValue 2)))
        (ValueExpr (NumValue 3))

  it "parses function calls" $ do
    parseExpression "COUNT()"
      `shouldParse` FunctionExpr (FunctionCall (Identifier "COUNT") [])

    parseExpression "LENGTH(x)"
      `shouldParse` FunctionExpr (FunctionCall (Identifier "LENGTH") [ArgIdent (Identifier "x")])

    parseExpression "SUM(1, 2, 3)"
      `shouldParse` FunctionExpr
        ( FunctionCall
            (Identifier "SUM")
            [ ArgValue (NumValue 1),
              ArgValue (NumValue 2),
              ArgValue (NumValue 3)
            ]
        )

-- | Tests for condition parsing
conditionSpec :: Spec
conditionSpec = describe "condition parser" $ do
  it "parses IS NULL conditions" $ do
    parseExpression "GABBJ NULL"
      `shouldParse` BinaryExpr OpEq (IdentExpr (Identifier "GABBJ")) (ValueExpr NullValue)

    parseExpression "GABBJ IS NULL"
      `shouldParse` BinaryExpr OpEq (IdentExpr (Identifier "GABBJ")) (ValueExpr NullValue)

  it "parses IS NOT NULL conditions" $ do
    parseExpression "GABBJ NOT NULL"
      `shouldParse` BinaryExpr OpNeq (IdentExpr (Identifier "GABBJ")) (ValueExpr NullValue)

    parseExpression "GABBJ IS NOT NULL"
      `shouldParse` BinaryExpr OpNeq (IdentExpr (Identifier "GABBJ")) (ValueExpr NullValue)

  it "parses comparison conditions" $ do
    parseExpression "GSTAT = 1007"
      `shouldParse` BinaryExpr OpEq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007))

    parseExpression "x > 5"
      `shouldParse` BinaryExpr OpGt (IdentExpr (Identifier "x")) (ValueExpr (NumValue 5))

    parseExpression "x NOT NULL AND >= 10 AND < 50"
      `shouldParse` BinaryExpr
        OpAnd
        ( BinaryExpr
            OpAnd
            (BinaryExpr OpNeq (IdentExpr (Identifier "x")) (ValueExpr NullValue))
            (BinaryExpr OpGte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 10)))
        )
        (BinaryExpr OpLt (IdentExpr (Identifier "x")) (ValueExpr (NumValue 50)))

  it "parses BETWEEN conditions" $ do
    parseExpression "x BETWEEN 1 AND 10"
      `shouldParse` BinaryExpr
        OpAnd
        (BinaryExpr OpGte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 1)))
        (BinaryExpr OpLte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 10)))

  it "parses IN conditions" $ do
    parseExpression "x IN (1)"
      `shouldParse` BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 1))

    parseExpression "x IN (1|2|3)"
      `shouldParse` BinaryExpr
        OpOr
        (BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 1)))
        ( BinaryExpr
            OpOr
            (BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 2)))
            (BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 3)))
        )

  it "parses IN with ranges" $ do
    parseExpression "x IN (1-5)"
      `shouldParse` BinaryExpr
        OpAnd
        (BinaryExpr OpGte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 1)))
        (BinaryExpr OpLte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 5)))

    parseExpression "x IN (1-5|10-20)"
      `shouldParse` BinaryExpr
        OpOr
        ( BinaryExpr
            OpAnd
            (BinaryExpr OpGte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 1)))
            (BinaryExpr OpLte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 5)))
        )
        ( BinaryExpr
            OpAnd
            (BinaryExpr OpGte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 10)))
            (BinaryExpr OpLte (IdentExpr (Identifier "x")) (ValueExpr (NumValue 20)))
        )

  it "parses NOT IN conditions" $ do
    parseExpression "x NOT IN (1|2)"
      `shouldParse` UnaryExpr
        OpNot
        ( BinaryExpr
            OpOr
            (BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 1)))
            (BinaryExpr OpEq (IdentExpr (Identifier "x")) (ValueExpr (NumValue 2)))
        )

  it "parses MATCHES conditions" $ do
    -- The MATCHES elaboration should add LPARZ as the first argument to LENGTH (whether it is explicitly declared as function or simply identifier)
    parseExpression "LPARZ MATCHES (LENGTH <= 12)"
      `shouldParse` BinaryExpr
        OpLte
        (FunctionExpr (FunctionCall (Identifier "LENGTH") [ArgIdent (Identifier "LPARZ")]))
        (ValueExpr (NumValue 12))

    parseExpression "LPARZ (LENGTH() <= 12)"
      `shouldParse` BinaryExpr
        OpLte
        (FunctionExpr (FunctionCall (Identifier "LENGTH") [ArgIdent (Identifier "LPARZ")]))
        (ValueExpr (NumValue 12))

-- | Tests for statement parsing
statementSpec :: Spec
statementSpec = describe "statement parser" $ do
  it "parses simple statements" $ do
    parseStatement "GSTAT = 1007"
      `shouldParse` SimpleStatement (BinaryExpr OpEq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007)))

  it "parses filtered statements" $ do
    parseStatement "GSTAT = 1007 WHERE GABBJ NOT NULL"
      `shouldParse` FilteredStatement
        (BinaryExpr OpEq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007)))
        (BinaryExpr OpNeq (IdentExpr (Identifier "GABBJ")) (ValueExpr NullValue))

-- | Tests for program parsing
programSpec :: Spec
programSpec = describe "program parser" $ do
  it "parses complete programs" $ do
    parseProgram "GSTAT = 1007"
      `shouldParse` SimpleStatement (BinaryExpr OpEq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007)))

  it "handles whitespace and comments" $ do
    parseProgram "  GSTAT = 1007  "
      `shouldParse` SimpleStatement (BinaryExpr OpEq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007)))

    parseProgram "-- This is a comment\nGSTAT = 1007"
      `shouldParse` SimpleStatement (BinaryExpr OpEq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007)))

-- | Tests based on real-world examples from the documents
realWorldExamplesSpec :: Spec
realWorldExamplesSpec = describe "real world examples" $ do
  it "parses BQ9980 rule" $ do
    parseProgram "GSTAT=1007 WHERE GABBJ NOT NULL"
      `shouldParse` FilteredStatement
        (BinaryExpr OpEq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007)))
        (BinaryExpr OpNeq (IdentExpr (Identifier "GABBJ")) (ValueExpr NullValue))

  it "parses complex conditions" $ do
    parseProgram "GKAT IN (1020|1030) AND WSTAT = 3004"
      `shouldParse` SimpleStatement
        ( BinaryExpr
            OpAnd
            ( BinaryExpr
                OpOr
                (BinaryExpr OpEq (IdentExpr (Identifier "GKAT")) (ValueExpr (NumValue 1020)))
                (BinaryExpr OpEq (IdentExpr (Identifier "GKAT")) (ValueExpr (NumValue 1030)))
            )
            (BinaryExpr OpEq (IdentExpr (Identifier "WSTAT")) (ValueExpr (NumValue 3004)))
        )

    parseProgram "GKAT NULL OR IN (1020|1030) AND WSTAT = 3004 OR GSTAT >= 1000 AND <> 1007"
      `shouldParse` SimpleStatement
        ( BinaryExpr
            OpOr
            ( BinaryExpr
                OpAnd
                ( BinaryExpr
                    OpOr
                    (BinaryExpr OpEq (IdentExpr (Identifier "GKAT")) (ValueExpr NullValue))
                    ( BinaryExpr
                        OpOr
                        (BinaryExpr OpEq (IdentExpr (Identifier "GKAT")) (ValueExpr (NumValue 1020)))
                        (BinaryExpr OpEq (IdentExpr (Identifier "GKAT")) (ValueExpr (NumValue 1030)))
                    )
                )
                (BinaryExpr OpEq (IdentExpr (Identifier "WSTAT")) (ValueExpr (NumValue 3004)))
            )
            ( BinaryExpr
                OpAnd
                (BinaryExpr OpGte (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1000)))
                (BinaryExpr OpNeq (IdentExpr (Identifier "GSTAT")) (ValueExpr (NumValue 1007)))
            )
        )

  it "parses nested function calls" $ do
    parseProgram "COUNT(WSTAT) > 0"
      `shouldParse` SimpleStatement
        ( BinaryExpr
            OpGt
            (FunctionExpr (FunctionCall (Identifier "COUNT") [ArgIdent (Identifier "WSTAT")]))
            (ValueExpr (NumValue 0))
        )

  it "should handle nested function call elaboration in MATCHES clause" $ do
    parseProgram "GEBNR NULL OR (LENGTH <=12 AND COUNT(LEADINGZERO)=0)"
      `shouldParse` SimpleStatement
        ( BinaryExpr
            OpOr
            (BinaryExpr OpEq (IdentExpr (Identifier "GEBNR")) (ValueExpr NullValue))
            ( BinaryExpr
                OpAnd
                (BinaryExpr OpLte (FunctionExpr (FunctionCall (Identifier "LENGTH") [ArgIdent (Identifier "GEBNR")])) (ValueExpr (NumValue 12)))
                (BinaryExpr OpEq (FunctionExpr (FunctionCall (Identifier "COUNT") [ArgFunction (FunctionCall (Identifier "LEADINGZERO") [ArgIdent (Identifier "GEBNR")])])) (ValueExpr (NumValue 0)))
            )
        )