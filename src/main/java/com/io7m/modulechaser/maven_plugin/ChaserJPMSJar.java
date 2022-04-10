/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

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

/**
 * A JPMS jar file.
 */

public final class ChaserJPMSJar
{
  private final JarFile jar;

  private ChaserJPMSJar(final JarFile inJar)
  {
    this.jar = Objects.requireNonNull(inJar, "jar");
  }

  /**
   * Create a reference to a jar file.
   *
   * @param jar The underlying jar file
   *
   * @return A JPMS jar file
   */

  public static ChaserJPMSJar ofJar(final JarFile jar)
  {
    return new ChaserJPMSJar(jar);
  }

  /**
   * @return The JPMS module name, if any
   *
   * @throws IOException On errors
   */

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
