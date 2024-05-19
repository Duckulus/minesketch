package de.duckulus.minesketch.plugin;

import de.duckulus.minesketch.interpreter.Interpreter;
import org.bukkit.plugin.java.JavaPlugin;

public class MinesketchPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    Interpreter interpreter = new Interpreter();

    getLogger().info(interpreter.interpret());
  }
}
