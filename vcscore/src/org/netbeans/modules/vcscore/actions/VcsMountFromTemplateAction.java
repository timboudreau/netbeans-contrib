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

package org.netbeans.modules.vcscore.actions;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.lang.ref.*;

import javax.swing.*;
import javax.swing.event.*;

import org.openide.*;
import org.openide.awt.Actions;
import org.openide.explorer.view.MenuView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.Mutex;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.util.WeakListener;
import org.openide.windows.TopComponent;

/** Creates a new VCS filesystem from template in the "Templates/Mount/VCS" folder.
 * Copied and adapted from org.openide.actions.NewTemplateAction
 *
 * @author Petr Hamernik, Dafe Simonek, Martin Entlicher
 */
public class VcsMountFromTemplateAction extends NodeAction {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 1537855553229904521L;
    /** Last node selected reference to (Node). */
    private static Reference where = new WeakReference (null);

    /** wizard */
    private static TemplateWizard defaultWizard;

    /** Standard wizard (unmodified).*/
    private static TemplateWizard standardWizard;
    
    /** Target folder */
    private static DataFolder targetFolder;
    
    private static DataFolder vcsFolder;
    
    private static DataFolder getVCSFolder() {
        if (vcsFolder == null) {
            vcsFolder = DataFolder.findFolder(TopManager.getDefault().getRepository().getDefaultFileSystem().findResource("Templates/Mount/VCS"));
        }
        return vcsFolder;
    }
    
    private static DataFolder getTargetFolder() {
        if (targetFolder == null) {
            targetFolder = DataFolder.findFolder(TopManager.getDefault().getRepository().getDefaultFileSystem().findResource("Mount"));
        }
        return targetFolder;
    }
    
    /** Getter for wizard.
     * @param the node that is currently activated
     * @return the wizard or null if the wizard should not be enabled
    */
    static TemplateWizard getWizard (Node n) {
        // reset target folder
        //targetFolder = null;
        if (n == null) {
            DataFolder vcs = getVCSFolder();
            n = vcs.getNodeDelegate();
            /*
            Node[] arr = TopManager.getDefault ().getWindowManager ().getRegistry ().getActivatedNodes ();
            if (arr.length == 1) {
                n = arr[0];
            }
             */
        }
        
        Cookie c = n == null ? null : (Cookie)n.getCookie (Cookie.class);
        if (c != null) {
            TemplateWizard t = c.getTemplateWizard ();
            if (t != null) {
                return t;
            }
        }
        
        if (defaultWizard == null) {
            defaultWizard = getStandardWizard();
        }

        //targetFolder = n == null ? null :(DataFolder)n.getCookie (DataFolder.class);

        return defaultWizard;
    }

    /** Getter for standard wizard.
    */
    static TemplateWizard getStandardWizard () {
        if (standardWizard == null) {
            standardWizard = new TemplateWizard ();
            standardWizard.setTemplatesFolder(getVCSFolder());
            standardWizard.setTargetFolder(getTargetFolder());
        }
        return standardWizard;
    }
    
    protected void performAction (Node[] activatedNodes) {
        try {
            Node n = activatedNodes.length == 1 ? activatedNodes[0] : null;
            
            TemplateWizard wizard = getWizard (n);
            
            // clears the name to default
            wizard.setTargetName(null);
            
            // if folder is selected then set it as target
            //if (targetFolder != null) wizard.setTargetFolder(targetFolder);
            wizard.setTemplatesFolder(getVCSFolder());
            wizard.setTargetFolder(getTargetFolder());

            // instantiates
            wizard.instantiate ();
        } catch (IOException e) {
            ErrorManager em = TopManager.getDefault().getErrorManager();
            Throwable e1 = em.annotate(e, "Creating from template did not succeed."); // NOI18N
            em.notify(ErrorManager.INFORMATIONAL, e1);
            String msg = e.getMessage();
            //if ((msg == null) || msg.equals("")) { // NOI18N
                //msg = ActionConstants.BUNDLE.getString("EXC_TemplateFailed");
            //}
            TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
        }
    }

