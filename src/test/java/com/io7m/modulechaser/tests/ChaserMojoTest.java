package com.io7m.modulechaser.tests;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;

import static io.takari.maven.testing.TestResources.assertFilesPresent;

public final class ChaserMojoTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ChaserMojoTest.class);

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
  public void testBug4()
    throws Exception
  {
    final File basedir = this.resources.getBasedir("bug-4");
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
