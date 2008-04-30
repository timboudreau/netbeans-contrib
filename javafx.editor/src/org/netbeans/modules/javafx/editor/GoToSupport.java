/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.javafx.editor;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javafx.code.JavafxTypes;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.api.javafx.source.JavaFXSource;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.javafx.source.Task;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.NbDocument;

/**
 *
 * @author Petr Nejedly
 */
public class GoToSupport {
    
    /** Static utility class */
    private GoToSupport() {
    }

    public static void goTo(Document doc, int offset, boolean goToSource) {
        performGoTo(doc, offset, goToSource, false, false);
    }

    public static String getGoToElementTooltip(Document doc, final int offset, final boolean goToSource) {
        return performGoTo(doc, offset, goToSource, true, false);
    }

    private static FileObject getFileObject(Document doc) {
        DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
        return od != null ? od.getPrimaryFile() : null;
    }

    private static String performGoTo(final Document doc, final int off, final boolean goToSource, final boolean tooltip, final boolean javadoc) {
        try {
            final FileObject fo = getFileObject(doc);
            
            if (fo == null)
                return null;
            
            JavaFXSource js = JavaFXSource.forFileObject(fo);
            
            if (js == null)
                return null;
            
            final String[] result = new String[1];
            
            js.runUserActionTask(new Task<CompilationController>() {
                public void run(CompilationController controller) throws Exception {
                    if (controller.toPhase(Phase.ANALYZED).lessThan(Phase.ANALYZED))
                        return;

                    Token<JFXTokenId>[] token = new Token[1];
                    int[] span = getIdentifierSpan(doc, off, token);

                    if (span == null) {
//                        CALLER.beep(goToSource, javadoc);
System.err.println("not an identifier");
                        return ;
                    }
                    
                    final int offset = span[0] + 1;
                    TreePath path = controller.getTreeUtilities().pathFor(offset);
                    
                    Tree leaf = path.getLeaf();
                    if (leaf == null) return;
//                    System.err.println("leaf=" + leaf);

                    Element el = controller.getTrees().getElement(path);
//                    System.err.println("el=" + el);
                    if (el == null) return;
 
                    if (tooltip) {
                        result[0] = getElementTooltip(el);
                        return;
                    } else {
                        if (goToSource && el instanceof VariableElement) {
                            Symbol sym = (Symbol)el;
                            Type type = sym.asType();
                            
                            // handle sequences as their element type
                            JavafxTypes types = controller.getJavafxTypes();
                            if (types.isSequence(type)) {
                                type = types.elementType(type);
                            }
                            
                            if (type != null && type.getKind() == TypeKind.DECLARED) {
                                el = ((DeclaredType)type).asElement();
                                
                                if (el == null) return;
                            }
                        }
                        
                        TreePath elpath = controller.getPath(el);
                        Tree tree = elpath != null && path.getCompilationUnit() == elpath.getCompilationUnit()? elpath.getLeaf(): null;
                        
                        if (tree != null) {
                            long startPos = controller.getTrees().getSourcePositions().getStartPosition(controller.getCompilationUnit(), tree);
                            
                            if (startPos != -1l) doOpen(fo, (int)startPos);
                        }                        
                    }
                }
            }, true);
            
            return result[0];
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
    
    
    private static String getElementTooltip (Element elem) {
        if (elem instanceof VariableElement) {
            String ret = "";
            VariableElement var = (VariableElement)elem;
            ElementKind kind = var.getKind();
            if (kind == ElementKind.FIELD) ret = ret + var.getEnclosingElement() + ".";
            return ret + var + " : " + var.asType();
        }
        
        if (elem instanceof TypeElement) {
            return elem.toString();
        }
        if (elem instanceof ExecutableElement) {
            ExecutableElement var = (ExecutableElement)elem;
            return "" + var.getEnclosingElement() + "." + var + " : " + var.getReturnType();
        }
        return null;
    }

    private static final Set<JFXTokenId> USABLE_TOKEN_IDS = EnumSet.of(JFXTokenId.IDENTIFIER, JFXTokenId.THIS, JFXTokenId.SUPER);

    public static int[] getIdentifierSpan(Document doc, int offset, Token<JFXTokenId>[] token) {
        if (getFileObject(doc) == null) {
            //do nothing if FO is not attached to the document - the goto would not work anyway:
            return null;
        }
        
        TokenHierarchy th = TokenHierarchy.get(doc);
        TokenSequence<JFXTokenId> ts = (TokenSequence<JFXTokenId>) th.tokenSequence();

        if (ts == null)
            return null;
        
        ts.move(offset);
        if (!ts.moveNext())
            return null;
        
        Token<JFXTokenId> t = ts.token();
        
        if (!USABLE_TOKEN_IDS.contains(t.id())) {
            ts.move(offset - 1);
            if (!ts.moveNext())
                return null;
            t = ts.token();
            if (!USABLE_TOKEN_IDS.contains(t.id()))
                return null;
        }
        
        if (token != null)
            token[0] = t;
        
        return new int [] {ts.offset(), ts.offset() + t.length()};
    }

    private static boolean doOpen(FileObject fo, int offset) {
        try {
            DataObject od = DataObject.find(fo);
            EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
            LineCookie lc = (LineCookie) od.getCookie(LineCookie.class);
            
            if (ec != null && lc != null && offset != -1) {                
                StyledDocument doc = ec.openDocument();                
                if (doc != null) {
                    int line = NbDocument.findLineNumber(doc, offset);
                    int lineOffset = NbDocument.findLineOffset(doc, line);
                    int column = offset - lineOffset;
                    
                    if (line != -1) {
                        Line l = lc.getLineSet().getCurrent(line);
                        
                        if (l != null) {
                            l.show(Line.SHOW_GOTO, column);
                            return true;
                        }
                    }
                }
            }
            
            OpenCookie oc = (OpenCookie) od.getCookie(OpenCookie.class);
            
            if (oc != null) {
                oc.open();                
                return true;
            }
        } catch (IOException e) {
        }
        
        return false;
    }
}