    /* Enables itself only when activates node is DataFolder.
    */
    protected boolean enable (Node[] activatedNodes) {
        return true;
        /*
        if ((activatedNodes == null) || (activatedNodes.length != 1))
            return false;

        Cookie c = (Cookie)activatedNodes[0].getCookie (Cookie.class);
        if (c != null) {
            // if the current node provides its own wizard...
            return c.getTemplateWizard () != null;
        }
        
        DataFolder cookie = (DataFolder)activatedNodes[0].getCookie(DataFolder.class);
        if (cookie != null && !cookie.getPrimaryFile ().isReadOnly ()) {
            return true;
        }
        return false;
         */
    }

    /* Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName() {
        return org.openide.util.NbBundle.getBundle(VcsMountAction.class).getString("CTL_MountActionName");
        //return ActionConstants.BUNDLE.getString("NewTemplate");
    }

    /* Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx (VcsMountFromTemplateAction.class);
    }

    /* Resource name for the icon.
    * @return resource name
    */
    protected String iconResource () {
        return "/org/openide/resources/actions/new.gif"; // NOI18N
    }
    
    /* Creates presenter that invokes the associated presenter.
    */
    public JMenuItem getMenuPresenter() {
        JMenuItem menu = new MenuView.Menu (getTemplateRoot (), new TemplateActionListener (), false);
        Actions.connect (menu, this, false);
        //    menu.setName (getName ());
        return menu;
        /*
        return new Actions.MenuItem (this, true) {
                   public void setEnabled (boolean e) {
                       super.setEnabled (true);
                   }
               };
         */
    }

    /* Creates presenter that invokes the associated presenter.
    */
    public Component getToolbarPresenter() {
        return new Actions.ToolbarButton (this) {
                   public void setEnabled (boolean e) {
                       super.setEnabled (true);
                   }
               };
    }
    
    /* Creates presenter that displayes submenu with all
    * templates.
    */
    public JMenuItem getPopupPresenter() {
        JMenuItem menu = new MenuView.Menu (getTemplateRoot (), new TemplateActionListener (), false);
        Actions.connect (menu, this, true);
        //    menu.setName (getName ());
        return menu;
    }

    /** Create a hierarchy of templates.
    * @return a node representing all possible templates
    */
    public static Node getTemplateRoot () {
        //TemplateWizard wizard = getWizard (null);
        
        //DataFolder f = wizard.getTemplatesFolder ();
        
        // listener used as filter (has method acceptDataObject)
        //Children ch = f.createNodeChildren (new TemplateActionListener ());
        // filter the children
        Children ch = new RootChildren ();
        // create the root
        return new AbstractNode (ch);
    }
    
    /** Cookie that can be implemented by a node if it wishes to have a 
     * special templates wizard.
     */
    public static interface Cookie extends Node.Cookie {
        /** Getter for the wizard that should be used for this cookie.
         */
        public TemplateWizard getTemplateWizard ();
    }
    
    private static void setTWIterator(TemplateWizard wizard, DataObject dobj) {
        /*
        TemplateWizard.Iterator iterator = wizard.getIterator(dobj);
        if (iterator == null) {
            try {
                wizard.setIterator(dobj, new VcsMountIterator());
            } catch (java.io.IOException exc) {}
        }
         */
    }


    /** Actions listener which instantiates the template */
    private static class TemplateActionListener
        implements NodeAcceptor, DataFilter {

        static final long serialVersionUID = 4733270452576543255L;
        public boolean acceptNodes (Node[] nodes) {
            if ((nodes == null) || (nodes.length != 1)) {
                return false;
            }
            Node n = nodes[0];
            DataObject obj = (DataObject)n.getCookie (DataObject.class);
            if (obj == null || !obj.isTemplate ()) {
                // do not accept
                return false;
            }
            
            // in this case the modified wizard will be used as default
            TemplateWizard wizard = getWizard (null);
            
            try {
                wizard.setTargetName (null);
                //setTWIterator(wizard, obj);
                wizard.instantiate (obj, targetFolder);
            } catch (IOException e) {
                ErrorManager em = TopManager.getDefault().getErrorManager();
                Throwable e1 = em.annotate(e, "Creating from template did not succeed."); // NOI18N
                em.notify(ErrorManager.INFORMATIONAL, e1);
                String msg = e.getMessage();
                //if ((msg == null) || msg.equals("")) // NOI18N
                    //msg = ActionConstants.BUNDLE.getString("EXC_TemplateFailed");
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }

            // ok
            return true;
        }

        /** Data filter impl.
        */
        public boolean acceptDataObject (DataObject obj) {
            return obj.isTemplate () || obj instanceof DataFolder;
        }
    }
    
