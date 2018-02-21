package com.io7m.modulechaser.maven_plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public final class ChaserGraphs
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ChaserGraphs.class);

  private ChaserGraphs()
  {

  }

  public static DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graphOf(
    final DependencyNode node)
  {
    Objects.requireNonNull(node, "node");

    final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph =
      new DirectedAcyclicGraph<>(ChaserDependencyEdge::of);

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
      graph.addEdge(current, child_current);
      graphBuild(graph, child_node);
    }
  }

  private static ChaserDependencyNode chaserNodeOfNode(
    final DependencyNode node)
  {
    final Artifact artifact = node.getArtifact();
    return ChaserDependencyNode.of(
      artifact.getGroupId(),
      artifact.getArtifactId(),
      artifact.getVersion(),
      Optional.ofNullable(artifact.getClassifier()),
      artifact.getType());
  }
}
