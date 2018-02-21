package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

import java.util.Optional;

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserDependencyNodeType
{
  @Value.Parameter
  String group();

  @Value.Parameter
  String artifact();

  @Value.Parameter
  String version();

  @Value.Parameter
  Optional<String> classifier();

  @Value.Parameter
  String type();
}
