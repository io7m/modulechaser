package com.io7m.modulechaser.maven_plugin;

import java.nio.file.Path;

public interface ChaserDependencyResolverType
{
  Path resolve(
    ChaserDependencyNode node)
    throws Exception;
}
