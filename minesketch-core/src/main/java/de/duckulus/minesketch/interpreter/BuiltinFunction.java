package de.duckulus.minesketch.interpreter;

import java.util.List;

public class BuiltinFunction implements Function {

  private int arity;
  private java.util.function.Function<List<Object>, Object> function;

  public BuiltinFunction(int arity, java.util.function.Function<List<Object>, Object> function) {
    this.arity = arity;
    this.function = function;
  }

  @Override
  public int arity() {
    return arity;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    return function.apply(arguments);
  }

  @Override
  public String toString() {
    return "<builtin fn>";
  }
}
