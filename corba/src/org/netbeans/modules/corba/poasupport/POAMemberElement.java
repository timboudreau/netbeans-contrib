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

package org.netbeans.modules.corba.poasupport;

import java.net.*;
import java.io.*;

import org.openide.src.ClassElement;
import org.openide.cookies.OpenCookie;

/*
 * @author Dusan Balek
 */

public abstract class POAMemberElement {
    
    public static String PROP_VAR_NAME = "VarName"; // NOI18N
    public static String PROP_TYPE_NAME = "TypeName"; // NOI18N
    public static String PROP_CONSTRUCTOR = "Constructor"; // NOI18N

    private String varName = null;
    private String typeName = null;
    private String ctor = null;
    private POAElement parentPOA;

    protected boolean writeable;

    /** Utility field holding list of PropertyChangeListeners. */
    private transient java.util.ArrayList propertyChangeListenerList;    

    public POAMemberElement(POAElement _parentPOA, boolean _writeable) {
        parentPOA = _parentPOA;
        writeable = _writeable;
        varName = getDefaultVarName();
    }
    
    public boolean isWriteable() {
        return writeable;
    }
    
    public boolean canUseAsVarName(String name) {
        return getParentPOA().canUseAsVarNameFor(name, this);
    }

    public boolean canUseAsNewVarName(String name) {
        return getParentPOA().canUseAsNewVarNameFor(name, this);
    }

    public abstract String getDefaultVarName();

    public ClassElement getDeclaringClass() {
        return getParentPOA().getDeclaringClass();
    }
    
    public OpenCookie getOpenCookie() {
        return getParentPOA().getOpenCookie();
    }

    public void setLinePosition() {
        getParentPOA().rootPOA.maker.setLinePosition(this);
    }
    
    public String getVarName () {
        return varName;
    }
        
    public void setVarName (String _varName) {
        if (!_varName.equals(varName)) {
            String oldName = varName;
            varName = _varName;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_VAR_NAME, oldName, varName));
        }
    }

    public String getTypeName () {
        return typeName;
    }
        
    public void setTypeName (String _typeName) {
        if (((_typeName != null)&&(!_typeName.equals(typeName))) || ((_typeName == null)&&(typeName != null))) {
            String oldName = typeName;
            typeName = _typeName;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_TYPE_NAME, oldName, typeName));
        }
    }
    
    public String getConstructor () {
        return ctor;
    }
        
    public void setConstructor (String _ctor) {
        if (((_ctor != null)&&(!_ctor.equals(ctor))) || ((_ctor == null)&&(ctor != null))) {
            String oldCtor = ctor;
            ctor = _ctor;
            firePropertyChange(new java.beans.PropertyChangeEvent(this, PROP_CONSTRUCTOR, oldCtor, ctor));
        }
    }
    
    public POAElement getParentPOA() {
        return parentPOA;
    }

    /** Registers PropertyChangeListener to receive events.
     * @param listener The listener to register.
 */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        if (propertyChangeListenerList == null ) {
            propertyChangeListenerList = new java.util.ArrayList ();
        }
        propertyChangeListenerList.add (listener);
    }
    
    /** Removes PropertyChangeListener from the list of listeners.
     * @param listener The listener to remove.
 */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        if (propertyChangeListenerList != null ) {
            propertyChangeListenerList.remove (listener);
        }
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param e The event to be fired
 */
    protected void firePropertyChange(java.beans.PropertyChangeEvent event) {
        java.util.ArrayList list;
        synchronized (this) {
            if (propertyChangeListenerList == null) return;
            list = (java.util.ArrayList)propertyChangeListenerList.clone ();
        }
        for (int i = 0; i < list.size (); i++) {
            ((java.beans.PropertyChangeListener)list.get (i)).propertyChange (event);
        }
    }
}
