package de.duckulus.minesketch.interpreter;

import java.util.List;

public interface Function {

  int arity();

  Object call(Interpreter interpreter, List<Object> arguments);

}
