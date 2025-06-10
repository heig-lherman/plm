{-
    Elaboration.hs - StandardQL Elaboration into an Internal Language (IL) for further evaluation
    Authors: Loïc Herman
-}
{-# LANGUAGE OverloadedStrings #-}

module StandardQL.Elaboration
  ( -- * Elaboration functions
    elaborateCondition,
  )
where

import StandardQL.Tree

-- | Elaborates condition syntax into full expressions
-- This function is used during parsing to immediately convert
-- condition shortcuts into their expression equivalents
elaborateCondition :: Identifier -> Condition -> Expression
elaborateCondition ident cond = case cond of
  CondOr c1 c2 ->
    BinaryExpr OpOr (elaborateCondition ident c1) (elaborateCondition ident c2)
  CondAnd c1 c2 ->
    BinaryExpr OpAnd (elaborateCondition ident c1) (elaborateCondition ident c2)
  CondIsNull ->
    BinaryExpr OpEq (IdentExpr ident) (ValueExpr NullValue)
  CondIsNotNull ->
    BinaryExpr OpNeq (IdentExpr ident) (ValueExpr NullValue)
  CondEq val ->
    BinaryExpr OpEq (IdentExpr ident) (ValueExpr val)
  CondNeq val ->
    BinaryExpr OpNeq (IdentExpr ident) (ValueExpr val)
  CondLt val ->
    BinaryExpr OpLt (IdentExpr ident) (ValueExpr val)
  CondLte val ->
    BinaryExpr OpLte (IdentExpr ident) (ValueExpr val)
  CondGt val ->
    BinaryExpr OpGt (IdentExpr ident) (ValueExpr val)
  CondGte val ->
    BinaryExpr OpGte (IdentExpr ident) (ValueExpr val)
  CondBetween v1 v2 ->
    BinaryExpr
      OpAnd
      (BinaryExpr OpGte (IdentExpr ident) (ValueExpr v1))
      (BinaryExpr OpLte (IdentExpr ident) (ValueExpr v2))
  CondIn values ->
    elaborateInCondition ident values False
  CondNotIn values ->
    elaborateInCondition ident values True
  CondMatches expr ->
    -- For MATCHES, we create a special function call that will be handled
    -- during evaluation. The MATCHES operator creates a closure where
    -- the identifier is implicitly passed as the first argument to
    -- any function calls within the expression
    elaborateMatchesExpression ident expr

-- | Elaborates IN/NOT IN conditions into OR/AND expressions
elaborateInCondition :: Identifier -> [ValueRange] -> Bool -> Expression
elaborateInCondition ident ranges isNegated =
  let -- Convert each range to an expression
      rangeToExpr (SingleValue v) =
        BinaryExpr OpEq (IdentExpr ident) (ValueExpr v)
      rangeToExpr (RangeValue v1 v2) =
        BinaryExpr
          OpAnd
          (BinaryExpr OpGte (IdentExpr ident) (ValueExpr v1))
          (BinaryExpr OpLte (IdentExpr ident) (ValueExpr v2))

      -- Combine all range expressions with OR
      combineWithOr [] = error "No ranges provided for IN/NOT IN condition"
      combineWithOr [e] = e
      combineWithOr (e : es) = BinaryExpr OpOr e (combineWithOr es)

      orExpr = combineWithOr (map rangeToExpr ranges)
   in if isNegated
        then UnaryExpr OpNot orExpr
        else orExpr

-- | Elaborates MATCHES expressions by transforming function calls
-- to include the identifier as the first argument when needed
elaborateMatchesExpression :: Identifier -> Expression -> Expression
elaborateMatchesExpression ident = transformExpr
  where
    transformExpr :: Expression -> Expression
    transformExpr e = case e of
      BinaryExpr op e1 e2 -> BinaryExpr op (transformExpr e1) (transformExpr e2)
      UnaryExpr op e1 -> UnaryExpr op (transformExpr e1)
      FunctionExpr fc -> FunctionExpr (transformFunctionCall fc)
      IdentExpr (Identifier name) -> FunctionExpr (FunctionCall (Identifier name) [ArgIdent ident])
      ValueExpr _ -> e

    transformFunctionCall :: FunctionCall -> FunctionCall
    transformFunctionCall (FunctionCall name args) =
      case args of
        [] ->
          -- No arguments: add the identifier as the first argument
          FunctionCall name [ArgIdent ident]
        _ ->
          -- Has arguments: transform nested function calls in arguments
          FunctionCall name (map transformArg args)

    transformArg :: Argument -> Argument
    transformArg arg = case arg of
      ArgFunction fc -> ArgFunction (transformFunctionCall fc)
      ArgIdent (Identifier name) -> ArgFunction (FunctionCall (Identifier name) [ArgIdent ident])
      _ -> arg
