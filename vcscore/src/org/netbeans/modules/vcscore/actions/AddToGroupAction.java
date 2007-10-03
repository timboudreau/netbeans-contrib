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

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JMenuItem;
import org.openide.awt.JMenuPlus;
import javax.swing.JMenu;
import javax.swing.event.*;
import java.io.*;

import org.openide.awt.Actions;
import org.openide.util.actions.*;
import org.openide.util.SharedClassObject;
import org.openide.filesystems.FileObject;
import org.openide.*;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;

import org.netbeans.modules.vcscore.grouping.*;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/** Action sensitive to the node selection that does something useful.
 *
 * @author  builder
 */
public class AddToGroupAction extends NodeAction implements ContextAwareDelegateAction.Delegatable {
    
    private boolean adding;

    private static final long serialVersionUID = -8318483915357096138L;
    
    protected void performAction (Node[] nodes) {
        // do work based on the current node selection, e.g.:
        if (nodes == null || nodes.length == 0) return;
        // ...
    }


    public String getName () {
        return NbBundle.getMessage(AddToGroupAction.class, "LBL_AddToGroupAction");
    }

    protected String iconResource () {
        return "org/netbeans/modules/vcscore/actions/AddToGroupActionIcon.gif";
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (AddToGroupAction.class);
    }

    /**
     * Get a menu item that can present this action in a <code>JMenu</code>.
     */
    public JMenuItem getMenuPresenter() {
        return getPresenter(true, org.openide.util.Utilities.actionsGlobalContext ());
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        return getPresenter(false, org.openide.util.Utilities.actionsGlobalContext ());
    }
    
