/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing.ast;

import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Types.CompoundType;
import scala.tools.nsc.symtab.Types.ConstantType;
import scala.tools.nsc.symtab.Types.MethodType;
import scala.tools.nsc.symtab.Types.OverloadedType;
import scala.tools.nsc.symtab.Types.PolyType;
import scala.tools.nsc.symtab.Types.SingletonType;
import scala.tools.nsc.symtab.Types.ThisType;
import scala.tools.nsc.symtab.Types.Type;
import scala.tools.nsc.symtab.Types.TypeRef;

/**
 *
 * @author Caoyuan Deng
 */
public class ElementFormatter {

    StringBuilder sb = new StringBuilder();

    public void printType(Type tpe) {
        printType0(tpe);
        if (tpe instanceof ThisType) {
            sb.append(".type");
        } else if (tpe instanceof SingletonType) {
            sb.append(".type");
        }
    }

    public void printTypes(scala.List tpes, String begin, String infix, String end) {
        if (!tpes.isEmpty()) {
            printTypes0(tpes, begin, infix, end);
        }
    }

    private void printType0(Type tpe) {
        Symbol sym = tpe.typeSymbol();

        if (tpe instanceof ThisType) {
//          if (sym instanceof NoSymbol) {
//              sb.append("this");
//          } else {
//              sb.append(sym.nameString()).append(".this");
//          }
        } else if (tpe instanceof SingletonType) {
            sb.append(sym.nameString());
        } else if (tpe instanceof TypeRef) {
            TypeRef tpe1 = (TypeRef) tpe;
            sb.append(sym.nameString());
            printTypes(tpe1.args(), "[", ",", "]");
        } else if (tpe instanceof CompoundType) {
            CompoundType tpe1 = (CompoundType) tpe;
            printTypes(tpe1.baseClasses(), "", " with", "");
        } else if (tpe instanceof MethodType) {
            Type tpe1 = tpe;
            while (tpe1 instanceof MethodType) {
                //printParameterTypes(tpe1.paramTypes(), "(", ", ", ")", true);
                tpe1 = tpe1.resultType();
            }
            sb.append(": ");
            printType(tpe1);
        } else if (tpe instanceof PolyType) {
            PolyType tpe1 = (PolyType) tpe;
            sb.append("[");
            scala.List tvars = tpe1.typeParams();
            if (!tvars.isEmpty()) {
                //printTVar(tvars.head());
//              for (Symbol tvar : tvars.tail()) {
//                  sb.append(", ");
//                  printTVar(tvar);
//              }
            }
            sb.append("]");
        } else if (tpe instanceof OverloadedType) {
        } else if (tpe instanceof ConstantType) {
            ConstantType tpe1 = (ConstantType) tpe;
            printType(tpe1.baseType(sym));
            print("(");
        } else {
            print("unknown");
        }


//    case ConstantType(base, num) =>
//      printType(base)
//      print("(" + num + ")")
//    case TypeFlag(TypeRef(_, _, List(tpe0)), flags) =>
//      if (Flags.is(Flags.TF_DEF, flags))
//        print("def ")
//      printType(tpe0)
//      if (Flags.is(Flags.TF_STAR, flags))
//        print("*")
//    case TypeFlag(tpe0, flags) =>
//      if (Flags.is(Flags.TF_DEF, flags))
//        print("def ")
//      printType(tpe0)
//    case _ =>
//      print("<unknown type>")
    }

    private StringBuilder printTypes0(scala.List tpes, String begin, String infix, String end) {
        print(begin);
        if (!tpes.isEmpty()) {
//      printType(tpes.head());
            //tpes.tail foreach (t => { print(infix); printType(t) })
        }
        print(end);
        return sb;
    }

    private StringBuilder print(String str) {
        sb.append(str);
        return sb;
    }

    private StringBuilder printParameterType(Type tpe, boolean basic) {
        if (tpe instanceof TypeRef) {
            TypeRef tpe1 = (TypeRef) tpe;
            SingletonType sType = (SingletonType) tpe1.pre();
            Symbol sym = tpe1.sym();
            scala.List args = tpe1.args();
            Symbol root = null;
            Symbol top = null;
            //TypeRef(SingletonType(ThisType(root), top), sym, args) = >
            if ((root.nameString().equals("<root>") || root.nameString().equals("")) &&
                    top.nameString().equals("scala") &&
                    sym.nameString().startsWith("Function")) {
                if ((args.length() == 2) && !isFunctionType((Type) args.head())) {
                    printType((Type) args.head());
                    print(" => ");
                    printParameterType((Type) args.tail().head(), basic);
                } else {
                    printParameterTypes((scala.List) args.take(args.length() - 1), "(", ", ", ")", basic);
                    print(" => ");
                    printParameterType((Type) args.last(), basic);
                }
            } else if (basic) {
                printType0(tpe);
            } else {
                printType(tpe);
            }
        } else {
            if (basic) {
                printType0(tpe);
            } else {
                printType(tpe);
            }
        }
        return sb;
    }

    private StringBuilder printParameterTypes(scala.List tpes, String begin, String infix, String end, boolean basic) {
        print(begin);
        if (!tpes.isEmpty()) {
            printParameterType((Type) tpes.head(), basic);
            scala.List tail = tpes.tail();
            for (int i = 0; i < tail.length(); i++) {
                Type t = (Type) tail.apply(i);
                print(infix);
                printParameterType(t, basic);
            }
        }

        print(end);
        return sb;
    }
    
    
//      private StringBuilder printTVar(Symbol tvar) {
//    if (tvar instanceof TypeSymbol) {
//        TypeSymbol sym = (TypeSymbol) tvar;
//        if (sym.isCovariant()) {
//            print("+" + sym.nameString());
//        } else if (sym.isContravariant()) {
//            print("-" + sym.nameString());
//        } else {
//            print(sym.nameString());
//        }
//      if (!isExternalType(sym.tpe(), "Any")) {
//        if (Flags.is(Flags.VIEWBOUND, sym.flags))
//          print(" <% ");
//        else
//          print(" <: ");
//            printType(sym.tpe());
//      }
//      if (!isExternalType(sym.lower, "Nothing")) {
//        print(" >: ")
//        printType(sym.lower)
//      }
//        return sb;
//    }
//
//  private boolean isExternalType(Type tpe, String name) {
//    case TypeRef(SingletonType(ThisType(root), pck), sym, Nil) =>
//      (root.name.equals("<root>") &&
//      pck.name.equals("scala") &&
//      sym.name.equals(name))
//    case _ => false
//  }

    private boolean isFunctionType(Type tpe) {
        if (tpe instanceof TypeRef) {
            TypeRef tpe1 = (TypeRef) tpe;
            SingletonType sType = (SingletonType) tpe1.pre();
            Symbol sym = tpe1.sym();
            scala.List args = tpe1.args();
            Symbol root = null;
            Symbol top = null;
            if ((root.nameString().equals("<root>") || root.nameString().equals("")) &&
                    top.nameString().equals("scala") &&
                    sym.nameString().startsWith("Function")) {
                return true;
            }
        }

        return false;
    }
}
