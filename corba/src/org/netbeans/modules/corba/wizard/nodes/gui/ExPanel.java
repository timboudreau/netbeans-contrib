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

package org.netbeans.modules.corba.wizard.nodes.gui;

import javax.swing.JPanel;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author  tzezula
 * @version 
 */
public class ExPanel extends JPanel {
    
    private PropertyChangeSupport listeners;

    /** Creates new ExPanel */
    public ExPanel() {
        super ();
        this.listeners = new PropertyChangeSupport(this);
    }
    
    
    public void addPropertyChangeListener (PropertyChangeListener listener) {
        this.listeners.addPropertyChangeListener (listener);
    }
    
    
    public void removePropertyChangeListener (PropertyChangeListener listener) {
        this.listeners.removePropertyChangeListener (listener);
    }
    
    public void enableOk () {
        PropertyChangeEvent event = new PropertyChangeEvent (this,"Ok",null, Boolean.TRUE);  //No I18N
        this.listeners.firePropertyChange (event);
    }
    
    public void disableOk () {
        PropertyChangeEvent event = new PropertyChangeEvent (this,"Ok",null, Boolean.FALSE);  //No I18N
        this.listeners.firePropertyChange (event);
    }
    
    public void enableCancel () {
        PropertyChangeEvent event = new PropertyChangeEvent (this,"Cancel",Boolean.TRUE,null);  //No I18N
        this.listeners.firePropertyChange (event);
    }
    
    public void disableCancel () {
        PropertyChangeEvent event = new PropertyChangeEvent (this,"Cancel",Boolean.FALSE,null);  //No I18N
        this.listeners.firePropertyChange (event);
    }

}
