package de.duckulus.minesketch.ast;

import de.duckulus.minesketch.token.Token;
import java.util.List;

public sealed interface Stmt {

  record VarDeclaration(Token identifier, Expr value, boolean isInput) implements Stmt {

  }

  record FunDeclaration(Token identifier, List<Token> params, BlockStmt body) implements Stmt {

  }

  record BlockStmt(List<Stmt> statements) implements Stmt {

  }

  record ExprStmt(Expr expr) implements Stmt {

  }

  record IfStmt(Expr condition, BlockStmt body) implements Stmt {

  }

  record WhileStmt(Expr condition, BlockStmt body) implements Stmt {

  }

  record ReturnStmt(Expr value) implements Stmt {

  }

}
