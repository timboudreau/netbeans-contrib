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
