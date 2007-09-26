/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import org.openide.util.WeakListeners;


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
     * Listener attached to iconManager (through its weak wrapper bellow).
     */
    private IconChangeListener iconChangeListener;
    
    /**
     * Weak Listener attached to iconManager.
     */
    private PropertyChangeListener weakIconChangeListener;

    
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
        if (ii == null) {
            return super.getIcon(type);
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
            PropertyChangeListener p = getIconChangeListener();
            weakIconChangeListener = WeakListeners.propertyChange(p, iconManager);
            iconManager.addPropertyChangeListener(weakIconChangeListener);
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
            iconManager.removePropertyChangeListener(weakIconChangeListener);
            iconChangeListener = null;
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
