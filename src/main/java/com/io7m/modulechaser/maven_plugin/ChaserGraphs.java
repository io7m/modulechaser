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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Objects;
import java.util.Optional;

/**
 * Functions handling dependency graphs.
 */

public final class ChaserGraphs
{
  private ChaserGraphs()
  {

  }

  /**
   * Create a dependency graph for the given node.
   *
   * @param node The root node
   *
   * @return A dependency graph
   */

  public static DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graphOf(
    final DependencyNode node)
  {
    Objects.requireNonNull(node, "node");

    final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph =
      new DirectedAcyclicGraph<>(ChaserDependencyEdge.class);

    graphBuild(graph, node);
    return graph;
  }

  private static void graphBuild(
    final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph,
    final DependencyNode node)
  {
    final ChaserDependencyNode current = chaserNodeOfNode(node);
    graph.addVertex(current);

    for (final DependencyNode child_node : node.getChildren()) {
      final ChaserDependencyNode child_current = chaserNodeOfNode(child_node);
      graph.addVertex(child_current);
      graph.addEdge(
        current,
        child_current,
        ChaserDependencyEdge.of(current, child_current));
      graphBuild(graph, child_node);
    }
  }

  private static ChaserDependencyNode chaserNodeOfNode(
    final DependencyNode node)
  {
    final Artifact artifact = node.getArtifact();

    final String scope =
      artifact.getScope() != null ? artifact.getScope() : "compile";

    return ChaserDependencyNode.of(
      artifact.getGroupId(),
      artifact.getArtifactId(),
      artifact.getVersion(),
      Optional.ofNullable(artifact.getClassifier()),
      artifact.getType(),
      scope);
  }
}
