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

package org.netbeans.modules.vcs.advanced.variables;

import java.util.*;
import javax.swing.event.*;

/**
 *
 * @author  Martin Entlicher
 * @version 
 */
public class VariableList extends TreeSet {

    private Vector listeners;

    /** Creates new VariableList */
    public VariableList() {
        listeners = new Vector();
    }

    public boolean add(Object obj) {
        boolean status = super.add(obj);
        //System.out.println("RevisionList.add("+obj+")");
        fireChanged();
        return status;
    }
    
    public void fireChanged() {
        //System.out.println("RevisionList.fireChange()");
        for(Enumeration enum = listeners.elements(); enum.hasMoreElements(); ) {
            ChangeListener listener = (ChangeListener) enum.nextElement();
            listener.stateChanged(new ChangeEvent(this));
        }
    }
    
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    public boolean removeChangeListener(ChangeListener listener) {
        return listeners.remove(listener);
    }
    
}
