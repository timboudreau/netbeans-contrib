/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.assistant;

import org.netbeans.modules.assistant.event.*;
import java.beans.*;
import java.util.*;
import java.net.*;
/*
 * AssistantModel.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public interface AssistantModel {
    
    /**
     * Sets the current ID
     * AssistantModelListeners are notified
     *
     * @param id the ID used to set
     */
    public void setCurrentID(AssistantID id);
    
    /**
     * Sets the current ID
     * AssistantModelListeners are notified
     *
     * @param id the ID name used to set
     */
    public boolean setCurrentID(String idName);
    
    /**
     * Gets the current ID
     */
    public AssistantID getCurrentID();
    
    /**
     * Sets the current URL
     * AssistantModelListeners are notified
     *
     * @param url the URL used to set
     */
    public void setCurrentURL(URL url);
    
    /**
     *Returns the assistantModel
    public static AssistantModel getModel();
    
    /**
     * Gets the current URL
     */
    public URL getCurrentURL();
    
    /**
     * Performs the action
     *
     */
    public void performAction(String action);
    
    /**
     * Adds a listener for the AssistantModelEvent posted after the model has
     * changed.
     *
     * @param l The listener to add.
     * @see org.netbeans.modules.AssistantModel#removeAssistantModelListener
     */
    public void addAssistantModelListener(AssistantModelListener l);
    
    /**
     * Removes a listener previously added with <tt>addAssistantModelListener</tt>
     *
     * @param l The listener to remove.
     * @see org.netbeans.modules.AssistantModel#addAssistantModelListener
     */
    public void removeAssistantModelListener(AssistantModelListener l);
    
    /**
     * Adds a listener to monitor changes to the properties in this model
     *
     * @param l  The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Removes a listener monitoring changes to the properties in this model
     *
     * @param l  The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
}
