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
import javax.swing.event.*;
import java.net.*;

/*
 * DefaultAssistantModel.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class DefaultAssistantModel implements AssistantModel{
    private Vector sections = null; 
    protected EventListenerList listenerList = new EventListenerList();
    private static AssistantModel model;
    private AssistantContentViewer contentViewer;
    
    public DefaultAssistantModel(){
        this(null);
    }
    
    public DefaultAssistantModel(AssistantSection[] section){
        this.sections = new Vector();
        if(section != null){
            for(int i = 0; i < section.length; i++)
                sections.addElement(section[i]);
        }
        contentViewer = AssistantContentViewer.createComp();
    }
    
    public static AssistantModel getModel(){
        if(model == null){
            model = new DefaultAssistantModel();
        }
        return model;
    }
    
    /**
     * Sets the current ID
     * AssistantModelListeners are notified
     *
     * @param id the ID used to set
     */
    public void setCurrentID(AssistantID id){
    }
    
    /**
     * Gets the current ID
     */
    public AssistantID getCurrentID(){
        return null;
    }
    
    /**
     * Sets the current URL
     * AssistantModelListeners are notified
     *
     * @param url the URL used to set
     */
    public void setCurrentURL(URL url){
        fireURLChanged(this, url);   
        contentViewer.open();
        contentViewer.requestFocus();
        contentViewer.setPage(url);
    }
    
    /**
     * Gets the current URL
     */
    public URL getCurrentURL(){
        return null;
    }
    
    /**
     * Adds new link into porper section
     *
     *@param id The id to set as reference
     */
    public void addLink(AssistantID id){
    }
    
    /**
     * Removes link from section
     *
     *@param id The id to remove
     *@return true if id was removed, false if id doesn't exist
     */
    public boolean removeLink(AssistantID id){
        return false;
    }
    
    /**
     * Adds new section into Assistant
     *
     *@param section The section to add
     */
    public void addSection(AssistantSection section){
        if(sections == null)
            sections = new Vector();
        sections.addElement(section);
    }
    
    /**
     * Removes section from Assistant
     *
     *@param section The section to remove
     */
    public void removeSection(AssistantSection section){
        if(sections != null)
            sections.removeElement(section);
    }
    
    /**
     * Returns all section in Assistant
     *
     */
    public Vector getSections(){
        return sections;
    }
    
    /**
     * Adds a listener for the AssistantModelEvent posted after the model has
     * changed.
     * 
     * @param l The listener to add.
     * @see org.netbeans.modules.AssistantModel#removeAssistantModelListener
     */
    public void addAssistantModelListener(AssistantModelListener l){        
	listenerList.add(AssistantModelListener.class, l);    
    }

    /**
     * Removes a listener previously added with <tt>addAssistantModelListener</tt>
     *
     * @param l The listener to remove.
     * @see org.netbeans.modules.AssistantModel#addAssistantModelListener
     */
    public void removeAssistantModelListener(AssistantModelListener l){
        listenerList.remove(AssistantModelListener.class, l);
    }

    /**
     * Adds a listener to monitor changes to the properties in this model
     *
     * @param l  The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener l){
    }

    /**
     * Removes a listener monitoring changes to the properties in this model
     *
     * @param l  The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener l){
    }
    
    protected void fireURLChanged(Object source, URL url) {
	Object[] listeners = listenerList.getListenerList();
	AssistantModelEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == AssistantModelListener.class) {
		if (e == null) {
		    e = new AssistantModelEvent(source, null, url);
		}		
		((AssistantModelListener)listeners[i+1]).idChanged(e);
	    }	       
	}
    }
}
