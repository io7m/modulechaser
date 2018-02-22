package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.SortedMap;

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserReportType
{
  @Value.Parameter
  DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph();

  @Value.Parameter
  SortedMap<ChaserDependencyNode, ChaserReportDependencyType> reports();
}
