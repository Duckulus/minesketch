package de.duckulus.minesketch.interpreter;

import java.util.HashMap;

public class Environment {

  private final Environment enclosing;
  private final HashMap<String, Object> data = new HashMap<>();

  public Environment() {
    this.enclosing = null;
  }

  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  public void define(String name, Object value) {
    data.put(name, value);
  }


  public void assign(String name, Object value) {
    if (data.containsKey(name)) {
      data.put(name, value);
    } else {
      if (enclosing == null) {
        throw new RuntimeException("Undefined Variable " + name);
      } else {
        enclosing.assign(name, value);
      }
    }

  }

  public boolean hasValue(String name) {
    return data.containsKey(name) || (enclosing != null && enclosing.hasValue(name));
  }

  public Object getValue(String name) {
    if (data.containsKey(name)) {
      return data.get(name);
    }
    if (enclosing != null) {
      return enclosing.getValue(name);
    }
    return null;
  }

  public void clear() {
    data.clear();
  }

}
