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

import java.util.Optional;

/**
 * The modularization status of a dependency.
 */

public interface ChaserModularizationStatusType
{
  /**
   * @return The status kind
   */

  Kind kind();

  /**
   * The various stages of modularization.
   */

  enum Kind
  {
    /**
     * The dependency has a full module-info.java descriptor.
     */

    MODULARIZED_FULLY,

    /**
     * The dependency has an automatic module name.
     */

    MODULARIZED_AUTOMATIC_MODULE_NAME,

    /**
     * The dependency is not modularized.
     */

    NOT_MODULARIZED,

    /**
     * The dependency is not a jar file.
     */

    NOT_JAR,

    /**
     * The dependency is unavailable.
     */

    UNAVAILABLE
  }

  /**
   * The dependency has a full module-info.java descriptor.
   */

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusModularizedFullyType
    extends ChaserModularizationStatusType
  {
    @Override
    default Kind kind()
    {
      return Kind.MODULARIZED_FULLY;
    }

    /**
     * @return The module name
     */

    @Value.Parameter
    String moduleName();

    /**
     * @return The module version
     */

    @Value.Parameter
    String version();
  }

  /**
   * The dependency has an automatic module name.
   */

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusModularizedAutomaticModuleNameType
    extends ChaserModularizationStatusType
  {
    @Override
    default Kind kind()
    {
      return Kind.MODULARIZED_AUTOMATIC_MODULE_NAME;
    }

    /**
     * @return The module name
     */

    @Value.Parameter
    String moduleName();

    /**
     * @return The module version
     */

    @Value.Parameter
    String version();
  }

  /**
   * The dependency is not modularized.
   */

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusNotModularizedType
    extends ChaserModularizationStatusType
  {
    @Override
    default Kind kind()
    {
      return Kind.NOT_MODULARIZED;
    }

    /**
     * @return The module version
     */

    @Value.Parameter
    String version();
  }

  /**
   * The dependency is not a jar file.
   */

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusNotJarType
    extends ChaserModularizationStatusType
  {
    @Override
    default Kind kind()
    {
      return Kind.NOT_JAR;
    }

    /**
     * @return The module version
     */

    @Value.Parameter
    String version();
  }

  /**
   * The dependency is unavailable.
   */

  @ChaserImmutableStyleType
  @Value.Immutable
  interface ChaserModularizationStatusUnavailableType
    extends ChaserModularizationStatusType
  {
    @Override
    default Kind kind()
    {
      return Kind.UNAVAILABLE;
    }

    /**
     * @return The error encountered
     */

    @Value.Parameter
    Optional<Exception> error();
  }
}
