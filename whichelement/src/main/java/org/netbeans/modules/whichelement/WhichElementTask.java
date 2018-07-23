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

package org.netbeans.modules.whichelement;

import com.sun.source.util.TreePath;
import java.util.Collection;
import java.util.Collections;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.swing.Icon;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.UiUtils;
import org.openide.awt.StatusLineElementProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 * This task is called every time the caret position changes in a Java editor.
 * <p>
 * The task finds the TreePath of the Tree under the caret, converts it to
 * an Element and then shows the information about the element in a text
 * field in the IDE status bar.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class WhichElementTask implements CancellableTask<CompilationInfo> {
    
    private WhichElementJavaSourceTaskFactory whichElementJavaSourceTaskFactory;
    private FileObject fileObject;
    private boolean canceled;
    WhichElementStatusElementProvider.WhichElementPanel whichElementPanel;
    
    WhichElementTask(WhichElementJavaSourceTaskFactory whichElementJavaSourceTaskFactory,FileObject fileObject) {
        this.whichElementJavaSourceTaskFactory = whichElementJavaSourceTaskFactory;
        this.fileObject = fileObject;
    }
    
    private static final Collection<Modifier> NO_MODIFIERS = Collections.<Modifier>emptySet();
    
    public void run(CompilationInfo compilationInfo) {
        // Find the TreePath for the caret position
        TreePath tp =
                compilationInfo.getTreeUtilities().pathFor(whichElementJavaSourceTaskFactory.getLastPosition(fileObject));
        
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
        
        String status = "";
        String iconToolTip = "";
        Icon icon = UiUtils.getElementIcon(ElementKind.PARAMETER, NO_MODIFIERS);
        
        if (element != null) {
            String modifiers = element.getModifiers().toString();
            if (modifiers.startsWith("[") && modifiers.endsWith("]")) {
                modifiers = modifiers.substring(1, modifiers.length() -1).replaceAll(",", "").trim();
            }
            iconToolTip =  modifiers + (modifiers.length() > 0 ? " " : "");            
            icon = UiUtils.getElementIcon(element.getKind(), element.getModifiers());
            
            if (element instanceof PackageElement) {
                PackageElement packageElement = (PackageElement) element;
                status = packageElement.toString();
                iconToolTip += element.getKind().name().toLowerCase();
            } else if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                status = typeElement.getQualifiedName().toString();
                iconToolTip += element.getKind().name().toLowerCase();
            } else if (element instanceof VariableElement) {
                VariableElement variableElement = (VariableElement) element;
                status = variableElement.toString() + ":" + variableElement.asType().toString();
                iconToolTip += element.getKind().name().toLowerCase();
            } else if (element instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) element;
                // Method
                if (element.getKind() == ElementKind.METHOD) {
                    status = executableElement.getEnclosingElement().toString()
                            + "."
                            + executableElement.toString()
                            + ":"
                            + executableElement.getReturnType().toString();
                    iconToolTip += element.getKind().name().toLowerCase();
                } else if (element.getKind() == ElementKind.CONSTRUCTOR) { // CTOR - use enclosing class name
                    status = executableElement.getEnclosingElement().toString()
                            + "."
                            + executableElement.toString();
                    iconToolTip += element.getKind().name().toLowerCase();
                }
            }
        }
        
        WhichElementStatusElementProvider.WhichElementPanel localWhichElementPanel = getWhichElementPanel();
        
        // Set the info
        if (localWhichElementPanel != null) {
            localWhichElementPanel.setIcon(icon);
            localWhichElementPanel.setIconToolTip(iconToolTip);
            localWhichElementPanel.setText(status);
            localWhichElementPanel.setToolTipText(status);
        }
    }
    
    private WhichElementStatusElementProvider.WhichElementPanel getWhichElementPanel() {
        if (whichElementPanel == null) {
            StatusLineElementProvider statusLineElementProvider = (StatusLineElementProvider) Lookup.getDefault().lookup(WhichElementStatusElementProvider.class);
            if (statusLineElementProvider != null) {
                whichElementPanel = (WhichElementStatusElementProvider.WhichElementPanel) statusLineElementProvider.getStatusLineElement();
            }
        }
        return whichElementPanel;
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
