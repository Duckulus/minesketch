package de.duckulus.minesketch.plugin.event;

import de.duckulus.minesketch.plugin.SketchRunner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class BukkitEventDispatcher implements Listener {

  private SketchRunner sketchRunner;

  public BukkitEventDispatcher(SketchRunner sketchRunner) {
    this.sketchRunner = sketchRunner;
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    sketchRunner.handleInteractEvent(event);
  }

  @EventHandler
  public void onSneak(PlayerToggleSneakEvent event) {
    sketchRunner.handleSneakEvent(event);
  }

}
