# Revision history for standardql-compiler

## 0.1.0.0 -- 2025-06-12

* Initial release
* Full StandardQL parser implementation using Megaparsec
* Immediate elaboration of condition syntax into expressions
* JSON serialization support for Java interoperability
* Command-line tool for parsing StandardQL files
* Comprehensive test suite using hspec and hspec-megaparsec
* Support for all language features as specified in the grammar:
  - Binary and unary expressions
  - Function calls with arguments
  - Condition syntax (NULL, BETWEEN, IN, MATCHES)
  - WHERE clauses for filtered statements
  - Proper operator precedence
