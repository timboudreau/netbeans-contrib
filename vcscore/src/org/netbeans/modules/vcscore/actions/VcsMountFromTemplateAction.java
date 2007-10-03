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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.vcscore.actions;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.lang.ref.*;

import javax.swing.*;
import javax.swing.event.*;

import org.openide.*;
import org.openide.awt.Actions;
import org.openide.explorer.view.MenuView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;

/** Creates a new VCS filesystem from template in the "Templates/Mount/VCS" folder.
 * Copied and adapted from org.openide.actions.NewTemplateAction
 *
 * @author Petr Hamernik, Dafe Simonek, Martin Entlicher
 */
public class VcsMountFromTemplateAction extends NodeAction {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 1537855553229904521L;
    
    private static final String ATTR_MNEMONIC = "VcsMountAction.mnemonic"; // NOI18N
    
    /** Standard wizard (unmodified).*/
    private static Reference standardWizardRef;
    
    private static DataFolder vcsFolder;
    
    private Node preferredActionNode;
    
    private static DataFolder getVCSFolder() {
        if (vcsFolder == null) {
            vcsFolder = DataFolder.findFolder(org.openide.filesystems.Repository.getDefault().getDefaultFileSystem().findResource("VCSMountTemplates"));
        }
        return vcsFolder;
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
        
        TemplateWizard wizard = null;
        Cookie c = n == null ? null : (Cookie)n.getCookie (Cookie.class);
        if (c != null) {
            TemplateWizard t = c.getTemplateWizard ();
            if (t != null) {
                wizard = t;
            }
        }
        
        if (wizard == null) {
            wizard = getStandardWizard();
        }

        //targetFolder = n == null ? null :(DataFolder)n.getCookie (DataFolder.class);
         //don't show help button!
        wizard.putProperty("WizardPanel_helpDisplayed",Boolean.FALSE);       // NOI18N
        wizard.setTitleFormat(new MessageFormat(NbBundle.getBundle(VcsMountFromTemplateAction.class).getString("MountWizardTitleFormat")));

        return wizard;
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
        return standardWizard;
    }
    
    /**
     * @return true not to run in AWT thread.
     * The wizard instantiation should not block the AWT thread.
     */
    protected boolean asynchronous() {
        return true;
    }
    
    protected void performAction (Node[] activatedNodes) {
        if (preferredActionNode != null) {
            launchWizard(preferredActionNode);
            return ;
        }
        try {
            Node n = activatedNodes.length == 1 ? activatedNodes[0] : null;
            TemplateWizard wizard = getWizard (n);
            
            // clears the name to default
            wizard.setTargetName(null);

            // instantiates
            DataObject[] children = getVCSFolder().getChildren();
            if (children.length == 1) {
                wizard.instantiate (children[0]);
            } else {
                wizard.instantiate ();
            }
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
        Node templateRoot = getTemplateRoot();
        JMenuItem menu;
        Node[] templateNodes = templateRoot.getChildren().getNodes(true);
        if (templateNodes.length == 1) {
            //return new MenuView.MenuItem(templateRoot);
            menu = new JMenuItem(getName());
            preferredActionNode = templateNodes[0];
        } else {
            preferredActionNode = null;
            menu = new MountMenu (templateRoot, new TemplateActionListener (), false, false);
        }
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
        Node templateRoot = getTemplateRoot();
        JMenuItem menu;
        Node[] templateNodes = templateRoot.getChildren().getNodes(true);
        if (templateNodes.length == 1) {
            //return new MenuView.MenuItem(templateRoot);
            menu = new JMenuItem(getName());
            preferredActionNode = templateNodes[0];
        } else {
            preferredActionNode = null;
            menu = new MountMenu (templateRoot, new TemplateActionListener (), false, true);
        }
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
                            mnemonic = b.getString (fo.getPath() + "_m"); // NOI18N
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
    
    private static boolean launchWizard(Node n) {
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
            wizard.instantiate (obj, null);
        } catch (IOException e) {
            ErrorManager em = ErrorManager.getDefault();
            Throwable e1 = em.annotate(e, "Creating from template did not succeed."); // NOI18N
            em.notify(ErrorManager.INFORMATIONAL, e1);
            String msg = e.getMessage();
            //if ((msg == null) || msg.equals("")) // NOI18N
                //msg = ActionConstants.BUNDLE.getString("EXC_TemplateFailed");
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
        }
        return true;
    }


    /** Actions listener which instantiates the template */
    private static class TemplateActionListener implements NodeAcceptor, DataFilter {

        static final long serialVersionUID = 4733270452576543255L;
        
        public TemplateActionListener () {}
        
        public boolean acceptNodes (Node[] nodes) {
            if ((nodes == null) || (nodes.length != 1)) {
                return false;
            }
            Node n = nodes[0];
            return launchWizard(n);
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
        
        public TW () {}

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
            retValue = null; // To free the added filesystems
        }
        
    } // end of TW

}
