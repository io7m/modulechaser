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
import java.util.jar.JarFile;
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

    return Optional.ofNullable(
      (String) this.jar.getManifest()
        .getMainAttributes()
        .get("Automatic-Module-Name"))
      .map(raw -> ChaserJPMSModuleName.of(raw, true));
  }
}
