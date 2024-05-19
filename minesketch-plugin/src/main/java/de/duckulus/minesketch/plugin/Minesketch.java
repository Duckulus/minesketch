package de.duckulus.minesketch.plugin;

import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Minesketch extends JavaPlugin {

  private static Minesketch instance;

  public static Minesketch get() {
    return instance;
  }

  public static Component PREFIX = Component.text("Mineskech", NamedTextColor.AQUA)
      .append(Component.text(" ~ ", NamedTextColor.DARK_GRAY));

  public static Component prefixedMessage(String message) {
    return PREFIX.append(Component.text(message, NamedTextColor.GRAY));
  }

  public static Component ERROR = Component.text("Error", NamedTextColor.RED)
      .append(Component.text(" ~ ", NamedTextColor.DARK_GRAY));

  public static Component errorMessage(String message) {
    return ERROR.append(Component.text(message, NamedTextColor.GRAY));
  }

  private SketchManager sketchManager;
  private SketchRunner sketchRunner;

  @Override
  public void onEnable() {
    instance = this;

    sketchManager = new SketchManager();
    try {
      sketchManager.loadSkeches(this.getDataFolder());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    sketchRunner = new SketchRunner();

    SketchCommand sketchCommand = new SketchCommand(sketchManager, sketchRunner);
    getCommand("sketch").setExecutor(sketchCommand);
    getCommand("sketch").setExecutor(sketchCommand);
  }
}
