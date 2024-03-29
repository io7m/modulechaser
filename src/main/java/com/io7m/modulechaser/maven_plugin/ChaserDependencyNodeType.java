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

import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * A node in the dependency graph.
 */

@ChaserImmutableStyleType
@Value.Immutable
public interface ChaserDependencyNodeType
  extends Comparable<ChaserDependencyNodeType>
{
  /**
   * @return The group
   */

  @Value.Parameter
  String group();

  /**
   * @return The artifact ID
   */

  @Value.Parameter
  String artifact();

  /**
   * @return The version
   */

  @Value.Parameter
  String version();

  /**
   * @return The classifier
   */

  @Value.Parameter
  Optional<String> classifier();

  /**
   * @return The type
   */

  @Value.Parameter
  String type();

  /**
   * @return The scope
   */

  @Value.Auxiliary
  @Value.Parameter
  String scope();

  /**
   * @return The node formatted as a terse string
   */

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
