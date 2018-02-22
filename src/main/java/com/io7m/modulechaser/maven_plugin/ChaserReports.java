package com.io7m.modulechaser.maven_plugin;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.TreeMap;
import java.util.jar.JarFile;

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
    final TreeMap<ChaserDependencyNode, ChaserReportDependencyType> reports =
      new TreeMap<>();

    while (iter.hasNext()) {
      final ChaserDependencyNode next = iter.next();
      LOG.debug("node: {}", next);

      try {
        final ChaserDependencyResolved resolved = resolver.resolve(next);
        LOG.debug("current file:    {}", resolved.sourceFile());
        LOG.debug("current version: {}", resolved.source().version());
        LOG.debug("highest file:    {}", resolved.highestFile());
        LOG.debug("highest version: {}", resolved.highestVersion());

        final ChaserJPMSJar current_jar =
          ChaserJPMSJar.ofJar(new JarFile(resolved.sourceFile().toFile()));
        final ChaserJPMSJar highest_jar =
          ChaserJPMSJar.ofJar(new JarFile(resolved.highestFile().toFile()));

        final ChaserReportDependencyOK report =
          ChaserReportDependencyOK.of(
            next,
            current_jar.moduleName(),
            resolved.highestVersion(),
            highest_jar.moduleName());

        reports.put(next, report);
      } catch (final Exception e) {
        LOG.error("error resolving: ", e);
        reports.put(next, ChaserReportDependencyError.of(next, e));
      }
    }

    return ChaserReport.of(graph, reports);
  }
}
