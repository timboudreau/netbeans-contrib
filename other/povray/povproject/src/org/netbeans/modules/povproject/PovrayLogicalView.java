/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * PovrayLogicalView.java
 *
 * Created on February 16, 2005, 5:57 PM
 */

package org.netbeans.modules.povproject;

import java.awt.Image;
import java.util.Arrays;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Logical view of a Povray project for the project tab in Explorer.
 * Provides a FilterNode wraps the default DataNode
 * for the project, replacing the icon.
 *
 * @author Timothy Boudreau
 */
class PovrayLogicalView implements LogicalViewProvider {
    private PovProject project;
    
    /** Creates a new instance of PovrayLogicalView */
    public PovrayLogicalView(PovProject project) {
        this.project = project;
    }

    public org.openide.nodes.Node createLogicalView() {
        try {
            //Get the scenes directory, creating if deleted
            FileObject scenes = project.getScenesFolder(true);
            
            //Get the DataObject that represents it
            DataObject scenesDataObject = 
                    DataObject.find (scenes);
            
            //Get its default node - we'll wrap our node around it to change the
            //display name, icon, etc.
            Node scenesNode = scenesDataObject.getNodeDelegate();
            
            //This FilterNode will be our project node
            return new ScenesNode (scenesNode, project);
        } catch (DataObjectNotFoundException donfe) {
            ErrorManager.getDefault().notify(donfe);
            return new AbstractNode (Children.LEAF);
        }
    }

    public org.openide.nodes.Node findPath(org.openide.nodes.Node root, Object target) {
        if (target instanceof DataObject) {
            return null; //XXX not implemented yet
        } else {
            return null;
        }
    }

    
    private static final class ScenesNode extends FilterNode {
        final PovProject proj;
        public ScenesNode (Node node, PovProject project) throws DataObjectNotFoundException {
            super (node, new FilterNode.Children (node),
                    //The projects system wants the project in the Node's lookup.
                    //NewAction and friends want the original Node's lookup.
                    //Make a merge of both
                    new ProxyLookup (new Lookup[] { Lookups.singleton(project), 
                    node.getLookup() }));
            this.proj = project;
        }
        
        public Image getIcon(int type) {
            return Utilities.loadImage ("org/netbeans/modules/povproject/resources/scenes.gif");
        }
        
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }      
        
        public String getDisplayName() {
            return proj.getProjectDirectory().getName();
        }
    }
    
}
