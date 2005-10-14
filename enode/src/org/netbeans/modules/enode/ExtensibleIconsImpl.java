/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;

import javax.swing.ImageIcon;

import org.openide.ErrorManager;
import org.openide.util.WeakListeners;
import org.openide.util.RequestProcessor;

import org.netbeans.api.enode.*;
import org.netbeans.spi.enode.IconSet;
import org.netbeans.api.registry.*;

/**
 * ExtensibleIcons implementation. 
 * @author David Strupl
 */
public class ExtensibleIconsImpl extends ExtensibleIcons {
    
    private static ErrorManager log = ErrorManager.getDefault().getInstance(ExtensibleIconsImpl.class.getName());
    private static boolean LOGGABLE = log.isLoggable(ErrorManager.INFORMATIONAL);
    
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /**
     * Our paths.
     */
    private String[] paths;
    
    /**
     *
     */
    private IconSet iconSet;
    
    /**
     * We hold a reference to the listener for preventing
     * the garbage collection.
     */
    private Listener listener;
    
    /**
     * Prevent the listeners to be attached more than once.
     */
    private boolean listenersAttached = false;
    
    /**
     * To prevent garbage collection of context where we attached
     * listeners. We just add items to the set and never do anything
     * with them. But that is the reason why it is here - to hold
     * strong references to the Context objects.
     */
    private Set listenersAttachedTo = new HashSet();
    
    /**
     * Just remember the parameter enode.
     */
    public ExtensibleIconsImpl(String[] paths) {
        this.paths = paths;
    }
    
        /**
     * Returns the default icon size of this <tt>IconSet</tt>.
     *
     * @return The default icon size.
     */
    public int getDefaultSize(  ) {
        return getIconSet().getDefaultSize();
    }
    
    
    /**
     * Returns the icon defined by the name and icon size.
     *
     * @param name The name of the icon.
     * @param size The size of the icon.
     *
     * @return The icon defined by the name or size or a default
     *          icon with the given size.
     */
    public ImageIcon getIcon( String name, int size ) {
        return getIconSet().getIcon(name, size);
    }
    
    
    /**
     * Returns the default icon for the given size.
     *
     * @return The default icon for the given size. If no default icon
     *          is defined a default icon with the given size is returned.
     */
    public ImageIcon getDefaultIcon( int size ) {
        return getIconSet().getDefaultIcon(size);
    }
    
    
    /**
     * Returns the default icon with the default size.
     *
     * @return The default icon for the default size. If no default icon
     *          is defined a default icon with the default size is returned.
     *
     * qsee #getDefaultSize
     */
    public ImageIcon getDefaultIcon(  ) {
        return getIconSet().getDefaultIcon();
    }
    
    /**
     * Returns the description of this <tt>IconSet</tt>.
     *
     * @return The description of this <tt>IconSet</tt>.
     */
    public String getDescription( ) {
        return getIconSet().getDescription();
    }
    
    /**
     * provides the display name of the icon taken from the bundle file
     * configured for the <tt>IconSet</tt>. If no bundle file was defined
     * or if no entry was found the internal name is returned and an
     * exception is logged.
     *
     * @param name The internal name of the icon.
     *
     * @return The localized display name of the icon.
     */
    public String getIconDisplayName( String name ) {
        return getIconSet().getIconDisplayName(name);
    }
    
    
    /**
     * Returns the names of all icons configured in this <tt>IconSet</tt>
     * that match the given size.
     *
     * @param size The icon size.
     *
     * @return The names of all icons with the given size.
     */
    public String[] getAllIconNames( int size ) {
        return getIconSet().getAllIconNames(size);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }
    
    /** 
     *
     */
    IconSet getIconSet() {
        if (LOGGABLE) log.log("getIconSet() called on " + this);
        if (iconSet != null) {
            if (LOGGABLE) log.log("getIconSet() returning cached value");
            return iconSet;
        }
        ArrayList arr = new ArrayList ();
        for (int i = 0; i < paths.length; i++) {
            String path = ExtensibleNode.E_NODE_ICONS + paths[i];
            try {
                boolean exists = true;
                Context con = Context.getDefault().getSubcontext(path);
                if (con == null) {
                    con = ExtensibleLookupImpl.findExistingContext(path);
                    exists = false;
                }
                if (!listenersAttached) {
                    ContextListener l1 = getContextListener(con);
                    con.addContextListener(l1);
                    listenersAttachedTo.add(con);
                }
                if (! exists) {
                    if (LOGGABLE) log.log("getIconSet() path " + path + " does not exist.");
                    continue;
                }
                List objects = con.getOrderedObjects();
                Iterator it = objects.iterator();
                if (LOGGABLE) log.log("getIconSet() examining object on path " + path);
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (LOGGABLE) log.log("getIconSet() trying to add " + obj);
                    if (obj instanceof IconSet) {
                        arr.add(obj);
                    } else {
                        if (LOGGABLE) log.log(obj + " is not icon set!");
                    }
                }
            } catch (Exception ce) {
                log.notify(ErrorManager.INFORMATIONAL, ce); // NOI18N
            }
        }
        listenersAttached = true;
        if (arr.isEmpty()) {
            return new IconSet();
        }
        
        IconSet previous = null;
        for (Iterator i = arr.iterator(); i.hasNext(); ) {
            IconSet next = (IconSet) i.next();
            if (LOGGABLE) log.log("getIconSet() next " + next);
            if (previous != null) {
                if (previous.getDelegate() == null) {
                    if (LOGGABLE) log.log("getIconSet() setting " + next + " as delegate for " + previous);
                    previous.setDelegate(next);
                }
            }
            previous = next;
        }
        
        iconSet = (IconSet)arr.get(0);
        return iconSet;
    }

    /**
     * Lazy initialization of the listener variable. This method
     * will return a weak listener according to the type argument.
     * In both cases the weak listener references the object hold
     * by the <code> listener </code> variable.
     * @param type ObjectChangeListener or NamespaceChangeListener
     */
    private ContextListener getContextListener(Object source) {
        if (listener == null) {
            listener = new Listener();
        }
        return (ContextListener)WeakListeners.create(ContextListener.class, listener, source);
    }
    
    /**
     * If something has changed on the system file system, force
     * the icon reload.
     */
    private void changeIcon() {
        // clear our cache first
        iconSet = null;
        // fire in separate thread
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                pcs.firePropertyChange("icons", null, null);
            }
        });
    }
    
    /**
     * Whatever happens in the selected context this listener only calls
     * changeIcon. 
     */
    private class Listener implements ContextListener {
        public void attributeChanged(AttributeEvent evt) {
            changeIcon();
        }
        
        public void bindingChanged(BindingEvent evt) {
            changeIcon();
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            changeIcon();
        }
    }
}
