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

import org.openide.windows.*;

import org.netbeans.modules.assistant.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import javax.swing.event.*;
import java.net.*;

/*
 * DefaultAssistantModel.java
 *
 * Created on October 11, 2002, 1:52 PM
 *
 * @author  Richard Gregor
 */
public class DefaultAssistantModel implements AssistantModel, PropertyChangeListener{
    private Vector sections = null; 
    protected EventListenerList listenerList = new EventListenerList();
    private static AssistantModel model;
    private AssistantContentViewer contentViewer;
    private AssistantID currentID;
    
    /*
     * Registry contains all registered IDs - assistant contents
     * Key is ID name, value is AssistantID
     *
     */
    private HashSet registry;
    
    public DefaultAssistantModel(){
        this(null);
    } 
    
    public DefaultAssistantModel(AssistantContext ctx){
        registry = new HashSet();
        if(ctx != null){
            registry.add(ctx);
        }
        contentViewer = AssistantContentViewer.createComp();
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }
    
    public static AssistantModel getModel(){
        if(model == null){
            model = new DefaultAssistantModel();
        }
        return model;
    }
    
    /**
     * Sets the current ID
     * AssistantModelListeners are notified. This should cause change of assistant content.
     *
     * @param id the ID used to set
     */
    public void setCurrentID(AssistantID id){
        this.currentID = id;
        fireIDChanged(this,id);
    }
    /**
     * Sets the current ID
     * AssistantModelListeners are notified. This should cause change of assistant content.
     *
     * @param id the ID used to set
     * @return True if ID exists
     */
    public synchronized boolean setCurrentID(String idName){        
        if(idName.equals("assistant"))
            return false;
        for(Iterator it = registry.iterator();it.hasNext();){
            AssistantContext ctx = (AssistantContext)it.next();
            for(Enumeration en = ctx.getIDs();en.hasMoreElements();){
                AssistantID id = (AssistantID)en.nextElement();
                if(id.getName().equals(idName)){
                    setCurrentID(id);                    
                    return true;
                }
            }
        }
        return false;
    }     
    
    /**
     * Gets the current ID
     */
    public AssistantID getCurrentID(){
        return currentID;
    }
    
    /**
     * Sets the current URL
     * AssistantModelListeners are notified
     *
     * @param url the URL used to set
     */
    public void setCurrentURL(URL url){
        debug("set url: "+url);
        //fireURLChanged(this, url); //need rethink whether to use events or not   
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
     * Registry the assistant's items with this model
     *
     *@param xmlFile The xml file definig assistant structure according to assistant-1_0.dtd 
     */
    public void registry(File xmlFile){
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
		((AssistantModelListener)listeners[i+1]).urlChanged(e);
	    }	       
	}
    }
    
    protected void fireIDChanged(Object source, AssistantID id) {
	Object[] listeners = listenerList.getListenerList();
	AssistantModelEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == AssistantModelListener.class) {
		if (e == null) {
		    e = new AssistantModelEvent(source, id, null);
		}		
		((AssistantModelListener)listeners[i+1]).idChanged(e);
	    }	       
	}
    }
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     *
     */
    public void propertyChange(PropertyChangeEvent evt) {
        TopComponent.Registry registry = (TopComponent.Registry)evt.getSource();
        TopComponent comp = registry.getActivated();
        if(comp != null){
            String helpID = comp.getHelpCtx().getHelpID();
            if (setCurrentID(comp.getHelpCtx().getHelpID()+"_"+comp.getName()));
            else setCurrentID(helpID);        
            
            debug("id: "+comp.getHelpCtx().getHelpID());
            debug("name: "+comp.getName());
            //setCurrentID(comp.getHelpCtx().getHelpID());
        }
    }

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("DefaultAssistantModel: "+msg);
	}
    }
}
