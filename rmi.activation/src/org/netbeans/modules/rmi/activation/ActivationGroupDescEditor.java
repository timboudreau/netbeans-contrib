/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.activation;

import java.beans.*;
import java.rmi.activation.*;

/**
 * Property editor for java.rmi.activation.ActivationGroupDesc.
 * @author  Jan Pokorsky
 * @version 
 */
public final class ActivationGroupDescEditor extends PropertyEditorSupport {

    /** Creates new ActivationGroupDescEditor */
    public ActivationGroupDescEditor() {
    }

/** Make available a full custom Component that edits its property value.
 * @return ActivationGroupDescCustomEditor
 */    
    public java.awt.Component getCustomEditor() {
        return new ActivationGroupDescCustomEditor((ActivationGroupDesc) getValue());
    }
    
/** Determines whether this property editor supports a custom editor.
 * @return <CODE>true</CODE>
 */    
    public boolean supportsCustomEditor() {
        return true;
    }
    
/** Sets (or change) the object that is to be edited.
 * @param obj the activation group descriptor to be edited.
 * @throws IllegalArgumentException if param obj not instance of
 * java.rmi.activation.ActivationGroupDesc.
 */    
    public void setValue(java.lang.Object obj) throws IllegalArgumentException {
        if ( !(obj instanceof ActivationGroupDesc) )
            throw new IllegalArgumentException();
        super.setValue(obj);
    }
    
/** Gets the property value as text.
 * @return value as text.
 */    
    public java.lang.String getAsText() {
        return (getValue() == null)? "null": ActivationGroupDesc.class.getName(); // NIO18N
    }
    
}
