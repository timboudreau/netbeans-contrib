/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.model.command;

import java.beans.PropertyChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public abstract class DebuggingSupport {
    
    public static final String PROP_DEBUGGING_ENABLED = "debuggingEnabled";
    public static final String PROP_CURRENT_SELECTED_NODE = "currentSelectedNode";
    
    /** Creates a new instance of DebuggingSupport */
    public DebuggingSupport() {
    }
    
    public static final DebuggingSupport getDefault() {
        return (DebuggingSupport) Lookup.getDefault().lookup(DebuggingSupport.class);
    }
    
    public abstract void addPropertyChangeListener(PropertyChangeListener l);
    
    public abstract void removePropertyChangeListener(PropertyChangeListener l);
    
    public abstract boolean isDebuggingEnabled();
    
    public abstract void setDebuggingEnabled(boolean e);
    
    public abstract Node getCurrentSelectedNode();
    
    public abstract void setCurrentSelectedNode(Node node);
    
}
