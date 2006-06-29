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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
