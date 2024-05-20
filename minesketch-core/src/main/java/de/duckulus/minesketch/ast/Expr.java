package de.duckulus.minesketch.ast;

import de.duckulus.minesketch.token.Token;
import java.util.List;

public sealed interface Expr {

  record AssignExpr(Token name, Expr value) implements Expr {

  }

  record IndexAssignExpr(IndexExpr name, Expr value) implements Expr {

  }

  record ArrayExpr(List<Expr> elements) implements Expr {

  }

  record BinaryExpr(Expr left, Token operator, Expr right) implements Expr {

  }

  record UnaryExpr(Expr operand, Token operator) implements Expr {

  }

  record LogicalExpr(Expr left, Token operator, Expr right) implements Expr {

  }

  record CallExpr(Expr callee, List<Expr> args) implements Expr {

  }

  record IndexExpr(Expr indexed, Expr index) implements Expr {

  }

  record VariableExpr(Token variable) implements Expr {

  }

  record LiteralExpr(Object value) implements Expr {

  }

}
