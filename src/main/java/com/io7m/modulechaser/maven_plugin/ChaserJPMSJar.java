package com.io7m.modulechaser.maven_plugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ModuleNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

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
    final ZipEntry entry = this.jar.getEntry("module-info.class");
    if (entry != null) {
      try (InputStream stream = this.jar.getInputStream(entry)) {
        final ClassReader reader = new ClassReader(stream);
        final ModuleReader visitor = new ModuleReader();
        reader.accept(visitor, 0);
        return Optional.of(ChaserJPMSModuleName.of(visitor.module.name, false));
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

  private static final class ModuleReader extends ClassVisitor
  {
    private ModuleNode module;

    ModuleReader()
    {
      super(Opcodes.ASM6);
    }

    @Override
    public ModuleVisitor visitModule(
      final String name,
      final int access,
      final String version)
    {
      this.module = new ModuleNode(name, access, version);
      return this.module;
    }
  }
}
