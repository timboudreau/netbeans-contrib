/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.Externalizable;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.openide.TopManager;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.src.Element;
import org.openide.src.SourceException;

/** Implementation of Element for classes.
*
* @author Dafe Simonek
*/
public abstract class ElementImpl extends Object implements Element.Impl, Externalizable {

    /** The element we aare asociated to. We provide an implementation
    * to that element */
    protected Element element;
    
    private PropertyChangeSupport support;
    
    static final long serialVersionUID =6363778502021582852L;

    /** Default constructor
    */
    public ElementImpl () {
    }

    /** Attaches this implementation to the element.
    *
    * @param element the element we are attached to
    */
    public void attachedToElement (Element element) {
        this.element = element;
    }

    /** We don't support property changes - does nothing */
    public void addPropertyChangeListener (PropertyChangeListener l) {
        if (support == null) 
            synchronized (this) {
                if (support == null)
                    support = new PropertyChangeSupport(this);
            }
        support.addPropertyChangeListener(l);
    }

    /** We don't support property changes - does nothing */
    public void removePropertyChangeListener (PropertyChangeListener l) {
        if (support != null)
            support.addPropertyChangeListener(l);
    }
    
    protected void firePropertyChange(String propName, Object old, Object n) {
        if (support != null)
            support.firePropertyChange(propName, old, n);
    }

    /** No cookie supported.
    * @return null
    */
    public Node.Cookie getCookie (Class type) {
        return null;
    }

    /** Mark the current element in the context of this element.
    * The current element means the position for inserting new elements.
    * @param beforeAfter <CODE>true</CODE> means that new element is inserted before
    *        the specified element, <CODE>false</CODE> means after.
    */
    public void markCurrent(boolean beforeAfter) {
        // nothing to do - class is not editable
    }

    
    protected final void throwReadOnlyException() throws SourceException {
        throw (SourceException)TopManager.getDefault().getErrorManager().annotate(
            new SourceException("Read-only element"), // NOI18N
            ErrorManager.USER,
            null, Util.getString("MSG_CantModify"),
            null, null
        );
    }        
}
