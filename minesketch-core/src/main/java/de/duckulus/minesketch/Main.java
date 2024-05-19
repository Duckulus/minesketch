package de.duckulus.minesketch;

import de.duckulus.minesketch.interpreter.Interpreter;
import java.util.Collections;

public class Main {


  private static final String text = """
         ~ asdfjklö
         var b = "Hallo Welt";
         b = b + " " + 1.5;
         println(b);
         
         fn main() {
          greet("John Cena");
         }
         
         fn fib(limit) {
          var a = 1;
          var b = 1;
          var c = 2;
          
          while (a < limit) {
            println(a);
            a = b;
            b = c;
            c = a + b;
          }
         }
         
         fn tqbf() { ~asdf
          input var x = 23.5 + 1;
          print(x[3]);
         }
         
         fn fortnite() {
          return 23;
         }
         
         fn poop() {
          println("the quick brown fox jumps over the lazy dog");
         }
         
         fn greet(name) {
          println("Hello " + name);
         }
      """;

  public static void main(String[] args) {
    Interpreter interpreter = new Interpreter();
    interpreter.interpret(text);
    interpreter.invokeFunction("main", Collections.emptyList());
  }

}
