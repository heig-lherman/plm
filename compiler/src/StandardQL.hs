{-
    StandardQL.hs - StandardQL Library : Exports the public API for the StandardQL language
    Authors: Loïc Herman
-}
module StandardQL
  ( -- * StandardQL AST

    -- ** Program structure
    Statement (..),

    -- ** Expressions
    Expression (..),
    BinaryOp (..),
    UnaryOp (..),

    -- ** Values and identifiers
    Value (..),
    Identifier (..),

    -- ** Function calls
    FunctionCall (..),
    Argument (..),

    -- * StandardQL Parser

    -- ** Main types
    Parser,

    -- ** Parsing functions
    parseProgram,
    parseStatement,
    parseExpression,

    -- * Error handling
    ParseError (..),
    formatError,
  )
where

import Data.Text (Text)
import StandardQL.Parser
import StandardQL.Tree
import Text.Megaparsec (ParseErrorBundle, errorBundlePretty)

formatError :: ParseErrorBundle Text ParseError -> String
formatError = errorBundlePretty
