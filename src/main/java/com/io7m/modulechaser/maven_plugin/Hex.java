package com.io7m.modulechaser.maven_plugin;

final class Hex
{
  private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

  private Hex()
  {

  }

  public static String show(final byte[] data)
  {
    final StringBuilder r = new StringBuilder(data.length * 2);
    for (final byte b : data) {
      r.append(HEX_CODE[(b >> 4) & 0xF]);
      r.append(HEX_CODE[(b & 0xF)]);
    }
    return r.toString();
  }
}
