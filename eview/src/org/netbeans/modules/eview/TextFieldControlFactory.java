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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import org.netbeans.api.eview.ControlFactory;
import org.openide.filesystems.FileObject;

/**
 * ControlFactory creating JTextFields.
 * Supports additional attributes "initValue" and "verifier".
 * @author David Strupl
 */
public class TextFieldControlFactory implements ControlFactory {

    /** Shared event instance. */
    private static final PropertyChangeEvent pcEvent = new PropertyChangeEvent(TextFieldControlFactory.class, "text", null, null);
    
    /** String value that is put into the text field right after initialization */
    private String initValue;
    /** Verifier object used to verify the text input */
    private InputVerifier verifier;
    
    /**
     * Creates a new instance of TextFieldControlFactory 
     */
    public TextFieldControlFactory(FileObject f) {
        Object o1 = f.getAttribute("initValue");
        if (o1 != null) {
            initValue = o1.toString();
        }
        Object o2 = f.getAttribute("verifier");
        if (o2 instanceof InputVerifier) {
            verifier = (InputVerifier)o2;
        }
    }

    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JTextField) {
            JTextField jtf = (JTextField)c;
            ControlListener controlListener = new ControlListener(l);
            jtf.getDocument().removeDocumentListener(controlListener);
            jtf.getDocument().addDocumentListener(controlListener);
        }
    }

    public JComponent createComponent() {
        JTextField result = new JTextField();
        if (verifier != null) {
            result.setInputVerifier(verifier);
        }
        if (initValue != null) {
            result.setText(initValue);
        }
        return result;
    }

    public Object getValue(JComponent c) {
        if (c instanceof JTextField) {
            JTextField jtf = (JTextField)c;
            return jtf.getText();
        }
        return null;
    }
    
    public String convertValueToString(JComponent c, Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JTextField) {
            JTextField jtf = (JTextField)c;
            ControlListener controlListener = new ControlListener(l);
            jtf.getDocument().removeDocumentListener(controlListener);
        }
    }

    public void setValue(JComponent c, Object value) {
        if (c instanceof JTextField) {
            JTextField jtf = (JTextField)c;
            if (value == null) {
                value = "null"; // NOI18N
            }
            jtf.setText(value.toString());
        }
    }
    
    /**
     * Listener attached to the control to propagate changes to our
     * listeners.
     */
    private class ControlListener implements DocumentListener {
        private PropertyChangeListener pcl;
        public ControlListener(PropertyChangeListener pcl){
            this.pcl = pcl;
        }
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            pcl.propertyChange(pcEvent);
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            pcl.propertyChange(pcEvent);
        }

        public void changedUpdate(javax.swing.event.DocumentEvent e) {
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
