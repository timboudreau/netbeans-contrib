/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.eview;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.event.DocumentListener;
import org.netbeans.api.eview.ControlFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * ControlFactory creating JCheckBoxes.
 * @author David Strupl
 */
public class CheckBoxControlFactory implements ControlFactory {

    /** Shared event instance. */
    private static final PropertyChangeEvent pcEvent = new PropertyChangeEvent(CheckBoxControlFactory.class, "state", null, null);
    
    /** String value that is put into the check box right after initialization */
    private boolean initValue;
    
    /**
     * Creates a new instance of CheckBoxControlFactory 
     */
    public CheckBoxControlFactory(FileObject f) {
        Object o1 = f.getAttribute("initValue");
        if (o1 instanceof Boolean) {
            initValue = ((Boolean)o1).booleanValue();
        }
    }

    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
            jcb.addActionListener(controlListener);
        }
    }

    public JComponent createComponent() {
        JCheckBox result = new JCheckBox();
        result.setSelected(initValue);
        return result;
    }

    public Object getValue(JComponent c) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            return Boolean.valueOf(jcb.isSelected());
        }
        return null;
    }
    
    public String convertValueToString(JComponent c, Object value) {
        if (value instanceof Boolean) {
            if ( ((Boolean)value).booleanValue()) {
                return NbBundle.getBundle(CheckBoxControlFactory.class).getString("LBL_True");
            } else {
                return NbBundle.getBundle(CheckBoxControlFactory.class).getString("LBL_False");
            }
        }
        return NbBundle.getBundle(CheckBoxControlFactory.class).getString("LBL_Error");
    }
    
    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
        }
    }

    public void setValue(JComponent c, Object value) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            jcb.setSelected(((Boolean)value).booleanValue());
        }
    }
    
    /**
     * Listener attached to the control to propagate changes to our
     * listeners.
     */
    private class ControlListener implements ActionListener {
        private PropertyChangeListener pcl;
        public ControlListener(PropertyChangeListener pcl){
            this.pcl = pcl;
        }
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            pcl.propertyChange(pcEvent);
        }

        public boolean equals(Object anotherObject) {
            if ( ! ( anotherObject instanceof ControlListener ) ) {
                return false;
            }
            ControlListener theOtherOne = (ControlListener)anotherObject;
            return pcl.equals(theOtherOne.pcl);
        }
        public int hashCode() {
            return pcl.hashCode();
        }
    }
}
