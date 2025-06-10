package ch.vd.ptep.mrq.compiler.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class StatementDeserializationTest {

    @Test
    void test() {
        var om = new ObjectMapper();
        om.enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());

        var json = """
            {
              "type": "SimpleStatement",
              "expr": {
                "type": "BinaryExpr",
                "op": "OpOr",
                "left": {
                  "type": "BinaryExpr",
                  "op": "OpAnd",
                  "left": {
                    "type": "BinaryExpr",
                    "op": "OpGt",
                    "left": {
                      "type": "FunctionCall",
                      "name": "COUNT",
                      "args": [
                        {
                          "type": "ArgIdent",
                          "ident": "EWID"
                        },
                        {
                          "type": "ArgValue",
                          "value": {
                            "contents": "str value",
                            "tag": "StrValue"
                          }
                        },
                        {
                          "type": "ArgFunction",
                          "name": "VALUE_EQ",
                          "args": [
                            {
                              "type": "ArgValue",
                              "value": {
                                "contents": 5,
                                "tag": "NumValue"
                              }
                            }
                          ]
                        }
                      ]
                    },
                    "right": {
                      "type": "ValueExpr",
                      "value": {
                        "contents": 5,
                        "tag": "NumValue"
                      }
                    }
                  },
                  "right": {
                    "type": "BinaryExpr",
                    "op": "OpOr",
                    "left": {
                      "type": "BinaryExpr",
                      "op": "OpEq",
                      "left": {
                        "type": "IdentExpr",
                        "ident": "STUFF"
                      },
                      "right": {
                        "type": "ValueExpr",
                        "value": {
                          "tag": "NullValue"
                        }
                      }
                    },
                    "right": {
                      "type": "BinaryExpr",
                      "op": "OpAnd",
                      "left": {
                        "type": "UnaryExpr",
                        "op": "OpNot",
                        "expr": {
                          "type": "BinaryExpr",
                          "op": "OpEq",
                          "left": {
                            "type": "IdentExpr",
                            "ident": "THIS"
                          },
                          "right": {
                            "type": "ValueExpr",
                            "value": {
                              "contents": 1020,
                              "tag": "NumValue"
                            }
                          }
                        }
                      },
                      "right": {
                        "type": "BinaryExpr",
                        "op": "OpEq",
                        "left": {
                          "type": "IdentExpr",
                          "ident": "ITEM"
                        },
                        "right": {
                          "type": "ValueExpr",
                          "value": {
                            "contents": "VAL",
                            "tag": "StrValue"
                          }
                        }
                      }
                    }
                  }
                },
                "right": {
                  "type": "BinaryExpr",
                  "op": "OpLt",
                  "left": {
                    "type": "IdentExpr",
                    "ident": "VAL"
                  },
                  "right": {
                    "type": "IdentExpr",
                    "ident": "VOL"
                  }
                }
              }
            }
            """;

        assertDoesNotThrow(() -> om.readValue(json, Statement.class));
    }

}
