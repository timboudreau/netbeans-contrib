/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.annotation;

import java.beans.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.NbBundle;

/** Property editor for annotation pattern properties
*
* @author Milos Kleint

*/
public abstract class AnnotationPatternPropertyEditor extends PropertyEditorSupport {


    private static final String CUSTOM_EDITOR_CLASS = "org.netbeans.modules.vcscore.annotation.AnnotPatternCustomEditor";

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
       java.awt.Component toReturn = null;
        try {
            Class customEdClass = Class.forName(CUSTOM_EDITOR_CLASS, true, org.openide.TopManager.getDefault().currentClassLoader());
            toReturn = (java.awt.Component)customEdClass.newInstance();
            java.lang.reflect.Method method = customEdClass.getMethod("setCallingPropertyEditor", new Class[] {AnnotationPatternPropertyEditor.class});
            if (method != null) {
                method.invoke(toReturn, new Object[] {this});
            } else {
//                System.out.println("is null");
            }
        } catch (final Exception clExc) {
//            System.out.println("exception");
            clExc.printStackTrace();
        }               
       
        return toReturn;
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}

