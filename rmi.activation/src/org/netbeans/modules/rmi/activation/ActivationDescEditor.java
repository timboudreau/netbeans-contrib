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

package org.netbeans.modules.rmi.activation;

import java.beans.*;
import java.rmi.activation.*;

/**
 * Property editor for java.rmi.activation.ActivationDesc.
 * @author  Jan Pokorsky
 * @version
 */
public final class ActivationDescEditor extends PropertyEditorSupport {

    /** Creates new ActivationDescEditor */
    public ActivationDescEditor() {
    }

/** Make available a full custom Component that edits its property value.
 * @return ActivationDescCustomEditor
 */
    public java.awt.Component getCustomEditor() {
        return new ActivationDescCustomEditor((ActivationDesc) getValue());
    }
    
/** Determines whether this property editor supports a custom editor.
 * @return <CODE>true</CODE>
 */    
    public boolean supportsCustomEditor() {
        return true;
    }
    
/** Sets (or change) the object that is to be edited.
 * @param obj the activation descriptor to be edited.
 * @throws IllegalArgumentException if param obj not instance of
 * java.rmi.activation.ActivationDesc.
 */    
    public void setValue(java.lang.Object obj) throws IllegalArgumentException {
        if ( !(obj instanceof ActivationDesc) )
            throw new IllegalArgumentException();
        super.setValue(obj);
    }
    
/** Gets the property value as text.
 * @return value as text.
 */    
    public java.lang.String getAsText() {
        return (getValue() == null)? "null": ActivationDesc.class.getName(); // NIO18N
    }
    
}
