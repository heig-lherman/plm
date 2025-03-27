module Main where

import qualified StandardQL (someFunc)

main :: IO ()
main = do
  putStrLn "Hello, Haskell!"
  StandardQL.someFunc
