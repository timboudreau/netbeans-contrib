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
