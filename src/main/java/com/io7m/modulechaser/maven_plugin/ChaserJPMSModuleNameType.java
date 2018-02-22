package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserJPMSModuleNameType
{
  @Value.Parameter
  String name();

  @Value.Parameter
  boolean isAutomatic();
}
