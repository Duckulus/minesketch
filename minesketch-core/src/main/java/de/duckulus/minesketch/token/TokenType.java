package de.duckulus.minesketch.token;

public enum TokenType {

  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET,
  COMMA, DOT, SEMICOLON,

  PLUS, MINUS, SLASH, STAR, MODULO,

  AND, OR,

  BANG, BANG_EQUAL,
  EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL,
  LESS, LESS_EQUAL,

  IDENTIFIER,

  LITERAL_STRING, LITERAL_INT, LITERAL_FLOAT, TRUE, FALSE,

  VAR, FN, WHILE, FOR, IF, ELSE, NULL, INPUT, RETURN,

  EOF

}
