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

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

/**
 * Functions to generate XHTML pages from reports.
 */

public final class ChaserReportXHTML
{
  private ChaserReportXHTML()
  {

  }

  /**
   * Generate an XHTML page from the given report.
   *
   * @param report The report
   * @param stream The output stream
   *
   * @throws ParserConfigurationException On XML parser issues
   * @throws TransformerException         On XML serializer issues
   */

  public static void writeXHTMLPage(
    final ChaserReport report,
    final OutputStream stream)
    throws ParserConfigurationException, TransformerException
  {
    Objects.requireNonNull(report, "report");
    Objects.requireNonNull(stream, "stream");
    serializeDocument(stream, buildXHTMLPage(report));
  }

  /**
   * Generate an XHTML element from the given report.
   *
   * @param report The report
   * @param stream The output stream
   *
   * @throws ParserConfigurationException On XML parser issues
   * @throws TransformerException         On XML serializer issues
   */

  public static void writeXHTMLCore(
    final ChaserReport report,
    final OutputStream stream)
    throws ParserConfigurationException, TransformerException
  {
    Objects.requireNonNull(report, "report");
    Objects.requireNonNull(stream, "stream");
    serializeDocument(stream, buildXHTMLCore(report));
  }

  private static void serializeDocument(
    final OutputStream stream,
    final Document document)
    throws TransformerException
  {
    final TransformerFactory transformer_factory =
      TransformerFactory.newInstance();
    final Transformer transformer =
      transformer_factory.newTransformer();

    final DOMImplementation dom = document.getImplementation();
    final DocumentType doctype =
      dom.createDocumentType(
        "html",
        "-//W3C//DTD XHTML 1.0 Strict//EN",
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    transformer.setOutputProperty(
      OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
    transformer.setOutputProperty(
      OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
    transformer.setOutputProperty(
      OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(
      "{http://xml.apache.org/xslt}indent-amount", "2");

    transformer.transform(new DOMSource(document), new StreamResult(stream));
  }

  /**
   * Generate an XHTML page from the given report.
   *
   * @param report The report
   *
   * @return A generated document
   *
   * @throws ParserConfigurationException On XML parser issues
   */

  public static Document buildXHTMLPage(
    final ChaserReport report)
    throws ParserConfigurationException
  {
    Objects.requireNonNull(report, "report");

    final Document core_doc = buildXHTMLCore(report);

    final DocumentBuilderFactory doc_factory =
      DocumentBuilderFactory.newInstance();
    final DocumentBuilder doc_builder =
      doc_factory.newDocumentBuilder();

    final Document doc = doc_builder.newDocument();
    doc.setStrictErrorChecking(true);

    final Element root = doc.createElement("html");
    root.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
    root.setAttribute("xml:lang", "en");
    doc.appendChild(root);

    final Element head = doc.createElement("head");
    root.appendChild(head);

    {
      final Element title = doc.createElement("title");
      head.appendChild(title);
      title.setTextContent("Modularization Status");
    }

    {
      final Element link = doc.createElement("link");
      head.appendChild(link);
      link.setAttribute("rel", "stylesheet");
      link.setAttribute("type", "text/css");
      link.setAttribute("href", "style.css");
    }

    final Element body = doc.createElement("body");
    root.appendChild(body);

    {
      final Element body_header = doc.createElement("div");
      body.appendChild(body_header);

      final Element h1 = doc.createElement("h1");
      body_header.appendChild(h1);
      h1.setTextContent("Modularization Status");

      body_header.appendChild(buildExplanatoryTest(doc));
      body_header.appendChild(buildStatisticsList(report, doc));
      body_header.appendChild(buildLastGenerated(doc));
    }

    body.appendChild(doc.importNode(core_doc.getDocumentElement(), true));
    return doc;
  }

  private static Element buildExplanatoryTest(final Document doc)
  {
    final Element p = doc.createElement("p");

    p.setTextContent(
      new StringBuilder(128)
        .append("Dependencies are given in reverse-topological order.")
        .append(System.lineSeparator())
        .append(
          "That is, for the tree of dependencies that were analyzed to produce this report, ")
        .append(
          "the artifacts closest to the leaves of the tree are given first.")
        .append(
          "This is the most efficient order in which to contact package maintainers ")
        .append(
          "to beg for modularization: If package A depends on package B, then A cannot be ")
        .append(
          "fully modularized before B and therefore B's maintainer should be contacted first.")
        .toString());
    return p;
  }

  private static Element buildLastGenerated(final Document doc)
  {
    final Element p = doc.createElement("p");
    final String timeString =
      ZonedDateTime.now(ZoneId.of("UTC")).format(ISO_ZONED_DATE_TIME);
    p.setTextContent("Last Generated: " + timeString);
    return p;
  }

  private static Element buildStatisticsList(
    final ChaserReport report,
    final Document doc)
  {
    final Element ul = doc.createElement("ul");
    final long total = report.dependenciesTotal();
    final long fully = report.dependenciesFullyModularized();
    final long named = report.dependenciesNamed();
    final long not_ready = report.dependenciesNotModularized();
    final double jlink = ((double) fully / (double) total) * 100.0;
    final double safe = ((double) (fully + named) / (double) total) * 100.0;


    {
      final Element li = doc.createElement("li");
      ul.appendChild(li);
      li.setTextContent(new StringBuilder(128)
                          .append("Analyzed ")
                          .append(total)
                          .append(" dependencies")
                          .toString());
    }

    {
      final Element li = doc.createElement("li");
      ul.appendChild(li);
      li.setTextContent(new StringBuilder(128)
                          .append(fully)
                          .append(" dependencies are fully modularized")
                          .toString());
    }

    {
      final Element li = doc.createElement("li");
      ul.appendChild(li);
      li.setTextContent(new StringBuilder(128)
                          .append(named)
                          .append(
                            " dependencies have Automatic-Module-Name entries")
                          .toString());
    }

    {
      final Element li = doc.createElement("li");
      ul.appendChild(li);
      li.setTextContent(new StringBuilder(128)
                          .append(not_ready)
                          .append(
                            " dependencies have not been modularized at all")
                          .toString());
    }

    {
      final Element li = doc.createElement("li");
      ul.appendChild(li);
      li.setTextContent(new StringBuilder(128)
                          .append(String.format("%.02f", Double.valueOf(jlink)))
                          .append(
                            "% of the dependencies are ready to be used in jlink distributions")
                          .toString());
    }

    {
      final Element li = doc.createElement("li");
      ul.appendChild(li);
      li.setTextContent(new StringBuilder(128)
                          .append(String.format("%.02f", Double.valueOf(safe)))
                          .append(
                            "% of the dependencies are safe to use as dependencies for modular projects")
                          .toString());
    }
    return ul;
  }

  /**
   * Generate an XHTML page from the given report.
   *
   * @param report The report
   *
   * @return A generated document
   *
   * @throws ParserConfigurationException On XML parser issues
   */

  public static Document buildXHTMLCore(
    final ChaserReport report)
    throws ParserConfigurationException
  {
    Objects.requireNonNull(report, "report");

    final DocumentBuilderFactory doc_factory =
      DocumentBuilderFactory.newInstance();
    final DocumentBuilder doc_builder =
      doc_factory.newDocumentBuilder();

    final Document doc = doc_builder.newDocument();
    doc.setStrictErrorChecking(true);

    final Element root = doc.createElement("div");
    doc.appendChild(root);

    root.appendChild(buildDependencyTable(report, doc));
    root.appendChild(buildDependencyTree(report, doc));
    return doc;
  }

  private static Element buildDependencyTree(
    final ChaserReport report,
    final Document doc)
  {
    final Element root = doc.createElement("div");
    final Element h2 = doc.createElement("h2");
    root.appendChild(h2);
    h2.setTextContent("Dependency Tree");

    final ChaserDependencyNode root_node = findRootNode(report.graph());
    root.appendChild(buildDependencyTreeRecursive(report, doc, root_node));
    return root;
  }

  private static ChaserDependencyNode findRootNode(
    final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph)
  {
    return graph.vertexSet()
      .stream()
      .filter(n -> graph.inDegreeOf(n) == 0)
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No root node present!"));
  }

  private static Element buildDependencyTreeRecursive(
    final ChaserReport report,
    final Document doc,
    final ChaserDependencyNode node)
  {
    final Element root = doc.createElement("ul");

    final Element li = doc.createElement("li");
    root.appendChild(li);

    final Element a = doc.createElement("a");
    a.setAttribute("href", "#" + nodeId(node));
    a.setTextContent(node.toTerseString());
    li.appendChild(a);

    final DirectedAcyclicGraph<ChaserDependencyNode, ChaserDependencyEdge> graph = report.graph();
    for (final ChaserDependencyEdge edge : graph.outgoingEdgesOf(node)) {
      li.appendChild(buildDependencyTreeRecursive(report, doc, edge.target()));
    }

    return root;
  }

  private static String nodeId(
    final ChaserDependencyNode node)
  {
    try {
      final MessageDigest digest =
        MessageDigest.getInstance("SHA-256");
      final byte[] result =
        digest.digest(node.toTerseString().getBytes(US_ASCII));
      return "dep_" + Hex.show(result);
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  private static Element buildDependencyTable(
    final ChaserReport report,
    final Document doc)
  {
    final Element table = doc.createElement("table");

    {
      final Element thead = doc.createElement("thead");
      table.appendChild(thead);
      final Element tr = doc.createElement("tr");
      thead.appendChild(tr);

      {
        final Element th = doc.createElement("th");
        tr.appendChild(th);
        th.setTextContent("Group");
      }

      {
        final Element th = doc.createElement("th");
        tr.appendChild(th);
        th.setTextContent("Artifact");
      }

      {
        final Element th = doc.createElement("th");
        tr.appendChild(th);
        th.setTextContent("Current Version");
      }

      {
        final Element th = doc.createElement("th");
        tr.appendChild(th);
        th.setTextContent("Current Status");
      }

      {
        final Element th = doc.createElement("th");
        tr.appendChild(th);
        th.setTextContent("Newest Version");
      }

      {
        final Element th = doc.createElement("th");
        tr.appendChild(th);
        th.setTextContent("Newest Status");
      }
    }

    final SortedMap<ChaserDependencyNode, ChaserReportDependency> reports =
      report.reports();
    final TopologicalOrderIterator<ChaserDependencyNode, ChaserDependencyEdge> iter =
      new TopologicalOrderIterator<>(report.graph());

    final List<ChaserDependencyNode> nodes = new ArrayList<>(reports.size());
    while (iter.hasNext()) {
      nodes.add(iter.next());
    }

    Collections.reverse(nodes);

    final Element table_body = doc.createElement("tbody");
    table.appendChild(table_body);

    for (final ChaserDependencyNode node : nodes) {
      final ChaserReportDependencyType node_report = reports.get(node);

      final Element tr = doc.createElement("tr");
      table_body.appendChild(tr);
      tr.setAttribute("id", nodeId(node));

      {
        final Element td = doc.createElement("td");
        tr.appendChild(td);
        td.appendChild(mavenCentralGroup(doc, node));
      }

      {
        final Element td = doc.createElement("td");
        tr.appendChild(td);
        td.appendChild(mavenCentralArtifact(doc, node));
      }

      tr.appendChild(versionCell(doc, node, node_report.statusCurrent()));
      tr.appendChild(statusCell(doc, node_report.statusCurrent()));
      tr.appendChild(versionCell(doc, node, node_report.statusHighest()));
      tr.appendChild(statusCell(doc, node_report.statusHighest()));
    }

    return table;
  }

  private static Element versionCell(
    final Document doc,
    final ChaserDependencyNode node,
    final ChaserModularizationStatusType status_input)
  {
    switch (status_input.kind()) {
      case MODULARIZED_FULLY: {
        final ChaserModularizationStatusModularizedFully status =
          (ChaserModularizationStatusModularizedFully) status_input;

        final Element td = doc.createElement("td");
        td.appendChild(mavenCentralArtifactVersion(
          doc,
          node,
          status.version()));
        return td;
      }

      case MODULARIZED_AUTOMATIC_MODULE_NAME: {
        final ChaserModularizationStatusModularizedAutomaticModuleName status =
          (ChaserModularizationStatusModularizedAutomaticModuleName) status_input;

        final Element td = doc.createElement("td");
        td.appendChild(mavenCentralArtifactVersion(
          doc,
          node,
          status.version()));
        return td;
      }

      case NOT_MODULARIZED: {
        final ChaserModularizationStatusNotModularized status =
          (ChaserModularizationStatusNotModularized) status_input;

        final Element td = doc.createElement("td");
        td.appendChild(mavenCentralArtifactVersion(
          doc,
          node,
          status.version()));
        return td;
      }

      case NOT_JAR: {
        final ChaserModularizationStatusNotJar status =
          (ChaserModularizationStatusNotJar) status_input;

        final Element td = doc.createElement("td");
        td.appendChild(mavenCentralArtifactVersion(
          doc,
          node,
          status.version()));
        return td;
      }

      case UNAVAILABLE: {
        final Element td = doc.createElement("td");
        td.setTextContent("Unavailable");
        return td;
      }
    }

    throw new IllegalStateException("Unreachable code");
  }

  private static Element statusCell(
    final Document doc,
    final ChaserModularizationStatusType status_input)
  {
    switch (status_input.kind()) {
      case MODULARIZED_FULLY: {
        final ChaserModularizationStatusModularizedFully status =
          (ChaserModularizationStatusModularizedFully) status_input;

        final Element td = doc.createElement("td");
        td.setAttribute("class", "chaser_module_full");
        td.setTextContent("Modularized: " + status.moduleName());
        return td;
      }

      case MODULARIZED_AUTOMATIC_MODULE_NAME: {
        final ChaserModularizationStatusModularizedAutomaticModuleName status =
          (ChaserModularizationStatusModularizedAutomaticModuleName) status_input;

        final Element td = doc.createElement("td");
        td.setAttribute("class", "chaser_module_automatic");
        td.setTextContent("Automatic Module: " + status.moduleName());
        return td;
      }

      case NOT_MODULARIZED: {
        final Element td = doc.createElement("td");
        td.setAttribute("class", "chaser_module_not_modularized");
        td.setTextContent("Not modularized");
        return td;
      }

      case NOT_JAR: {
        final Element td = doc.createElement("td");
        td.setAttribute("class", "chaser_module_not_jar");
        td.setTextContent("Not A Jar");
        return td;
      }

      case UNAVAILABLE: {
        final Element td = doc.createElement("td");
        td.setAttribute("class", "chaser_module_unavailable");
        td.setTextContent("Unavailable");
        return td;
      }
    }

    throw new IllegalStateException("Unreachable code");
  }

  private static Element mavenCentralArtifactVersion(
    final Document document,
    final ChaserDependencyNode node,
    final String version)
  {
    final Element a = document.createElement("a");
    a.setAttribute(
      "href",
      new StringBuilder(64)
        .append("http://search.maven.org/#artifactdetails|")
        .append(node.group())
        .append("|")
        .append(node.artifact())
        .append("|")
        .append(version)
        .append("|")
        .toString());
    a.setTextContent(version);
    return a;
  }

  private static Element mavenCentralArtifact(
    final Document document,
    final ChaserDependencyNode node)
  {
    final Element a = document.createElement("a");
    a.setAttribute(
      "href",
      new StringBuilder(64)
        .append("http://search.maven.org/#search|ga|1|g%3A%22")
        .append(node.group())
        .append("%22")
        .toString());
    a.setTextContent(node.artifact());
    return a;
  }

  private static Element mavenCentralGroup(
    final Document document,
    final ChaserDependencyNode node)
  {
    final Element a = document.createElement("a");
    a.setAttribute(
      "href",
      new StringBuilder(64)
        .append("http://search.maven.org/#search|ga|1|g%3A%22")
        .append(node.group())
        .append("%22%20AND%20%22")
        .append(node.artifact())
        .append("%22")
        .toString());
    a.setTextContent(node.group());
    return a;
  }
}
