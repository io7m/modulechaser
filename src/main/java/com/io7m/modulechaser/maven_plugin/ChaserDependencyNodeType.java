package com.io7m.modulechaser.maven_plugin;

import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserDependencyNodeType
  extends Comparable<ChaserDependencyNodeType>
{
  @Value.Parameter
  String group();

  @Value.Parameter
  String artifact();

  @Value.Parameter
  String version();

  @Value.Parameter
  Optional<String> classifier();

  @Value.Parameter
  String type();

  default String toTerseString()
  {
    return new StringBuilder(64)
      .append(this.group())
      .append(":")
      .append(this.artifact())
      .append(":")
      .append(this.version())
      .append(":")
      .append(this.classifier().map(c -> ":" + c).orElse(""))
      .append(this.type())
      .toString();
  }

  @Override
  default int compareTo(final ChaserDependencyNodeType other)
  {
    Objects.requireNonNull(other, "other");
    return Comparator.comparing(ChaserDependencyNodeType::group)
      .thenComparing(ChaserDependencyNodeType::artifact)
      .thenComparing(ChaserDependencyNodeType::version)
      .thenComparing(ChaserDependencyNodeType::type)
      .compare(this, other);
  }
}
