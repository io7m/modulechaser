package com.io7m.modulechaser.maven_plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
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
import org.apache.maven.shared.dependencies.resolve.DependencyResolver;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Mojo(
  name = "chaseModules",
  requiresDependencyCollection = ResolutionScope.TEST,
  threadSafe = true)
public final class ChaserMojo extends AbstractMojo
{
  @Parameter(
    required = true,
    defaultValue = "${project.build.directory}/modulechaser/modules.xhtml",
    name = "outputFile",
    property = "modulechaser.outputFile")
  private String outputFile;

  @Parameter(
    required = true,
    defaultValue = "XHTML",
    name = "outputType",
    property = "modulechaser.outputType")
  private String outputType;

  @Parameter(
    required = true,
    name = "scopes",
    property = "modulechaser.scopes")
  private String[] scopes;

  @Parameter(
    defaultValue = "${project}",
    readonly = true,
    required = true)
  private MavenProject project;

  @Parameter(
    defaultValue = "${session}",
    readonly = true,
    required = true)
  private MavenSession session;

  @Parameter(
    defaultValue = "${reactorProjects}",
    readonly = true,
    required = true)
  private List<MavenProject> reactorProjects;

  @Component(hint = "default")
  private DependencyGraphBuilder dependencyGraphBuilder;

  @Component
  private DependencyResolver dependencyResolver;

  @Component
  private ArtifactResolver artifactResolver;

  @Component
  private ArtifactHandlerManager artifactHandlerManager;

  @Component(hint = "maven")
  private ArtifactMetadataSource metadataSource;

  public ChaserMojo()
  {

  }

  private interface SerializerType
  {
    void serialize(
      Path output,
      ChaserReport report)
      throws Exception;
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
    Objects.requireNonNull(
      this.metadataSource, "this.metadataSource");

    if (this.scopes.length == 0) {
      this.scopes = new String[] { "compile", "provided", "runtime" };
    }

    final SerializerType serialize = this.getSerializer();
    final Log log = this.getLog();

    final ProjectBuildingRequest request =
      new DefaultProjectBuildingRequest(
        this.session.getProjectBuildingRequest());

    request.setProject(this.project);

    try {
      final DependencyNode node =
        this.dependencyGraphBuilder.buildDependencyGraph(
          request, this::filterArtifact, this.reactorProjects);
      final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph =
        ChaserGraphs.graphOf(node);

      final ChaserReport report =
        ChaserReports.reportOf(
          current_node -> ChaserResolvers.resolve(
            log,
            this.project,
            this.session,
            this.artifactHandlerManager,
            this.artifactResolver,
            this.metadataSource,
            current_node), graph);

      final Path path = Paths.get(this.outputFile);
      Files.createDirectories(path.getParent());
      serialize.serialize(path, report);
    } catch (final Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private boolean filterArtifact(final Artifact artifact)
  {
    for (final String scope : this.scopes) {
      final String a_scope = artifact.getScope().toUpperCase();
      final String s_scope = scope.toUpperCase();
      if (Objects.equals(s_scope, a_scope)) {
        return true;
      }
    }
    return false;
  }

  private SerializerType getSerializer()
    throws MojoFailureException
  {
    final SerializerType serialize;
    switch (this.outputType.toUpperCase()) {
      case "XHTML": {
        serialize = (path, report) -> {
          try (OutputStream output = Files.newOutputStream(path)) {
            ChaserReportXHTML.writeXHTMLPage(report, output);
          }
        };
        break;
      }
      case "PLAIN": {
        serialize = (path, report) -> {
          try (OutputStream output = Files.newOutputStream(path)) {
            ChaserReportPlain.writePlain(report, output);
          }
        };
        break;
      }
      default: {
        throw new MojoFailureException(
          new StringBuilder(64)
            .append("Unsupported output type")
            .append(System.lineSeparator())
            .append("  Use: XHTML or PLAIN")
            .append(System.lineSeparator())
            .toString());
      }
    }
    return serialize;
  }
}
