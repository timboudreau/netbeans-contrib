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

package org.netbeans.modules.vcscore.annotation;

import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.Lookup;

import org.openide.util.NbBundle;

/** Property editor for annotation pattern properties
*
* @author Milos Kleint

*/
public abstract class AnnotationPatternPropertyEditor extends PropertyEditorSupport {


    public void setValue(Object value) {
        super.setValue(value);
    }

    public java.lang.String getAsText() {
        java.lang.String retValue;
        
        retValue = super.getAsText();
        return retValue;
    }
    
    public void setAsText(java.lang.String str) throws java.lang.IllegalArgumentException {
        super.setAsText(str);
    }
    
    public java.lang.Object getValue() {
        java.lang.Object retValue;
        
        retValue = super.getValue();
        return retValue;
    }
    
    public abstract String[] getPatterns();
    
    public abstract String[] getPatternDisplaNames();

    public abstract String getDefaultAnnotationPattern();
    
    public java.awt.Component getCustomEditor() {
        AnnotPatternCustomEditor editor = new AnnotPatternCustomEditor();
        editor.setCallingPropertyEditor(this);
        return editor;
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}

