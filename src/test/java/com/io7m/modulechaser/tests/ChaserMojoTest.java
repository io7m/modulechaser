package com.io7m.modulechaser.tests;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static io.takari.maven.testing.TestResources.assertFilesPresent;

public final class ChaserMojoTest
{
  @Rule
  public final TestResources resources = new TestResources();

  @Rule
  public final TestMavenRuntime maven = new TestMavenRuntime();

  @Rule
  public final ExpectedException expected = ExpectedException.none();

  @Test
  public void testEmpty()
    throws Exception
  {
    final File basedir = this.resources.getBasedir("empty");
    this.maven.executeMojo(basedir, "chaseModules");
    assertFilesPresent(basedir, "target/modulechaser/modules.xhtml");
  }

  @Test
  public void testJartifact()
    throws Exception
  {
    final File basedir = this.resources.getBasedir("jartifact");
    this.maven.executeMojo(basedir, "chaseModules");
    assertFilesPresent(basedir, "target/modules.xhtml");
  }
}
