/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.api.eview;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Data object representing data edited by the user in a panel
 * created by <code>ExtensibleView.createExtensiblePanel(String)</code>.
 * You can obtain an instance of this object by calling
 * <code>ExtensibleView.getPanelData(JPanel)</code>.
 *
 * @author David Strupl
 */
public interface PanelData {

    /**
     * Map containing current values edited by the user. Calling this method
     * can take rather long time since all of the GUI controls are queried
     * for the values. 
     */
    public Map/*<String, Object>*/ getValues();
    
    /**
     * Set a value in the control. The value gets propagated to the GUI
     * controls.
     */
    public void setValue(String key, Object value);
    
    /**
     * Returns true if the user has modified the data somehow.
     */
    public boolean isModified();
    
    /** Clears the modification flag. If nothing is changed by the user
     * between calling this method and isModified - isModified will return
     * false.
     */
    public void clearModified();
    
    /** Allows listening on changes in the data edited by the user. */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /** Allows listening on changes in the data edited by the user. */
    public void removePropertyChangeListener(PropertyChangeListener l);
}
