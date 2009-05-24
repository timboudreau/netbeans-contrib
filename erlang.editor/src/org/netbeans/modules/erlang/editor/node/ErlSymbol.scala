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

package org.netbeans.modules.erlang.editor.node

import org.netbeans.modules.erlang.editor.ast.{AstSym}

abstract class ErlSymbol extends AstSym
object ErlSymbol {
   val NO_TYPE = "<notype>"
    
   case class ErlTerm(name:String) extends ErlSymbol

   case class ErlFunction(var in:Option[String], var name:String, var arity:Int) extends ErlSymbol {
      var returnType :String = NO_TYPE
      var argTypes :List[String] = Nil
   }
   case class ErlModule(name:String) extends ErlSymbol
   case class ErlInclude(isLib:Boolean, path:String) extends ErlSymbol
   case class ErlExport(functions:List[ErlFunction]) extends ErlSymbol
   case class ErlRecord(name:String, fields:Seq[ErlRecordField]) extends ErlSymbol
   case class ErlRecordField(name:String, field:String) extends ErlSymbol
   case class ErlMacro(name:String, params:Seq[String], var body:String) extends ErlSymbol

   def symbolEquals(sym1:AstSym, sym2:AstSym) = {
      (sym1, sym2) match {
         case (ErlRecord(name1, _), ErlRecord(name2, _))
            if name1 == name2 => true
         case (ErlRecordField(name1, field1), ErlRecordField(name2, field2))
            if name1 == name2 && field1 == field2 => true
         case _ => sym1 == sym2
      }
   }
}