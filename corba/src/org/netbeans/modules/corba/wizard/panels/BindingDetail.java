/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.panels;

/**
 *
 * @author  Dusan Balek
 */
public abstract class BindingDetail extends javax.swing.JPanel {
    
    /** Utility field holding list of ChangeListeners. */
    private transient java.util.ArrayList changeListenerList;
    private String value;
    
    public abstract void setData (Object data);

    public abstract Object getData ();
    
    public abstract void setTitle (String title);
    
    public abstract String getTitle ();
    
    public void setValue (String value) {
        this.value = value;
    }
    
    public String getValue () {
        return this.value;
    }

    public abstract boolean isValid ();
    
    /** Registers ChangeListener to receive events.
     * @param listener The listener to register.
 */
    public synchronized void addChangeListener(javax.swing.event.ChangeListener listener) {
        if (changeListenerList == null ) {
            changeListenerList = new java.util.ArrayList ();
        }
        changeListenerList.add (listener);
    }    
    
    /** Removes ChangeListener from the list of listeners.
     * @param listener The listener to remove.
 */
    public synchronized void removeChangeListener(javax.swing.event.ChangeListener listener) {
        if (changeListenerList != null ) {
            changeListenerList.remove (listener);
        }
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param param1 Parameter #1 of the <CODE>ChangeEvent<CODE> constructor.
 */
    protected void fireChange(Object source) {
        java.util.ArrayList list;
        synchronized (this) {
            if (changeListenerList == null) return;
            list = (java.util.ArrayList)changeListenerList.clone ();
        }
        javax.swing.event.ChangeEvent e = new javax.swing.event.ChangeEvent (source);
        for (int i = 0; i < list.size (); i++) {
            ((javax.swing.event.ChangeListener)list.get (i)).stateChanged (e);
        }
    }
    
}
