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

    private static DebuggingSupport INSTANCE;

    public static synchronized DebuggingSupport getDefault() {
        if (INSTANCE == null) {
            INSTANCE = (DebuggingSupport) Lookup.getDefault().lookup(DebuggingSupport.class);
        }
        
        return INSTANCE;
    }
    
    public abstract void addPropertyChangeListener(PropertyChangeListener l);
    
    public abstract void removePropertyChangeListener(PropertyChangeListener l);
    
    public abstract boolean isDebuggingEnabled();
    
    public abstract void setDebuggingEnabled(boolean e);
    
    public abstract Node getCurrentSelectedNode();
    
    public abstract void setCurrentSelectedNode(Node node);
    
}
