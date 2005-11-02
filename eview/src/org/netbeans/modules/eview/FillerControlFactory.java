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

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.api.eview.ControlFactory;
import org.openide.filesystems.FileObject;

/**
 * ControlFactory creating empty filler space.
 * @author David Strupl
 */
public class FillerControlFactory implements ControlFactory {

    /**
     * Creates a new instance of CheckBoxControlFactory 
     */
    public FillerControlFactory(FileObject f) {
    }

    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
    }

    public JComponent createComponent() {
        JPanel p = new JPanel();
        p.putClientProperty("foregroundArea", Boolean.TRUE);
        return p;
    }

    public Object getValue(JComponent c) {
        return null;
    }
    
    public String convertValueToString(JComponent c, Object value) {
        return null;
    }
    
    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
    }

    public void setValue(JComponent c, Object value) {
    }
}
