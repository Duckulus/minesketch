package de.duckulus.minesketch.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SketchCommand implements CommandExecutor, TabCompleter {

  private final SketchManager sketchManager;
  private final SketchRunner sketchRunner;

  public SketchCommand(SketchManager sketchManager, SketchRunner sketchRunner) {
    this.sketchManager = sketchManager;
    this.sketchRunner = sketchRunner;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
      @NotNull String s, @NotNull String[] args) {
    if (args.length == 0) {
      sendHelp(commandSender);
      return true;
    }
    switch (args[0]) {
      case "list":
        commandSender.sendMessage(Minesketch.prefixedMessage("Available Sketches:"));
        for (Sketch sketch : sketchManager.getSketches()) {
          commandSender.sendMessage(Component.text("- ", NamedTextColor.GRAY)
              .append(Component.text(sketch.name(), NamedTextColor.BLUE)));
        }
        break;
      case "run":
        if (args.length < 2) {
          sendHelp(commandSender);
          break;
        }
        if (sketchRunner.isBusy()) {
          commandSender.sendMessage(Minesketch.errorMessage(
              "A sketch is already running. Cancel it first before running another one"));
          break;
        }
        Optional<Sketch> sketchOptional = sketchManager.getSketch(args[1]);
        if (sketchOptional.isPresent()) {
          sketchRunner.runSketch(commandSender, sketchOptional.get());
        } else {
          commandSender.sendMessage(Minesketch.errorMessage("Sketch not found"));
        }
        break;
      case "reload":
        Bukkit.getScheduler().runTaskAsynchronously(Minesketch.get(), () -> {
          try {
            sketchManager.loadSkeches(Minesketch.get().getDataFolder());
            commandSender.sendMessage(Minesketch.prefixedMessage("Successfully reloaded sketches"));
          } catch (Exception e) {
            commandSender.sendMessage(
                Minesketch.errorMessage("Error while reloading sketches: " + e.getMessage()));
          }
        });
        break;
      case "stop":
        if (!sketchRunner.isBusy()) {
          commandSender.sendMessage(
              Minesketch.errorMessage("There is no sketch currently running"));
          break;
        }
        sketchRunner.stopSketch();
        commandSender.sendMessage(Minesketch.prefixedMessage("Stopped Sketch"));
        break;
      default:
        sendHelp(commandSender);
    }

    return true;
  }

  private void sendHelp(CommandSender commandSender) {
    commandSender.sendMessage(Minesketch.prefixedMessage("Available Subcommands:"));
    commandSender.sendMessage(
        Component.text("/sketch list", NamedTextColor.GRAY)
            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
            .append(Component.text("Lists all available sketches", NamedTextColor.BLUE)));
    commandSender.sendMessage(
        Component.text("/sketch run <name>", NamedTextColor.GRAY)
            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
            .append(Component.text("Runs a sketch", NamedTextColor.BLUE)));
    commandSender.sendMessage(
        Component.text("/sketch reload", NamedTextColor.GRAY)
            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
            .append(
                Component.text("Reloads sketches from the data folder", NamedTextColor.BLUE)));
    commandSender.sendMessage(
        Component.text("/sketch stop", NamedTextColor.GRAY)
            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
            .append(Component.text("Stops the actively running sketch", NamedTextColor.BLUE)));
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender,
      @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (args.length == 1) {
      List<String> subcommands = List.of("list", "run", "reload", "stop");
      List<String> completions = new ArrayList<>();
      StringUtil.copyPartialMatches(args[0], subcommands, completions);
      Collections.sort(completions);
      return completions;
    } else if (args.length == 2) {
      if (args[0].equals("run")) {
        List<String> sketches = sketchManager.getSketches().stream().map(Sketch::name).toList();
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[1], sketches, completions);
        Collections.sort(completions);
        return completions;
      }
    }
    return Collections.emptyList();
  }
}
