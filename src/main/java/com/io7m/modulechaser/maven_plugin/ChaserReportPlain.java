package com.io7m.modulechaser.maven_plugin;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;

public final class ChaserReportPlain
{
  private ChaserReportPlain()
  {

  }

  public static void writePlain(
    final ChaserReport report,
    final OutputStream stream)
    throws IOException
  {
    Objects.requireNonNull(report, "report");
    Objects.requireNonNull(stream, "stream");

    try (BufferedWriter writer =
           new BufferedWriter(
             new OutputStreamWriter(
               new CloseShieldOutputStream(stream),
               StandardCharsets.UTF_8))) {

      final SortedMap<ChaserDependencyNode, ChaserReportDependencyType> reports =
        report.reports();
      final TopologicalOrderIterator<ChaserDependencyNode, ChaserDependencyEdge> iter =
        new TopologicalOrderIterator<>(report.graph());

      final List<ChaserDependencyNode> nodes = new ArrayList<>(reports.size());
      while (iter.hasNext()) {
        nodes.add(iter.next());
      }

      Collections.reverse(nodes);

      for (final ChaserDependencyNode node : nodes) {
        final ChaserReportDependencyType node_report = reports.get(node);
        writer.append(node.group());
        writer.append(":");
        writer.append(node.artifact());
        writer.append(" ");

        switch (node_report.kind()) {
          case OK: {
            final ChaserReportDependencyOK ok = (ChaserReportDependencyOK) node_report;
            writer.append(showModuleStatus(
              "current",
              node.version(),
              ok.currentModule()));
            writer.append(", ");
            writer.append(showModuleStatus(
              "latest",
              ok.highestVersion(),
              ok.highestModule()));
            break;
          }
          case ERROR: {
            final ChaserReportDependencyError error = (ChaserReportDependencyError) node_report;
            writer.append("could not be checked: ");
            writer.append(error.exception().getClass().getCanonicalName());
            writer.append(": ");
            writer.append(error.exception().getMessage());
            break;
          }
        }
        writer.newLine();
      }

      writer.flush();
    }
  }

  private static String showModuleStatus(
    final String name,
    final String version,
    final Optional<ChaserJPMSModuleName> module)
  {
    return new StringBuilder(128)
      .append(name)
      .append(" version ")
      .append(version)
      .append(module.map(module_name -> {
        if (module_name.isAutomatic()) {
          return " is named automatic module '" + module_name.name() + "'";
        }
        return " is fully modularized as '" + module_name.name() + "'";
      }).orElse(" is not modularized"))
      .toString();
  }
}
