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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.Repository;

import java.lang.reflect.Modifier;
import org.openide.util.WeakListener;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;

import org.openide.nodes.Node.Cookie;
import org.openide.nodes.CookieSet.Factory;
import org.netbeans.modules.classfile.ClassFile;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.InstanceSupport;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.io.BufferedInputStream;
import org.openide.src.nodes.ElementNodeFactory;
import org.openide.src.nodes.SourceElementFilter;
import org.openide.src.nodes.SourceChildren;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.src.ConstructorElement;
import org.openide.src.Identifier;
import org.openide.src.Type;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.CookieSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.openide.cookies.SourceCookie;
import org.openide.cookies.InstanceCookie;
import org.openide.ErrorManager;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;




/** This DataObject loads sourceless classes and provides a common framework
 * for presenting them in the IDE.
 * The descendants define specific behaviour, restrictions and operations.<P>
 * <B>Note:</B> The previous version of ClassDataObject has become CompiledDataObject.
 * Because ClassDataObject was public, the name was retained for compatibility reasons.
 * Method behaviour specific to compiled classes was moved to CompiledDataObject and
 * the behaviour specific to serialized objects was moved into SerDataObject.
 *
 * @author sdedic
 * @version 1.0
 */
public class ClassDataObject extends MultiDataObject implements Factory, SourceCookie {
    public static final String PROP_CLASS_LOADING_ERROR = "classLoadingError"; // NOI18N
    /**
     * Holds an exception that occured during an attempt to create the class.
     */
    private Throwable classLoadingError;

    /** The generic type */
    protected static final int UNDECIDED = 0;

    /** The DataObject contains an applet */
    protected static final int APPLET = 1;

    /** The DataObject is an application. */
    protected static final int APPLICATION = 2;

    /** CHANGE!!! */
    static final long serialVersionUID = -1;

    /** Support for working with class */
    transient private InstanceSupport.Origin instanceSupport;

    transient private boolean sourceCreated;
    /**
     * Quick check whether the class was already loaded.
     */
    transient boolean classLoaded;
    
    transient PropL propL;
    
    transient Reference srcEl = new WeakReference(null);
    
    transient private ClassFile mainClass; // don't access directly, use getMainClass() instead
    
    /** Creates a new sourceless DataObject.
     */
    public ClassDataObject(final FileObject fo, final MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
    }

    static ClassDataObject createSerDataObject(FileObject fo, ClassDataLoader dl)
                throws DataObjectExistsException, IOException {
        return new SerDataObject(fo, dl);
    }

    static ClassDataObject createCompiledDataObject(FileObject fo, ClassDataLoader dl)
                throws DataObjectExistsException, IOException {
        return new CompiledDataObject(fo, dl);
    }
    
