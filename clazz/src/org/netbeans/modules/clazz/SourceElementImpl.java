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

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.JavaPackage;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.classfile.ClassFile;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.Node;
import org.openide.src.*;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/** The implementation of source element for class objects.
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Dafe Simonek, Jan Jancura
*/
final class SourceElementImpl extends MemberElementImpl
    implements SourceElement.Impl, ElementProperties, Node.Cookie, TaskListener {

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
    
    Task    loadingTask;
    
    int     status = -1;
    
    private transient boolean attached;
    
    static final long serialVersionUID =-4870331896218546842L;
    
    SourceElementImpl (ClassDataObject cdo) {
        this((Resource)null, cdo);
    }
        
    /** Creates object with asociated class and with asociated
    * class data object which created this source element (can be null).
    */
    public SourceElementImpl (Resource res, ClassDataObject cdo) {
        super(res);
        this.cdo = cdo;
    }
        
    public void setResource(Resource data) {
        int oldStatus;
        int newStatus;

        synchronized (this) {
            oldStatus = getStatus();
            this.data = data;
            topClass = null;
            allClasses = null;
            newStatus = getStatus();
            loadingTask = null;
        }
        if (oldStatus != SourceElement.STATUS_NOT) {
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
    
    Resource getResource() {
        checkData();
        return (Resource)data;
    }

    /** @return The package of class which we are representing.
    */
    public Identifier getPackage () {
        if (getResource() == null)
            return null;
        if (packg != null)
            return packg;
        JavaPackage p = (JavaPackage) getResource().refImmediateComposite();
        if (p == null)
            return null;
        return packg = Identifier.create(p.getName());
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
        checkData();
        if (data == null)
            return NO_CLASSES;
        return new ClassElement[] { getClassElement() };
    }

    /** Finds an inner class with given name.
    * @param name the name to look for
    * @return the element or null if such class does not exist
    */
    public ClassElement getClass (Identifier name) {
        ClassElement el = getClassElement();
        String srcName = name.getSourceName();
        String fullName = name.getFullName();
        Identifier idEl = el.getName();
        String srcEl = idEl.getSourceName();
        String fullEl = idEl.getFullName();
        
        if (srcEl.equals(srcName)) {
            if (srcName.equals(fullName) || fullEl.equals(fullName))
                return el;
            else
                return null;
        } else if (fullEl.equals(fullName)) {
            return el;
        }
        return null;
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
        if (status != -1) {
            return status;
        }
        int s;
        if (data != null) {
            s = SourceElement.STATUS_OK;
        } else {
            Task t = prepare();
            if (t.isFinished()) {
                checkData();
                s = data == null ? SourceElement.STATUS_ERROR : SourceElement.STATUS_OK;
            } else {
                synchronized (this) {
                    if (!attached) {
                        attached = true;
                        t.addTaskListener(this);
                    }
                }
                s = SourceElement.STATUS_NOT;
            }
        }
        setStatus(s);
        return s;
    }
    
    void setStatus(int newStatus) {
        int old;
        
        synchronized (this) {
            if (status == newStatus)
                return;
            old = status;
            status = newStatus;
        }
        if (old != -1)
            firePropertyChange(ElementProperties.PROP_STATUS, new Integer(old), new Integer(newStatus));
    }
    
    void checkData() {
        if (data == null) {
            FileObject fo=cdo.getPrimaryFile();

            data=JavaMetamodel.getManager().getResource(fo);
        }
    }

    /** Returns empty task, because we don't need any preparation.
    */
    public Task prepare() {
        return Task.EMPTY;
    }
    
    /** .class file are always read only for Java Hierarchy API
    */
    public boolean isReadOnly() {
	return true;
    }

    /************* utility methods *********/

    /** Returns class element for asociated class data.
    * Care must be taken, 'cause we are playing with soft reference.
    */
    private ClassElement getClassElement () {
        ClassElement result =
            (topClass == null) ? null : (ClassElement)topClass.get();
        if (result == null) {
            if (getResource() == null)
                return null;
            JavaClass[] c = (JavaClass[])getResource().getClassifiers().toArray(new JavaClass[0]);
            if (c.length != 1)
                return null;
            result = new ClassElement(new ClassElementImpl(c[0]), (SourceElement)element);
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
            checkData();
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

    /**
     * Tries to acquire data file from the owning DataObject, and parse
     * it using Classfile library.
     */
    public void run() {
        if (cdo == null) {
            throw new IllegalStateException("Cannot load classfile without " // NOI18N
                + "the DataObject"); // NOI18N
        }
        setResource(null);
    }
    
    public void taskFinished(Task task) {
        checkData();
        attached = false;
        task.removeTaskListener(this);
        setStatus(data == null ? SourceElement.STATUS_ERROR : SourceElement.STATUS_OK);
    }
}
