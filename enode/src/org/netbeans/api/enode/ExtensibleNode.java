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

package org.netbeans.api.enode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.Icon;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;


/**
 * A node capable of reading the list of actions from
 * the system file system. The content of its lookup can also be
 * specified declaratively. 
 * @author David Strupl
 */
public class ExtensibleNode extends AbstractNode {
    
    /** Folder on the system filesystem (context in the Registry)
     * where actions for extensible nodes are stored.
     */
    public static final String E_NODE_ACTIONS = "/ExtensibleNode/Actions/"; // NOI18N
    
    /** Folder on the system filesystem (context in the Registry)
     * where lookup objects for extensible nodes are stored.
     */
    public static final String E_NODE_LOOKUP = "/ExtensibleNode/Lookup/"; // NOI18N
    
    /** Folder on the system filesystem (context in the Registry)
     * where the icons base dirs are stored.
     */
    public static final String E_NODE_ICONS = "/ExtensibleNode/Icons/"; // NOI18N
    
    /** Folder on the system filesystem (context in the Registry)
     * where submenu information is kept.
     */
    public static final String E_NODE_SUBMENUS = "/ExtensibleNode/SubMenu/"; // NOI18N
    /**
     * Our Registry context paths.
     */
    private String[] paths;

    /**
     * Reference the implementation of the actions finder.
     */
    private ExtensibleActions actionManager;
    
    /**
     * Reference the implementation of the icons finder.
     */
    private ExtensibleIcons iconManager;
    
    /**
     * Listener attached to iconManager.
     */
    private IconChangeListener iconChangeListener;
    
    /**
     * The configured icons can be switched via setting the icon name.
     */
    private String iconName;
    
    /**
     * Creates a new instance of ExtensibleNode. The paths
     * parameter is used as a base directory for finding
     * actions, lookup objects and icon.
     * @param path folder path on the system file system
     * @param useHierarchicalPath whether the content of parent folders
     *      up to the root for a given entity (actions, lookup) is to be
     *      taken into account when searching for the objects
     */
    public ExtensibleNode(String path, boolean useHierarchicalPath) {
        this(useHierarchicalPath ? 
                computeHierarchicalPaths(path) : 
                new String[] { path }
        );
    }
    
    /**
     * Creates a new instance of ExtensibleNode. The paths
     * parameter is used as a base directory for finding
     * actions, lookup objects and icon.
     * @param path folder path on the system file system
     */
    public ExtensibleNode(String[] paths) {
        this(Children.LEAF, paths);
    }
    
    /**
     * Creates a new instance of ExtensibleNode. The paths
     * parameter is used as a base directory for finding
     * actions, lookup objects and icon.
     * @param ch children of the node
     * @param path folder path on the system file system
     * @param useHierarchicalPath whether the content of parent folders
     *      up to the root for a given entity (actions, lookup) is to be
     *      taken into account when searching for the objects
     */
    public ExtensibleNode(Children ch, String path, boolean useHierarchicalPath) {
        this(ch, useHierarchicalPath ? 
                computeHierarchicalPaths(path) : 
                new String[] { path }
        );
    }
    
    /**
     * Creates a new instance of ExtensibleNode. The paths
     * parameter is used as a base directory for finding
     * actions, lookup objects and icon.
     * @param ch children of the node
     * @param path folder path on the system file system
     */
    public ExtensibleNode(Children ch, String[] paths) {
        this(ch, paths, new ExtensibleLookup());
    }
    
    /**
     * Creates a new instance of ExtensibleNode. The paths
     * parameter is used as a base directory for finding
     * actions and icon. <EM> Warning:</EM> By using this constructor
     * you are responsible for creating the lookup for this node. The
     * extensible lookup is NOT used in this case.
     * @param ch children of the node
     * @param path folder path on the system file system
     * @param Lookup l used as a lookup for this node. 
     *      <EM> Warning:</EM> By using this constructor
     *      you are responsible for creating the lookup for this node. The
     *      extensible lookup is NOT used in this case.
     * @param useHierarchicalPath whether the content of parent folders
     *      up to the root for a given entity (actions, lookup) is to be
     *      taken into account when searching for the objects
     */
    public ExtensibleNode(Children ch, Lookup l, String path, boolean useHierarchicalPath) {
        this(ch, l, useHierarchicalPath ? 
                computeHierarchicalPaths(path) : 
                new String[] { path }
        );
    }
    
    /**
     * Creates a new instance of ExtensibleNode. The paths
     * parameter is used as a base directory for finding
     * actions and icon. <EM> Warning:</EM> By using this constructor
     * you are responsible for creating the lookup for this node. The
     * extensible lookup is NOT used in this case.
     * @param ch children of the node
     * @param path folder path on the system file system
     * @param Lookup l used as a lookup for this node. 
     *      <EM> Warning:</EM> By using this constructor
     *      you are responsible for creating the lookup for this node. The
     *      extensible lookup is NOT used in this case.
     */
    public ExtensibleNode(Children ch, Lookup l, String[] paths) {
        super(ch, l);
        this.paths = paths;
    }
    
