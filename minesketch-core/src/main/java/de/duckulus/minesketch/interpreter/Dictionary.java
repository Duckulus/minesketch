package de.duckulus.minesketch.interpreter;

import java.util.HashMap;

public class Dictionary {

  private final HashMap<String, Object> data = new HashMap<>();

  public Object get(String key) {
    return data.get(key);
  }

  public void set(String key, Object value) {
    data.put(key, value);
  }

}
