/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
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
