package de.duckulus.minesketch.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SketchManager {

  private final List<Sketch> sketches = new ArrayList<>();

  public void loadSkeches(File dataDir) throws IOException {
    sketches.clear();

    File folder = new File(dataDir, "sketches");
    if (!folder.exists()) {
      if (!folder.mkdirs()) {
        throw new RuntimeException("Could not create sketch folder");
      }
    }

    File[] files = folder.listFiles();
    if (files == null) {
      throw new RuntimeException("Error listing files in Sketch directory");
    }
    for (File file : files) {
      if (file.getName().endsWith(".mcsketch")) {
        sketches.add(new Sketch(file.getName().substring(0, file.getName().length() - 9),
            Files.readString(file.toPath())));
      }
    }
  }

  public Optional<Sketch> getSketch(String name) {
    return sketches.stream().filter(sketch -> sketch.name().equals(name)).findFirst();
  }

  public List<Sketch> getSketches() {
    return List.copyOf(sketches);
  }

}