    public JMenuItem getPresenter(boolean isMenu, Lookup lookup) {
        String label;
        if (adding) {
            label = NbBundle.getMessage(AddToGroupAction.class, "LBL_AddToGroupAction"); // NOI18N
        } else {
            label = NbBundle.getMessage(AddToGroupAction.class, "LBL_MoveToVcsGroupAction"); // NOI18N
        }
        JMenu menu=new JMenuPlus(label); // NOI18N
        Actions.setMenuText (menu, label, isMenu);
        if (isMenu) {
            menu.setIcon(getIcon());
        }
        HelpCtx.setHelpIDString (menu, AddToGroupAction.class.getName ());
        JMenuItem item=null;
        DataFolder folder = GroupUtils.getMainVcsGroupFolder();
        FileObject foFolder = folder.getPrimaryFile();
        Enumeration children = foFolder.getData(false);
        boolean hasAny = false;
        while (children.hasMoreElements()) {
            FileObject fo = (FileObject)children.nextElement();
            if (fo.getExt().equals(VcsGroupNode.PROPFILE_EXT)) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(fo.getInputStream()));
                    String line = reader.readLine();
                    String dispName = "";
                    if (line.startsWith(VcsGroupNode.PROP_NAME + "=")) {
                        dispName = getBundleValue(line);
                    }
                    FileObject f = foFolder.getFileObject(fo.getName());
                    if (f != null && f.isFolder() && dispName.length() > 0) {
                        hasAny = true;
                        menu.add(createItem(fo.getName(), dispName, lookup));
                    }
                } catch (IOException exc) {
                    // just ignore missing resource or error while reading the props..
                    ErrorManager manager = ErrorManager.getDefault();
                    manager.notify(ErrorManager.INFORMATIONAL, exc);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ioex) {}
                    }
                }
            }
        }
        if (!hasAny) {
            VcsGroupNode def = GroupUtils.getDefaultGroupInstance();
            if (def != null) {
                menu.add(createItem("default", def.getDisplayName(), lookup));
            }
        }
        return menu;
    }
    
    private String getBundleValue(String keyValue) {
        if (keyValue != null) {
            int index = keyValue.indexOf('=');
            if (index > 0 && keyValue.length() > index) {
                return keyValue.substring(index + 1);
            }
        }
        return "";
    }    

    //-------------------------------------------
    private JMenuItem createItem(String name, String dispName, Lookup lookup) {
        JMenuItem item=null ;
        
        //item=new JMenuItem(g(name));
        item = new JMenuItem ();
        Actions.setMenuText (item, dispName, false);
        item.setActionCommand(dispName);
        item.addActionListener(new AddActionListener(lookup, adding));
        return item;
    }

    public boolean enable(org.openide.nodes.Node[] node) {
        if (node == null || node.length == 0) return false;
        VcsGroupSettings settings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
        if (settings.isDisableGroups()) return false;
        adding = true;
        // XXX this fails for filter nodes. A cookie check must be used instead
        for (int m = 0; m < node.length; m++) {
            if (node[m] instanceof VcsGroupNode) return false;
            if (node[m] instanceof VcsGroupFileNode) {
                adding = false;
            }
        }
        DataFolder folder = GroupUtils.getMainVcsGroupFolder();
        DataObject[] children = folder.getChildren();
        if (children == null || children.length == 0) {
            return false;
        }
        for (int i = 0; i < node.length; i++) {
            DataObject dobj = (DataObject)node[i].getCookie(DataObject.class);
            if (dobj != null) {
                if (!dobj.getPrimaryFile().isData()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true; // none of the nodes is a folder..
    }    

    public javax.swing.Action createContextAwareInstance(Lookup actionContext) {
        return new ContextAwareDelegateAction (this, actionContext);
    }
    

    private static void movePerformed(String groupName, Node[] actNodes) {
        Node grFolder = GroupUtils.getMainVcsGroupNodeInstance();
        Node[] dobjs = grFolder.getChildren().getNodes();
        DataFolder group = null;
        if (dobjs == null) return;
        for (int i = 0; i < dobjs.length; i++) {
            if (dobjs[i].getName().equals(groupName)) {
                DataFolder fold = (DataFolder)dobjs[i].getCookie(DataObject.class);
                group = fold;
                break;
            }
        }
        if (group == null) return;
        if (actNodes == null) return;
        for (int j = 0; j < actNodes.length; j++) {
            if (actNodes[j] instanceof VcsGroupFileNode) {
                VcsGroupFileNode nd = (VcsGroupFileNode)actNodes[j];
                DataShadow shadow = (DataShadow)nd.getCookie(DataShadow.class);
                try {
                    if (!group.equals(shadow.getFolder())) {
                        shadow.getOriginal().createShadow(group);
                        shadow.delete();
                    }
                } catch (IOException exc) {
                    NotifyDescriptor excMess = new NotifyDescriptor.Message(
                    NbBundle.getBundle(AddToGroupAction.class).getString("MoveToVcsGroupAction.movingError"),
                    NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(excMess);
                }
            }
        }
    }
    
    private static void addPerformed(String groupName, Node[] nodes) {
        Node grFolder = GroupUtils.getMainVcsGroupNodeInstance();
        Node[] dobjs = grFolder.getChildren().getNodes();
        DataFolder group = null;
        if (dobjs == null) return;
        for (int i = 0; i < dobjs.length; i++) {
            if (dobjs[i].getName().equals(groupName)) {
                DataFolder fold = (DataFolder)dobjs[i].getCookie(DataObject.class);
                group = fold;
                break;
            }
        }
        if (group == null) return;
        GroupUtils.addToGroup(group, nodes);
    }

    private static final class AddActionListener extends Object implements ActionListener {
        
        private Lookup lookup;
        private boolean adding;
        
        public AddActionListener(Lookup lookup, boolean adding) {
            this.lookup = lookup;
            this.adding = adding;
        }
    
        public void actionPerformed(ActionEvent actionEvent) {
            Node[] nodes = (Node[])lookup.lookup (new Lookup.Template (Node.class)).allInstances().toArray (new Node[0]);
            if (adding) {
                addPerformed(actionEvent.getActionCommand(), nodes);
            } else {
                movePerformed(actionEvent.getActionCommand(), nodes);
            }
            VcsGroupMenuAction.GroupExplorerPanel groups = VcsGroupMenuAction.GroupExplorerPanel.getDefault();
            if (!groups.isOpened()) {
                // Activate only when not opened.
                groups.open();
                groups.requestActive();
            }
        }
        
    }
}
