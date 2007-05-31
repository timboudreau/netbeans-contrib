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

package org.netbeans.core.registry.convertors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TestBean {

    private PropertyChangeSupport propertySupport;

    private String prop1 = "initial1";

    private String prop2 = "initial2";

    public TestBean() {
        propertySupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public String getProp1() {
        return prop1;
    }
    
    public void setProp1(String value) {
        String oldProp1 = prop1;
        prop1 = value;
        propertySupport.firePropertyChange("prop1", oldProp1, prop1);
    }
    
    public String getProp2() {
        return prop2;
    }
    
    public void setProp2(String value) {
        String oldProp2 = prop2;
        prop2 = value;
        propertySupport.firePropertyChange("prop2", oldProp2, prop2);
    }
    
}
