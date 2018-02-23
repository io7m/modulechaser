package com.io7m.modulechaser.maven_plugin;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.legacy.metadata.DefaultMetadataResolutionRequest;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class ChaserResolvers
{
  private ChaserResolvers()
  {

  }

  public static ChaserDependencyResolved resolve(
    final Log log,
    final MavenProject project,
    final MavenSession session,
    final ArtifactHandlerManager artifactHandlerManager,
    final ArtifactResolver artifactResolver,
    final ArtifactMetadataSource metadataSource,
    final ChaserDependencyNode node)
    throws ArtifactResolverException, ArtifactMetadataRetrievalException
  {
    Objects.requireNonNull(log, "log");
    Objects.requireNonNull(project, "project");
    Objects.requireNonNull(session, "session");
    Objects.requireNonNull(artifactHandlerManager, "artifactHandlerManager");
    Objects.requireNonNull(artifactResolver, "artifactResolver");
    Objects.requireNonNull(metadataSource, "metadataSource");
    Objects.requireNonNull(node, "node");

    log.debug("resolve: " + node.toTerseString());

    final ArtifactHandler handler =
      artifactHandlerManager.getArtifactHandler(node.type());

    final DefaultArtifact current_artifact =
      new DefaultArtifact(
        node.group(),
        node.artifact(),
        node.version(),
        node.scope(),
        node.type(),
        node.classifier().orElse(null),
        handler);

    final DefaultMetadataResolutionRequest version_request =
      new DefaultMetadataResolutionRequest();
    version_request.setArtifact(current_artifact);
    version_request.setForceUpdate(true);
    version_request.setOffline(false);
    version_request.setLocalRepository(session.getLocalRepository());
    version_request.setRemoteRepositories(project.getRemoteArtifactRepositories());

    final List<ArtifactVersion> versions =
      metadataSource.retrieveAvailableVersions(version_request);

    Collections.sort(versions);

    if (versions.isEmpty()) {
      throw new ArtifactResolverException(
        "No version available for " + node.toTerseString(),
        new IOException("No available versions"));
    }

    final ArtifactVersion highest = versions.get(versions.size() - 1);

    final ProjectBuildingRequest current_request =
      new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());

    final ArtifactResult current_resolved =
      artifactResolver.resolveArtifact(current_request, current_artifact);

    final DefaultArtifact highest_artifact =
      new DefaultArtifact(
        node.group(),
        node.artifact(),
        highest.toString(),
        node.scope(),
        node.type(),
        node.classifier().orElse(null),
        handler);

    final ProjectBuildingRequest highest_request =
      new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());

    final ArtifactResult highest_resolved =
      artifactResolver.resolveArtifact(highest_request, highest_artifact);

    return ChaserDependencyResolved.of(
      node,
      current_resolved.getArtifact().getFile().toPath(),
      highest_resolved.getArtifact().getVersion(),
      highest_resolved.getArtifact().getFile().toPath());
  }
}
