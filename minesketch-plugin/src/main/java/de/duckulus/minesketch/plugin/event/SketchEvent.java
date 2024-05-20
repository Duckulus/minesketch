package de.duckulus.minesketch.plugin.event;

import de.duckulus.minesketch.interpreter.Dictionary;
import org.bukkit.event.Event;

public record SketchEvent(Class<? extends Event> eventType, Dictionary data) {

}
