package de.duckulus.minesketch.token;

public record Token(TokenType type, String lexeme, Object literalValue, int line) {

}
