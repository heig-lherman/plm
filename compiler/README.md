# StandardQL Parser

A Haskell implementation of the StandardQL language parser for the Swiss Federal Register of Buildings and Dwellings (RegBL).

## Overview

StandardQL is a declarative constraint language designed for expressing quality rules in the RegBL system. This parser:

- Implements the full StandardQL grammar as specified in the BNF
- Immediately elaborates condition syntax into expressions
- Produces a JSON-serializable AST for Java interoperability
- Handles operator precedence correctly (LL(2) parser)

## Building

```bash
cabal build
```

## Testing

```bash
cabal test
```

## Usage

### Command Line

```bash
# Parse from stdin
echo "GSTAT = 1007 WHERE GABBJ NOT NULL" | cabal run standardql-compiler

# Parse from file
cabal run standardql-compiler -- -i rule.sql -o output.json

# Pretty print AST
cabal run standardql-compiler -- -i rule.sql -p

# Validate only (no output)
cabal run standardql-compiler -- -i rule.sql -v
```

### As a Library

```haskell
import StandardQL
import Data.Aeson (encode)

-- Parse a program
case parseProgram "GSTAT = 1007" of
  Left err -> putStrLn $ errorBundlePretty err
  Right program -> print $ encode program
```

## Language Features

### Basic Expressions

```sql
-- Simple comparisons
GSTAT = 1007
x > 5
name != "test"

-- Arithmetic
x + 1 = 10
price * 1.2 < 100

-- Logical operators
x > 0 AND y < 10
status = 1 OR status = 2
```

### Conditions (Syntactic Sugar)

```sql
-- NULL checks
GABBJ NULL          -- elaborates to: GABBJ = NULL
GABBJ NOT NULL      -- elaborates to: GABBJ != NULL

-- BETWEEN
x BETWEEN 1 AND 10  -- elaborates to: x >= 1 AND x <= 10

-- IN
x IN (1|2|3)        -- elaborates to: x = 1 OR x = 2 OR x = 3
x IN (1-5)          -- elaborates to: x >= 1 AND x <= 5
x IN (1|5-10|20)    -- mixed ranges

-- NOT IN
x NOT IN (1|2)      -- elaborates to: NOT (x = 1 OR x = 2)
```

### MATCHES Operator

The MATCHES operator creates a closure where the identifier is implicitly passed as the first argument to function calls:

```sql
LPARZ MATCHES (LENGTH <= 12)
-- elaborates to: LENGTH(LPARZ) <= 12

name (LENGTH > 0 AND < 100)
-- elaborates to: LENGTH(name) > 0 AND LENGTH(name) < 100
```

### WHERE Clauses

```sql
GSTAT = 1007 WHERE GABBJ NOT NULL
-- Statement with a filter condition
```

### Function Calls

```sql
COUNT(WSTAT) > 0
SUM(amount) <= 1000
LENGTH(name) BETWEEN 1 AND 50
```

## Examples from RegBL

```sql
-- BQ9980: Building status must be "demolished" when demolition year is set
GSTAT = 1007 WHERE GABBJ NOT NULL

-- BQ7447: Construction year must not be after demolition year
GABBJ>=GBAUJ
WHERE GBAUJ NOT NULL AND GABBJ NOT NULL
```

## AST Structure

The parser produces a JSON-serializable AST with the following main types:

- **Program**: Top-level container
- **Statement**: Simple or filtered (with WHERE)
- **Expression**: Binary operations, function calls, identifiers, values
- **Value**: Numbers, strings, or NULL
- **Identifier**: Variable/attribute names
- **FunctionCall**: Function name with arguments

## Implementation Notes

1. **Condition Elaboration**: All condition syntax is immediately transformed into equivalent expressions during parsing
2. **Operator Precedence**: Follows standard precedence (multiplication before addition, AND before OR, etc.)
3. **Reserved Words**: AND, OR, IS, NOT, NULL, BETWEEN, IN, MATCHES, WHERE cannot be used as identifiers
4. **Case Sensitivity**: Keywords are case-insensitive, identifiers are case-sensitive
5. **Comments**: Supports `--` line comments and `{- -}` block comments
