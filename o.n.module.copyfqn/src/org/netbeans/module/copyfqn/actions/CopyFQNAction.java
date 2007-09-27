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

package org.netbeans.module.copyfqn.actions;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.editor.Registry;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;

/**
 * This action copies the fully qualified name of the Java Class under the caret
 * or the Java Class of selected node in the Projects/Files windows to the system
 * clipboard.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class CopyFQNAction extends CookieAction {
    private Clipboard clipboard;
    
    public CopyFQNAction() {
        clipboard = Lookup.getDefault().lookup(ExClipboard.class);
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
    }
    
    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes.length > 0) {
            setClipboardContents((String) null);
            
            // Get data object
            DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
            if (dataObject != null) {
                
                // Get file object
                final FileObject fileObject = dataObject.getPrimaryFile();
                if (fileObject != null) {
                    
                    // Get JavaSource
                    JavaSource javaSource = JavaSource.forFileObject(fileObject);
                    if (javaSource == null) {
                        // may be a class file? Can we handle it?
                        Logger.getLogger(CopyFQNAction.class.getName()).log(Level.WARNING, "Not a java file " + fileObject.getPath());
                    } else {
                        try {
                            javaSource.runUserActionTask(new CancellableTask<CompilationController>() {
                                public void cancel() {}
                                public void run(CompilationController compilationController) throws IOException {
                                    // Move to resolved phase
                                    compilationController.toPhase(Phase.ELEMENTS_RESOLVED);
                                    
                                    // Get document if open
                                    Document document = compilationController.getDocument();
                                    if (document != null) {
                                        
                                        // Is the current editor fod this document
                                        JTextComponent editor = Registry.getMostActiveComponent();
                                        if (editor.getDocument() == document) {
                                            
                                            // Get Caret position
                                            int dot = editor.getCaret().getDot();
                                            
                                            // Find the TreePath for the caret position
                                            TreePath tp = compilationController.getTreeUtilities().pathFor(dot);
                                            
                                            // Get Element
                                            Element element = compilationController.getTrees().getElement(tp);
                                            
                                            if (element instanceof TypeElement) {
                                                setClipboardContents(((TypeElement) element).getQualifiedName().toString());
                                                return;
                                            } if (element instanceof VariableElement) {
                                                setClipboardContents(((VariableElement) element).asType().toString());
                                                return;
                                            } else if (element instanceof ExecutableElement) {
                                                // Method
                                                if (element.getKind() == ElementKind.METHOD) {
                                                    setClipboardContents(((ExecutableElement) element).getReturnType().toString());
                                                } else if (element.getKind() == ElementKind.CONSTRUCTOR) { // CTOR - use enclosing class name
                                                    setClipboardContents(((TypeElement)((ExecutableElement) element).getEnclosingElement()).getQualifiedName().toString());
                                                }
                                                return;
                                            }
                                        }
                                    }
                                    
                                    // Just use (preferably public) Class in the file
                                    setClipboardContents(compilationController);
                                }
                            }, true);
                        } catch (IOException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                }
            }
        }
    }
    
    private void setClipboardContents(CompilationController compilationController) {
        List<? extends Tree> typeDeclsTrees = compilationController.getCompilationUnit().getTypeDecls();
        ClassTree firstClassTree = null;
        for (Tree tree : typeDeclsTrees) {
            if (tree instanceof ClassTree) {
                ClassTree classTree = (ClassTree) tree;
                // Prefer public class
                if (classTree.getModifiers().getFlags().contains(Modifier.PUBLIC) ) {
                    setClipboardContents(compilationController, classTree);
                    return;
                }
                firstClassTree = (ClassTree) tree;
            }
        }
        if (firstClassTree != null) {
            setClipboardContents(compilationController, firstClassTree);
            return;
        }
    }
    
    private void setClipboardContents(CompilationController compilationController, ClassTree classTree) {
        Trees trees = compilationController.getTrees();
        TreePath treePath = trees.getPath(compilationController.getCompilationUnit(),
                classTree);
        if (treePath != null) {
            TypeElement typeElement = (TypeElement) trees.getElement(treePath);
            if (typeElement != null) {
                setClipboardContents(typeElement.getQualifiedName().toString());
            }
        }
    }
    
    private void setClipboardContents(String content) {
        if (clipboard != null) {
            if (content == null) {
                StatusDisplayer.getDefault().setStatusText("");
                clipboard.setContents(null, null);
            } else {
                StatusDisplayer.getDefault().setStatusText("Clipboard: " + content);
                clipboard.setContents(new StringSelection(content), null);
            }
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(CopyFQNAction.class, "CTL_CopyFQNAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
        };
    }
    
    protected String iconResource() {
        return "org/netbeans/module/copyfqn/actions/fqn.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}

