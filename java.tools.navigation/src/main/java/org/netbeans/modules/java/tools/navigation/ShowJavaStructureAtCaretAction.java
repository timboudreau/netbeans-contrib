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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.java.tools.navigation;

import com.sun.source.util.TreePath;

import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;

import org.netbeans.editor.Registry;

import org.openide.cookies.EditorCookie;

import org.openide.filesystems.FileObject;

import org.openide.loaders.DataObject;

import org.openide.nodes.Node;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ShowJavaStructureAtCaretAction
    extends AbstractNavigationAction {
    protected void performAction(Node[] activatedNodes) {
        EditorCookie editorCookie = (EditorCookie) activatedNodes[0].getLookup()
                                                                    .lookup(EditorCookie.class);

        if (editorCookie == null) {
            return;
        }

        // Get data object
        DataObject dataObject = (DataObject) activatedNodes[0].getLookup()
                                                              .lookup(DataObject.class);

        if (dataObject != null) {
            // Get file object
            final FileObject fileObject = dataObject.getPrimaryFile();

            if (fileObject != null) {
                // Get JavaSource
                JavaSource javaSource = JavaSource.forFileObject(fileObject);

                if (javaSource == null) {
                    // may be a class file? Can we handle it?
                    Logger.global.log(Level.WARNING,
                        "Not a java file " + fileObject.getPath());
                } else {
                    try {
                        javaSource.runUserActionTask(new CancellableTask<CompilationController>() {
                                public void cancel() {
                                }

                                public void run(
                                    CompilationController compilationController)
                                    throws IOException {
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
                                            TreePath tp = compilationController.getTreeUtilities()
                                                                               .pathFor(dot);

                                            // Get Element
                                            Element element = compilationController.getTrees()
                                                                                   .getElement(tp);

                                            if (element instanceof TypeElement) {
                                                FileObject elementFileObject = SourceUtils.getFile(element,
                                                        compilationController.getClasspathInfo());

                                                if (elementFileObject != null) {
                                                    JavaStructure.show(elementFileObject);
                                                }

                                                return;
                                            }

                                            if (element instanceof VariableElement) {
                                                TypeMirror typeMirror = ((VariableElement) element).asType();

                                                if (typeMirror.getKind() == TypeKind.DECLARED) {
                                                    element = ((DeclaredType) typeMirror).asElement();

                                                    if (element != null) {
                                                        FileObject elementFileObject =
                                                            SourceUtils.getFile(element,
                                                                compilationController.getClasspathInfo());

                                                        if (elementFileObject != null) {
                                                            JavaStructure.show(elementFileObject);
                                                        }
                                                    }
                                                }

                                                return;
                                            } else if (element instanceof ExecutableElement) {
                                                // Method
                                                if (element.getKind() == ElementKind.METHOD) {
                                                    TypeMirror typeMirror = ((ExecutableElement) element).getReturnType();

                                                    if (typeMirror.getKind() == TypeKind.DECLARED) {
                                                        element = ((DeclaredType) typeMirror).asElement();

                                                        if (element != null) {
                                                            FileObject elementFileObject =
                                                                SourceUtils.getFile(element,
                                                                    compilationController.getClasspathInfo());

                                                            if (elementFileObject != null) {
                                                                JavaStructure.show(elementFileObject);
                                                            }
                                                        }
                                                    }

                                                    return;
                                                } else if (element.getKind() == ElementKind.CONSTRUCTOR) { // CTOR - use enclosing class name
                                                    element = element.getEnclosingElement();

                                                    if (element != null) {
                                                        FileObject elementFileObject =
                                                            SourceUtils.getFile(element,
                                                                compilationController.getClasspathInfo());

                                                        if (elementFileObject != null) {
                                                            JavaStructure.show(elementFileObject);
                                                        }
                                                    }
                                                }

                                                return;
                                            }
                                        }
                                    }
                                }
                            }, true);
                    } catch (IOException e) {
                        Logger.global.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(ShowJavaStructureAtCaretAction.class,
            "CTL_ShowJavaStructureAtCaretAction");
    }

    protected Class[] cookieClasses() {
        return new Class[] { EditorCookie.class };
    }

    protected String iconResource() {
        return "org/netbeans/modules/java/tools/navigation/resources/structureatcaret.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
