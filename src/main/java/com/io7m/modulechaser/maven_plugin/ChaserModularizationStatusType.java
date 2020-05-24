/*
 * Copyright © 2020 <code@io7m.com> https://www.io7m.com
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

public interface ChaserModularizationStatusType
{
  Kind kind();

  enum Kind
  {
    MODULARIZED_FULLY,
    MODULARIZED_AUTOMATIC_MODULE_NAME,
    NOT_MODULARIZED,
    NOT_JAR,
    UNAVAILABLE
  }

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

    @Value.Parameter
    String moduleName();

    @Value.Parameter
    String version();
  }

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

    @Value.Parameter
    String moduleName();

    @Value.Parameter
    String version();
  }

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

    @Value.Parameter
    String version();
  }

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

    @Value.Parameter
    String version();
  }

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

    @Value.Parameter
    Optional<Exception> error();
  }
}
