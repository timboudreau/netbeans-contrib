/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.api.enode;

import java.util.*;

import javax.swing.ImageIcon;
import java.beans.PropertyChangeListener;

import org.netbeans.modules.enode.ExtensibleIconsImpl;
import org.netbeans.modules.enode.TimedSoftReference;

/**
 * This object allows reading information about icons from the
 * configuration storage (system file system, registry). If you need an instance
 * of this class please use one of the provided static factory methods.
 * @author David Strupl
 */
public abstract class ExtensibleIcons {
    /**
     * Maps List<String> --> ExtensibleIcons. The key is list of
     * folders passed as paths parameter or computed by
     * ExtensibleNode.computeHierarchicalPaths().
     */
    private static Map cache = new HashMap();
    
    /**
     * This constructor will throw an exception if you attempt to call
     * it from your code. Please DO NOT create instances of this class -
     * use static factory methods getInstance.
     */
    protected ExtensibleIcons() {
        if (! getClass().equals(ExtensibleIconsImpl.class)) {
            throw new IllegalStateException("You cannot create a subclass of this class. Please read the JavaDoc comment"); // NOI18N
        }
    }
    
    /**
     * Returns the default icon size.
     *
     * @return The default icon size.
     */
    public abstract int getDefaultSize(  );
    
    
    /**
     * Returns the icon defined by the name and icon size.
     *
     * @param name The name of the icon.
     * @param size The size of the icon.
     *
     * @return The icon defined by the name or size or a default
     *          icon with the given size.
     */
    public abstract ImageIcon getIcon( String name, int size );
    
    
    /**
     * Returns the default icon for the given size.
     *
     * @return The default icon for the given size. If no default icon
     *          is defined a default icon with the given size is returned.
     */
    public abstract ImageIcon getDefaultIcon( int size );
    
    
    /**
     * Returns the default icon with the default size.
     *
     * @return The default icon for the default size. If no default icon
     *          is defined a default icon with the default size is returned.
     *
     * qsee #getDefaultSize
     */
    public abstract ImageIcon getDefaultIcon(  );
    
    /**
     * Returns the description of the icon.
     *
     * @return The description of the icon.
     */
    public abstract String getDescription( );
    
    /**
     * Provides the display name of the icon taken from the bundle file.
     * If no bundle file was defined
     * or if no entry was found the internal name is returned and an
     * exception is logged.
     *
     * @param name The internal name of the icon.
     *
     * @return The localized display name of the icon.
     */
    public abstract String getIconDisplayName( String name );
    
    
    /**
     * Returns the names of all icons configured
     * that match the given size.
     *
     * @param size The icon size.
     *
     * @return The names of all icons with the given size.
     */
    public abstract String[] getAllIconNames( int size );

    /**
     * This object will fire a propety change when the configuration is changed
     * and the user should update the icons.
     */
    public abstract void addPropertyChangeListener(PropertyChangeListener pcl);
    
    /**
     * This object will fire a propety change when the configuration is changed
     * and the user should update the icons.
     */
    public abstract void removePropertyChangeListener(PropertyChangeListener pcl);
    
    /**
     * This method finds/creates an instance of ExtensibleIcons bound to given
     * path. Please use this method instead of constructor - it allows
     * caching/sharing the instances of this class.
     *
     * @param path Path to the folder where the configured icons
     * are stored.
     * @param recurse If set to true the parent folders contents
     * are also added to the result.
     * @return Fully initialized ExtensibleIcons object
     * configured using the parameters provided.
     */
    public static ExtensibleIcons getInstance(String path, boolean recurse) {
        String[] paths = null;
        if (recurse) {
            paths = ExtensibleNode.computeHierarchicalPaths(path);
        } else {
            paths = new String[] { path };
        }
        return getInstance(paths);
    }
    
    /**
     * This method finds/creates an instance of ExtensibleIcons bound to given
     * path. Please use this method instead of constructor - it allows
     * caching/sharing the instances of this class.
     *
     * @param paths Paths to the folders where the configured icons
     * are stored.
     * @return Fully initialized ExtensibleIcons object
     * configured using the parameters provided.
     */
    public static ExtensibleIcons getInstance(String[] paths) {
        // We use list as the key. It ensures that the hashCode will
        // be same for the arrays of equals String instances.
        Object key = Arrays.asList(paths);
        
        TimedSoftReference ref = null;
        synchronized (cache) {
            ref = (TimedSoftReference)cache.get(key);
        }
        ExtensibleIcons instance = null;
        if (ref != null) {
            instance = (ExtensibleIcons)ref.get();
        }
        if (instance == null) {
            instance = new ExtensibleIconsImpl(paths);
            synchronized (cache) {
                cache.put(key, new TimedSoftReference(instance, cache, key));
            }
        }
        return instance;
    }
}
