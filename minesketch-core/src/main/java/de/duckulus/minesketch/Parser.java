package de.duckulus.minesketch;

import de.duckulus.minesketch.ast.Expr;
import de.duckulus.minesketch.ast.Expr.ArrayExpr;
import de.duckulus.minesketch.ast.Expr.AssignExpr;
import de.duckulus.minesketch.ast.Expr.BinaryExpr;
import de.duckulus.minesketch.ast.Expr.CallExpr;
import de.duckulus.minesketch.ast.Expr.IndexAssignExpr;
import de.duckulus.minesketch.ast.Expr.IndexExpr;
import de.duckulus.minesketch.ast.Expr.LiteralExpr;
import de.duckulus.minesketch.ast.Expr.LogicalExpr;
import de.duckulus.minesketch.ast.Expr.UnaryExpr;
import de.duckulus.minesketch.ast.Expr.VariableExpr;
import de.duckulus.minesketch.ast.Stmt;
import de.duckulus.minesketch.ast.Stmt.BlockStmt;
import de.duckulus.minesketch.ast.Stmt.ExprStmt;
import de.duckulus.minesketch.ast.Stmt.FunDeclaration;
import de.duckulus.minesketch.ast.Stmt.IfStmt;
import de.duckulus.minesketch.ast.Stmt.ReturnStmt;
import de.duckulus.minesketch.ast.Stmt.VarDeclaration;
import de.duckulus.minesketch.ast.Stmt.WhileStmt;
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
      return varDeclaration();
    } else if (match(TokenType.FN)) {
      return funDeclaration();
    } else {
      return statement();
    }
  }

  private Stmt statement() {
    if (match(TokenType.IF)) {
      return ifStatement();
    } else if (match(TokenType.WHILE)) {
      return whileStatement();
    } else if (match(TokenType.FOR)) {
      return forStatement();
    } else if (match(TokenType.RETURN)) {
      return returnStatement();
    } else if (match(TokenType.LEFT_BRACE)) {
      return block();
    }

    Expr expr = expression();
    consume(TokenType.SEMICOLON);
    return new ExprStmt(expr);
  }

  private Stmt forStatement() {
    consume(TokenType.LEFT_PAREN);
    Stmt declaration = null;
    if (!match(TokenType.SEMICOLON)) {
      if (match(TokenType.VAR)) {
        declaration = varDeclaration();
      } else {
        declaration = new ExprStmt(expression());
        consume(TokenType.SEMICOLON);
      }
    }

    Expr condition = null;
    if (!match(TokenType.SEMICOLON)) {
      condition = expression();
      consume(TokenType.SEMICOLON);
    }

    Expr increment = null;
    if (!match(TokenType.RIGHT_PAREN)) {
      increment = expression();
      consume(TokenType.RIGHT_PAREN);
    }

    consume(TokenType.LEFT_BRACE);
    BlockStmt body = block();

    if (increment != null) {
      body.statements().add(new ExprStmt(increment));
    }

    List<Stmt> statements = new ArrayList<>();
    if (declaration != null) {
      statements.add(declaration);
    }
    statements.add(new WhileStmt(condition == null ? new LiteralExpr(true) : condition, body));

    return new BlockStmt(statements);
  }

  private Stmt returnStatement() {
    Expr expr = null;
    if (!match(TokenType.SEMICOLON)) {
      expr = expression();
      consume(TokenType.SEMICOLON);
    }
    return new ReturnStmt(expr);
  }

  private Stmt whileStatement() {
    consume(TokenType.LEFT_PAREN);
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN);
    consume(TokenType.LEFT_BRACE);
    BlockStmt body = block();
    return new WhileStmt(condition, body);
  }

  private Stmt ifStatement() {
    consume(TokenType.LEFT_PAREN);
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN);
    consume(TokenType.LEFT_BRACE);
    BlockStmt body = block();
    BlockStmt elseBlock = null;
    if (match(TokenType.ELSE)) {
      consume(TokenType.LEFT_BRACE);
      elseBlock = block();
    }

    return new IfStmt(condition, body, elseBlock);
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

    consume(TokenType.LEFT_BRACE);

    BlockStmt block = block();

    return new FunDeclaration(identifier, params, block);
  }

  private BlockStmt block() {
    List<Stmt> statements = new ArrayList<>();
    while (!match(TokenType.RIGHT_BRACE)) {
      statements.add(declaration());
    }
    return new BlockStmt(statements);
  }


  private Stmt inputVarDeclaration() {
    consume(TokenType.VAR);
    Token identifier = consume(TokenType.IDENTIFIER);
    consume(TokenType.SEMICOLON);
    return new VarDeclaration(identifier, null, true);
  }

  private Stmt varDeclaration() {
    Token identifier = consume(TokenType.IDENTIFIER);
    consume(TokenType.EQUAL);
    Expr expr = expression();
    consume(TokenType.SEMICOLON);
    return new VarDeclaration(identifier, expr, false);
  }

  private Expr expression() {
    return assignment();
  }

  private Expr assignment() {
    Expr left = logicOr();

    if (match(TokenType.EQUAL)) {
      Expr right = assignment();

      if (left instanceof VariableExpr varExpr) {
        return new AssignExpr(varExpr.variable(), right);
      } else if (left instanceof IndexExpr indexExpr) {
        return new IndexAssignExpr(indexExpr, right);
      } else {
        throw new RuntimeException("Invalid assignment target " + left);
      }
    }

    return left;
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
    while (match(TokenType.SLASH) || match(TokenType.STAR) || match(TokenType.MODULO)) {
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
      return new LiteralExpr(previous().literalValue());
    }
    if (match(TokenType.LEFT_PAREN)) {
      Expr expr = expression();
      consume(TokenType.RIGHT_PAREN);
      return expr;
    }
    if (match(TokenType.LEFT_BRACKET)) {
      List<Expr> elements = new ArrayList<>();
      if (!match(TokenType.RIGHT_BRACKET)) {
        do {
          elements.add(expression());
        } while (match(TokenType.COMMA));
        consume(TokenType.RIGHT_BRACKET);
      }
      return new ArrayExpr(elements);
    }
    if (match(TokenType.IDENTIFIER)) {
      return new VariableExpr(previous());
    }
    throw new RuntimeException(
        "Line " + current().line() + ": " + current().lexeme() + " is not a valid expression");
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
