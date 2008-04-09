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

package org.netbeans.api.javafx.source;

import com.sun.javafx.api.tree.JavaFXTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javafx.api.JavafxcTrees;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.project.libraries.LibraryManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

/**
 *
 * @author answer
 */
public class JavaFXSourceUtils {

    public static boolean isJavaFXApplet(final FileObject file) {
        if (file == null) {
            return false;
        }
        System.setProperty("env.class.path", getAdditionalCP(System.getProperty("env.class.path")));
        
        JavaFXSource js = JavaFXSource.forFileObject(file);
        if (js == null) {
            return false;
        }
        final boolean[] result = new boolean[] {false};
        try {
            js.runUserActionTask(new CancellableTask<CompilationController>() {
                
                public void run(CompilationController control) throws Exception {
                    if (JavaFXSource.Phase.ELEMENTS_RESOLVED.compareTo(control.toPhase(JavaFXSource.Phase.ELEMENTS_RESOLVED))<=0) {
                        control.impl.getJavafxcTask().analyze();
                        
                        Elements elements = control.getElements();
                        JavafxcTrees trees = control.getTrees();
                        Types types = control.getTypes();
                        TypeElement fxapplet = elements.getTypeElement("javafx.ui.Applet");     //NOI18N
                        TypeElement applet = elements.getTypeElement("java.applet.Applet");     //NOI18N
                        TypeElement japplet = elements.getTypeElement("javax.swing.JApplet");   //NOI18N
                        CompilationUnitTree cu = control.getCompilationUnit();
                        List<? extends Tree> topLevels = cu.getTypeDecls();
                        for (Tree topLevel : topLevels) {
                            if (((JavaFXTree)topLevel).getJavaFXKind() == JavaFXTree.JavaFXKind.CLASS_DECLARATION) {
                                TypeElement type = (TypeElement) trees.getElement(TreePath.getPath(cu, topLevel));
                                if (type != null) {
                                    Set<Modifier> modifiers = type.getModifiers();
                                    if (modifiers.contains(Modifier.PUBLIC) && 
                                        ((applet != null && types.isSubtype(type.asType(), applet.asType())) 
                                        || (fxapplet != null && types.isSubtype(type.asType(), fxapplet.asType()))
                                        || (japplet != null && types.isSubtype(type.asType(), japplet.asType())))) {
                                            result[0] = true;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                public void cancel() {}
            }, true);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        return result[0];

    }    
    
    public static String getAdditionalCP(String cp) {
        LibraryManager lm = LibraryManager.getDefault();
        List<URL> libs = lm.getLibrary("JavaFXUserLib").getContent("classpath");
        for (int i = 0; i < libs.size(); i++) {
            FileObject fo = URLMapper.findFileObject(libs.get(i));
            String addPath = null;
            try {
                addPath = fo.getURL().getFile();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
            addPath = addPath.substring(6, addPath.length()-2);
            if (cp != null) {
                if (!cp.contains(addPath))
                    cp += File.pathSeparatorChar + addPath;
            } else {
                cp = addPath;
            }
        }
        return cp;
    }
    
}
