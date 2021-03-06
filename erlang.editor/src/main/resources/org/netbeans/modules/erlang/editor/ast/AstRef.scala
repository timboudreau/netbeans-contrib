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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor.ast

import org.netbeans.api.lexer.{Token,TokenId}
import org.netbeans.modules.csl.api.ElementKind

/**
 * Mirror with AstDfn information
 * 
 * Represent usage/reference of an AstDfn
 * 
 * @author Caoyuan Deng
 */
class AstRef(_idToken:Option[Token[TokenId]], _kind:ElementKind) extends AstItem with LanguageAstRef {
  make(_idToken, _kind)

  def this(idToken:Option[Token[TokenId]]) = this(idToken, ElementKind.OTHER)
    
  override def getKind :ElementKind = super.getKind match {
    // if it's a OTHER, we could try to get its kind from its dfn
    case kindX@ElementKind.OTHER => enclosingScope match {
        case None =>  kindX
        case Some(scope) => scope.findDfnOf(this) match {
            case None => kindX
            case Some(dfn) => dfn.getKind
          }
      }
    case kindX => kindX
  }

  override def toString = {
    "Ref: " + name + " (idToken=" + super.idToken + ")"
  }
}

trait LanguageAstRef {self:AstRef =>
  import ElementKind._
  import org.netbeans.modules.erlang.editor.node.ErlSymbol._

  def isOccurrence(ref:AstRef) :Boolean = ref.getKind match {
    case CALL if self.getKind == CALL => (symbol, ref.symbol) match {
        case (ErlFunction(Some(inX), nameX, arityX), ErlFunction(Some(inY), nameY, arityY))
          if inX == inY && nameX == nameY && arityX == arityY => true
        case _ => false
      }
    case _ =>
      if (ref.name == name) {
        true
      } else false
  }
}
