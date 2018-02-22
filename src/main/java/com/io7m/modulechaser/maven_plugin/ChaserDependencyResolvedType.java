package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

import java.nio.file.Path;

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserDependencyResolvedType
{
  @Value.Parameter
  ChaserDependencyNode source();

  @Value.Parameter
  Path sourceFile();

  @Value.Parameter
  String highestVersion();

  @Value.Parameter
  Path highestFile();
}
