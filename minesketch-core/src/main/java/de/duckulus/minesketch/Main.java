package de.duckulus.minesketch;

import de.duckulus.minesketch.ast.Stmt;
import de.duckulus.minesketch.token.Token;
import java.util.List;

public class Main {


  private static String text = """
         ~ asdfjklö
         fn tqbf() { ~asdf
          input var x = 23.5 + 1;
          print(x[3]);
         }
      """;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(text);

    List<Token> tokens = scanner.scan();

    System.out.println(tokens);

    Parser parser = new Parser(tokens);

    List<Stmt> stmts = parser.parse();

    System.out.println(stmts);
  }

}
