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
