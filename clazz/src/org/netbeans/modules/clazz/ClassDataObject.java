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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Modifier;
import java.lang.ref.*;
import java.io.*;
import java.util.*;

import org.openide.cookies.ElementCookie;
import org.openide.cookies.SourceCookie;
import org.openide.cookies.InstanceCookie;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.loaders.DataObject;
import org.openide.loaders.FileEntry;
import org.openide.loaders.InstanceSupport;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;

import org.openide.src.SourceElement;
import org.openide.src.nodes.ElementNodeFactory;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.nodes.SourceChildren;
import org.openide.src.nodes.SourceElementFilter;

import org.openide.util.HelpCtx;
import org.openide.util.WeakListener;
import org.openide.loaders.DataObjectExistsException;


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
public class ClassDataObject extends org.openide.loaders.MultiDataObject 
    implements ElementCookie, CookieSet.Factory, SourceCookie {
    public static final String PROP_CLASS_LOADING_ERROR = "classLoadingError";
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
    
    /** Creates a new sourceless DataObject.
     */
    public ClassDataObject(final FileObject fo,final MultiFileLoader loader) 
        throws org.openide.loaders.DataObjectExistsException {
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
    
    private class PropL extends FileChangeAdapter implements PropertyChangeListener, Runnable {
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
            org.openide.util.RequestProcessor.postRequest(this, 100);
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
            s = new SourceElement(new SourceElementImpl(getMainClass(), this));
            srcEl = new WeakReference(s);
        }
        return s;
    }
    
    public Node.Cookie createCookie(Class desired) {
        if (desired == InstanceCookie.class || desired == InstanceCookie.Origin.class) {
            return createInstanceSupport();
        }
        return null;
    }
    
    protected Throwable getClassLoadingError() {
        if (!classLoaded)
            getMainClass();
        return this.classLoadingError;
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
            classLoaded = false;
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
                impl.setClassObject(getMainClass());
        }
    }
    
    /**
     * Returns the "main" class for this DataObject, or null if the class
     * cannot be loaded. In such case, it records the error trace into
     * {@link #classLoadingError} variable.
     */
    protected Class getMainClass() {
        Class mainClass = null;
        Throwable t = this.classLoadingError;
        try {
            // try to load the class.
            mainClass = createInstanceSupport().instanceClass();
            classLoadingError = null;
        } catch (RuntimeException ex) {
            classLoadingError = ex;
        } catch (IOException ex) {
            classLoadingError = ex;
        } catch (ClassNotFoundException ex) {
            classLoadingError = ex;
        }
        firePropertyChange(PROP_CLASS_LOADING_ERROR, t, classLoadingError);
        return mainClass;
    }
    
    /** Help context for this object.
    * @return help context
    */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ClassDataObject.class);
    }

    // implementation of ElementCookie ..........................................

    /**
     * Get the alternate node representation.
     * @return the node
     * @see org.openide.loaders.DataObject#getNodeDelegate
    */
    public Node getElementsParent () {
        ElementNodeFactory cef = getBrowserFactory();
        final SourceChildren sourceChildren = new SourceChildren (cef);
        SourceElementFilter sourceElementFilter = new SourceElementFilter();
        sourceElementFilter.setAllClasses (true);
        sourceChildren.setFilter (sourceElementFilter);

        Class ourClass = getMainClass();
        if (ourClass == null)
            return null;
        sourceChildren.setElement (
            new SourceElement(new SourceElementImpl(ourClass, this))
        );

        AbstractNode alteranteParent = new AbstractNode (sourceChildren) {
            {
                getCookieSet().add(sourceChildren);
            }
        };

        return alteranteParent;
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

    public Class getSuperclass () throws IOException, ClassNotFoundException {
        return createInstanceSupport().instanceClass ().getSuperclass ();
    }

    public String getModifiers () throws IOException, ClassNotFoundException {
        return Modifier.toString (createInstanceSupport().instanceClass().getModifiers());
    }

    public String getClassName () {
        try {
            return instanceSupport.instanceClass ().getName ();
        } catch (Exception e) {
            // ignore and return the instance name from the InstanceSupport,
            // which is by default the file name of the original instance
        }
        return instanceSupport.instanceName ();
    }

    public Class getBeanClass () throws IOException, ClassNotFoundException {
        return createInstanceSupport().instanceClass ();
    }

    // =============== The mechanism for regeisteing node factories ==============

    private static ArrayList explorerFactories = new ArrayList();
    private static ArrayList browserFactories = new ArrayList();

    static {
        explorerFactories.add( new ClassElementNodeFactory() );

        ClassElementNodeFactory cef = new ClassElementNodeFactory();
        cef.setGenerateForTree (true);

        browserFactories.add( cef ) ;
    }

    /** 
     * Adds another FilterFactory at the top of the current explorer factory chain.
     * The passed factory will be the first one contacted when a node for explorer
     * view will need to be created.
     * @param factory the factory to add
     */
    public static void addExplorerFilterFactory( FilterFactory factory ) {
        addFactory( explorerFactories, factory );
    }

    /**
     * Remove the specified factory from the factory chain
     * @param factory the factory to remove
     */
    public static void removeExplorerFilterFactory( FilterFactory factory ) {
        removeFactory( explorerFactories, factory );
    }

    public static ElementNodeFactory getExplorerFactory() {
        return (ElementNodeFactory)explorerFactories.get( explorerFactories.size() - 1);
    }

    public static void addBrowserFilterFactory( FilterFactory factory ) {
        addFactory( browserFactories, factory );
    }

    public static void removeBrowserFilterFactory( FilterFactory factory ) {
        removeFactory( browserFactories, factory );
    }

    public static ElementNodeFactory getBrowserFactory() {
        return (ElementNodeFactory)browserFactories.get( browserFactories.size() - 1 );
    }

    private static synchronized void addFactory( List factories, FilterFactory factory ) {
        factory.attachTo( (ElementNodeFactory)factories.get( factories.size() - 1 ) );
        factories.add( factory );
    }

    private static synchronized void removeFactory( List factories, FilterFactory factory ) {
        int index = factories.indexOf( factory );

        if ( index <= 0 )
            return;
        else if ( index == factories.size() - 1 )
            factories.remove( index );
        else {
            ((FilterFactory)factories.get( index + 1 )).attachTo( (ElementNodeFactory)factories.get( index - 1 ) );
            factories.remove( index );
        }
    }
    
    protected final class ClazzInstanceSupport extends org.openide.loaders.InstanceSupport.Origin {
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

            org.openide.execution.NbClassLoader loader = 
                new org.openide.execution.NbClassLoader(
                    new org.openide.filesystems.FileSystem[] {},
                    org.openide.TopManager.getDefault().currentClassLoader());
            loader.setDefaultPermissions(perms);
            return loader;
        }
    }
}
