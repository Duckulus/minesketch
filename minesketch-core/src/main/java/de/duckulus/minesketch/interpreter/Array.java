package de.duckulus.minesketch.interpreter;

import java.util.List;

public class Array {

  private Object[] data;

  public Array(int capacity) {
    data = new Object[capacity];
  }

  public Array(Object[] data) {
    this.data = data;
  }

  public static Object of(List<Object> elements) {
    return new Array(elements.toArray());
  }

  public Object get(int index) {
    if (index >= data.length) {
      throw new RuntimeException(
          "Index " + index + " out of bounds for array of length " + data.length);
    }
    return data[index];
  }

  public void set(int index, Object object) {
    data[index] = object;
  }

  public int length() {
    return data.length;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < data.length - 1; i++) {
      sb.append(data[i]).append(", ");
    }
    if (data.length != 0) {
      sb.append(data[data.length - 1]);
    }
    sb.append("]");
    return sb.toString();
  }
}
