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
 * Software is Nokia. Portions Copyright 2005 Nokia.
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
