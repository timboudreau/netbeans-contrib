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

package org.netbeans.modules.latex.model.command.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.netbeans.modules.latex.model.command.DebuggingSupport;
import org.netbeans.modules.latex.model.command.Node;

/**
 *
 * @author Jan Lahoda
 */
public final class DebuggingSupportImpl extends DebuggingSupport {
    
    private boolean debuggingEnabled;
    private Node    currentNode;
    private PropertyChangeSupport pcs;
    
    /** Creates a new instance of DebuggingSupportImpl */
    public DebuggingSupportImpl() {
        pcs = new PropertyChangeSupport(this);
    }

    public void setDebuggingEnabled(boolean e) {
        boolean old = debuggingEnabled;
        
        debuggingEnabled = e;
        
        if (isDebuggingEnabled())
            pcs.firePropertyChange(PROP_DEBUGGING_ENABLED, Boolean.valueOf(old), Boolean.valueOf(debuggingEnabled));
    }

    public Node getCurrentSelectedNode() {
        if (isDebuggingEnabled())
            return currentNode;
        else
            return null;
    }

    public boolean isDebuggingEnabled() {
        return debuggingEnabled;
    }

    public void setCurrentSelectedNode(Node node) {
        Node old = currentNode;
        
        currentNode = node;
        
        if (isDebuggingEnabled())
            pcs.firePropertyChange(PROP_CURRENT_SELECTED_NODE, old, currentNode);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
}
