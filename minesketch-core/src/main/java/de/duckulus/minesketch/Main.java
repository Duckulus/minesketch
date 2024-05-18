package de.duckulus.minesketch;

public class Main {


  private static String text = """
         ~ asdfjklö
         fun tqbf() { ~asdf
          var x = 23.5;
         }
      """;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(text);

    System.out.println(scanner.scan());
  }

}
