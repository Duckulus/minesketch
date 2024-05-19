package de.duckulus.minesketch;

import de.duckulus.minesketch.interpreter.Interpreter;

public class Main {


  private static final String text = """
      fn main() {
        var arr = array(10);
        arr[0]=12;
        arr[1]=4;
        arr[2]=8;
        arr[3]=9;
        arr[4]=1;
        arr[5]=3;
        arr[6]=7;
        arr[7]=1;
        arr[8]=10;
        arr[9]=787;
      
        bubbleSort(arr);
        println(arr);
      }
      
      fn bubbleSort(nums) {
       var i = 0;
       while (i < length(nums)) {
        var j = 0;
        while (j < length(nums) - 1) {
         if(nums[j] > nums[j+1]) {
          var temp = nums[j];
          nums[j] = nums[j+1];
          nums[j+1] = temp;
         }
         j = j + 1;
        }
        i = i + 1;
       }
      }
      
      main();
      """;

  public static void main(String[] args) {
    Interpreter interpreter = new Interpreter();
    interpreter.interpret(text);
  }

}
