/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2006-2007 Robert Grimm
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
package xtc.type;

import java.io.IOException;

/**
 * Representation of a cast reference.  
 *
 * @author Robert Grimm
 * @version $Revision: 1.3 $
 */
public class CastReference extends RelativeReference {

  /**
   * Create a new cast reference.
   *
   * @param base The base reference.
   * @param type The cast-to type.
   */
  public CastReference(Type type, Reference base) {
    super(type, base);

    // Update the type.
    normalize();
  }

  public boolean isPrefix() {
    return true;
  }

  public boolean isCast() {
    return true;
  }

  public int hashCode() {
    return base.hashCode();
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (! (o instanceof CastReference)) return false;
    CastReference other = (CastReference)o;
    return this.base.equals(other.base) && this.type.equals(other.type);
  }

  public void write(Appendable out) throws IOException {
    out.append('(');
    type.write(out);
    out.append(')');
    base.write(out);
  }

}
