/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2005-2007 Robert Grimm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
#include <stddef.h>
#include <stdio.h>
#include <limits.h>
#include <wchar.h>
#include <string.h>

#define stringify(s) xstringify(s)
#define xstringify(s) #s

int rank(const char*);

/**
 * The C program to generate <code>xtc.Limits</code>.
 *
 * @author Robert Grimm
 * @version $Revision: 1.22 $
 */
int main(void) {
  const int SIZE_SHORT     = sizeof(short);
  const int SIZE_INT       = sizeof(int);
  const int SIZE_LONG      = sizeof(long);
  const int SIZE_LONG_LONG = sizeof(long long);
         
  printf("/*\n"
         " * xtc - The eXTensible Compiler\n"
         " * Copyright (C) 2005-2007 Robert Grimm\n"
         " *\n"
         " * This program is free software; you can redistribute it and/or\n"
         " * modify it under the terms of the GNU General Public License\n"
         " * version 2 as published by the Free Software Foundation.\n"
         " *\n"
         " * This program is distributed in the hope that it will be useful,\n"
         " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
         " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
         " * GNU General Public License for more details.\n"
         " *\n"
         " * You should have received a copy of the GNU General Public License\n"
         " * along with this program; if not, write to the Free Software\n"
         " * Foundation, 51 Franklin Street, Fifth Floor, Boston, "
         "MA 02110-1301,\n"
         " * USA.\n"
         " */\n"
         "package xtc;\n"
         "\n"
         "import java.math.BigInteger;\n"
         "\n"
         "/**\n"
         " * The platform-dependent C type limits.\n"
         " *\n"
         " * <p />To recreate this class, compile <code>limits.c</code> in "
         "the same\n"
         " * directory as this interface and run the resulting executable "
         "while\n"
         " * piping standard output to <code>Limits.java</code>.\n"
         " *\n"
         " * <p />The rank for pointer difference, sizeof, and wide character "
         "types\n"
         " * reflects the ordering <code>char</code>, <code>short</code>,\n"
         " * <code>int</code>, <code>long</code>, and <code>long long</code>,\n"
         " * starting at 1 and ignoring the sign.  This program requires that "
         "the\n"
         " * <code>__PTRDIFF_TYPE__</code>, <code>__SIZE_TYPE__</code>, and\n"
         " * <code>__WCHAR_TYPE__</code> preprocessor macros are defined.\n"
         " *\n"
         " * @author Robert Grimm\n"
         " * @version $" "Revision" "$\n"
         " */\n"
         "public class Limits {\n"
         "\n"
         "  /** Hide constructor. */\n"
         "  private Limits() { /* Nothing to do. */ }\n"
         "\n"
         "  /** The flag for whether the machine is big endian. */\n");

  union { long l; char c[sizeof(long)]; } u;
  u.l = 1;
  if (u.c[sizeof(long)-1] == 1) {
    printf("  public static final boolean IS_BIG_ENDIAN = true;\n");
  } else {
    printf("  public static final boolean IS_BIG_ENDIAN = false;\n");
  }

  printf("\n"
         "  /** The size of void types. */\n"
         "  public static final int VOID_SIZE = %d;\n"
         "\n"
         "  /** The alignment of void types. */\n"
         "  public static final int VOID_ALIGN = %d;\n"
         "\n"
         "  /** The size of function types. */\n"
         "  public static final int FUNCTION_SIZE = %d;\n"
         "\n"
         "  /** The alignment of function types. */\n"
         "  public static final int FUNCTION_ALIGN = %d;\n"
         "\n"
         "  /** The size of pointer types. */\n"
         "  public static final int POINTER_SIZE = %d;\n"
         "\n"
         "  /** The alignment of pointer types. */\n"
         "  public static final int POINTER_ALIGN = %d;\n"
         "\n"
         "  /** The rank of pointer difference types. */\n"
         "  public static final int PTRDIFF_RANK = %d;\n"
         "\n"
         "  /** The rank of sizeof expressions. */\n"
         "  public static final int SIZEOF_RANK = %d;\n"
         "\n"
         "  /** The size of pointer difference types. */\n"
         "  public static final int PTRDIFF_SIZE = %d;\n"
         "\n"
         "  /** The size of sizeof expressions. */\n"
         "  public static final int SIZEOF_SIZE = %d;\n"
         "\n"
         "  /** The maximum size of fixed size arrays. */\n"
         "  public static final BigInteger ARRAY_MAX = "
         "BigInteger.valueOf(1073741824L);\n"
         "\n"
         "  /** The flag for whether <code>char</code> is signed. */\n",
         sizeof(void), __alignof(void),
         sizeof(main), __alignof(main),
         sizeof(void*), __alignof(struct { void* f; }),
         rank(stringify(__PTRDIFF_TYPE__)), rank(stringify(__SIZE_TYPE__)),
         sizeof(ptrdiff_t), sizeof(size_t));

  if ((char)0xff > 0) {
    printf("  public static final boolean IS_CHAR_SIGNED = false;\n");
  } else {
    printf("  public static final boolean IS_CHAR_SIGNED = true;\n");
  }

  printf("\n"
         "  /** The bit width of char types. */\n"
         "  public static final int CHAR_BITS = %d;\n"
         "\n"
         "  /** The minimum value of signed char types. */\n"
         "  public static final BigInteger CHAR_MIN = "
         "new BigInteger(\"%hhd\");\n"
         "\n"
         "  /** The maximum value of signed char types. */\n"
         "  public static final BigInteger CHAR_MAX = "
         "new BigInteger(\"%hhd\");\n"
         "\n"
         "  /** The modulo of signed char types. */\n"
         "  public static final BigInteger CHAR_MOD = "
         "CHAR_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The maximum value of unsigned char types. */\n"
         "  public static final BigInteger UCHAR_MAX = "
         "new BigInteger(\"%hhu\");\n"
         "\n"
         "  /** The modulo of unsigned char types. */\n"
         "  public static final BigInteger UCHAR_MOD = "
         "UCHAR_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The flag for whether <code>wchar_t</code> is signed. */\n",
         CHAR_BIT, SCHAR_MIN, SCHAR_MAX, UCHAR_MAX);

  if (0 == WCHAR_MIN) {
    printf("  public static final boolean IS_WCHAR_SIGNED = false;\n");
  } else {
    printf("  public static final boolean IS_WCHAR_SIGNED = true;\n");
  }

  printf("\n"
         "  /** The rank of wide character types. */\n"
         "  public static final int WCHAR_RANK = %d;\n"
         "\n"
         "  /** The size of wide char types. */\n"
         "  public static final int WCHAR_SIZE = %d;\n"
         "\n"
         "  /** The size of short types. */\n"
         "  public static final int SHORT_SIZE = %d;\n"
         "\n"
         "  /** The alignment of short types. */\n"
         "  public static final int SHORT_ALIGN = %d;\n"
         "\n"
         "  /** The bit width of short types. */\n"
         "  public static final int SHORT_BITS = %d;\n"
         "\n"
         "  /** The minimum value of signed short types. */\n"
         "  public static final BigInteger SHORT_MIN = "
         "new BigInteger(\"%hd\");\n"
         "\n"
         "  /** The maximum value of signed short types. */\n"
         "  public static final BigInteger SHORT_MAX = "
         "new BigInteger(\"%hd\");\n"
         "\n"
         "  /** The modulo of signed short types. */\n"
         "  public static final BigInteger SHORT_MOD = "
         "SHORT_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The maximum value of unsigned short types. */\n"
         "  public static final BigInteger USHORT_MAX = "
         "new BigInteger(\"%hu\");\n"
         "\n"
         "  /** The modulo of unsigned short types. */\n"
         "  public static final BigInteger USHORT_MOD = "
         "USHORT_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The flag for whether <code>int</code> is signed in "
         "bit-fields. */\n",
         rank(stringify(__WCHAR_TYPE__)), sizeof(wchar_t),
         SIZE_SHORT, __alignof(struct { short f; }), SIZE_SHORT * CHAR_BIT, 
         SHRT_MIN, SHRT_MAX, USHRT_MAX);

  struct {
    int field : 2;
  } data = { 0x03 };

  if (data.field > 0) {
    printf("  public static final boolean IS_INT_SIGNED = false;\n");
  } else {
    printf("  public static final boolean IS_INT_SIGNED = true;\n");
  }

  printf("\n"
         "  /** The size of int types. */\n"
         "  public static final int INT_SIZE = %d;\n"
         "\n"
         "  /** The alignment of int types. */\n"
         "  public static final int INT_ALIGN = %d;\n"
         "\n"
         "  /** The bit width of int types. */\n"
         "  public static final int INT_BITS = %d;\n"
         "\n"
         "  /** The minimum value of signed int types. */\n"
         "  public static final BigInteger INT_MIN = "
         "new BigInteger(\"%d\");\n"
         "\n"
         "  /** The maximum value of signed int types. */\n"
         "  public static final BigInteger INT_MAX = "
         "new BigInteger(\"%d\");\n"
         "\n"
         "  /** The modulo of signed int types. */\n"
         "  public static final BigInteger INT_MOD = "
         "INT_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The maximum value of unsigned int types. */\n"
         "  public static final BigInteger UINT_MAX = "
         "new BigInteger(\"%u\");\n"
         "\n"
         "  /** The modulo of unsigned int types. */\n"
         "  public static final BigInteger UINT_MOD = "
         "UINT_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The size of long types. */\n"
         "  public static final int LONG_SIZE = %d;\n"
         "\n"
         "  /** The alignment of long types. */\n"
         "  public static final int LONG_ALIGN = %d;\n"
         "\n"
         "  /** The bit width of long types. */\n"
         "  public static final int LONG_BITS = %d;\n"
         "\n"
         "  /** The minimum value of signed long types. */\n"
         "  public static final BigInteger LONG_MIN = "
         "new BigInteger(\"%ld\");\n"
         "\n"
         "  /** The maximum value of signed long types. */\n"
         "  public static final BigInteger LONG_MAX = "
         "new BigInteger(\"%ld\");\n"
         "\n"
         "  /** The modulo of signed long types. */\n"
         "  public static final BigInteger LONG_MOD = "
         "LONG_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The maximum value of unsigned long types. */\n"
         "  public static final BigInteger ULONG_MAX = "
         "new BigInteger(\"%lu\");\n"
         "\n"
         "  /** The modulo of unsigned long types. */\n"
         "  public static final BigInteger ULONG_MOD = "
         "ULONG_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The size of long long types. */\n"
         "  public static final int LONG_LONG_SIZE = %d;\n"
         "\n"
         "  /** The alignment of long long types. */\n"
         "  public static final int LONG_LONG_ALIGN = %d;\n"
         "\n"
         "  /** The bit width of long long types. */\n"
         "  public static final int LONG_LONG_BITS = %d;\n"
         "\n"
         "  /** The minimum value of signed long long types. */\n"
         "  public static final BigInteger LONG_LONG_MIN =\n"
         "    new BigInteger(\"%lld\");\n"
         "\n"
         "  /** The maximum value of signed long long types. */\n"
         "  public static final BigInteger LONG_LONG_MAX =\n"
         "    new BigInteger(\"%lld\");\n"
         "\n"
         "  /** The modulo of signed long long types. */\n"
         "  public static final BigInteger LONG_LONG_MOD =\n"
         "    LONG_LONG_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The maximum value of unsigned long long types. */\n"
         "  public static final BigInteger ULONG_LONG_MAX =\n"
         "    new BigInteger(\"%llu\");\n"
         "\n"
         "  /** The modulo of unsigned long long types. */\n"
         "  public static final BigInteger ULONG_LONG_MOD =\n"
         "    ULONG_LONG_MAX.add(BigInteger.ONE);\n"
         "\n"
         "  /** The size of float types. */\n"
         "  public static final int FLOAT_SIZE = %d;\n"
         "\n"
         "  /** The alignment of float types. */\n"
         "  public static final int FLOAT_ALIGN = %d;\n"
         "\n"
         "  /** The size of double types. */\n"
         "  public static final int DOUBLE_SIZE = %d;\n"
         "\n"
         "  /** The alignment of double types. */\n"
         "  public static final int DOUBLE_ALIGN = %d;\n"
         "\n"
         "  /** The size of long double types. */\n"
         "  public static final int LONG_DOUBLE_SIZE = %d;\n"
         "\n"
         "  /** The alignment of long double types. */\n"
         "  public static final int LONG_DOUBLE_ALIGN = %d;\n"
         "\n"
         "  /**\n"
         "   * Convert the specified size to the corresponding bit width.\n"
         "   *\n"
         "   * @param size The size.\n"
         "   * @return The corresponding bit width.\n"
         "   */\n"
         "  public static int toWidth(int size) {\n"
         "    return size * %d;\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into a char.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into a char.\n"
         "   */\n"
         "  public static boolean fitsChar(BigInteger value) {\n"
         "    return ((CHAR_MIN.compareTo(value) <= 0) &&\n"
         "            (CHAR_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into an unsigned\n"
         "   * char.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into an unsigned\n"
         "   *   char.\n"
         "   */\n"
         "  public static boolean fitsUnsignedChar(BigInteger value) {\n"
         "    return ((BigInteger.ZERO.compareTo(value) <= 0) &&\n"
         "            (UCHAR_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into a short.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into a short.\n"
         "   */\n"
         "  public static boolean fitsShort(BigInteger value) {\n"
         "    return ((SHORT_MIN.compareTo(value) <= 0) &&\n"
         "            (SHORT_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into an unsigned\n"
         "   * short.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into an unsigned\n"
         "   *   short.\n"
         "   */\n"
         "  public static boolean fitsUnsignedShort(BigInteger value) {\n"
         "    return ((BigInteger.ZERO.compareTo(value) <= 0) &&\n"
         "            (USHORT_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into an int.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into an int.\n"
         "   */\n"
         "  public static boolean fitsInt(BigInteger value) {\n"
         "    return ((INT_MIN.compareTo(value) <= 0) &&\n"
         "            (INT_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into an unsigned\n"
         "   * int.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into an unsigned\n"
         "   *   int.\n"
         "   */\n"
         "  public static boolean fitsUnsignedInt(BigInteger value) {\n"
         "    return ((BigInteger.ZERO.compareTo(value) <= 0) &&\n"
         "            (UINT_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into a long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into a long.\n"
         "   */\n"
         "  public static boolean fitsLong(BigInteger value) {\n"
         "    return ((LONG_MIN.compareTo(value) <= 0) &&\n"
         "            (LONG_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into an unsigned\n"
         "   * long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into an unsigned\n"
         "   *   long.\n"
         "   */\n"
         "  public static boolean fitsUnsignedLong(BigInteger value) {\n"
         "    return ((BigInteger.ZERO.compareTo(value) <= 0) &&\n"
         "            (ULONG_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into a long long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into a long long.\n"
         "   */\n"
         "  public static boolean fitsLongLong(BigInteger value) {\n"
         "    return ((LONG_LONG_MIN.compareTo(value) <= 0) &&\n"
         "            (LONG_LONG_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Determine whether the specified value fits into an unsigned\n"
         "   * long long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return <code>true</code> if the value fits into an unsigned\n"
         "   *   long long.\n"
         "   */\n"
         "  public static boolean fitsUnsignedLongLong(BigInteger value) {\n"
         "    return ((BigInteger.ZERO.compareTo(value) <= 0) &&\n"
         "            (ULONG_LONG_MAX.compareTo(value) >= 0));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as a signed char.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as a signed char.\n"
         "   */\n"
         "  public static BigInteger maskAsSignedChar(BigInteger value) {\n"
         "    return value.remainder(CHAR_MOD);\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as an unsigned char.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as an unsigned char.\n"
         "   */\n"
         "  public static BigInteger maskAsUnsignedChar(BigInteger value) {\n"
         "    return (value.signum() >= 0) ? value.remainder(UCHAR_MOD) :\n"
         "      UCHAR_MOD.add(value.remainder(UCHAR_MOD));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as a signed short.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as a signed short.\n"
         "   */\n"
         "  public static BigInteger maskAsShort(BigInteger value) {\n"
         "    return value.remainder(SHORT_MOD);\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as an unsigned short.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as an unsigned short.\n"
         "   */\n"
         "  public static BigInteger maskAsUnsignedShort(BigInteger value) {\n"
         "    return (value.signum() >= 0) ? value.remainder(USHORT_MOD) :\n"
         "      USHORT_MOD.add(value.remainder(USHORT_MOD));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as a signed int.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as a signed int.\n"
         "   */\n"
         "  public static BigInteger maskAsInt(BigInteger value) {\n"
         "    return value.remainder(INT_MOD);\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as an unsigned int.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as an unsigned int.\n"
         "   */\n"
         "  public static BigInteger maskAsUnsignedInt(BigInteger value) {\n"
         "    return (value.signum() >= 0) ? value.remainder(UINT_MOD) :\n"
         "      UINT_MOD.add(value.remainder(UINT_MOD));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as a signed long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as a signed long.\n"
         "   */\n"
         "  public static BigInteger maskAsLong(BigInteger value) {\n"
         "    return value.remainder(LONG_MOD);\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as an unsigned long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as an unsigned long.\n"
         "   */\n"
         "  public static BigInteger maskAsUnsignedLong(BigInteger value) {\n"
         "    return (value.signum() >= 0) ? value.remainder(ULONG_MOD) :\n"
         "      ULONG_MOD.add(value.remainder(ULONG_MOD));\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as a signed long long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as a signed long long.\n"
         "   */\n"
         "  public static BigInteger maskAsLongLong(BigInteger value) {\n"
         "    return value.remainder(LONG_LONG_MOD);\n"
         "  }\n"
         "\n"
         "  /**\n"
         "   * Mask the specified value as an unsigned long long.\n"
         "   *\n"
         "   * @param value The value.\n"
         "   * @return The value as an unsigned long long.\n"
         "   */\n"
         "  public static BigInteger maskAsUnsignedLongLong(BigInteger value)"
         " {\n"
         "    return (value.signum() >= 0) ? value.remainder(ULONG_LONG_MOD)"
         " :\n"
         "      ULONG_LONG_MOD.add(value.remainder(ULONG_LONG_MOD));\n"
         "  }\n"
         "\n"
         "}\n",
         SIZE_INT, __alignof(struct { int f; }), SIZE_INT * CHAR_BIT,
         INT_MIN, INT_MAX, UINT_MAX,
         SIZE_LONG, __alignof(struct { long f; }), SIZE_LONG * CHAR_BIT,
         LONG_MIN, LONG_MAX, ULONG_MAX,
         SIZE_LONG_LONG, __alignof(struct { long long f; }), 
         SIZE_LONG_LONG * CHAR_BIT,
         LLONG_MIN, LLONG_MAX, ULLONG_MAX,
         sizeof(float), __alignof(struct { float f; }),
         sizeof(double), __alignof(struct { double f; }),
         sizeof(long double), __alignof(struct { long double f; }),
         CHAR_BIT);

  return 0;
}

/**
 * Compute the rank of the specified type.
 *
 * @param type The type.
 * @return Its rank.
 */
int rank(const char* type) {
  if (strstr(type, "char")) {
    return 1;
  } else if (strstr(type, "short")) {
    return 2;
  }

  const char* p = strstr(type, "long");

  if (! p) {
    return 3;
  } else if (! strstr(p+1, "long")) {
    return 4;
  } else {
    return 5;
  }
}
