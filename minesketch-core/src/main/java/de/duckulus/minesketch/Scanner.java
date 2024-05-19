package de.duckulus.minesketch;

import de.duckulus.minesketch.token.Token;
import de.duckulus.minesketch.token.TokenType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

  private static final Map<String, TokenType> keywords = new HashMap<>();

  static {
    keywords.put("var", TokenType.VAR);
    keywords.put("fn", TokenType.FN);
    keywords.put("while", TokenType.WHILE);
    keywords.put("for", TokenType.FOR);
    keywords.put("if", TokenType.IF);
    keywords.put("else", TokenType.ELSE);
    keywords.put("null", TokenType.NULL);
    keywords.put("input", TokenType.INPUT);
    keywords.put("true", TokenType.TRUE);
    keywords.put("false", TokenType.FALSE);
    keywords.put("return", TokenType.RETURN);
  }

  private final List<Token> tokens = new ArrayList<>();

  private final String text;

  private int start = 0;
  private int index = 0;
  private int line = 1;

  public Scanner(String text) {
    this.text = text;
  }

  public List<Token> scan() {
    while (!isAtEnd()) {
      start = index;
      scanToken();
    }
    tokens.add(new Token(TokenType.EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '\n':
        line++;
      case ' ':
      case '\r':
      case '\t':
        break;
      case '~':
        while (!isAtEnd() && !match('\n')) {
          index++;
        }
        line++;
        break;
      case '(':
        addToken(TokenType.LEFT_PAREN);
        break;
      case ')':
        addToken(TokenType.RIGHT_PAREN);
        break;
      case '{':
        addToken(TokenType.LEFT_BRACE);
        break;
      case '}':
        addToken(TokenType.RIGHT_BRACE);
        break;
      case '[':
        addToken(TokenType.LEFT_BRACKET);
        break;
      case ']':
        addToken(TokenType.RIGHT_BRACKET);
        break;
      case ',':
        addToken(TokenType.COMMA);
        break;
      case '.':
        addToken(TokenType.DOT);
        break;
      case ';':
        addToken(TokenType.SEMICOLON);
        break;
      case '+':
        addToken(TokenType.PLUS);
        break;
      case '-':
        addToken(TokenType.MINUS);
        break;
      case '/':
        addToken(TokenType.SLASH);
        break;
      case '*':
        addToken(TokenType.STAR);
        break;
      case '%':
        addToken(TokenType.MODULO);
        break;
      case '&':
        consume('&', "Expected '&'");
        addToken(TokenType.AND);
        break;
      case '|':
        consume('|', "Expected '|'");
        addToken(TokenType.OR);
        break;
      case '!':
        addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
        break;
      case '=':
        addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
        break;
      case '>':
        addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        break;
      case '<':
        addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        break;
      case '"':
        while (!isAtEnd() && !match('"')) {
          advance();
        }

        if (isAtEnd()) {
          throw new RuntimeException("Unterminated String");
        }

        addToken(TokenType.LITERAL_STRING, text.substring(start + 1, index - 1));
        break;
      default:
        if (Character.isDigit(c)) {
          while (Character.isDigit(peek())) {
            advance();
          }
          if (peek() == '.' && Character.isDigit(peekNext())) {
            advance();
            while (Character.isDigit(peek())) {
              advance();
            }
            addToken(TokenType.LITERAL_FLOAT, Double.parseDouble(text.substring(start, index)));
          } else {
            addToken(TokenType.LITERAL_INT, Integer.parseInt(text.substring(start, index)));
          }
        } else if (Character.isLetter(c)) {
          while (Character.isLetterOrDigit(peek())) {
            advance();
          }
          addToken(keywords.getOrDefault(text.substring(start, index), TokenType.IDENTIFIER));
        } else {
          throw new RuntimeException("Unexpected Symbol " + c);
        }
        break;

    }
  }

  private boolean isAtEnd() {
    return index >= text.length();
  }

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }
    return text.charAt(index);
  }

  private char peekNext() {
    if (index + 1 >= text.length()) {
      return '\0';
    }
    return text.charAt(index + 1);
  }

  private char advance() {
    return text.charAt(index++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literalValue) {
    tokens.add(new Token(type, text.substring(start, index), literalValue, line));
  }

  private void consume(char c, String errorMessage) {
    if (advance() != c) {
      throw new RuntimeException(errorMessage);
    }
  }

  private boolean match(char c) {
    if (text.charAt(index) == c) {
      index++;
      return true;
    }
    return false;
  }

}