    /** Root template childen.
     */
    private static class RootChildren extends Children.Keys
    implements NodeListener {
        /** last wizard used with the root */
        private TemplateWizard wizard;
        /** node to display templates for or null if current selection
         * should be followed
         */
        private WeakReference current;
        /** weak listener */
        private NodeListener listener = WeakListener.node (this, null);
        
        /** Instance not connected to any node.
         */
        public RootChildren () {
            TopComponent.Registry reg = TopManager.getDefault ().getWindowManager ().getRegistry ();
            reg.addPropertyChangeListener (WeakListener.propertyChange (this, reg));
            
            updateWizard (getWizard (null));
        }
               

        /** Creates nodes for nodes.
         */
        protected Node[] createNodes (Object key) {
            Node n = (Node)key;
            
            DataObject obj = (DataObject)n.getCookie (DataObject.class);
            if (obj != null) {
                if (obj.isTemplate ()) {
                    // on normal nodes stop recursion
                    return new Node[] { new FilterNode (n, LEAF) };
                }
            
                if (obj instanceof DataFolder) {
                    // on folders use normal filtering
                    return new Node[] { new FilterNode (n, new TemplateChildren (n)) };
                }
            }
            
            return null;
        }
        
        /** Check whether the node has not been updated.
         */
        private void updateNode (Node n) {            
            if (current != null && current.get () == n) {
                return;
            }
            
            if (current != null && current.get () != null) {
                ((Node)current.get ()).removeNodeListener (listener);
            }
            
            n.addNodeListener (listener);
            current = new WeakReference (n);
        }
        
        /** Check whether the wizard was not updated.
         */
        private void updateWizard (TemplateWizard w) {
            if (wizard == w) {
                return;
            }
            
            if (wizard != null) {
                Node n = wizard.getTemplatesFolder ().getNodeDelegate ();
                n.removeNodeListener (listener);
            }
            
            Node newNode = w.getTemplatesFolder ().getNodeDelegate ();
            newNode.addNodeListener (listener);
            wizard = w;
            
            updateKeys ();
        }
        
        /** Updates the keys.
         */
        private void updateKeys () {
            setKeys (wizard.getTemplatesFolder ().getNodeDelegate ().getChildren ().getNodes ());
        }
        
        /** Fired when the order of children is changed.
         * @param ev event describing the change
         */
        public void childrenReordered(NodeReorderEvent ev) {
            updateKeys ();
        }        
        
        /** Fired when a set of children is removed.
         * @param ev event describing the action
         */
        public void childrenRemoved(NodeMemberEvent ev) {
            updateKeys ();
        }
        
        /** Fired when a set of new children is added.
         * @param ev event describing the action
         */
        public void childrenAdded(NodeMemberEvent ev) {
            updateKeys ();
        }
        
        /** Fired when the node is deleted.
         * @param ev event describing the node
         */
        public void nodeDestroyed(NodeEvent ev) {
        }

        /** Listen on changes of cookies.
         */
        public void propertyChange(java.beans.PropertyChangeEvent ev) {
            String pn = ev.getPropertyName ();
            
            if (current != null && ev.getSource () == current.get ()) {
                // change in current node
                if (Node.PROP_COOKIE.equals (pn)) {
                    // check change in wizard
                    updateWizard (getWizard ((Node)current.get ()));
                }
            } else {
                // change in selected nodes
                if (TopComponent.Registry.PROP_ACTIVATED_NODES.equals (pn)) {
                    // change the selected node
                    Node[] arr = TopManager.getDefault ().getWindowManager ().getRegistry ().getActivatedNodes ();
                    if (arr.length == 1) {
                        // only if the size is 1
                        updateNode (arr[0]);
                    }
                }
            }   
        }
        
    }

    /** Filter node children, that stops on data objects (does not go futher)
    */
    private static class TemplateChildren extends FilterNode.Children {
        public TemplateChildren (Node or) {
            super (or);
        }

        protected Node copyNode (Node n) {
            DataFolder df = (DataFolder)n.getCookie (DataFolder.class);
            if (df == null || df.isTemplate ()) {
                // on normal nodes stop recursion
                return new FilterNode (n, LEAF);
            } else {
                // on folders use normal filtering
                return new FilterNode (n, new TemplateChildren (n));
            }
        }
    }

}
