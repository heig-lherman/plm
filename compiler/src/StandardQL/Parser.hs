{-
    Parser.hs - StandardQL Parser
    Authors: Loïc Herman
-}
module StandardQL.Parser where

type Parser a = String -> [(a, String)]
