package com.io7m.modulechaser.maven_plugin;

public interface ChaserDependencyResolverType
{
  ChaserDependencyResolved resolve(
    ChaserDependencyNode node)
    throws Exception;
}
