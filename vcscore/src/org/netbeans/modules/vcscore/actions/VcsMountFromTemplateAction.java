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
import org.openide.DialogDisplayer;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
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
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;
import org.openide.util.WeakListener;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** Creates a new VCS filesystem from template in the "Templates/Mount/VCS" folder.
 * Copied and adapted from org.openide.actions.NewTemplateAction
 *
 * @author Petr Hamernik, Dafe Simonek, Martin Entlicher
 */
public class VcsMountFromTemplateAction extends NodeAction {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 1537855553229904521L;
    
    private static final String ATTR_MNEMONIC = "VcsMountAction.mnemonic"; // NOI18N
    
    /** Last node selected reference to (Node). */
    private static Reference where = new WeakReference (null);

    /** wizard */
    private static TemplateWizard defaultWizard;

    /** Standard wizard (unmodified).*/
    private static Reference standardWizardRef;
    
    /** Target folder */
    private static DataFolder targetFolder;
    
    private static DataFolder vcsFolder;
    
    private static DataFolder getVCSFolder() {
        if (vcsFolder == null) {
            vcsFolder = DataFolder.findFolder(org.openide.filesystems.Repository.getDefault().getDefaultFileSystem().findResource("Templates/Mount/VCS"));
        }
        return vcsFolder;
    }
    
    private static DataFolder getTargetFolder() {
        if (targetFolder == null) {
            targetFolder = DataFolder.findFolder(org.openide.filesystems.Repository.getDefault().getDefaultFileSystem().findResource("Mount"));
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
        TemplateWizard standardWizard = (standardWizardRef == null) ? null : (TemplateWizard) standardWizardRef.get();
        if (standardWizard == null) {
            standardWizard = new TW ();
            standardWizardRef = new SoftReference(standardWizard);
        }
        standardWizard.setTemplatesFolder(getVCSFolder());
        standardWizard.setTargetFolder(getTargetFolder());
        return standardWizard;
    }
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction (Node[] activatedNodes) {
        try {
            Node n = activatedNodes.length == 1 ? activatedNodes[0] : null;
            
            TemplateWizard wizard = getWizard (n);
            
            // clears the name to default
            wizard.setTargetName(null);

            // instantiates
            wizard.instantiate ();
        } catch (IOException e) {
            ErrorManager em = ErrorManager.getDefault();
            Throwable e1 = em.annotate(e, "Creating from template did not succeed."); // NOI18N
            em.notify(ErrorManager.INFORMATIONAL, e1);
            String msg = e.getMessage();
            //if ((msg == null) || msg.equals("")) { // NOI18N
                //msg = ActionConstants.BUNDLE.getString("EXC_TemplateFailed");
            //}
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
        }
    }

    /* Enables itself only when activates node is DataFolder.
    */
    protected boolean enable (Node[] activatedNodes) {
        return true;
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
        return "org/netbeans/modules/vcscore/actions/mountNewVCS.gif"; // NOI18N
    }
    
    /* Creates presenter that invokes the associated presenter.
    */
    public JMenuItem getMenuPresenter() {
        JMenuItem menu = new MountMenu (getTemplateRoot(), new TemplateActionListener (), false, false);
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
        JMenuItem menu = new MountMenu (getTemplateRoot(), new TemplateActionListener (), false, true);
        Actions.connect (menu, this, true);
        //    menu.setName (getName ());
        return menu;
    }

    /** Create a hierarchy of templates.
    * @return a node representing all possible templates
    */
    public static Node getTemplateRoot () {
        return getWizard(null).getTemplatesFolder().getNodeDelegate();
    }
    
    /** Cookie that can be implemented by a node if it wishes to have a 
     * special templates wizard.
     */
    public static interface Cookie extends Node.Cookie {
        /** Getter for the wizard that should be used for this cookie.
         */
        public TemplateWizard getTemplateWizard ();
    }
    
    private class MountMenu extends MenuView.Menu {
        
        private boolean popupMenu;
        
        /**
         * @param popupMenu Whether it is a popup menu or not. A popup menu does not require mnemonics.
         */
        public MountMenu (final Node node, final NodeAcceptor action, final boolean setName,
                          final boolean popupMenu) {
            super(node, action, setName);
            this.popupMenu = popupMenu;
        }
        
        /** Create a menu element for a node. The default implementation creates
         * {@link MenuView.MenuItem}s for leafs and <code>Menu</code> for other nodes.
         * Here we add a menmonic.
         *
         * @param n node to create element for
         * @return the created node
         */
        protected JMenuItem createMenuItem (Node n) {
            JMenuItem item = super.createMenuItem(n);
            if (!popupMenu) {
                String mnemonic = null;
                DataObject obj = (DataObject) n.getCookie (DataObject.class);
                if (obj != null) {
                    FileObject fo = obj.getPrimaryFile();
                    String bundleName = (String) fo.getAttribute(ATTR_MNEMONIC);
                    if (bundleName != null) {
                        try {
                            bundleName = org.openide.util.Utilities.translate(bundleName);
                            ResourceBundle b = NbBundle.getBundle (bundleName, Locale.getDefault (),(ClassLoader)Lookup.getDefault().lookup(ClassLoader.class));
                            mnemonic = b.getString (fo.getPackageNameExt ('/', '.') + "_m"); // NOI18N
                        } catch (MissingResourceException ex) {
                            // ignore--normal
                        }
                    }
                }
                if (mnemonic != null && mnemonic.length() > 0) {
                    item.setMnemonic(mnemonic.charAt(0));
                }
            }
            return item;
        }
    }


    /** Actions listener which instantiates the template */
    private static class TemplateActionListener implements NodeAcceptor, DataFilter {

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
                ErrorManager em = ErrorManager.getDefault();
                Throwable e1 = em.annotate(e, "Creating from template did not succeed."); // NOI18N
                em.notify(ErrorManager.INFORMATIONAL, e1);
                String msg = e.getMessage();
                //if ((msg == null) || msg.equals("")) // NOI18N
                    //msg = ActionConstants.BUNDLE.getString("EXC_TemplateFailed");
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
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

    /** My special version of template wizard.
    */
    private static final class TW extends TemplateWizard implements FileSystem.AtomicAction {

        /** Calls iterator's instantiate. It is called when user selects
         * a option which is not CANCEL_OPTION or CLOSED_OPTION.
         */
        protected synchronized java.util.Set handleInstantiate() throws java.io.IOException {
            org.openide.filesystems.Repository.getDefault ().getDefaultFileSystem ().runAtomicAction (this);
            
            return retValue;
        }

        /** used for communication with handleInstantiate & run */
        private java.util.Set retValue;
        
        /** Handles instantiate in atomic action.
         */
        public void run () throws java.io.IOException {
            retValue = super.handleInstantiate();
            
            // order the objects, so the new created will be the last
            java.util.Iterator it = retValue.iterator ();
            while (it.hasNext ()) {
                DataObject obj = (DataObject)it.next ();
                DataFolder parent = obj.getFolder ();
                java.util.List children = new java.util.ArrayList (
                    java.util.Arrays.asList (parent.getChildren ())
                );
                // add the object to the end
                children.remove (obj);
                children.add (obj);
                
                // change the order
                parent.setOrder ((DataObject[])children.toArray (new DataObject[0]));
            }
        }
        
    } // end of TW

}
