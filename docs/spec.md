# `minesketch specifikation`

## Hello World

The broadcast function will broadcast a message on your servers chat.

Functions are declared using the `fn`-keyword.

The functions setup and tick are special functions that need to exist in every sketch.
The setup function gets executed exactly once when the sketch starts.
The tick function will be executed on every Minecraft tick, which happens 20 times in a second.
You can also write code at the top level, not inside any functions. It will be executed before the
setup function.

```
fn setup() {
    broadcast("Hello, World!);
}

fn tick() {

}
```

## Variables

Variables are declared using the `var`-keyword. There is no type checking, so variables are not
assignes a specific time. You can create an int and later assign it a string. There are also no
type annotaions in minesketch.

Every variable has one of the following types at runtime:

- null (not really a type, but a special value, which represents the lack of a value)
- string
- integer
- floating point number
- boolean
- array
- dictionary
- function

```
var myString = "sup";
var myInt = 42;
var myFloat = 3.141;
var myBool = false;
var myArray = ["hi", 16]; ~ Arrays may have values of different types in them
var myArray2 = array(26); ~ Creates an array of 26 null values
```

Input variables allow your sketch to accept metadata from outside the sketch.

The x, y and z input variables are set to the coordinates of the player who executed the sketch.

```
input var x;
input var y;
input var z;
```

## Comments

You can create line-comments using the `~` Symbol

```
~ this is a comment
```

## Control Flow

Control flow structures work like those from other C-style langugages

```
~ if-statement
if(condition) {
    ~ then block
}

~ if-else-statement
if(condition) {
    ~ then block
} else {
    ~ else block
}

~ while-loop
while(condition) {
    ~ is repeated as long as condition holds true
}

~ for-loop
for(var i = 0; i < 10; i = i + 1) {
    ~ i goes from 0 to 9
}
```

## builtin functions

println(string) -> prints string to the console

array(int) -> returns an array of specified size filled with null

length(string/array) -> returns the length of the string or array

sin(number) -> returns the sin value at a point

cos(number) -> returns the cosine value at a point

broadcast(string) -> broadcasts a message to the minecraft chat

stop() -> cancels the execution of your sketch

setBlock(int, int, int, string) -> Places a block in the minecraft world, first three arguments are
the x,y and z coordinates, fourth argument is the name of the block

handle(string, function) -> Register [event handler](#events).

## Events

Events allow you to define functions that get called once a player. Your event function has to take
in a single parameter. When your function gets called this parameter will be set to a dictionary
that contains some more information about the event. The contents of the dictionary depend on the
event type.

List of Events and the information about them:

- sneak
    - Is called when a player sneaks and unsneaks
    - data
        - player (string) -> The name of the player who sneaked
        - sneaking (boolean) -> True, if the player is now sneaking
- playerInteract
    - Is called when a player interacts with the world
    - data
        - player (string) -> The name of the player involved
        - x (int) -> x-coordinate of the clicked block
        - y (int) -> y-coordinate of the clicked block
        - z (int) -> z-coordinate of the clicked block

### Example

```
fn handleSneak(data) {
    broadcast(data["player"] + " sneaked");
}

fn setup() {
    ~ The event type is passed as the first parameter, the function handlin the event as the second parameter
    handle("sneak", handleSneak); 
}
```
