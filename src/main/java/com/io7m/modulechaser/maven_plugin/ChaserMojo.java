package com.io7m.modulechaser.maven_plugin;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.apache.maven.shared.dependencies.resolve.DependencyResolver;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Mojo(
  name = "chaseModules",
  requiresDependencyCollection = ResolutionScope.TEST,
  threadSafe = true)
public final class ChaserMojo extends AbstractMojo
{
  /**
   * The Maven project.
   */

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;

  /**
   * Contains the full list of projects in the reactor.
   */

  @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
  private List<MavenProject> reactorProjects;

  /**
   * The dependency tree builder to use.
   */

  @Component(hint = "default")
  private DependencyGraphBuilder dependencyGraphBuilder;

  @Component
  private DependencyResolver dependencyResolver;

  @Component
  private ArtifactResolver artifactResolver;

  @Component
  private ArtifactHandlerManager artifactHandlerManager;

  public ChaserMojo()
  {

  }

  @Override
  public void execute()
    throws MojoExecutionException, MojoFailureException
  {
    Objects.requireNonNull(
      this.artifactResolver, "this.artifactResolver");
    Objects.requireNonNull(
      this.artifactHandlerManager, "this.artifactHandlerManager");
    Objects.requireNonNull(
      this.session, "this.session");
    Objects.requireNonNull(
      this.project, "this.project");
    Objects.requireNonNull(
      this.dependencyGraphBuilder, "this.dependencyGraphBuilder");

    final Log log = this.getLog();

    final ProjectBuildingRequest request =
      new DefaultProjectBuildingRequest(
        this.session.getProjectBuildingRequest());

    request.setProject(this.project);

    final ArtifactFilter filter = artifact -> true;

    try {
      final DependencyNode node =
        this.dependencyGraphBuilder.buildDependencyGraph(
          request, filter, this.reactorProjects);
      final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph =
        ChaserGraphs.graphOf(node);
      final ChaserReport report =
        ChaserReports.reportOf(this::resolve, graph);

    } catch (final DependencyGraphBuilderException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private Path resolve(final ChaserDependencyNode node)
    throws ArtifactResolverException
  {
    final ArtifactHandler handler =
      this.artifactHandlerManager.getArtifactHandler(node.type());

    final DefaultArtifact target_artifact =
      new DefaultArtifact(
        node.group(),
        node.artifact(),
        node.version(),
        "compile",
        node.type(),
        node.classifier().orElse(null),
        handler);

    final ProjectBuildingRequest request =
      new DefaultProjectBuildingRequest(this.session.getProjectBuildingRequest() );

    final ArtifactResult resolved =
      this.artifactResolver.resolveArtifact(request, target_artifact);

    return resolved.getArtifact().getFile().toPath();
  }
}