    /**
     * Private constructor taking the lookup argument. The lookup is
     * not fully initialized until the call to <code>setExtensibleNode</code>.
     */
    private ExtensibleNode(Children ch, String[] paths, ExtensibleLookup l) {
        super(ch, l);
        this.paths = paths;
        l.setNode(this);
    }
    
    /**
     * Overriding superclass method. This implementaion can call
     * super.getActions or reads the actions list from the system file system
     * (layer files, Registry).
     * @param context please see <code>AbstractNode.getActions(boolean)</code>
     *      for details regarding this argument
     */
    public Action[] getActions (boolean context) {
        if (context) {
            return super.getActions(context);
        }
        return getActionManager().getActions();
    }
    
    /**
     * Overriden to fetch the icons from the ExtensibleIcons instance.
     */
    public Image getIcon(int type) {
        int size = (type == 1 || type == 3) ? 16 : 32;
        ImageIcon ii = null;
        if (getIconName() == null) {
            ii = getIconManager().getDefaultIcon(size);
        } else {            
            ii = getIconManager().getIcon(getIconName(), size);
        }
        return ii.getImage();
    }
    
    /**
     * Overriden to fetch the icons from the ExtensibleIcons instance.
     */
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
    
    /**
     * Lazy initialization of the actions manager
     */
    private ExtensibleActions getActionManager() {
        if (actionManager == null) {
            actionManager = ExtensibleActions.getInstance(getPaths());
        }
        return actionManager;
    }
    
    /**
     * Lazy initialization of the icon manager
     */
    private ExtensibleIcons getIconManager() {
        if (iconManager == null) {
            iconManager = ExtensibleIcons.getInstance(getPaths());
            iconManager.addPropertyChangeListener(getIconChangeListener());
        }
        return iconManager;
    }
    
    /**
     * Getter for the paths on the system file system (Registry).
     * @return String[] the entries in the resulting array do not contain
     *      the prefix E_NODE_ACTIONS, E_NODE_LOOKUP. So the returned paths are
     *      not absolute but relative (the same paths as are passed to one of the
     *      constructors)
     */
    public final String[] getPaths() {
        return paths;
    }
    
    /** Sets new paths. The paths
     * parameter is used as a base directory for finding
     * actions, lookup objects and icon.
     * @param path folder path on the system file system
     * @param useHierarchicalPath whether the content of parent folders
     *      up to the root for a given entity (actions, lookup) is to be
     *      taken into account when searching for the objects
     */
    public final void setPaths(String path, boolean useHierarchicalPath) {
        setPaths(useHierarchicalPath ? 
                computeHierarchicalPaths(path) : 
                new String[] { path });
    }
    
    /** Sets new paths. The paths
     * parameter is used as a base directory for finding
     * actions, lookup objects and icon.
     * @param paths folders on the system file system
     */
    public final void setPaths(String[] paths) {
        if (iconChangeListener != null) {
            iconManager.removePropertyChangeListener(iconChangeListener);
        }
        
        // clear cached values for icons and actions
        iconManager = null;
        actionManager = null;
        
        // lookup update
        Lookup myLookup = getLookup();
        if (myLookup instanceof ExtensibleLookup) {
            ExtensibleLookup el = (ExtensibleLookup)myLookup;
            el.setNode(this);
        }
        
        // fire
        Object oldValue = this.paths;
        this.paths = paths;
        firePropertyChange("paths", oldValue, paths);
        
        fireIconChange();
        fireOpenedIconChange();
    }
    
    /**
     * Currently selected name of the icon. Please set the icon name using
     * method setIconName. If setIconName was not called a defualt icon
     * will be used instead.
     */
    public final String getIconName() {
        return iconName;
    }
    
    /**
     * Setter for the name of the currently used icon. Calling this method
     * will refresh the displayed icon.
     */
    public final void setIconName(String name) {
        Object oldVal = iconName;
        iconName = name;
        firePropertyChange("iconName", oldVal, iconName); // NOI18N
        fireIconChange();
        fireOpenedIconChange();
    }
    
    /**
     * Lazy initialization of iconChangeListener.
     */
    private IconChangeListener getIconChangeListener() {
        if (iconChangeListener == null) {
            iconChangeListener = new IconChangeListener();
        }
        return iconChangeListener;
    }
    
    /**
     * For one folder returns an array containing the folder and
     * all of its parents. Uses '/' as the folder delimiter.
     * @return String[] For "a/b/c" returns { "a/b/c", "a/b", "a", "" }
     */
    static String[] computeHierarchicalPaths(String path) {
        if (path == null) {
            return new String[0];
        }
        String tmp = path;
        ArrayList list = new ArrayList();
        while (tmp.length() > 0) {
            list.add(tmp);
            if (tmp.lastIndexOf('/') >= 0) {
                tmp = tmp.substring(0, tmp.lastIndexOf('/'));
            } else {
                tmp = "";
            }
        }
        list.add(tmp); // add also the last ""
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * Listener attached to the ExtensibleIcons intstance.
     */
    private final class IconChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent ev) {
            fireIconChange();
            fireOpenedIconChange();
        }
    }
}
