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

import java.lang.reflect.Modifier;
import java.io.*;
import java.util.*;

import org.openide.cookies.ElementCookie;
import org.openide.cookies.SourceCookie;

import org.openide.filesystems.FileObject;
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
public class ClassDataObject extends org.openide.loaders.MultiDataObject implements ElementCookie {

    /** The generic type */
    protected static final int UNDECIDED = 0;

    /** The DataObject contains an applet */
    protected static final int APPLET = 1;

    /** The DataObject is an application. */
    protected static final int APPLICATION = 2;

    /** CHANGE!!! */
    static final long serialVersionUID = -1;
    
    /** The flag for recognizing if cookies are initialized or not */
    transient private boolean cookiesInitialized = false;

    /** Support for working with class */
    transient protected InstanceSupport instanceSupport;

    /** Creates a new sourceless DataObject.
     */
    public ClassDataObject(final FileObject fo,final MultiFileLoader loader) 
        throws org.openide.loaders.DataObjectExistsException {
        super(fo, loader);
        instanceSupport = new InstanceSupport.Origin(getPrimaryEntry());
    }

    /** Initializes the DataObject and sets up relevant cookies.
     * Overriden by SerDataObject and ClassDataObject.
     */
    protected void initCookies() {
        Class ourClass = null;
        try {
            ourClass = instanceSupport.instanceClass();
        } catch (IOException ex) {
            return;
        } catch (ClassNotFoundException ex) {
            return;
        }
        CookieSet cs = getCookieSet();
        cs.add(new SourceSupport(ourClass, this));
    }

    /** Overrides superclass getCookie.<P>
    * Lazily initialize cookies. (When they are requested for the first time)
    */
    public Node.Cookie getCookie (Class type) {
        if (!cookiesInitialized) {
            cookiesInitialized = true;
            initCookies();
        }
        return super.getCookie(type);
    }

    /** Invalidates cookies in the cookies set. Next time getCookie is invoked,
     * cookies relevant to this object are added to the cookie set.
     */
    private void readObject (java.io.ObjectInputStream is)
    throws java.io.IOException, ClassNotFoundException {
        is.defaultReadObject();
        cookiesInitialized = false;
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
        SourceChildren sourceChildren = new SourceChildren (cef);
        SourceElementFilter sourceElementFilter = new SourceElementFilter();
        sourceElementFilter.setAllClasses (true);
        sourceChildren.setFilter (sourceElementFilter);
        try {
            Class ourClass = instanceSupport.instanceClass();
            sourceChildren.setElement (
                new SourceElement(new SourceElementImpl(ourClass, this))
            );
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }

        AbstractNode alteranteParent = new AbstractNode (sourceChildren);
        CookieSet cs = alteranteParent.getCookieSet();
        cs.add (sourceChildren);

        return alteranteParent;
    }

    // =======================================================================
    // Various properties describing the file although it is questionable to 
    // put them at the file level. At least JavaBean and Applet are properties
    // of a class, not a file or serialized object.
    public boolean isJavaBean () {
        return instanceSupport.isJavaBean();
    }

    public boolean isApplet () {
        return instanceSupport.isApplet ();
    }

    public boolean isInterface () {
        return instanceSupport.isInterface ();
    }

    public Class getSuperclass () throws IOException, ClassNotFoundException {
        return instanceSupport.instanceClass ().getSuperclass ();
    }

    public String getModifiers () throws IOException, ClassNotFoundException {
        return Modifier.toString (instanceSupport.instanceClass().getModifiers());
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
        return instanceSupport.instanceClass ();
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

    /** The implementation of the source cookie.
     * Class data object cannot implement source cookie directly,
     * because it's optional (if there's no instance cookie, then also
     * no source cookie is supported)
     */
    protected static final class SourceSupport extends Object implements SourceCookie {
        /** The class which acts as a source element data  */
        private Class data;
        
        /** Reference to outer class  */
        private ClassDataObject cdo;
        
        /** Creates source support with asociated class object  */
        SourceSupport(Class data,ClassDataObject cdo) {
            this.data = data;
            this.cdo = cdo;
        }
        
        /** @return The source element for this class data object  */
        public SourceElement getSource() {
            return new SourceElement(new SourceElementImpl(data, cdo));
        }
        
        
}
    
}
