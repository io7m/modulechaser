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

  default long dependenciesTotal()
  {
    return (long) this.reports().size();
  }

  default long dependenciesFullyModularized()
  {
    return this.reports()
      .values()
      .stream()
      .filter(report -> report instanceof ChaserReportDependencyOK)
      .map(report -> (ChaserReportDependencyOK) report)
      .filter(report -> report.highestModule().map(m -> Boolean.valueOf(!m.isAutomatic())).orElse(
        Boolean.FALSE).booleanValue())
      .count();
  }

  default long dependenciesNamed()
  {
    return this.reports()
      .values()
      .stream()
      .filter(report -> report instanceof ChaserReportDependencyOK)
      .map(report -> (ChaserReportDependencyOK) report)
      .filter(report -> report.highestModule().map(m -> Boolean.valueOf(m.isAutomatic())).orElse(
        Boolean.FALSE).booleanValue())
      .count();
  }

  default long dependenciesNotModularized()
  {
    return this.reports()
      .values()
      .stream()
      .filter(report -> report instanceof ChaserReportDependencyOK)
      .map(report -> (ChaserReportDependencyOK) report)
      .filter(report -> !report.highestModule().isPresent())
      .count();
  }
}
