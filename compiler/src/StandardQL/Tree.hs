{-
    Tree.hs - StandardQL Abstract Syntax Tree
    Authors: Loïc Herman
-}
{-# LANGUAGE DeriveAnyClass #-}
{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE DerivingStrategies #-}
{-# LANGUAGE OverloadedStrings #-}

-- | Abstract Syntax Tree for StandardQL
module StandardQL.Tree
  ( -- * Program structure
    Statement (..),

    -- * Expressions
    Expression (..),
    BinaryOp (..),
    UnaryOp (..),

    -- * Values and identifiers
    Value (..),
    Identifier (..),

    -- * Function calls
    FunctionCall (..),
    Argument (..),

    -- * Internal condition types (for parsing)
    Condition (..),
    ValueRange (..),
  )
where

import Data.Aeson hiding (Value)
import Data.Text (Text)
import GHC.Generics (Generic)

-- | Statements are the top-level elements of the StandardQL syntax tree.
-- | A statement can be a simple expression or a filtered expression (with WHERE clause)
data Statement
  = SimpleStatement Expression
  | FilteredStatement Expression Expression
  deriving (Eq, Show, Generic)

instance ToJSON Statement where
  toEncoding (SimpleStatement e) = pairs ("type" .= ("SimpleStatement" :: Text) <> "expr" .= e)
  toEncoding (FilteredStatement e f) = pairs ("type" .= ("FilteredStatement" :: Text) <> "expr" .= e <> "filter" .= f)

-- | Binary operators in the language
data BinaryOp
  = OpOr
  | OpAnd
  | OpEq
  | OpNeq
  | OpLt
  | OpLte
  | OpGt
  | OpGte
  | OpAdd
  | OpSub
  | OpMul
  | OpDiv
  deriving (Eq, Show, Generic)

instance ToJSON BinaryOp where
  toJSON OpOr = String "OpOr"
  toJSON OpAnd = String "OpAnd"
  toJSON OpEq = String "OpEq"
  toJSON OpNeq = String "OpNeq"
  toJSON OpLt = String "OpLt"
  toJSON OpLte = String "OpLte"
  toJSON OpGt = String "OpGt"
  toJSON OpGte = String "OpGte"
  toJSON OpAdd = String "OpAdd"
  toJSON OpSub = String "OpSub"
  toJSON OpMul = String "OpMul"
  toJSON OpDiv = String "OpDiv"

-- | Unary operators in the language
data UnaryOp
  = OpNot
  deriving (Eq, Show, Generic)

instance ToJSON UnaryOp where
  toJSON OpNot = String "OpNot"

-- | Expressions are the building blocks of statements.
-- | They can be binary or unary operations, identifiers, values, function calls, or parenthesized expressions.
data Expression
  = BinaryExpr BinaryOp Expression Expression
  | UnaryExpr UnaryOp Expression
  | IdentExpr Identifier
  | ValueExpr Value
  | FunctionExpr FunctionCall
  deriving (Eq, Show, Generic)

instance ToJSON Expression where
  toEncoding (BinaryExpr op left right) = pairs ("type" .= ("BinaryExpr" :: Text) <> "op" .= op <> "left" .= left <> "right" .= right)
  toEncoding (UnaryExpr op expr) = pairs ("type" .= ("UnaryExpr" :: Text) <> "op" .= op <> "expr" .= expr)
  toEncoding (IdentExpr ident) = pairs ("type" .= ("IdentExpr" :: Text) <> "ident" .= ident)
  toEncoding (ValueExpr value) = pairs ("type" .= ("ValueExpr" :: Text) <> "value" .= value)
  toEncoding (FunctionExpr (FunctionCall name args)) = pairs ("type" .= ("FunctionCall" :: Text) <> "name" .= name <> "args" .= args)

-- | Values can be numbers or strings
data Value
  = NumValue Double
  | StrValue Text
  | NullValue
  deriving (Eq, Show, Generic, ToJSON)

-- | Identifiers are simple names
newtype Identifier = Identifier
  { identName :: Text
  }
  deriving (Eq, Show, Generic)
  deriving newtype (ToJSON)

-- | Function calls consist of a name and arguments
data FunctionCall = FunctionCall
  { funcName :: Identifier,
    funcArgs :: [Argument]
  }
  deriving (Eq, Show, Generic, ToJSON)

-- | Arguments can be identifiers, values, or nested function calls
data Argument
  = ArgIdent Identifier
  | ArgValue Value
  | ArgFunction FunctionCall
  deriving (Eq, Show, Generic)

instance ToJSON Argument where
  toEncoding (ArgIdent ident) = pairs ("type" .= ("ArgIdent" :: Text) <> "ident" .= ident)
  toEncoding (ArgValue value) = pairs ("type" .= ("ArgValue" :: Text) <> "value" .= value)
  toEncoding (ArgFunction (FunctionCall name args)) = pairs ("type" .= ("ArgFunction" :: Text) <> "name" .= name <> "args" .= args)

-- | Helper type for condition parsing
-- This is used internally during parsing and immediately elaborated
data Condition
  = CondOr Condition Condition
  | CondAnd Condition Condition
  | CondIsNull
  | CondIsNotNull
  | CondEq Value
  | CondNeq Value
  | CondLt Value
  | CondLte Value
  | CondGt Value
  | CondGte Value
  | CondBetween Value Value
  | CondIn [ValueRange]
  | CondNotIn [ValueRange]
  | CondMatches Expression
  deriving (Eq, Show)

-- | A value range for IN conditions
data ValueRange
  = SingleValue Value
  | RangeValue Value Value -- from-to range
  deriving (Eq, Show)
