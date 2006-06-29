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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.zeroadmin.test;

import java.beans.*;

/**
 * Test setting containing configurable number of properties.
 * @author David Strupl
 */
public class TestSetting {

    private final static String PROP_TEST = "test"; //NOI18N

    private PropertyChangeSupport pcSupport;

    private String test = "value";

    /** Number of properties */
    private int size = 1;

    /** Creates a new instance of TestSetting */
    public TestSetting() {
    }

    //  property change event support
    public void addPropertyChangeListener(PropertyChangeListener l) {
        getPCSupport().addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        getPCSupport().removePropertyChangeListener(l);
    }
    
    // getters/setters
    public String getTest() {
        return test;
    }
    
    public void setTest(String test) {
        String old = this.test;
        this.test = test;
        getPCSupport().firePropertyChange(PROP_TEST, old, test);
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    private PropertyChangeSupport getPCSupport() {
        if (pcSupport == null) {
            pcSupport = new PropertyChangeSupport(this);
        }
        return pcSupport;
    }
    
    // readProperties/writeProperties called by XMLPropertiesConvertor
    private void readProperties(java.util.Properties p) {
        test = p.getProperty(PROP_TEST);
        // the rest of properties is ignored for now
    }
    
    private void writeProperties(java.util.Properties p) {
        p.setProperty(PROP_TEST, test);
        for (int i = 0; i < this.size; i++) {
            p.setProperty(PROP_TEST+i, test);
        }
    }
}
