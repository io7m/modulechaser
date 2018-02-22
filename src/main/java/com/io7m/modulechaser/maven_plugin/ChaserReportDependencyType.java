package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

import java.util.Optional;

public interface ChaserReportDependencyType
{
  enum Kind {
    OK,
    ERROR
  }

  ChaserDependencyNode node();

  Kind kind();

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserReportDependencyOKType extends ChaserReportDependencyType
  {
    @Override
    default Kind kind()
    {
      return Kind.OK;
    }

    @Value.Parameter
    @Override
    ChaserDependencyNode node();

    @Value.Parameter
    Optional<ChaserJPMSModuleName> currentModule();

    @Value.Parameter
    String highestVersion();

    @Value.Parameter
    Optional<ChaserJPMSModuleName> highestModule();
  }

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserReportDependencyErrorType extends ChaserReportDependencyType
  {
    @Override
    default Kind kind()
    {
      return Kind.ERROR;
    }

    @Value.Parameter
    @Override
    ChaserDependencyNode node();

    @Value.Parameter
    Exception exception();
  }
}
