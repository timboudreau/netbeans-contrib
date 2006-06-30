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

package org.netbeans.modules.vcs.advanced.variables;

import java.util.*;
import javax.swing.event.*;

/**
 *
 * @author  Martin Entlicher
 */
public class VariableList extends TreeSet {

    private Vector listeners;

    private static final long serialVersionUID = 6033245279678422552L;

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
