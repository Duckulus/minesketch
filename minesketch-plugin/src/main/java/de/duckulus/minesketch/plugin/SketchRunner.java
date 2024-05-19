package de.duckulus.minesketch.plugin;

import de.duckulus.minesketch.interpreter.BuiltinFunction;
import de.duckulus.minesketch.interpreter.Interpreter;
import java.util.Collections;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SketchRunner {

  private final Interpreter interpreter = new Interpreter();
  private Sketch runningSketch = null;
  private BukkitTask task;

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
  }

  public void runSketch(Audience audience, Sketch sketch) {
    if (runningSketch != null) {
      throw new IllegalStateException("Can't run a sketch while another one is already running");
    }

    this.runningSketch = sketch;
    try {
      interpreter.interpret(sketch.content());
      interpreter.invokeFunction("setup", Collections.emptyList());
    } catch (Exception e) {
      audience.sendMessage(Minesketch.errorMessage(e.getMessage()));
      stopSketch();
      return;
    }

    this.task = Bukkit.getScheduler().runTaskTimer(Minesketch.get(), () -> {
      try {
        interpreter.invokeFunction("tick", Collections.emptyList());
      } catch (Exception e) {
        audience.sendMessage(Minesketch.errorMessage(e.getMessage()));
        stopSketch();
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
