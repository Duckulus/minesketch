package de.duckulus.minesketch.plugin.event;

import de.duckulus.minesketch.plugin.SketchRunner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class BukkitEventDispatcher implements Listener {

  private final SketchRunner sketchRunner;

  public BukkitEventDispatcher(SketchRunner sketchRunner) {
    this.sketchRunner = sketchRunner;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInteract(PlayerInteractEvent event) {
    sketchRunner.handleInteractEvent(event);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onSneak(PlayerToggleSneakEvent event) {
    sketchRunner.handleSneakEvent(event);
  }

}
