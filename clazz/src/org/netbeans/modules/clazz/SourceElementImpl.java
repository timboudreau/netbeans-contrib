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

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.ref.SoftReference;

import org.openide.util.Task;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.src.*;

import org.netbeans.modules.classfile.ClassFile;

/** The implementation of source element for class objects.
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Dafe Simonek, Jan Jancura
*/
final class SourceElementImpl extends MemberElementImpl
    implements SourceElement.Impl, ElementProperties, Node.Cookie {

    private static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap(0));
    
    /** Empty array of imports - constant to return fro getImports() */
    static final Import[] EMPTY_IMPORTS = new Import[0];
    static final ClassElement[] NO_CLASSES = new ClassElement[0];
    
    /* Soft reference to the class element */
    private SoftReference topClass;
    /** Soft ref to the map holding all inners */
    private SoftReference allClasses;
    /** The identifier of the package of the class data */
    private Identifier packg;
    /** Association with the class data object (can be null) */
    private ClassDataObject cdo;

    private ClassLoader loader;

    static final long serialVersionUID =-4870331896218546842L;

    /** Creates object with asociated class and no class data object.
     *  This SourceElements contain always only one top-level class,
     *  specified by the `data' parameter.
     */
    SourceElementImpl (ClassLoader loader, ClassFile data) {
        this(data, null);
        this.loader = loader;
    }

    /** Creates object with asociated class and with asociated
    * class data object which created this source element (can be null).
    */
    public SourceElementImpl (ClassFile data, ClassDataObject cdo) {
        super(data);
        this.cdo = cdo;
    }
    
    public void setClassObject(ClassFile data) {
        ClassFile oldData = (ClassFile)this.data;
        int oldStatus;
        int newStatus;

        synchronized (this) {
            oldStatus = getStatus();
            this.data = data;
            topClass = null;
            allClasses = null;
            newStatus = getStatus();
        }
        if (oldData != null || data != null) {
            firePropertyChange(PROP_CLASSES, null, null);
            firePropertyChange(PROP_ALL_CLASSES, null, null);
        }
        firePropertyChange(PROP_STATUS, new Integer(oldStatus), new Integer(newStatus));
    }

    /** Not supported. Throws SourceException.
    */
    public void setPackage (Identifier id) throws SourceException {
        throwReadOnlyException();
    }

    /** @return The package of class which we are representing.
    */
    public Identifier getPackage () {
        if (data == null)
            return null;
        
        if (packg == null) {
            packg = Identifier.create(((ClassFile)data).getPackage());
        }
        return packg;
    }

    /** @return always returns empty array
    */
    public Import[] getImports () {
        return EMPTY_IMPORTS;
    }

    /** Not supported. Throws SourceException.
    */
    public void changeImports (Import[] elems, int action) throws SourceException {
        throwReadOnlyException();
    }

    /** Not supported. Throws SourceException.
    */
    public void changeClasses (ClassElement[] elems, int action) throws SourceException {
        throwReadOnlyException();
    }

    /** Always returns only one class element which belongs to the
    * class data we were given in constructor.
    */
    public ClassElement[] getClasses () {
        if (data == null)
            return NO_CLASSES;
        return new ClassElement[] { getClassElement() };
    }

    /** Finds an inner class with given name.
    * @param name the name to look for
    * @return the element or null if such class does not exist
    */
    public ClassElement getClass (Identifier name) {
        return (ClassElement)getAllClassesMap ().get(name);
    }

    /** @return Top level class which we are asociated with
    * and all its innerclasses and innerinterfaces.
    */
    public ClassElement[] getAllClasses () {
        return (ClassElement[])getAllClassesMap ().values().toArray (new ClassElement[0]);
    }

    /** @return Always returns STATUS_OK, 'cause we always have the class...
    */
    public int getStatus () {
        return data == null ? SourceElement.STATUS_ERROR : SourceElement.STATUS_OK;
    }

    /** Returns empty task, because we don't need any preparation.
    */
    public Task prepare () {
        return Task.EMPTY;
    }
    
    /** .class file are always read only for Java Hierarchy API
    */
    public boolean isReadOnly() {
	return true;
    }

    /************* utility methods *********/
    java.io.InputStream findStreamForClass(String n) throws java.io.IOException {
        if (this.cdo != null) {
            java.util.Set files = cdo.files();
            n = n + ".class"; // NOI18N
            for (java.util.Iterator iter = files.iterator(); iter.hasNext();){
                FileObject fo = ((FileObject)iter.next());
                if( fo.getNameExt().equals(n) ){
                    return fo.getInputStream();
                }
            } 
            return null;
        } else {
            // assume we have a valid classloader
            ClassFile cf = (ClassFile)data;
            StringBuffer sb = new StringBuffer();
            sb.append(cf.getPackage().replace('.', '/'));
            sb.append('/');
            sb.append(n);
            sb.append(".class"); // NOI18N
            String s = sb.toString();
            return loader.getResourceAsStream(s);
        }
    }

    /** Returns class element for asociated class data.
    * Care must be taken, 'cause we are playing with soft reference.
    */
    private ClassElement getClassElement () {
        ClassElement result =
            (topClass == null) ? null : (ClassElement)topClass.get();
        if (result == null) {
            if (data == null)
                return null;
            result = new ClassElement(
                     new ClassElementImpl((ClassFile)data), (SourceElement)element);
            topClass = new SoftReference(result);
        }
        return result;
    }

    /** Returns map with all innerclasses.
    * @return map with all innerclasses.
    */
    private Map getAllClassesMap () {
        Map allClassesMap = (allClasses == null) ? null : (Map)allClasses.get();
        if (allClassesMap == null) {
            if (data != null) {
                // soft ref null, we must recreate
                allClassesMap = createClassesMap();
                // remember it, please ...
                allClasses = new SoftReference(allClassesMap);
            } else {
                // can't really do anything - return empty map:
                return EMPTY_MAP;
            }
        }
        return allClassesMap;
    }

    /** Recursively creates the map of all classes.
    * The entries in the map are built from
    * identifier - class element pairs.
    */
    private Map createClassesMap () {
        Map result = new HashMap(15);
        addClassElement(result, getClassElement());
        return result;
    }

    /** Adds given class element to the output map and
    * recurses on its inner classes and interfaces.
    */
    private void addClassElement (Map map, final ClassElement outer) {
        map.put(outer.getName(), outer);
        // recurse on inners
        ClassElement[] inners = null;
        try {
            inners = outer.getClasses();
        } catch (Throwable exc) {
            // rethrow only ThreadDeath, ignore otherwise
            if (exc instanceof ThreadDeath)
                throw (ThreadDeath)exc;
            return;
        }
        for (int i = 0; i < inners.length; i++) {
            addClassElement(map, inners[i]);
        }
    }

    /** Lock the underlaing document to have exclusive access to it and could make changes
    * on this SourceElement.
    *
    * @param run the action to run
    */
    public void runAtomic (Runnable run) {
        run.run();
    }

    /** Executes given runnable in "user mode" does not allowing any modifications
    * to parts of text marked as guarded. The actions should be run as "atomic" so
    * either happen all at once or none at all (if a guarded block should be modified).
    *
    * @param run the action to run
    */
    public void runAtomicAsUser (Runnable run) {
        run.run();
    }

    /** DataObject cookie supported.
    * @return data object cookie or null
    */
    public Node.Cookie getCookie (Class type) {
        if (type.equals(DataObject.class) || type.equals(MultiDataObject.class) ||
                ClassDataObject.class.isAssignableFrom(type)) {
            return cdo;
        } else if (type == SourceElement.Impl.class) {
            return this;
        }
        return null;
    }

    public Object readResolve() {
        return new SourceElement(this);
    }
}

/*
* Log
*  11   src-jtulach1.10        1/20/00  David Simonek   #2119 bugfix
*  10   src-jtulach1.9         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  9    src-jtulach1.8         9/14/99  David Simonek   classes with no package 
*       now supported correctly
*  8    src-jtulach1.7         8/9/99   Ian Formanek    Generated Serial Version 
*       UID
*  7    src-jtulach1.6         7/8/99   Petr Hamernik   runAtomic simple 
*       implementation
*  6    src-jtulach1.5         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  5    src-jtulach1.4         4/1/99   Jan Jancura     Object browser support
*  4    src-jtulach1.3         3/26/99  David Simonek   properties, actions 
*       completed, more robust now
*  3    src-jtulach1.2         2/17/99  Petr Hamernik   serialization changed.
*  2    src-jtulach1.1         2/11/99  David Simonek   
*  1    src-jtulach1.0         1/29/99  David Simonek   
* $
*/
