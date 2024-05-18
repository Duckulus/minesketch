package de.duckulus.minesketch.ast;

import de.duckulus.minesketch.token.Token;
import java.util.List;

public sealed interface Stmt {

  record VarDeclaration(Token identifier, Expr value, boolean isInput) implements Stmt {

  }

  record FunDeclaration(Token identifier, List<Token> params, BlockStatement body) implements Stmt {

  }

  record BlockStatement(List<Stmt> statements) {

  }

  record ExprStmt(Expr expr) implements Stmt {

  }

}
