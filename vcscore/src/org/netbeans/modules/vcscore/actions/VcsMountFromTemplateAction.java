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
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.*;
import java.lang.ref.*;

import javax.swing.*;
import javax.swing.event.*;

import org.openide.*;
import org.openide.awt.Actions;
import org.openide.cookies.InstanceCookie;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.explorer.view.MenuView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileSystemCapability;
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
            standardWizard = new TW ();
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
        return org.openide.util.NbBundle.getBundle(VcsMountFromTemplateAction.class).getString("CTL_MountActionName");
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
        return "/org/netbeans/modules/vcscore/actions/mountNewVCS.gif"; // NOI18N
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

    /** My special version of template wizard.
    */
    private static final class TW extends TemplateWizard 
    implements WizardDescriptor.Panel {
        /** the sheet component */
        private PropertySheet sheet;
        /** the node to display */
        private Node node;
        
        /** Panel that is used to choose target package and
         * name of the template.
         */
        public Panel targetChooser() {
            return this;
        }

        /** Provides the wizard panel with the opportunity to update the
         * settings with its current customized state.
         * Rather than updating its settings with every change in the GUI, it should collect them,
         * and then only save them when requested to by this method.
         * Also, the original settings passed to {@link #readSettings} should not be modified (mutated);
         * rather, the (copy) passed in here should be mutated according to the collected changes.
         * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
         * @param settings the object representing a settings of the wizard
         */
        public void storeSettings (Object settings) {
        }
        
        /** Help for this panel.
         * When the panel is active, this is used as the help for the wizard dialog.
         * @return the help or <code>null</code> if no help is supplied
         */
        public HelpCtx getHelp () {
            return HelpCtx.DEFAULT_HELP;
        }
        
        /** Provides the wizard panel with the current data--either
         * the default data or already-modified settings, if the user used the previous and/or next buttons.
         * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
         * @param settings the object representing wizard panel state, as originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}
         * @exception IllegalStateException if the the data provided
         * by the wizard are not valid.
         */
        public void readSettings (Object settings) {
            try {
                TemplateWizard wiz = (TemplateWizard)settings;
                wiz.putProperty ("WizardPanel_contentSelectedIndex", new Integer (2)); // NOI18N

                DataObject obj = wiz.getTemplate ();
                InstanceCookie ic = (InstanceCookie)obj.getCookie (InstanceCookie.class);
                Object instance = ic.instanceCreate ();

                node = new BeanNode (instance);
                
                if (instance instanceof FileSystem) {
                    node = new CapNode (node, (FileSystem)instance);
                }
            } catch (java.lang.Exception ex) {
                TopManager.getDefault().getErrorManager ().notify (ex);
            }
        }
        
        /** Get the component displayed in this panel.
         * @return the component
         */
        public java.awt.Component getComponent () {
            if (node != null && node.hasCustomizer ()) {
                return node.getCustomizer ();
            }

            if (sheet == null) {
                sheet = new PropertySheet ();

                sheet.setName (NbBundle.getMessage (VcsMountFromTemplateAction.class, "Edit_properties"));            
            }
            
            if (node != null) {
                sheet.setNodes (new Node[] { node });
            }
            
            return sheet;
        }
        
        /** Add a listener to changes of the panel's validity.
         * @param l the listener to add
         * @see #isValid
         */
        public void addChangeListener (ChangeListener l) {
        }
        
        /** Remove a listener to changes of the panel's validity.
         * @param l the listener to remove
         */
        public void removeChangeListener (ChangeListener l) {
        }
        
    } // end of TW

    /** A special filter node that adds Capabilities tab to regular
    * instance node.
    */
    private static final class CapNode extends FilterNode {
        private PropertySet[] sets;
        private FileSystem fs;

        public CapNode (Node filter, FileSystem fs) {
            super (filter);
            this.fs = fs;
        }

        public CapNode (Node filter, FileSystem fs, org.openide.nodes.Children ch) {
            super (filter, ch);
            this.fs = fs;
        }

        public Node getOrig () {
            return getOriginal ();
        }

        protected NodeListener createNodeListener () {
            return new NodeAdapter (this) {
                protected void propertyChange (FilterNode fn, java.beans.PropertyChangeEvent ev) {
                    CapNode fs = (CapNode)fn;
                    if (PROP_PROPERTY_SETS.equals (ev.getPropertyName ())) {
                        fs.sets = null;
                    }
                    super.propertyChange (fn, ev);
                }
            };
        }

        public PropertySet[] getPropertySets () {
            Node.PropertySet[] s = sets;
            Node.PropertySet[] sup = super.getPropertySets ();

            if (s != null && sup.length + 1 == s.length) {
                return s;
            }

            // add the capabilities tab
            s = sup;

            FileSystemCapability cap = ((FileSystem)fs).getCapability ();
            try {
                if (cap != null) {
                    BeanInfo bi = Introspector.getBeanInfo (cap.getClass (), FileSystemCapability.class);
                    BeanNode.Descriptor d = BeanNode.computeProperties (cap, bi);

                    Sheet.Set ss = new Sheet.Set ();
                    ss.setName ("Capabilities"); // NOI18N
                    ss.setDisplayName (NbBundle.getMessage (VcsMountFromTemplateAction.class, "PROP_Capabilities"));
                    ss.setShortDescription (NbBundle.getMessage (VcsMountFromTemplateAction.class, "HINT_Capabilities"));

                    ss.put (d.property);
                    ss.put (d.expert);

                    PropertySet[] arr = new PropertySet[s.length + 1];
                    int from = 0;
                    boolean placed = false;
                    for (int i = 0; i < arr.length; i++) {
                        if (!placed) {
                            boolean ok = i == s.length;
                            if (!ok) {
                                String n = s[from].getName ();
                                ok = n.equals ("files") || n.equals (DataFolder.SET_SORTING); // NOI18N
                            }

                            if (ok) {
                                placed = true;
                                arr[i] = ss;
                                continue;
                            }
                        }
                        arr[i] = s[from++];
                    }
                    sets = s = arr;
                }
            } catch (IntrospectionException e) {
            }
            return s;
        }
    }
}
