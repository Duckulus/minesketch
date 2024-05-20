package de.duckulus.minesketch.plugin;

import de.duckulus.minesketch.interpreter.BuiltinFunction;
import de.duckulus.minesketch.interpreter.Dictionary;
import de.duckulus.minesketch.interpreter.Function;
import de.duckulus.minesketch.interpreter.Interpreter;
import de.duckulus.minesketch.plugin.event.SketchEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SketchRunner {

  private final Interpreter interpreter = new Interpreter();
  private Sketch runningSketch = null;
  private Thread thread;
  private World world;

  private Map<Class<? extends Event>, Function> registeredEvents = new HashMap<>();
  private Queue<SketchEvent> eventQueue = new ArrayBlockingQueue<>(100);

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
      Bukkit.getScheduler().runTask(Minesketch.get(), () -> {
        world.getBlockAt((int) args.get(0), (int) args.get(1), (int) args.get(2))
            .setType(Material.valueOf(
                (String) args.get(3)));
      });
      return null;
    }));
    interpreter.addBuiltinFunction("handle", new BuiltinFunction(2, args -> {
      Class<? extends Event> event = switch (args.getFirst().toString()) {
        case "playerInteract" -> PlayerInteractEvent.class;
        case "sneak" -> PlayerToggleSneakEvent.class;
        default ->
            throw new RuntimeException("Unrecognized Event type " + args.getFirst().toString());
      };
      if (!(args.get(1) instanceof Function function)) {
        throw new RuntimeException("Second argument to handle Function has to be a Function");
      }
      registeredEvents.put(event, function);
      return null;
    }));
  }

  public void runSketch(CommandSender audience, Sketch sketch) {
    if (runningSketch != null) {
      throw new IllegalStateException("Can't run a sketch while another one is already running");
    }

    eventQueue.clear();

    Location location = new Location(Bukkit.getWorlds().getFirst(), 0d, 0d, 0d);
    if (audience instanceof Player player) {
      location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
    }

    this.world = location.getWorld();

    interpreter.setInputValue("x", location.getBlockX());
    interpreter.setInputValue("y", location.getBlockY());
    interpreter.setInputValue("z", location.getBlockZ());

    this.runningSketch = sketch;
    thread = new Thread(() -> {
      try {
        interpreter.interpret(sketch.content());
        interpreter.invokeFunction("setup", Collections.emptyList());
      } catch (Exception e) {
        stopSketch();
        audience.sendMessage(Minesketch.errorMessage(e.toString()));
        return;
      }

      long lastTick = 0;
      while (!Thread.currentThread().isInterrupted()) {
        if (System.currentTimeMillis() - lastTick >= 50) {
          try {
            while (!eventQueue.isEmpty()) {
              SketchEvent event = eventQueue.poll();
              Function function = registeredEvents.get(event.eventType());
              if (function != null) {
                function.call(interpreter, Collections.singletonList(event.data()));
              }
            }

            interpreter.invokeFunction("tick", Collections.emptyList());
          } catch (Exception e) {
            stopSketch();
            audience.sendMessage(Minesketch.errorMessage(e.toString()));
          }
          lastTick = System.currentTimeMillis();
        }
      }
    });
    thread.start();
  }

  public void stopSketch() {
    Bukkit.getScheduler().runTaskAsynchronously(Minesketch.get(), () -> {
      if (thread != null) {
        thread.interrupt();
      }
      this.runningSketch = null;
    });
  }

  public boolean isBusy() {
    return runningSketch != null;
  }

  public void handleInteractEvent(PlayerInteractEvent event) {
    if (!isBusy() || !registeredEvents.containsKey(event.getClass())) {
      return;
    }
    if (event.getHand()!= EquipmentSlot.HAND || event.getClickedBlock() == null) {
      return;
    }
    Dictionary data = new Dictionary();
    data.set("player", event.getPlayer().getName());
    data.set("x", event.getClickedBlock().getX());
    data.set("y", event.getClickedBlock().getY());
    data.set("z", event.getClickedBlock().getZ());
    eventQueue.offer(new SketchEvent(event.getClass(), data));
  }

  public void handleSneakEvent(PlayerToggleSneakEvent event) {
    if (!isBusy() || !registeredEvents.containsKey(event.getClass())) {
      return;
    }
    if(!event.getPlayer().isOnGround()) {
      return;
    }
    Dictionary data = new Dictionary();
    data.set("player", event.getPlayer().getName());
    data.set("sneaking", event.isSneaking());
    eventQueue.offer(new SketchEvent(event.getClass(), data));
  }

}
