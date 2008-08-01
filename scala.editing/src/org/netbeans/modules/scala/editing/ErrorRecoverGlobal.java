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
package org.netbeans.modules.scala.editing;

import javax.swing.text.BadLocationException;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.scala.editing.ast.AstDef;
import org.netbeans.modules.scala.editing.ast.AstItem;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.ast.AstTreeVisitor;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import org.openide.util.Exceptions;
import scala.tools.nsc.CompilationUnits.CompilationUnit;
import scala.tools.nsc.Global;
import scala.tools.nsc.Settings;
import scala.tools.nsc.ast.Trees.Tree;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.util.BatchSourceFile;

/**
 *
 * @author Caoyuan Deng
 */
public class ErrorRecoverGlobal {

    private static Global global;

    private static void checkGlobal(Settings settings) {
        if (global != null && !global.settings().equals(settings)) {
            global = null;
        }

        if (global == null) {
            global = new Global(settings) {

                @Override
                public boolean onlyPresentation() {
                    return true;
                }

                @Override
                public void logError(String msg, Throwable t) {
                    //Exceptions.printStackTrace(t);
                }
            };
        }
    }

    public static Symbol resolveObject(Settings settings, ScalaParserResult pResult, BaseDocument doc, AstItem item) {
        checkGlobal(settings);
        
        doc.readLock();

        TokenSequence ts = ScalaLexUtilities.getTokenSequence(pResult.getTokenHierarchy(), 1);

        int importStart = 0;
        int importEnd = 0;
        boolean startImportSet = false;

        while (ts.isValid() && ts.moveNext()) {
            Token tk = ts.token();
            if (tk.id() == ScalaTokenId.Import) {
                int offset = ts.offset();
                if (!startImportSet) {
                    importStart = offset;
                    startImportSet = true;
                }
                importEnd = offset;
            }
        }

        doc.readUnlock();

        try {
            String itemName = item.getName();

            StringBuilder sb = new StringBuilder();

            AstDef pkgDef = item.getEnclosingScope().getEnclosingDef(org.netbeans.modules.gsf.api.ElementKind.PACKAGE);
            if (pkgDef != null) {
                Symbol packaging = pkgDef.getSymbol();
                String pkgName = packaging.fullNameString();
                if (!pkgName.equals("") && !pkgName.equals("<empty>")) {
                    sb.append("package ");
                    sb.append(packaging.fullNameString());
                    sb.append(";");
                }
            }

            importEnd = Utilities.getRowEnd(doc, importEnd);
            if (importEnd > importStart) {
                String imports = doc.getText(importStart, importEnd - importStart);
                sb.append(imports);
                sb.append("\n");
            }

            sb.append("class NetBeansErrorRecover {");
            sb.append(itemName);
            sb.append("}");

            TokenHierarchy th = TokenHierarchy.create(sb, ScalaTokenId.language());
            if (th != null) {
                String filePath = "<NetBeansErrorRecover>";
                BatchSourceFile srcFile = new BatchSourceFile(filePath, sb.toString().toCharArray());

                CompilationUnit unit = ScalaGlobal.compileSource(global, srcFile);
                if (unit != null) {
                    final Tree tree = unit.body();
                    AstRootScope root = new AstTreeVisitor(tree, th, srcFile).getRootScope();
                    AstItem found = root.findFirstItemWithName(itemName);
                    if (found != null) {
                        return found.getSymbol();
                    }
                }
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    public static Symbol resolvePackage(Settings settings, ScalaParserResult pResult, BaseDocument doc, String pkgQName) {
        checkGlobal(settings);

        StringBuilder sb = new StringBuilder();
        sb.append("package ");
        sb.append(pkgQName);
        sb.append(";");

        TokenHierarchy th = TokenHierarchy.create(sb, ScalaTokenId.language());
        if (th != null) {
            String filePath = "<NetBeansErrorRecover>";
            BatchSourceFile srcFile = new BatchSourceFile(filePath, sb.toString().toCharArray());

            CompilationUnit unit = ScalaGlobal.compileSource(global, srcFile);
            if (unit != null) {
                final Tree tree = unit.body();
                AstRootScope root = new AstTreeVisitor(tree, th, srcFile).getRootScope();
                
                int lastDot = pkgQName.lastIndexOf('.');
                String lastPath = lastDot == -1 ? pkgQName : pkgQName.substring(lastDot + 1, pkgQName.length());
                
                AstItem found = root.findFirstItemWithName(lastPath);
                if (found != null) {
                    return found.getSymbol();
                }
            }
        }

        return null;
    }
}
