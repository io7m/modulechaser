package com.io7m.modulechaser.maven_plugin;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Objects;

public final class ChaserReports
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ChaserReports.class);

  private ChaserReports()
  {

  }

  public static ChaserReport reportOf(
    final ChaserDependencyResolverType resolver,
    final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph)
  {
    Objects.requireNonNull(resolver, "resolver");
    Objects.requireNonNull(graph, "graph");

    final TopologicalOrderIterator<ChaserDependencyNode, ChaserDependencyEdge> iter =
      new TopologicalOrderIterator<>(graph);

    while (iter.hasNext()) {
      final ChaserDependencyNode next = iter.next();
      LOG.debug("node: {}", next);

      try {
        final Path file = resolver.resolve(next);
      } catch (final Exception e) {
        LOG.error("error resolving: ", e);
      }
    }

    return ChaserReport.of(graph);
  }
}
