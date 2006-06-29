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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.eview;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.eview.PanelData;

/**
 *
 * @author David Strupl
 */
public class PanelDataImpl implements PanelData {

    private Map data = new HashMap();
    private boolean modified;
    private EViewPanel myPanel;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /** Creates a new instance of PanelDataImpl */
    public PanelDataImpl() {
    }

    /** Creates a new instance of PanelDataImpl */
    public PanelDataImpl(EViewPanel evp) {
        myPanel = evp;
        myPanel.extractDataFromControls(this);
    }
    
    public boolean isModified() {
        if (myPanel != null) {
            modified = myPanel.isModified();
        }
        return modified;
    }

    public Map getValues() {
        if (isModified()) {
            myPanel.extractDataFromControls(this);
        }
        return data;
    }
    
    public void setModified(boolean m) {
        if (modified == m) {
            return;
        }
        modified = m;
        pcs.firePropertyChange("modified", ! modified, modified);
    }
    
    public void put(String key, Object value) {
        data.put(key, value);
    }

    public void clearModified() {
        modified = false;
        if (myPanel != null) {
            myPanel.clearModified();
        }
        pcs.firePropertyChange("modified", ! modified, modified);
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public void setValue(String key, Object value) {
        myPanel.updateControl(key, value);
    }
}
