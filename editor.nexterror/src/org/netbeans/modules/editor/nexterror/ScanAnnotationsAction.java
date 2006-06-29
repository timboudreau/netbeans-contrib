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

package org.netbeans.modules.editor.nexterror;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author JanLahoda
 */
public class ScanAnnotationsAction extends AbstractAction {
    
    private WeakReference lastComponent;
    private int           lastLine;
    private int           lastAnnotation;
    
    /** Creates a new instance of ScanAnnotationsAction */
    public ScanAnnotationsAction() {
        putValue(NAME, NbBundle.getMessage(ScanAnnotationsAction.class, "LBL_Next_Error"));
    }

    public void actionPerformed(ActionEvent e) {
        JTextComponent comp = Registry.getMostActiveComponent();
        
        if (comp == null) {
            lastComponent = null;
            lastLine = -1;
            lastAnnotation = -1;
            return ;
        }
        
        JTextComponent lastComp = null;
        
        if (lastComponent != null) {
            lastComp = (JTextComponent) lastComponent.get();
        }
        
        if (lastComp == null || lastComp != comp) {
            lastComponent = new WeakReference(comp);
            try {
                lastLine = Utilities.getLineOffset(Utilities.getDocument(comp), comp.getCaret().getDot()) + 1;
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                lastLine = 0;
            }
            lastAnnotation = -1;
        }
        
        AnnotationDesc nextAnnotation = findNextAnnotation(comp);
        
        if (nextAnnotation == null) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            comp.getCaret().setDot(nextAnnotation.getOffset());
            
            Utilities.setStatusText(comp, nextAnnotation.getShortDescription());
        }
    }
    
    /*package private for tests*/ AnnotationDesc findNextAnnotation(JTextComponent comp) {
        BaseDocument doc = Utilities.getDocument(comp);
        Annotations  ann = doc.getAnnotations();
        int          lines = Utilities.getRowCount(doc);
        boolean looped = false;
        
        while (true) {
            AnnotationDesc[] pasive = null;
            
            if (ann.getActiveAnnotation(lastLine) == null || (pasive = ann.getPasiveAnnotations(lastLine)) == null || pasive.length <= lastAnnotation) {
                lastLine = ann.getNextLineWithAnnotation(lastLine + 1);
                if (lastLine == (-1) || lastLine > lines) {
                    if (looped)
                        return null;
                    looped = true;
                    lastLine = 0;
                    continue;
                }
                
                lastAnnotation = -1;
            }
            
            AnnotationDesc proposed;
            
            if (lastAnnotation == -1) {
                lastAnnotation = 0;
                proposed = ann.getActiveAnnotation(lastLine);
            } else {
                proposed = pasive != null ? pasive[lastAnnotation++] : null; 
            }
            
            if (proposed == null)
                return null;
            
            if (proposed.getAnnotationTypeInstance().isBrowseable())
                return proposed;
        }
    }
    
}
