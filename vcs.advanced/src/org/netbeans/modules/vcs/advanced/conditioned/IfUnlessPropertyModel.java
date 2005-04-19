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

package org.netbeans.modules.vcs.advanced.conditioned;

import java.beans.PropertyChangeSupport;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 * @author  Martin Entlicher
 */
public class IfUnlessPropertyModel implements PropertyModel {
    
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private IfUnlessCondition iuc;
    
    /** Creates a new instance of IfUnlessPropertyModel */
    public IfUnlessPropertyModel() {
    }
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    public Class getPropertyEditorClass() {
        return IfUnlessPropertyEditor.class;
    }
    
    public Class getPropertyType() {
        return IfUnlessCondition.class;
    }
    
    public Object getValue() throws java.lang.reflect.InvocationTargetException {
        return iuc;
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    public void setValue(Object v) throws java.lang.reflect.InvocationTargetException {
        Object old = this.iuc;
        this.iuc = (IfUnlessCondition) v;
        pcs.firePropertyChange("value", old, v);
    }
    
}