    private class PropL extends FileChangeAdapter implements Runnable, PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent ev) {
            String prop = ev.getPropertyName();
            if (PROP_PRIMARY_FILE.equals(prop)) {
                FileObject p = getPrimaryFile();
                p.addFileChangeListener(WeakListener.fileChange(this, getPrimaryFile()));
                postReload();
            }
        }
        
        public void fileChanged(FileEvent ev) {
            postReload();
        }
        
        public void run() {
            forceReload();
        }
        
        private void postReload() {
            Util.getClassProcessor().post(this, 100);
        }
    }
    
    protected void initCookies() {
        getCookieSet().add(SourceCookie.class, this);
    }

    /**
     * Creates InstanceSupport for the primary .class file.
     */
    protected InstanceSupport.Origin createInstanceSupport() {
        if (instanceSupport != null)
            return instanceSupport;
        synchronized (this) {
            if (instanceSupport == null) {
                instanceSupport = new ClazzInstanceSupport(getPrimaryEntry());
                if (propL == null) {
                    propL = new PropL();
                    FileObject p = getPrimaryFile();
                    p.addFileChangeListener(WeakListener.fileChange(propL, p));
                }
            }
        }
        return instanceSupport;
    }
    
    public SourceElement getSource() {
        SourceElement s;

        s = (SourceElement)srcEl.get();
        if (s != null)
            return s;
        synchronized (this) {
            s = (SourceElement)srcEl.get();
            if (s != null)
                return s;
            sourceCreated = true;
            s = new SourceElement(new SourceElementImpl(getClassFile(), this));
            srcEl = new WeakReference(s);
        }
        return s;
    }
    
    public Cookie createCookie(Class desired) {
        if (desired == InstanceCookie.class || desired == InstanceCookie.Origin.class) {
            return createInstanceSupport();
        }
        return null;
    }
    
    protected Throwable getClassLoadingError() {
        getClassFile();
        return classLoadingError;
    }
    
    /**
     * Forces reload of the class and the whole instance support.
     */
    protected void forceReload() {
        CookieSet s = getCookieSet();
        InstanceCookie prevCookie;
    
        prevCookie = (InstanceCookie)getCookie(InstanceCookie.class);
        synchronized (this) {
            instanceSupport = null;
            mainClass = null;
        }
        // if the previous support was != null, it recreates the cookie
        // (and fires PROP_COOKIE change).
        if (prevCookie != null) {
            s.remove(prevCookie);
            s.add(new Class[] {
                InstanceCookie.Origin.class
            }, this);
        }

        if (sourceCreated) {
            SourceCookie sc = (SourceCookie)getCookie(SourceCookie.class);
            SourceElementImpl impl = (SourceElementImpl)sc.getSource().getCookie(SourceElement.Impl.class);
            if (impl != null)
                impl.setClassObject(getClassFile());
        }
    }
    
    /**
     * Returns the "main" class for this DataObject, or null if the class
     * cannot be loaded. In such case, it records the error trace into
     * {@link #classLoadingError} variable.
     */
    protected ClassFile getClassFile() {
        if (mainClass==null) {        
            Throwable t = this.classLoadingError;
            classLoadingError = null;
            try {
                mainClass = loadClassFile();            
            } catch (RuntimeException ex) {
                classLoadingError = ex;
            } catch (IOException ex) {
                classLoadingError = ex;
            } catch (ClassNotFoundException ex) {
                classLoadingError = ex;
            }
            if (classLoadingError != null)
                firePropertyChange(PROP_CLASS_LOADING_ERROR, t, classLoadingError);
        }
        return mainClass;
    }
    
    protected ClassElement getMainClass() {
        ClassElement ce[]=getSource().getClasses();
        
        if (ce.length==0)
            return null;
        return ce[0];
    }
    
    protected ClassFile loadClassFile() throws IOException,ClassNotFoundException {
        InputStream stream=getPrimaryEntry().getFile().getInputStream();
        
        if (stream==null)
            return null;
        try {
            return new ClassFile(stream,false);
        } finally {
            stream.close();
        }   
    }
    
    /** Help context for this object.
    * @return help context
    */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ClassDataObject.class);
    }

    // DataObject implementation .............................................

    /** Getter for copy action.
    * @return true if the object can be copied
    */
    public boolean isCopyAllowed () {
        return true;    
    }

    public boolean isMoveAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    public boolean isRenameAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    // =======================================================================
    // Various properties describing the file although it is questionable to 
    // put them at the file level. At least JavaBean and Applet are properties
    // of a class, not a file or serialized object.
    public boolean isJavaBean () {
        return createInstanceSupport().isJavaBean();
    }

    public boolean isApplet () {
        return createInstanceSupport().isApplet ();
    }

    public boolean isInterface () {
        return createInstanceSupport().isInterface ();
    }

    public String getSuperclass () {
        ClassElement ce=getMainClass();
        
        if (ce==null)
            return "";
        return ce.getSuperclass().getFullName();
    }

    public String getModifiers () throws IOException, ClassNotFoundException {
        ClassElement ce=getMainClass();
        
        if (ce==null)
            throw new ClassNotFoundException();
        return Modifier.toString(ce.getModifiers());
    }

    public String getClassName () {
        return createInstanceSupport().instanceName ();
    }

    public Class getBeanClass () throws IOException, ClassNotFoundException {
        return createInstanceSupport().instanceClass ();
    }

    // =============== The mechanism for regeisteing node factories ==============

    // =============== The mechanism for regeisteing node factories ==============
    
    private static NodeFactoryPool explorerFactories;
    private static NodeFactoryPool browserFactories;
    private static ElementNodeFactory basicBrowser;
    private static ElementNodeFactory basicExplorer;

    /**
     * DO NOT USE THIS METHOD!!! <P>
     * This method is intended to be called only during initialization of java
     * module-provided node factories from the installation layer. It won't
     * be maintained for compatibility reasons.
     */
    synchronized static ElementNodeFactory createBasicExplorerFactory() {
        if (basicExplorer == null) {
            basicExplorer = new ClassElementNodeFactory();
        }
        return basicExplorer;
    }
    
    /**
     * DO NOT USE THIS METHOD!!! <P>
     * This method is intended to be called only during initialization of java
     * module-provided node factories from the installation layer. It won't
     * be maintained for compatibility reasons.
     */
    synchronized static ElementNodeFactory createBasicBrowserFactory() {
        if (basicBrowser == null) {
            basicBrowser = new ClassElementNodeFactory();
            ((ClassElementNodeFactory)basicBrowser).setGenerateForTree (true);
        }
        return basicBrowser;
    }

    public static ElementNodeFactory getExplorerFactory() {
        NodeFactoryPool pool = createExplorerFactory();
        ElementNodeFactory f = null;
        
        if (pool != null)
            f = pool.getHead();
        if (f == null)
            f = createBasicExplorerFactory();
        return f;
    }
    
    public static ElementNodeFactory getBrowserFactory() {
        NodeFactoryPool pool = createBrowserFactory();
        ElementNodeFactory f = null;
        
        if (pool != null)
            f = pool.getHead();
        if (f == null)
            f = createBasicBrowserFactory();
        return f;
    }

    static NodeFactoryPool createFactoryPool(String folderName, ElementNodeFactory def) {
        FileObject f = Repository.getDefault().findResource(folderName);
	if (f == null)
    	    return null;
        try {
            DataFolder folder = (DataFolder)DataObject.find(f).getCookie(DataFolder.class);
            return new NodeFactoryPool(folder, def);
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
    }
    
    synchronized static NodeFactoryPool createBrowserFactory() {
        if (browserFactories != null)
            return browserFactories;
        browserFactories = createFactoryPool("/NodeFactories/clazz/objectbrowser", createBasicBrowserFactory());
        return browserFactories;
    }
    
    synchronized static NodeFactoryPool createExplorerFactory() {
        if (explorerFactories != null)
            return explorerFactories;
        explorerFactories = createFactoryPool("/NodeFactories/clazz/explorer", createBasicExplorerFactory());
        return explorerFactories;
    }

    /**
     * @deprecated use installation layer for registering a factory for the the whole
     * time a module is installed. Note: This feature will be dropped in the next
     * release.
     */
    public static void addExplorerFilterFactory( FilterFactory factory ) {
        NodeFactoryPool p = createExplorerFactory();
        if (p != null)
            p.addFactory(factory);
    }

    /**
     * @deprecated use installation layer for registering a factory for the the whole
     * time a module is installed. Note: This feature will be dropped in the next
     * release.
     */
    public static void removeExplorerFilterFactory( FilterFactory factory ) {
        NodeFactoryPool p = createExplorerFactory();
        if (p != null)
            p.removeFactory(factory);
    }

    /**
     * @deprecated use installation layer for registering a factory for the the whole
     * time a module is installed. Note: This feature will be dropped in the next
     * release.
     */
    public static void addBrowserFilterFactory(FilterFactory factory) {
        NodeFactoryPool p = createBrowserFactory();
        if (p != null)
            p.addFactory(factory);
    }

    /**
     * @deprecated use installation layer for registering a factory for the the whole
     * time a module is installed. Note: This feature will be dropped in the next
     * release.
     */
    public static void removeBrowserFilterFactory( FilterFactory factory ) {
        NodeFactoryPool p = createBrowserFactory();
        if (p != null)
            p.removeFactory(factory);
    }

    protected final class ClazzInstanceSupport extends InstanceSupport.Origin {

        /** the class is bean */
        private Boolean bean;

        /** the class is executable */
        private Boolean executable;

        ClazzInstanceSupport(MultiDataObject.Entry entry) {
            super(entry);
        }
        
        public Class instanceClass() throws IOException, ClassNotFoundException {
            try {
                Class c = super.instanceClass();
                return c;
            } catch (RuntimeException ex) {
                // convert RuntimeExceptions -> CNFE
                ClassDataObject.this.classLoadingError = ex;
                throw new ClassNotFoundException(ex.getMessage());
            }
        }
        
        /** 
         * Creates class loader that permits <<ALL FILES>> and all properties to be read,
         * and is based on currentClassLoader().
         */
        protected ClassLoader createClassLoader() {
            java.security.Permissions perms = new java.security.Permissions();
            perms.add(new java.io.FilePermission("<<ALL FILES>>", "read")); // NOI18N
            perms.add(new java.util.PropertyPermission("*", "read")); // NOI18N
            perms.setReadOnly();

            org.openide.execution.NbClassLoader loader = new org.openide.execution.NbClassLoader();
            loader.setDefaultPermissions(perms);
            return loader;
        }
        
        /** Is this a JavaBean?
         * @return <code>true</code> if this class represents JavaBean (is public and has a public default constructor).
         */
        public boolean isJavaBean() {
        if (bean != null) return bean.booleanValue ();
        
        // if from ser file => definitely it is a java bean
        if (isSerialized ()) {
            bean = Boolean.TRUE;
            return true;
        }
        
        // try to find out...
        try {
            ClassElement clazz = getMainClass();
            
            if (clazz==null) return false;
            int modif = clazz.getModifiers();
            if (!Modifier.isPublic(modif) || Modifier.isAbstract(modif)) {
                bean = Boolean.FALSE;
                return false;
            }
            ConstructorElement c=clazz.getConstructor(new Type[0]);
            if ((c == null) || !Modifier.isPublic(c.getModifiers())) {
                bean = Boolean.FALSE;
                return false;
            }
            // check: if the class is an inner class, all outer classes have
            // to be public and in the static context:
            
            for (ClassElement outer = clazz.getDeclaringClass(); outer != null; outer = outer.getDeclaringClass()) {
                // check if the enclosed class is static
                if (!Modifier.isStatic(modif)) {
                    bean = Boolean.FALSE;
                    return false;
                }
                modif = outer.getModifiers();
                // ... and the enclosing class is public
                if (!Modifier.isPublic(modif)) {
                    bean = Boolean.FALSE;
                    return false;
                }
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (ThreadDeath t) {
            throw t;
        } catch (Throwable t) {
            // false when other errors occur (NoClassDefFoundError etc...)
            bean = Boolean.FALSE;
            return false;
        }
        // okay, this is bean...
        //    return isBean = java.io.Serializable.class.isAssignableFrom (clazz);
        bean = Boolean.TRUE;
        return true;   
        }
        
        /** Is this an interface?
         * @return <code>true</code> if the class is an interface
         */
        public boolean isInterface() {
            ClassElement ce=getMainClass();
            
            return (ce==null)?false:ce.isInterface();
        }
                
        public String instanceName() {
            ClassElement ce=getMainClass();
            
            if (ce==null)
                return super.instanceName();
            return ce.getName().getFullName();
        }
        
        /*Query to found out if the object created by this cookie is 
        * instance of given type.
        * @param type the class type we want to check
        * @return true if this cookie can produce object of given type
        */
        public boolean instanceOf(Class type) {
            String className=type.getName();
            ClassElement ce=getMainClass();
            
            if (ce == null)
                return false;
            boolean isClassType = !type.isInterface();
            String typename = type.getName().replace('$', '.');
            Identifier id;
            LinkedList l = new LinkedList();
            
            do {
                if (ce.getName().getFullName().equals(typename)) 
                    return true;
                id = ce.getSuperclass();
                Identifier[] itfs = ce.getInterfaces();
                for (int i = 0; i < itfs.length; i++) {
                    l.addLast(itfs[i]);
                }
                if (id == null) {
                    if (l.isEmpty())
                        return false;
                    else
                        id = (Identifier)l.removeFirst();
                }
                ce = ClassElement.forName(id.getFullName());
                while (ce == null && !l.isEmpty()) 
                    ce = ClassElement.forName(((Identifier)l.removeFirst()).getFullName());
            } while (ce != null);
            return false;
        }

        /** Is this a standalone executable?
         * @return <code>true</code> if this class has main method
         * (e.g., <code>public static void main (String[] arguments)</code>).
         */
        public boolean isExecutable() {
            if (executable == null) {
                ClassElement ce=getMainClass();

                executable = ((ce==null) ? false : ce.hasMainMethod()) ? Boolean.TRUE : Boolean.FALSE;
            }
            return executable.booleanValue ();
        }    
        
        /** Test whether the instance represents serialized version of a class
         * or not.
         * @return true if the file entry extension is ser
         */
        private boolean isSerialized () {
            return instanceOrigin().getExt().equals("ser"); // NOI18N
        }

        public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
            try {
                if (isSerialized()) {
                    // create from ser file
                    BufferedInputStream bis = new BufferedInputStream(instanceOrigin().getInputStream(), 1024);
                    CMObjectInputStream cis = new CMObjectInputStream(bis,createClassLoader());
                    Object o = null;
                    try {
                        o = cis.readObject();
                    } finally {
                        cis.close();
                    }
                    return o;
                } else {
                    return super.instanceCreate();
                }
            } catch (IOException ex) {
                // [PENDING] annotate with localized message
                ErrorManager.getDefault().annotate(ex, instanceName());
                throw ex;
            } catch (ClassNotFoundException ex) {
                throw ex;
            } catch (Exception e) {
                // turn other throwables into class not found ex.
                throw new ClassNotFoundException(e.toString(), e);
            } catch (LinkageError e) {
                throw new ClassNotFoundException(e.toString(), e);
                
            }
        }
        
        private final class CMObjectInputStream extends ObjectInputStream {
            
            private ClassLoader loader;
            
            protected CMObjectInputStream(InputStream s, ClassLoader cl) throws IOException {
                super(s);
                loader=cl;
            }
            
            protected Class resolveClass(ObjectStreamClass s) throws IOException, ClassNotFoundException {
                return Class.forName(s.getName(), false, loader);
            }
            
        }        
    }
}
