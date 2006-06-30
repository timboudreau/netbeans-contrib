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

package org.netbeans.modules.clazz;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.Externalizable;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.openide.ErrorManager;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.src.Element;
import org.openide.src.SourceException;
import org.openide.util.NbBundle;

/** Implementation of Element for classes.
*
* @author Dafe Simonek
*/
public abstract class ElementImpl extends Object implements Element.Impl, Externalizable {

    /** The element we are asociated to. We provide an implementation
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
        throw (SourceException)ErrorManager.getDefault().annotate(
            new SourceException("Read-only element"), // NOI18N
            ErrorManager.USER,
            null, NbBundle.getMessage(ElementImpl.class, "MSG_CantModify"),
            null, null
        );
    }        
}
