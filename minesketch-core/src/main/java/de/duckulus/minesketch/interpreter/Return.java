package de.duckulus.minesketch.interpreter;

public class Return extends RuntimeException {

  private final Object value;

  public Return(Object value) {
    this.value = value;
  }

  public Object value() {
    return value;
  }
}
