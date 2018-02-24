package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

import java.util.Optional;

public interface ChaserModularizationStatusType
{
  enum Kind {
    MODULARIZED_FULLY,
    MODULARIZED_AUTOMATIC_MODULE_NAME,
    NOT_MODULARIZED,
    NOT_JAR,
    UNAVAILABLE
  }

  Kind kind();

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusModularizedFullyType
    extends ChaserModularizationStatusType
  {
    default Kind kind()
    {
      return Kind.MODULARIZED_FULLY;
    }

    @Value.Parameter
    String moduleName();

    @Value.Parameter
    String version();
  }

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusModularizedAutomaticModuleNameType
    extends ChaserModularizationStatusType
  {
    default Kind kind()
    {
      return Kind.MODULARIZED_AUTOMATIC_MODULE_NAME;
    }

    @Value.Parameter
    String moduleName();

    @Value.Parameter
    String version();
  }

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusNotModularizedType
    extends ChaserModularizationStatusType
  {
    default Kind kind()
    {
      return Kind.NOT_MODULARIZED;
    }

    @Value.Parameter
    String version();
  }

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusNotJarType
    extends ChaserModularizationStatusType
  {
    default Kind kind()
    {
      return Kind.NOT_JAR;
    }

    @Value.Parameter
    String version();
  }

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusUnavailableType
    extends ChaserModularizationStatusType
  {
    default Kind kind()
    {
      return Kind.UNAVAILABLE;
    }

    @Value.Parameter
    Optional<Exception> error();
  }
}
