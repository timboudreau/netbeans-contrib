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

import org.openide.src.MemberElement;
import org.openide.src.SourceException;
import org.openide.src.Identifier;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.nodes.Node;
import org.openide.util.Task;

import javax.jmi.reflect.RefObject;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.jmi.javamodel.Feature;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.util.RequestProcessor;

/** Implementation of the MemberElement.Impl for the class objects.
*
* @author Dafe Simonek
*/
public abstract class MemberElementImpl extends ElementImpl implements MemberElement.Impl, Node.Cookie {
    /** Asociated java reflection data */
    protected Object data;
    /** Cached name identifier */
    private transient Identifier name;
    
    private int modifiers = -1;

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

    public void initializeData() {
        getName();
        getModifiers();
    }
    
    /** @return Modifiers for this element.
    */
    public int getModifiers () {
        if (modifiers == -1) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    return 0;
                }
                Feature f = getClassFeature();
                modifiers = f.getModifiers();
            } finally {
                repo.endTrans();
            }
        }
        return modifiers;
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
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Identifier.create(""); // NOI18N
            }
            String name = getClassFeature().getName();
            return Identifier.create(name);
        } finally {
            repo.endTrans();
        }
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
        if (type == org.openide.src.Element.Impl.class)
            return this;
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

    org.netbeans.jmi.javamodel.Element getJMIElement() {
        return data instanceof org.netbeans.jmi.javamodel.Element ?
            (org.netbeans.jmi.javamodel.Element) data : null;
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
    
    public boolean isValid() {
        if (data instanceof org.netbeans.jmi.javamodel.Element) {
            boolean valid = ((org.netbeans.jmi.javamodel.Element) data).isValid();
            /*
            if (!valid) {
                final SourceElementImpl source = (SourceElementImpl)getCookie(SourceElement.Impl.class);
                if (source != null) {
                    RequestProcessor.getDefault().post(new Runnable() {
                        public void run() {
                            source.refreshData();
                        }
                    });
                }
            }
             */
            return valid;
        }
        return false;
    }
    
    public ElementImpl getDeclaringElement() {
        if ((element == null) || !(element instanceof MemberElement))
            return null;
        ClassElement declClass = ((MemberElement)element).getDeclaringClass();
        if (declClass != null)
            return (ElementImpl) declClass.getCookie(org.openide.src.Element.Impl.class);
        if (element instanceof ClassElement) {
            SourceElement source = ((ClassElement) element).getSource();
            if (source != null)
                return (ElementImpl) source.getCookie(org.openide.src.Element.Impl.class);
        }
        return null;
    }
    
    public Task refresh() {
        return Task.EMPTY;
    }
    
}
