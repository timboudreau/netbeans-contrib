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

package org.netbeans.modules.declarationandjavadoc;

import com.sun.javadoc.Doc;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.swing.SwingUtilities;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.openide.filesystems.FileObject;

/**
 * This task is called every time the caret position changes in a Java editor.
 * <p>
 * The task finds the TreePath of the Tree under the caret, converts it to
 * an Element and then shows the declartion of the element in Declaration window
 * and javadoc in the Javadoc window.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class DeclarationAndJavadocTask implements CancellableTask<CompilationInfo> {
    
    private DeclarationAndJavadocJavaSourceTaskFactory declarationAndJavadocJavaSourceTaskFactory;
    private FileObject fileObject;
    private boolean canceled;
    
    
    DeclarationAndJavadocTask(DeclarationAndJavadocJavaSourceTaskFactory whichElementJavaSourceTaskFactory,FileObject fileObject) {
        this.declarationAndJavadocJavaSourceTaskFactory = whichElementJavaSourceTaskFactory;
        this.fileObject = fileObject;
    }
    
    private static final Collection<Modifier> NO_MODIFIERS = Collections.<Modifier>emptySet();
    
    public void run(CompilationInfo compilationInfo) {
        
        String declartion = "";
        String javadoc = "";
        
        setDeclaration(declartion);
        setJavadoc("", javadoc);
        
        // Find the TreePath for the caret position
        TreePath tp =
                compilationInfo.getTreeUtilities().pathFor(declarationAndJavadocJavaSourceTaskFactory.getLastPosition(fileObject));
        
        // if cancelled, return
        if (isCancelled()) {
            return;
        }
        
        // Get Element
        Element element = compilationInfo.getTrees().getElement(tp);
        
        // if cancelled, return
        if (isCancelled()) {
            return;
        }
        
        if (element != null) {
            if (element instanceof PackageElement) {
                setDeclaration("package " + element.toString() + ";");
            } else {
                Doc doc = compilationInfo.getElementUtilities().javaDocFor(element);
                if (doc != null) {
                    setJavadoc(element.toString(), doc.getRawCommentText());
                }
                Tree tree = compilationInfo.getTrees().getTree(element);
                if (tree == null) {
                    FileObject fileObject = SourceUtils.getFile(element, compilationInfo.getClasspathInfo());
                    if (fileObject != null) {
                        switch (element.getKind()) {
                        case PACKAGE:
                        case CLASS:
                        case INTERFACE:
                        case ENUM:
                        case METHOD:
                        case CONSTRUCTOR:
                        case INSTANCE_INIT:
                        case STATIC_INIT:
                        case FIELD:
                        case ENUM_CONSTANT:
                            final ElementHandle elementHandle = ElementHandle.create(element);
                            JavaSource javaSource = JavaSource.forFileObject(fileObject);
                            try {
                                javaSource.runUserActionTask(new CancellableTask<CompilationController>() {
                                    public void cancel() {}
                                    public void run(CompilationController compilationController) throws IOException {
                                        // Move to resolved phase
                                        compilationController.toPhase(Phase.ELEMENTS_RESOLVED);
                                        Element element = elementHandle.resolve(compilationController);
                                        if (element != null) {
                                            Tree tree = compilationController.getTrees().getTree(element);
                                            if (tree != null) {
                                                String declaration = tree.toString();
                                                if (element.getKind() ==  ElementKind.CONSTRUCTOR) {
                                                    String constructorName = element.getEnclosingElement().getSimpleName().toString();
                                                    declaration = declaration.replaceAll(Pattern.quote("<init>"), Matcher.quoteReplacement(constructorName));
                                                }
                                                setDeclaration(declaration);
                                            }
                                        }
                                    }
                                }, true);
                            } catch (IOException ex) {
                                Logger.global.log(Level.WARNING, ex.getMessage(), ex);;
                            }
                            break;
                        }
                    }
                } else {
                    String declaration = tree.toString();
                    if (element.getKind() ==  ElementKind.CONSTRUCTOR) {
                        String constructorName = element.getEnclosingElement().getSimpleName().toString();
                        declaration = declaration.replaceAll(Pattern.quote("<init>"), Matcher.quoteReplacement(constructorName));
                    }                    
                    setDeclaration(declaration);
                }
            }
        }
    }
    
    private void setDeclaration(final String declaration) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DeclarationTopComponent declarationTopComponent = DeclarationTopComponent.findInstance();
                if (declarationTopComponent != null && declarationTopComponent.isOpened()) {
                    declarationTopComponent.setDeclaration(declaration);
                }
            }
        });
    }
    
    private void setJavadoc(final String header, final String javadoc) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JavadocTopComponent javadocTopComponent = JavadocTopComponent.findInstance();
                if (javadocTopComponent != null && javadocTopComponent.isOpened()) {
                    javadocTopComponent.setJavadoc(header, javadoc);
                }
            }
        });
    }
    
    /**
     * After this method is called the task if running should exit the run
     * method immediately.
     */
    public final synchronized void cancel() {
        canceled = true;
    }
    
    protected final synchronized boolean isCancelled() {
        return canceled;
    }
}
