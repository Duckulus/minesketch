package de.duckulus.minesketch;

import de.duckulus.minesketch.ast.Expr;
import de.duckulus.minesketch.ast.Expr.AssignExpr;
import de.duckulus.minesketch.ast.Expr.BinaryExpr;
import de.duckulus.minesketch.ast.Expr.CallExpr;
import de.duckulus.minesketch.ast.Expr.IndexExpr;
import de.duckulus.minesketch.ast.Expr.LiteralExpr;
import de.duckulus.minesketch.ast.Expr.LogicalExpr;
import de.duckulus.minesketch.ast.Expr.UnaryExpr;
import de.duckulus.minesketch.ast.Expr.VariableExpr;
import de.duckulus.minesketch.ast.Stmt;
import de.duckulus.minesketch.ast.Stmt.BlockStatement;
import de.duckulus.minesketch.ast.Stmt.ExprStmt;
import de.duckulus.minesketch.ast.Stmt.FunDeclaration;
import de.duckulus.minesketch.ast.Stmt.VarDeclaration;
import de.duckulus.minesketch.token.Token;
import de.duckulus.minesketch.token.TokenType;
import java.util.ArrayList;
import java.util.List;

public class Parser {

  private final List<Token> tokens;
  private int index = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public List<Stmt> parse() {
    List<Stmt> declarations = new ArrayList<>();
    while (!isAtEnd()) {
      declarations.add(declaration());
    }
    return declarations;
  }

  private Stmt declaration() {
    if (match(TokenType.INPUT)) {
      return inputVarDeclaration();
    } else if (match(TokenType.VAR)) {
      return varDeclaration(false);
    } else if (match(TokenType.FN)) {
      return funDeclaration();
    } else {
      return statement();
    }
  }

  private Stmt statement() {
    Expr expr = expression();
    consume(TokenType.SEMICOLON);
    return new ExprStmt(expr);
  }

  private Stmt funDeclaration() {
    Token identifier = consume(TokenType.IDENTIFIER);
    consume(TokenType.LEFT_PAREN);

    List<Token> params = new ArrayList<>();

    if (!match(TokenType.RIGHT_PAREN)) {
      do {
        params.add(consume(TokenType.IDENTIFIER));
      } while (match(TokenType.COMMA));
      consume(TokenType.RIGHT_PAREN);
    }

    BlockStatement block = block();

    return new FunDeclaration(identifier, params, block);
  }

  private BlockStatement block() {
    consume(TokenType.LEFT_BRACE);
    List<Stmt> statements = new ArrayList<>();
    while (!match(TokenType.RIGHT_BRACE)) {
      statements.add(declaration());
    }
    return new BlockStatement(statements);
  }


  private Stmt inputVarDeclaration() {
    consume(TokenType.VAR);
    return varDeclaration(true);
  }

  private Stmt varDeclaration(boolean input) {
    Token identifier = consume(TokenType.IDENTIFIER);
    consume(TokenType.EQUAL);
    Expr expr = expression();
    consume(TokenType.SEMICOLON);
    return new VarDeclaration(identifier, expr, input);
  }

  private Expr expression() {
    return assignment();
  }

  private Expr assignment() {
    if (current().type() == TokenType.IDENTIFIER && next().type() == TokenType.EQUAL) {
      Token identifier = consume(TokenType.IDENTIFIER);
      consume(TokenType.EQUAL);
      Expr right = assignment();
      return new AssignExpr(identifier, right);
    }

    return logicOr();
  }

  private Expr logicOr() {
    Expr left = logicAnd();
    while (match(TokenType.OR)) {
      Token or = previous();
      Expr right = logicAnd();
      left = new LogicalExpr(left, or, right);
    }
    return left;
  }

  private Expr logicAnd() {
    Expr left = equality();
    while (match(TokenType.AND)) {
      Token and = previous();
      Expr right = equality();
      left = new LogicalExpr(left, and, right);
    }
    return left;
  }

  private Expr equality() {
    Expr left = comparison();
    while (match(TokenType.BANG_EQUAL) || match(TokenType.EQUAL_EQUAL)) {
      Token op = previous();
      Expr right = comparison();
      left = new BinaryExpr(left, op, right);
    }
    return left;
  }

  private Expr comparison() {
    Expr left = term();
    while (match(TokenType.GREATER) || match(TokenType.GREATER_EQUAL) || match(TokenType.LESS)
        || match(TokenType.LESS_EQUAL)) {
      Token op = previous();
      Expr right = term();
      left = new BinaryExpr(left, op, right);
    }
    return left;
  }

  private Expr term() {
    Expr left = factor();
    while (match(TokenType.MINUS) || match(TokenType.PLUS)) {
      Token op = previous();
      Expr right = factor();
      left = new BinaryExpr(left, op, right);
    }
    return left;
  }

  private Expr factor() {
    Expr left = unary();
    while (match(TokenType.SLASH) || match(TokenType.STAR)) {
      Token op = previous();
      Expr right = unary();
      left = new BinaryExpr(left, op, right);
    }
    return left;
  }

  private Expr unary() {
    if (match(TokenType.MINUS) || match(TokenType.BANG)) {
      Token op = previous();
      Expr expr = unary();
      return new UnaryExpr(expr, op);
    }
    return call();
  }

  private Expr call() {
    Expr expr = primary();
    while (true) {
      if (match(TokenType.LEFT_PAREN)) {
        List<Expr> args = new ArrayList<>();
        if (!match(TokenType.RIGHT_PAREN)) {
          do {
            args.add(expression());
          } while (match(TokenType.COMMA));
          consume(TokenType.RIGHT_PAREN);
        }
        expr = new CallExpr(expr, args);
      } else if (match(TokenType.LEFT_BRACKET)) {
        Expr index = expression();
        consume(TokenType.RIGHT_BRACKET);
        expr = new IndexExpr(expr, index);
      } else {
        break;
      }
    }
    return expr;
  }

  private Expr primary() {
    if (match(TokenType.TRUE)) {
      return new LiteralExpr(true);
    }
    if (match(TokenType.FALSE)) {
      return new LiteralExpr(false);
    }
    if (match(TokenType.NULL)) {
      return new LiteralExpr(null);
    }
    if (match(TokenType.LITERAL_FLOAT) || match(TokenType.LITERAL_INT) || match(
        TokenType.LITERAL_STRING)) {
      return new LiteralExpr(previous());
    }
    if (match(TokenType.LEFT_PAREN)) {
      Expr expr = expression();
      consume(TokenType.RIGHT_PAREN);
      return expr;
    }
    if (match(TokenType.IDENTIFIER)) {
      return new VariableExpr(previous());
    }
    throw new RuntimeException("Expected Expression");
  }

  private Token consume(TokenType tokenType) {
    if (match(tokenType)) {
      return previous();
    }
    throw new RuntimeException("Expected " + tokenType + " but found " + current());
  }

  private Token current() {
    return tokens.get(index);
  }

  private Token next() {
    if (isAtEnd()) {
      return current();
    }
    return tokens.get(index + 1);
  }

  private Token previous() {
    return tokens.get(index - 1);
  }

  private Token advance() {
    if (!isAtEnd()) {
      index++;
    }
    return previous();
  }

  private boolean match(TokenType tokenType) {
    if (tokenType == tokens.get(index).type()) {
      advance();
      return true;
    }
    return false;
  }

  private boolean isAtEnd() {
    return current().type() == TokenType.EOF;
  }


}
