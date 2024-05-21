# `minesketch`

minesketch is a dynamically typed scripting language which can be used to program stuff in
Minecraft.
It was created for the Hack Club programming language jam.

## Demo

This demonstration shows a part of what's possible using the minesketch language. Its source code
can
be found in [./examples/gameoflife.mcsketch](./examples/gameoflife.mcsketch)

https://github.com/Duckulus/minesketch/assets/76813487/593eecd8-3f29-4e5d-895b-064b343209b1

## Your first sketch

A sketch is a program which can run inside minecraft. Sketches are stored in files ending
in `.mcsketch`.
They generally look like this:

```
var i = 0;

~ setup function is executed once when the sketch gets loaded
fn setup() {
	broadcast("hello world");
}

~ tick function is executed 20 times per second while the sketch is active
fn tick() {
    ~ this sketch just counts up indefinitely in chat
    broadcast(i);
    i = i + 1;
}
```

https://github.com/Duckulus/minesketch/assets/76813487/70f00824-952c-49a5-ad51-882989be7a79

Have a look at [the specification](./docs/spec.md) for a detailed introduction the minesketch
language

## Running the minesketch plugin

Sketches are ran using a spigot plugin. The plugin is compatible with Paper 1.20.6 and above. To
compile the plugin an installation of
the [Java 21 JDK](https://adoptium.net/de/temurin/releases/?os=any&package=jdk&version=21&arch=x64)
is required.

``./gradlew :minesketch-plugin:shadowJar`` \
will compile the plugin and put it into the `minesketch-plugin/build/libs` folder for you.

Put your Sketches into `{Server Folder}/plugins/Minesketch/sketches` so the plugin can find them.

You interact with the plugin using the /sketch command. There are its subcommands:

```
/sketch list - Lists all available sketches
/sketch reload - Reloads sketches from the data folder
/sketch run <name> - Runs the sketch with the specified name
/sketch stop - Stops the currently active sketch
```
