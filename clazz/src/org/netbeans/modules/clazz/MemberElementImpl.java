/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.lang.reflect.Modifier;

import org.openide.src.MemberElement;
import org.openide.src.SourceException;
import org.openide.src.Identifier;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.nodes.Node;
import org.netbeans.modules.classfile.ClassFile;
import org.netbeans.modules.classfile.Access;
import org.openide.util.Task;

import javax.jmi.reflect.RefObject;
import org.netbeans.jmi.javamodel.Feature;

/** Implementation of the MemberElement.Impl for the class objects.
*
* @author Dafe Simonek
*/
public abstract class MemberElementImpl extends ElementImpl implements MemberElement.Impl {
    /** Asociated java reflection data */
    protected Object data;
    /** Cached name identifier */
    private transient Identifier name;

    static final long serialVersionUID =-6841890195552268874L;
    /** Constructor, asociates this impl with java reflection
    * Member element, which acts as data source.
    */
    public MemberElementImpl (final Object data) {
        super();
        this.data = data;
    }
    
    Feature getClassFeature() {
        return (Feature)data;
    }

    /** @return Modifiers for this element.
    */
    public int getModifiers () {
        Feature f = getClassFeature();
        return f.getModifiers();
    }

    /** Unsupported. Throws SourceException
    */
    public void setModifiers (int mod) throws SourceException {
        throwReadOnlyException();
    }

    /** Getter for name of the field.
    * @return the name
    */
    public Identifier getName () {
        if (name == null) {
	    name = createName(this.data);
	}
        return name;
    }
    
    protected Identifier createName(Object data) {
	String name = getClassFeature().getName();
	return Identifier.create(name);
    }

    /** Unsupported. Throws SourceException.
    */
    public void setName (Identifier name) throws SourceException {
        throwReadOnlyException();
    }

    /** Delegates to source element implementation class,
    * if it's possible.
    */
    public Node.Cookie getCookie (Class type) {
        ClassElement ce = ((MemberElement)element).getDeclaringClass();
        if ((ce == null) && (element instanceof ClassElement)) {
            ce = (ClassElement)element;
        }
        if (ce != null) {
            SourceElement se = ce.getSource();
            if (se != null) {
                return se.getCookie(type);
            }
        }
        return null;
    }

    public void writeExternal (ObjectOutput oi) throws IOException {
        oi.writeObject(data);
    }

    public void readExternal (ObjectInput oi) throws IOException, ClassNotFoundException {
        data = oi.readObject();
    }

    
    public RefObject getRefObject() {
        if (data instanceof RefObject)
            return (RefObject)data;
        return null;
    }
    
    public Task refresh() {
        return Task.EMPTY;
    }
}
