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
package org.netbeans.modules.scala.editor.ast

import _root_.java.lang.annotation.Annotation
import _root_.javax.lang.model.element.{AnnotationMirror, Element, ElementKind,
                                        ElementVisitor, ExecutableElement, VariableElement}
import org.netbeans.api.lexer.{Token, TokenHierarchy}
import org.netbeans.api.language.util.ast.{AstScope, AstItem}
import org.netbeans.modules.csl.api.{HtmlFormatter, OffsetRange}

/**
 * Element with AstNode information
 * 
 * Represents a program element such as a package, class, or method. Each element 
 * represents a static, language-level construct (and not, for example, a runtime 
 * construct of the virtual machine). 
 * 
 * @author Caoyuan Deng
 */
object JavaElement {
  /*_
   def isMirroredBy(element:Element, mirror:AstMirror): Boolean = {
   if (element.isInstanceOf[ExecutableElement] && mirror.isInstanceOf[FunctionCall]) {
   val function = element.asInstanceOf[ExecutableElement]
   val funCall = mirror.asInstanceOf[FunctionCall]
   val params = function.getParameters
   // only check local call only
   if (funCall.isLocal) {
   return element.getSimpleName.toString.equals(funCall.getCall().getSimpleName().toString()) &&
   params != null &&
   params.size == funCall.getArgs.size
   } else {
   val containsVariableLengthArg = Function.isVarArgs(function)
   if (element.getSimpleName.toString.equals(funCall.getCall().getSimpleName().toString()) || element.getSimpleName.toString.equals("apply") && funCall.isLocal) {
   if (params.size == funCall.getArgs.size || containsVariableLengthArg) {
   return true
   }
   }

   return false
   }
   } else if (element.isInstanceOf[VariableElement]) {
   if (element.getSimpleName.toString.equals(mirror.getSimpleName.toString)) {
   return true
   }
   }

   false
   }
   */
}

abstract class JavaElement(name: CharSequence,
                           pickToken: Token[_],
                           var bindingScope: AstScope,
                           var kind: ElementKind
) extends Element {

  if (bindingScope != null) {
    this.bindingScope = bindingScope
    //this.bindingScope.bindingDfn = this
  }

  def accept[R, P](arg0: ElementVisitor[R, P], arg1: P): R = {
    arg0.visit(this, arg1)
  }

  //  def getEnclosedElements: List[_ <: JavaElement] = {
  //    if (bindingScope != null) {
  //      bindingScope.getElements
  //    } else {
  //      _root_.java.util.Collections.emptyList[JavaElement]
  //    }
  //  }

  //  def getEnclosingElement: JavaElement = {
  //    getEnclosingScope.bindingElement
  //  }

  def getAnnotation[A <: Annotation](arg0: Class[A]): A = {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  def getAnnotationMirrors: _root_.java.util.List[_ <: AnnotationMirror] = {
    throw new UnsupportedOperationException("Not supported yet.")
  }

  def setKind(kind: ElementKind): Unit = {
    this.kind = kind
  }

  def getKind: ElementKind = {
    kind
  }

  override def toString = {
    getSimpleName + "(kind=" + getKind + ", type=" + asType + ")"
  }

  def getBindingScope: AstScope = {
    assert(bindingScope != null , toString + ": Each definition should set binding scope!")
    bindingScope
  }

  def getPickOffset(th: TokenHierarchy[_]): Int = {
    pickToken.offset(th)
  }

  def getBoundsOffset(th: TokenHierarchy[_]): Int = {
    getBindingScope.boundsOffset(th)
  }

  def getBoundsEndOffset(th: TokenHierarchy[_]): Int = {
    return getBindingScope.boundsEndOffset(th)
  }

  def getRange(th: TokenHierarchy[_]): OffsetRange = {
    getBindingScope.range(th)
  }

  //  def isMirroredBy(mirror:AstMirror): Boolean = {
  //    getSimpleName.toString.equals(mirror.getSimpleName.toString)
  //  }

  def mayEquals(element: JavaElement): Boolean = {
    getSimpleName.toString.equals(element.getSimpleName.toString)
  }

  def htmlFormat(formatter: HtmlFormatter): Unit = {
    formatter.appendText(getSimpleName.toString)
  }
}
