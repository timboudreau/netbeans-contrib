/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.scala.editor.ast

import org.netbeans.modules.csl.api.{ElementKind, Modifier, OffsetRange}

import org.netbeans.modules.scala.editor.ScalaGlobal

import _root_.scala.tools.nsc.symtab.Flags

trait ScalaUtils {self: ScalaGlobal =>

  object ScalaUtil {
    def getModifiers(symbol: Symbol): _root_.java.util.Set[Modifier] = {
      val modifiers = new _root_.java.util.HashSet[Modifier]

      if (symbol hasFlag Flags.PROTECTED) {
        modifiers.add(Modifier.PROTECTED)
      } else if (symbol hasFlag Flags.PRIVATE) {
        modifiers.add(Modifier.PRIVATE)
      } else {
        modifiers.add(Modifier.PUBLIC)
      }

      if (symbol hasFlag Flags.MUTABLE)    modifiers.add(Modifier.STATIC) // to use STATIC icon only
      if (symbol hasFlag Flags.DEPRECATED) modifiers.add(Modifier.DEPRECATED)

      modifiers
    }

    def getKind(symbol: Symbol): ElementKind = {
      try {
        if (symbol.isClass) {
          ElementKind.CLASS
        } else if (symbol.isConstructor) {
          ElementKind.CONSTRUCTOR
        } else if (symbol.isConstant) {
          ElementKind.CONSTANT
        } else if (symbol.isValue) {
          ElementKind.FIELD
        } else if (symbol.isModule) {
          ElementKind.MODULE
        } else if (symbol.isLocal && symbol.isVariable) {
          ElementKind.VARIABLE
        } else if (symbol.isMethod) {
          ElementKind.METHOD
        } else if (symbol.isPackage) {
          ElementKind.PACKAGE
        } else if (symbol.isValueParameter) {
          ElementKind.PARAMETER
        } else if (symbol.isTypeParameter) {
          ElementKind.CLASS
        } else {
          ElementKind.OTHER
        }
      } catch {case t: Throwable =>
          ElementKind.OTHER
          // java.lang.Error: no-symbol does not have owner
          //      at scala.tools.nsc.symtab.Symbols$NoSymbol$.owner(Symbols.scala:1609)
          //      at scala.tools.nsc.symtab.Symbols$Symbol.isLocal(Symbols.scala:346)
      }
    }

    def symbolQualifiedName(symbol: Symbol): String = {
      symbolQualifiedName(symbol, true)
    }

    /**
     * Due to the ugly implementation of scala's Symbols.scala, Symbol#fullNameString()
     * may cause:
     * java.lang.Error: no-symbol does not have owner
     *        at scala.tools.nsc.symtab.Symbols$NoSymbol$.owner(Symbols.scala:1565)
     * We should bypass it via symbolQualifiedName
     */
    def symbolQualifiedName(symbol: Symbol, forScala: Boolean): String = {
      if (symbol.isError) {
        "<error>"
      } else if (symbol == NoSymbol) {
        "<none>"
      } else {
        var paths: List[String] = Nil
        var owner = symbol.owner
        // remove type parameter part at the beginnig, for example: scala.Array[T0] will be: scala.Array.T0
        if (!symbol.isTypeParameterOrSkolem) {
          paths = symbol.nameString :: paths
        }
        while (!owner.nameString.equals("<none>") && !owner.nameString.equals("<root>")) {
          if (!symbol.isTypeParameterOrSkolem) {
            paths = owner.nameString :: paths
          }
          owner = owner.owner
        }

        paths.reverse
        val sb = paths.mkString(".")

        if (sb.length == 0) {
          if (symbol.isPackage) {
            ""
          } else {
            if (forScala) symbol.nameString else "Object" // it maybe a TypeParameter likes: T0
          }
        } else sb
      }
    }

    def typeQualifiedName(tpe: Type, forScala: Boolean): String = {
      symbolQualifiedName(tpe.typeSymbol, forScala);
    }

    def isInherited(template: Symbol, member: Symbol): Boolean = {
      !symbolQualifiedName(template).equals(symbolQualifiedName(member.enclClass))
    }

    def typeToString(tpe: Type): String = {
      val str = try {
        tpe.toString
      } catch {
        case ex: _root_.java.lang.AssertionError => ScalaGlobal.reset(self); null // ignore assert ex from scala
        case ex: Throwable => ScalaGlobal.reset(self); null
      }

      if (str != null) str else tpe.termSymbol.nameString
    }
  }
}
