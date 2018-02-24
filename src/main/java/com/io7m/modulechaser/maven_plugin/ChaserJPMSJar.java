package com.io7m.modulechaser.maven_plugin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class ChaserJPMSJar
{
  private final JarFile jar;

  private ChaserJPMSJar(final JarFile jar)
  {
    this.jar = Objects.requireNonNull(jar, "jar");
  }

  public static ChaserJPMSJar ofJar(final JarFile jar)
  {
    return new ChaserJPMSJar(jar);
  }

  public Optional<ChaserJPMSModuleName> moduleName()
    throws IOException
  {
    final Optional<JarEntry> entry_opt =
      this.jar.stream()
        .filter(e -> e.getName().endsWith("module-info.class"))
        .max(Comparator.comparing(JarEntry::getName));

    if (entry_opt.isPresent()) {
      final JarEntry entry = entry_opt.get();
      try (InputStream stream = this.jar.getInputStream(entry)) {
        final ModuleDescriptor descriptor = ModuleDescriptor.read(stream);
        return Optional.of(ChaserJPMSModuleName.of(descriptor.name(), false));
      }
    }

    final Manifest manifest = this.jar.getManifest();
    if (manifest == null) {
      /*
       * There actually are completely broken jars in Maven Central that
       * do not contain manifests at all: javax.inject:javax.inject:1
       */
      return Optional.empty();
    }

    final Attributes attributes = manifest.getMainAttributes();
    final String name = attributes.getValue("Automatic-Module-Name");
    if (name != null) {
      return Optional.of(ChaserJPMSModuleName.of(name, true));
    }
    return Optional.empty();
  }
}
