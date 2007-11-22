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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.addproperty.impl;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.Collections;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.swing.text.Document;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.addproperty.api.AddPropertyGenerator;
import org.netbeans.modules.java.addproperty.ui.AddPropertyPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.Comment;
import org.netbeans.api.java.source.Comment.Style;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.java.editor.codegen.CodeGenerator;
import org.openide.loaders.DataObject;

/**
 *
 * @author lahvac
 */
public class AddPropertyCodeGenerator implements CodeGenerator {

    public AddPropertyCodeGenerator() {
    }
    
    public String getDisplayName() {
        return "Add Property...";
    }

    public void invoke(JTextComponent component) {
        Object o = component.getDocument().getProperty(Document.StreamDescriptionProperty);
        
        if (o instanceof DataObject) {
            DataObject d = (DataObject) o;
            
            perform(d.getPrimaryFile());
        }
    }

    public static void perform(FileObject file) {
        final AddPropertyPanel addPropertyPanel = new AddPropertyPanel(file);
        NotifyDescriptor d =
                new NotifyDescriptor.Confirmation(addPropertyPanel, "Add Property",
                NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.PLAIN_MESSAGE);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            try {
                JavaSource.forFileObject(file).runModificationTask(new Task<WorkingCopy>() {
                    public void run(WorkingCopy parameter) throws Exception {
                        parameter.toPhase(Phase.ELEMENTS_RESOLVED);

                        String code = AddPropertyGenerator.getDefault().generate(addPropertyPanel.getAddPropertyConfig());
                        TokenSequence<JavaTokenId> ts = TokenHierarchy.create(code, JavaTokenId.language()).tokenSequence(JavaTokenId.language());
                        SourcePositions[] positions = new SourcePositions[1];
                        String prefix = "new Object() {";
                        Tree t = parameter.getTreeUtilities().parseExpression(prefix + code + "}", positions);
                        
                        assert t != null && t.getKind() == Kind.NEW_CLASS;

                        ClassTree orig = (ClassTree) parameter.getCompilationUnit().getTypeDecls().get(0);
                        ClassTree ct = orig;
                        TreePath path = new TreePath(new TreePath(parameter.getCompilationUnit()), orig);
                        ImportFQNsHack h = new ImportFQNsHack(parameter);

                        for (Tree member : ((NewClassTree) t).getClassBody().getMembers()) {
                            ct = parameter.getTreeMaker().addClassMember(ct, member);

                            h.scan(new TreePath(path, member), null);
                            
                            //attach the javadoc comment:
                            ts.move((int) positions[0].getStartPosition(null, member) - prefix.length());
                            
                            boolean movePreviousPassed;
                            
                            while ((movePreviousPassed = ts.movePrevious()) && ts.token().id() == JavaTokenId.WHITESPACE)
                                ;
                            
                            if (movePreviousPassed && ts.token().id() == JavaTokenId.JAVADOC_COMMENT) {
                                parameter.getTreeMaker().addComment(member, Comment.create(Style.JAVADOC, -1, -1, -1, ts.token().text().toString()), true);
                            }
                        }

                        parameter.rewrite(orig, ct);
                    }
                }).commit();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private static final class ImportFQNsHack extends TreePathScanner<String, Void> {
        
        private WorkingCopy wc;

        public ImportFQNsHack(WorkingCopy wc) {
            this.wc = wc;
        }

        @Override
        public String visitIdentifier(IdentifierTree node, Void p) {
            return node.getName().toString();
        }

        @Override
        public String visitMemberSelect(MemberSelectTree node, Void p) {
            String parents = scan(node.getExpression(), p);
            
            if (parents == null) {
                return null;
            }
            
            String current = parents + '.' + node.getIdentifier().toString();
            
            if (getCurrentPath().getParentPath().getLeaf().getKind() != Kind.MEMBER_SELECT) {
                TypeElement e = wc.getElements().getTypeElement(current);
                
                if (e == null) {
                    e = wc.getElements().getTypeElement(parents);
                    
                    if (e != null) {
                        wc.rewrite(node.getExpression(), wc.getTreeMaker().QualIdent(e));
                    }
                } else {
                    wc.rewrite(node, wc.getTreeMaker().QualIdent(e));
                }
                return null;
            } else {
                TypeElement e = wc.getElements().getTypeElement(current);

                if (e != null) {
                    return current;
                }
                
                PackageElement pack = wc.getElements().getPackageElement(current);
                
                if (pack != null) {
                    return current;
                }
                
                
                e = wc.getElements().getTypeElement(parents);

                if (e != null) {
                    wc.rewrite(node.getExpression(), wc.getTreeMaker().QualIdent(e));
                }
                return null;
            }
        }
        
    }

    public static final class Factory implements CodeGenerator.Factory {

        public Iterable<? extends CodeGenerator> create(CompilationController cc, TreePath path) throws IOException {
            while (path != null && path.getLeaf().getKind() != Kind.CLASS) {
                path = path.getParentPath();
            }
            
            if (path == null) {
                return Collections.emptyList();
            }
            
            return Collections.singleton(new AddPropertyCodeGenerator());
        }
        
    }
}
