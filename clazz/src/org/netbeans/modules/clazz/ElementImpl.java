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
import java.beans.PropertyChangeListener;

import org.openide.nodes.Node;
import org.openide.src.Element;

/** Implementation of Element for classes.
*
* @author Dafe Simonek
*/
public abstract class ElementImpl extends Object implements Element.Impl, Externalizable {

    /** The element we aare asociated to. We provide an implementation
    * to that element */
    protected Element element;

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
    }

    /** We don't support property changes - does nothing */
    public void removePropertyChangeListener (PropertyChangeListener l) {
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

}

/*
* Log
*  9    src-jtulach1.8         1/20/00  David Simonek   #2119 bugfix
*  8    src-jtulach1.7         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  7    src-jtulach1.6         8/9/99   Ian Formanek    Generated Serial Version 
*       UID
*  6    src-jtulach1.5         7/8/99   Petr Hamernik   interface Element.Impl 
*       changes
*  5    src-jtulach1.4         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  4    src-jtulach1.3         3/18/99  Petr Hamernik   
*  3    src-jtulach1.2         2/17/99  Petr Hamernik   serialization changed.
*  2    src-jtulach1.1         2/3/99   David Simonek   
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
