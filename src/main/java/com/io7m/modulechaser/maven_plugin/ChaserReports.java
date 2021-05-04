/*
 * Copyright © 2020 <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.modulechaser.maven_plugin;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.jar.JarFile;

/**
 * Functions to generate reports.
 */

public final class ChaserReports
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ChaserReports.class);

  private ChaserReports()
  {

  }

  /**
   * Generate a report using the resolver and graph.
   *
   * @param resolver The resolver
   * @param graph    The graph
   *
   * @return A report
   */

  public static ChaserReport reportOf(
    final ChaserDependencyResolverType resolver,
    final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph)
  {
    Objects.requireNonNull(resolver, "resolver");
    Objects.requireNonNull(graph, "graph");

    final TopologicalOrderIterator<ChaserDependencyNode, ChaserDependencyEdge> iter =
      new TopologicalOrderIterator<>(graph);
    final TreeMap<ChaserDependencyNode, ChaserReportDependency> reports =
      new TreeMap<>();

    while (iter.hasNext()) {
      final ChaserDependencyNode next = iter.next();
      LOG.debug("node: {}", next);

      try {
        final ChaserDependencyResolved resolved = resolver.resolve(next);
        final Path current_file = resolved.sourceFile();
        final Path highest_file = resolved.highestFile();
        final String current_version = resolved.source().version();
        final String highest_version = resolved.highestVersion();

        LOG.debug("current file:    {}", current_file);
        LOG.debug("current version: {}", current_version);
        LOG.debug("highest file:    {}", highest_file);
        LOG.debug("highest version: {}", highest_version);

        final ChaserModularizationStatusType status_current =
          determineStatus(current_file, current_version);
        final ChaserModularizationStatusType status_highest =
          determineStatus(highest_file, highest_version);

        reports.put(
          next,
          ChaserReportDependency.of(status_current, status_highest));
      } catch (final Exception e) {
        LOG.error("error resolving: ", e);
        reports.put(next, ChaserReportDependency.of(
          ChaserModularizationStatusUnavailable.of(Optional.of(e)),
          ChaserModularizationStatusUnavailable.of(Optional.of(e))));
      }
    }

    return ChaserReport.of(graph, reports);
  }

  private static ChaserModularizationStatusType determineStatus(
    final Path file,
    final String version)
    throws IOException
  {
    if (appearsToBeJar(file)) {
      final ChaserJPMSJar jar = ChaserJPMSJar.ofJar(new JarFile(file.toFile()));
      return jar.moduleName().map(name -> {
        if (name.isAutomatic()) {
          return ChaserModularizationStatusModularizedAutomaticModuleName.of(
            name.name(),
            version);
        }
        return ChaserModularizationStatusModularizedFully.of(
          name.name(),
          version);
      }).orElse(ChaserModularizationStatusNotModularized.of(version));
    }
    return ChaserModularizationStatusNotJar.of(version);
  }

  private static boolean appearsToBeJar(final Path path)
  {
    try {
      if (Files.isRegularFile(path)) {
        try (JarFile ignored = new JarFile(
          path.toFile(),
          false,
          JarFile.OPEN_READ)) {
          return true;
        }
      }
      return false;
    } catch (final IOException e) {
      LOG.debug("error opening file as jar: ", e);
      return false;
    }
  }
}
