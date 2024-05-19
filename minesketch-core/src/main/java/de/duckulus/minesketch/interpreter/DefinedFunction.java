package de.duckulus.minesketch.interpreter;

import de.duckulus.minesketch.ast.Stmt.FunDeclaration;
import de.duckulus.minesketch.token.Token;
import java.util.List;

public class DefinedFunction implements Function {

  private FunDeclaration declaration;
  private Environment environment;

  public DefinedFunction(FunDeclaration declaration, Environment environment) {
    this.declaration = declaration;
    this.environment = environment;
  }

  @Override
  public int arity() {
    return declaration.params().size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment env = new Environment(environment);

    List<Token> params = declaration.params();
    for (int i = 0; i < params.size(); i++) {
      Token param = params.get(i);
      env.define(param.lexeme(), arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body().statements(), env);
    } catch (Return returnValue) {
      return returnValue.value();
    }
    return null;
  }

  @Override
  public String toString() {
    return "<fn " + declaration.identifier().lexeme() + ">";
  }
}
