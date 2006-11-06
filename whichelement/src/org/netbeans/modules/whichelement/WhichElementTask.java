/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.whichelement;

import com.sun.source.util.TreePath;
import java.security.spec.EllipticCurve;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.UiUtils;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.StatusLineElementProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

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
    private WhichElementStatusElementProvider.WhichElementPanel whichElementPanel;
    
    
    WhichElementTask(WhichElementJavaSourceTaskFactory whichElementJavaSourceTaskFactory,FileObject fileObject) {
        this.whichElementJavaSourceTaskFactory = whichElementJavaSourceTaskFactory;
        this.fileObject = fileObject;
        
        StatusLineElementProvider statusLineElementProvider = (StatusLineElementProvider) Lookup.getDefault().lookup(WhichElementStatusElementProvider.class);
        if (statusLineElementProvider != null) {
            whichElementPanel = (WhichElementStatusElementProvider.WhichElementPanel) statusLineElementProvider.getStatusLineElement();
        }
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
        
        // Set the info
        if (whichElementPanel != null) {
            whichElementPanel.setIcon(icon);
            whichElementPanel.setIconToolTip(iconToolTip);
            whichElementPanel.setText(status);
            whichElementPanel.setToolTipText(status);
        }
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
