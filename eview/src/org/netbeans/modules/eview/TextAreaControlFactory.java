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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import org.netbeans.api.eview.ControlFactory;
import org.openide.filesystems.FileObject;

/**
 * ControlFactory creating JTextAreas.
 * Supports additional attributes "initValue" and "verifier".
 * @author David Strupl
 */
public class TextAreaControlFactory implements ControlFactory {

    /** Shared event instance. */
    private static final PropertyChangeEvent pcEvent = new PropertyChangeEvent(TextFieldControlFactory.class, "text", null, null);
    
    /** String value that is put into the text field right after initialization */
    private String initValue;
    /** Verifier object used to verify the text input */
    private InputVerifier verifier;
    
    /** */
    private boolean separateLines = false;
    
    /** */
    private int numberOfLines = 3;
    
    /**
     * Creates a new instance of TextAreaControlFactory 
     */
    public TextAreaControlFactory(FileObject f) {
        Object o1 = f.getAttribute("initValue");
        if (o1 != null) {
            initValue = o1.toString();
        }
        Object o2 = f.getAttribute("verifier");
        if (o2 instanceof InputVerifier) {
            verifier = (InputVerifier)o2;
        }
        Object o3 = f.getAttribute("separateLines");
        if (o3 instanceof Boolean) {
            separateLines = ((Boolean)o3).booleanValue();
        }
        Object o4 = f.getAttribute("lines");
        if (o4 instanceof Integer) {
            numberOfLines = ((Integer)o4).intValue();
        }
    }

    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            ControlListener controlListener = new ControlListener(l);
            jtf.getDocument().removeDocumentListener(controlListener);
            jtf.getDocument().addDocumentListener(controlListener);
        }
    }

    public JComponent createComponent() {
        JTextArea result = new JTextArea();
        if (verifier != null) {
            result.setInputVerifier(verifier);
        }
        if (initValue != null) {
            result.setText(initValue);
        }
        result.setRows(numberOfLines+1);
        JScrollPane jsp = new JScrollPane();
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setViewportView(result);
        return jsp;
    }

    public Object getValue(JComponent c) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            if (separateLines) {
                String s = jtf.getText();
                String []ss = s.split("\n");
                return ss;
            }
            return jtf.getText();
        }
        return null;
    }

    public String convertValueToString(JComponent c, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String[]) {
            String[] sa = (String[])value;
            if (sa.length > 0) {
                return sa[0];
            }
            return "";
        }
        return value.toString();
    }
    
    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            ControlListener controlListener = new ControlListener(l);
            jtf.getDocument().removeDocumentListener(controlListener);
        }
    }

    public void setValue(JComponent c, Object value) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            if (value == null) {
                value = "null"; // NOI18N
            }
            if (separateLines) {
                String res = "";
                if (value instanceof String[]) {
                    String ss[] = (String[])value;
                    for (int i = 0; i < ss.length; i++) {
                        res += ss[i] + "\n";
                    }
                }
                jtf.setText(res);
            } else {
                jtf.setText(value.toString());
            }
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
