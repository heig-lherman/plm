{-
    Main executable for the StandardQL compiler
    This program reads StandardQL constraint rules from a file, stdin, or inline argument,
    parses them, and outputs the resulting AST in JSON format or pretty-printed form.

    Authors: Loïc Herman
-}
{-# LANGUAGE OverloadedStrings #-}

module Main where

import Control.Monad (unless)
import Data.Aeson (encode)
import Data.ByteString.Lazy.Char8 qualified as BSL
import Data.Text (Text)
import Data.Text.IO qualified as TIO
import Options.Applicative
import Options.Applicative.Help (Doc, vsep)
import StandardQL qualified as SQL
import System.Exit (exitFailure)
import System.IO (hPutStrLn, stderr)

-- | Command line options
data Options = Options
  { optInput :: Maybe FilePath,
    optOutput :: Maybe FilePath,
    optPretty :: Bool,
    optValidate :: Bool,
    optInline :: Maybe Text
  }
  deriving (Show)

-- | Parser for command line options
optionsParser :: Parser Options
optionsParser =
  Options
    <$> optional
      ( strOption
          ( long "input"
              <> short 'i'
              <> metavar "FILE"
              <> help "Input file (default: stdin if no inline input)"
          )
      )
    <*> optional
      ( strOption
          ( long "output"
              <> short 'o'
              <> metavar "FILE"
              <> help "Output file (default: stdout)"
          )
      )
    <*> switch
      ( long "pretty"
          <> short 'p'
          <> help "Pretty print the AST instead of JSON"
      )
    <*> switch
      ( long "validate"
          <> short 'v'
          <> help "Only validate, don't output (exit code indicates success)"
      )
    <*> optional
      ( argument
          str
          ( metavar "STATEMENT"
              <> help "StandardQL statement to parse (alternative to file input)"
          )
      )

examples :: Doc
examples =
  vsep
    [ "Examples:",
      "  standardql-compiler \"GSTAT NOT NULL OR IN (1007-1020|1030)\"      # Parse inline statement",
      "  standardql-compiler -i input.sql                                 # Parse from file",
      "  cat input.sql | standardql-compiler                              # Parse from stdin",
      "  standardql-compiler -p \"GSTAT NOT NULL OR IN (1007-1020|1030)\"   # Pretty print output"
    ]

-- | Main entry point
main :: IO ()
main = do
  options <- execParser opts
  input <- getInput options

  case SQL.parseProgram input of
    Left err -> do
      hPutStrLn stderr "Parse error:"
      hPutStrLn stderr $ SQL.formatError err
      exitFailure
    Right program -> do
      unless (optValidate options) $ do
        output <- case optOutput options of
          Nothing -> return BSL.putStrLn
          Just path -> return $ BSL.writeFile path

        if optPretty options
          then output $ BSL.pack $ show program
          else output $ encode program
  where
    opts =
      info
        (optionsParser <**> helper)
        ( fullDesc
            <> progDesc "Parse StandardQL constraint rules"
            <> header "standardql-compiler - StandardQL parser and compiler"
            <> footerDoc (Just examples)
        )

-- | Get input from inline argument, file, or stdin (in that priority order)
getInput :: Options -> IO Text
getInput options =
  case optInline options of
    Just statement -> return statement
    Nothing -> readInput (optInput options)

-- | Read input from file or stdin
readInput :: Maybe FilePath -> IO Text
readInput Nothing = TIO.getContents
readInput (Just path) = TIO.readFile path
