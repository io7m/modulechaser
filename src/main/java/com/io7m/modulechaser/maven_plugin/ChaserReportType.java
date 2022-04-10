/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import org.immutables.value.Value;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.SortedMap;

/**
 * A full dependency report.
 */

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserReportType
{
  /**
   * @return The dependency graph
   */

  @Value.Parameter
  DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph();

  /**
   * @return The set of reports, per each dependency
   */

  @Value.Parameter
  SortedMap<ChaserDependencyNode, ChaserReportDependency> reports();

  /**
   * @return The number of dependencies
   */

  default long dependenciesTotal()
  {
    return this.reports().size();
  }

  /**
   * @return The number of dependencies that are JPMS modules
   */

  default long dependenciesFullyModularized()
  {
    return this.reports()
      .values()
      .stream()
      .filter(report -> report.statusHighest() instanceof ChaserModularizationStatusModularizedFully)
      .count();
  }

  /**
   * @return The number of dependencies that use automatically named modules
   */

  default long dependenciesNamed()
  {
    return this.reports()
      .values()
      .stream()
      .filter(report -> report.statusHighest() instanceof ChaserModularizationStatusModularizedAutomaticModuleName)
      .count();
  }

  /**
   * @return The number of dependencies that are not modularized
   */

  default long dependenciesNotModularized()
  {
    return this.reports()
      .values()
      .stream()
      .filter(report -> report.statusHighest() instanceof ChaserModularizationStatusNotModularized)
      .count();
  }
}
