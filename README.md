# ![minesketch](./assets/Minesketch.png)

minesketch is a dynamically typed scripting language which can be used to program stuff in
Minecraft.
It was created for the Hack Club programming language jam.

![demo](./assets/demo.gif)

Check out [the gameoflife example](./examples/gameoflife.mcsketch) for the code of this demo

## Getting Started

To get started with minesketch, check out the [guide](./docs/guide.md) or have a look at
the [examples](./examples)

## Building the plugin from source 

Sketches are ran using a spigot plugin. The plugin is compatible with Paper 1.20.6 and above. To
compile the plugin an installation of
the [Java 21 JDK](https://adoptium.net/de/temurin/releases/?os=any&package=jdk&version=21&arch=x64)
is required.

``./gradlew :minesketch-plugin:shadowJar`` \
will compile the plugin and put it into the `minesketch-plugin/build/libs` folder for you.

## Working with the plugin

Put your Sketches into `{Server Folder}/plugins/Minesketch/sketches` so the plugin can find them.

You interact with the plugin using the /sketch command. These are its subcommands:

```
/sketch list - Lists all available sketches
/sketch reload - Reloads sketches from the data folder
/sketch run <name> - Runs the sketch with the specified name
/sketch stop - Stops the currently active sketch
```
