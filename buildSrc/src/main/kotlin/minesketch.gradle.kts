plugins {
  id("java")
}

group = "de.duckulus"
version = "1.0-SNAPSHOT"

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
  mavenCentral()
}
