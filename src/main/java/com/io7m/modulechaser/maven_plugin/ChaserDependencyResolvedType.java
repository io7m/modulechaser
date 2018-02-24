package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

import java.nio.file.Path;

/**
 * A resolved dependency.
 */

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserDependencyResolvedType
{
  /**
   * @return The dependency graph node that has been resolved.
   */

  @Value.Parameter
  ChaserDependencyNode source();

  /**
   * The artifact for the current version of the module.
   *
   * @return The artifact file for the module
   */

  @Value.Parameter
  Path sourceFile();

  /**
   * @return The highest available version of the module
   */

  @Value.Parameter
  String highestVersion();

  /**
   * The artifact for the highest available version of the module.
   *
   * @return The artifact file for the module
   */

  @Value.Parameter
  Path highestFile();
}
