package de.duckulus.minesketch.plugin;

import de.duckulus.minesketch.interpreter.BuiltinFunction;
import de.duckulus.minesketch.interpreter.Interpreter;
import java.util.Collections;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class SketchRunner {

  private final Interpreter interpreter = new Interpreter();
  private Sketch runningSketch = null;
  private BukkitTask task;
  private World world;

  public SketchRunner() {
    interpreter.addBuiltinFunction("broadcast", new BuiltinFunction(1, args -> {
      Bukkit.broadcast(
          Component.text(args.getFirst() == null ? "null" : args.getFirst().toString()));
      return null;
    }));
    interpreter.addBuiltinFunction("stop", new BuiltinFunction(0, args -> {
      this.stopSketch();
      return null;
    }));
    interpreter.addBuiltinFunction("setBlock", new BuiltinFunction(4, args -> {
      world.getBlockAt((int) args.get(0), (int) args.get(1), (int) args.get(2))
          .setType(Material.valueOf(
              (String) args.get(3)));
      return null;
    }));
  }

  public void runSketch(CommandSender audience, Sketch sketch) {
    if (runningSketch != null) {
      throw new IllegalStateException("Can't run a sketch while another one is already running");
    }

    Location location = new Location(Bukkit.getWorlds().getFirst(), 0d, 0d, 0d);
    if (audience instanceof Player player) {
      location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
    }

    this.world = location.getWorld();

    interpreter.setInputValue("x", location.getBlockX());
    interpreter.setInputValue("y", location.getBlockY());
    interpreter.setInputValue("z", location.getBlockZ());

    this.runningSketch = sketch;
    try {
      interpreter.interpret(sketch.content());
      interpreter.invokeFunction("setup", Collections.emptyList());
    } catch (Exception e) {
      stopSketch();
      audience.sendMessage(Minesketch.errorMessage(e.toString()));
      return;
    }

    this.task = Bukkit.getScheduler().runTaskTimer(Minesketch.get(), () -> {
      try {
        interpreter.invokeFunction("tick", Collections.emptyList());
      } catch (Exception e) {
        stopSketch();
        audience.sendMessage(Minesketch.errorMessage(e.toString()));
      }
    }, 0, 1);

  }

  public void stopSketch() {
    this.runningSketch = null;
    if (task != null) {
      task.cancel();
    }
  }

  public boolean isBusy() {
    return runningSketch != null;
  }

}
