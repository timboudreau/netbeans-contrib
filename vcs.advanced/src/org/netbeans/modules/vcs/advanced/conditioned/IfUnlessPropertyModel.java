/*
 * IfUnlessPropertyModel.java
 *
 * Created on March 1, 2004, 3:54 PM
 */

package org.netbeans.modules.vcs.advanced.conditioned;

import java.beans.PropertyChangeSupport;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
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
