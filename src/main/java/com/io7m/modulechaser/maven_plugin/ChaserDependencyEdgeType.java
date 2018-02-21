package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserDependencyEdgeType
{
  @Value.Parameter
  ChaserDependencyNode source();

  @Value.Parameter
  ChaserDependencyNode target();
}
