/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Template;

import org.netbeans.spi.enode.LookupContentFactory;

/** Wrapper object for delaying of loading classes of the attached
 * objects. Reads the file attributes needed for this optimalization.
 * @author David Strupl
 */
public class FactoryWrapper implements LookupContentFactory {
    
    /**
     * File object on the system filesystem. The attributes of this
     * file object are used to determine the class of the result.
     */
    private FileObject f;
    
    /**
     * The result of call to the <code> instantiate </code> method.
     */
    private Object obj;
    
    /** Just remembers the parameter.*/
    public FactoryWrapper(FileObject f) {
        this.f = f;
    }
    
    /**
     * Method from the LookupContentFactory interface. This method
     * passes the <code>target</code> argument to the delegated
     * LookupContentFactory.
     */
    public Object create(Node target) {
        if (obj == null) {
            obj = instantiate();
        }
        if (obj instanceof LookupContentFactory) {
            LookupContentFactory lcf = (LookupContentFactory)obj;
            return lcf.create(target);
        }
        return obj;
    }
    
    /**
     * Method from the LookupContentFactory interface. This method
     * passes the <code>target</code> argument to the delegated
     * LookupContentFactory.
     */
    public Lookup createLookup(Node target) {
        if (obj == null) {
            obj = instantiate();
        }
        if (obj instanceof LookupContentFactory) {
            LookupContentFactory lcf = (LookupContentFactory)obj;
            return lcf.createLookup(target);
        }
        return null;
    }
    
    /**
     * Checks whether we can match the template. If the resulting object
     * has been computed we just use its class or if it has not the file
     * object is examined for the "implements" attribute.
     */
    boolean matches(Template template) {
        if (template.getType() != null) {
            if (obj != null) {
                return template.getType().isAssignableFrom(obj.getClass());
            }
            if (! resultImplements().contains(template.getType().getName())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Parses the value of attribute "implements"
     * @return List of String with names of classes/interfaces
     *      implemented by the resulting object
     */
    private List resultImplements() {
        String classAttr = (String)f.getAttribute("implements"); // NOI18N
        ArrayList res = new ArrayList();
        StringTokenizer t = new StringTokenizer(classAttr, ",");
        while (t.hasMoreElements()) {
            res.add(t.nextElement());
        }
        return res;
    }
    
    /**
     * We use the system classloader for resolving the class specified
     * in the file attribute.
     * @return Class of the resulting object. If the object has not
     *      been created yet the attribute "factoryClass" is consulted
     * @throws IllegalStateException if something went wrong
     */
    private Class clazz() {
        if (obj != null) {
            return obj.getClass();
        }
        try {
            String classAttr = (String)f.getAttribute("factoryClass"); // NOI18N
            ClassLoader cl = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
            if (classAttr != null) {
                Class c = Class.forName(classAttr, true, cl);
                return c;
            } else {
                throw new IllegalStateException("Attribute factoryClass not specified for " + f); // NOI18N
            }
        } catch (ClassNotFoundException cnfe) {
            IllegalStateException ise = new IllegalStateException();
            ErrorManager.getDefault().annotate(ise, cnfe);
            throw ise;
        }
    }
    
    /**
     * After calling the clazz method newInstance of the resulting
     * class is returned.
     * @throws IllegalStateException if something went wrong
     */
    private Object instantiate() {
        try {
            return clazz().newInstance();
        } catch (InstantiationException is) {
            IllegalStateException ise = new IllegalStateException();
            ErrorManager.getDefault().annotate(ise, is);
            throw ise;
        } catch (IllegalAccessException iae) {
            IllegalStateException ise = new IllegalStateException();
            ErrorManager.getDefault().annotate(ise, iae);
            throw ise;
        }
    }
    
    /**
     * @return Human readable description of the wrapper object.
     */
    public String toString() {
        if (obj != null) {
            return "FactoryWrapper[" + clazz().getName() + "]"; // NOI18N
        }
        return "FactoryWrapper[" + f.getAttribute("factoryClass") + "]"; // NOI18N
    }
    
}
