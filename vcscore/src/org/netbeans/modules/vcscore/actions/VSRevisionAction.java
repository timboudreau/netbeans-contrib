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

//import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JMenuItem;
import java.util.*;

import org.openide.awt.JInlineMenu;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent.Registry;

import org.netbeans.modules.vcscore.util.OrderedSet;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

//import org.netbeans.modules.vcscore.cmdline.*;
//import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;

/**
 * The revision action on Versioning System.
 *
 * @author  Martin Entlicher
 */
public class VSRevisionAction extends SystemAction implements Presenter.Menu, Presenter.Popup,
                                                              ContextAwareAction,
                                                              ContextAwareDelegateAction.Delegatable {

    private static final JMenuItem[] NONE = new JMenuItem[] {};

    private static final long serialVersionUID = -787778250446630604L;
    
    /** Creates new RevisionAction 
     * Gets revision actions from filesystem and acts on a file object.
     * Both the filesystem and file object are obtained from the revision node.
     */
    public VSRevisionAction() {
    }
    
    public String getName(){
        return NbBundle.getMessage(VSRevisionAction.class, "CTL_Revision_Action"); // NOI18N
    }

    //-------------------------------------------
    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }
    
    public boolean enable(Node[] nodes) {
        return true;
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
        return getPresenter(false, org.openide.util.Utilities.actionsGlobalContext());
    }
    
    public JMenuItem getPresenter(boolean inMenu, Lookup lookup) {
        return new Menu(!inMenu, lookup);
    }
    
    private static JMenuItem[] createMenu(boolean popUp, Node[] nodes) {
        HashMap rListMap = new HashMap();
        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                RevisionList list = (RevisionList) nodes[i].getCookie(RevisionList.class);
                if (list == null) continue;
                RevisionItem item = (RevisionItem) nodes[i].getCookie(RevisionItem.class);
                if (item == null) continue;
                Set itemSet = (Set) rListMap.get(list);
                if (itemSet == null ) {
                    rListMap.put(list, itemSet = new OrderedSet());
                    itemSet.add(item);
                } else {
                    itemSet.add(item);
                }
            }
                /* At present not allowed to construct actions for selected nodes on more filesystems - its safe behaviour
                 * If this restriction will be considered as right solution, then code of this method can be simplified
                 */
            if (rListMap.size() == 0 || rListMap.size() > 1) return createMenu(org.openide.util.Enumerations.empty(), popUp);
            
            Iterator entrySetIt = rListMap.entrySet().iterator();
            LinkedList result = new LinkedList ();
            
            while (entrySetIt.hasNext()) {
                Map.Entry entry = (Map.Entry)entrySetIt.next();
                RevisionList list = (RevisionList) entry.getKey();
                Set itemSet = (Set) entry.getValue();
                try {
                    VersioningFileSystem vs = VersioningFileSystem.findFor(list.getFileObject().getFileSystem());
                    result.addAll (Arrays.asList (vs.getRevisionActions(list.getFileObject(), itemSet)));
                } catch (FileStateInvalidException exc) {
                    continue;
                }
            }
            return createMenu(Collections.enumeration (result), popUp);
        }
        return NONE;
    }

    /** Creates list of menu items that should be used for given
     * revision item object.
     * @param en enumeration of SystemAction that should be added
     *   into the menu if enabled and if not duplicated
     */
    private static JMenuItem[] createMenu (Enumeration en, boolean popUp) {
        en = org.openide.util.Enumerations.removeDuplicates (en);

        ArrayList items = new ArrayList ();
        while (en.hasMoreElements ()) {
            SystemAction a = (SystemAction)en.nextElement ();
           
            if (a.isEnabled ()) {
                JMenuItem item = null;
                if (popUp) {
                    if (a instanceof Presenter.Popup) {
                        item = ((Presenter.Popup)a).getPopupPresenter ();
                    }
                } else {
                    if (a instanceof Presenter.Menu) {
                        item = ((Presenter.Menu)a).getMenuPresenter ();
                    }
                }
                // test if we obtained the item
                if (item != null) {
                    items.add (item);
                }
            }
        }
        JMenuItem[] array = new JMenuItem [items.size ()];
        items.toArray (array);
        return array;
    }
        
    /* Do nothing.
    * This action itself does nothing, it only presents other actions.
    * @param ev ignored
    */
    public void actionPerformed (java.awt.event.ActionEvent e) {}

    public javax.swing.Action createContextAwareInstance(Lookup actionContext) {
        return new ContextAwareDelegateAction (this, actionContext);
    }
    
    /** Presenter for this action.
    */
    private class Menu extends JInlineMenu {
        /** menu presenter (true) or popup presenter (false) */
        private boolean popup;
        /** last registered items */
        private JMenuItem[] last = NONE;
        /** own property change listner */
        private PropL propL = new PropL ();
        private Lookup lookup;

        static final long serialVersionUID =2650151487189209766L;

        /** Creates new instance for menu/popup presenter.
        * @param popup true if this should represent popup
        */
        Menu (boolean popup, Lookup lookup) {
            this.popup = popup;
            this.lookup = lookup;
            Node[] nodes = (Node[])lookup.lookup (new Lookup.Template (Node.class)).allInstances().toArray (new Node[0]);
            changeMenuItems (createMenu (popup, nodes));

            Registry r = WindowManager.getDefault().getRegistry ();

            r.addPropertyChangeListener (
                org.openide.util.WeakListeners.propertyChange (propL, r)
            );
        }

        /** Changes the selection to new items.
        * @param items the new items
        */
        synchronized void changeMenuItems (JMenuItem[] items) {
            removeListeners (last);
            addListeners (items);
            last = items;
            setMenuItems (items);
        }

        /** Add listeners to menu items.
        * @param items the items
        */
        private void addListeners (JMenuItem[] items) {
            int len = items.length;
            for (int i = 0; i < len; i++) {
                items[i].addPropertyChangeListener (propL);
            }
        }

        /** Remove all listeners from menu items.
        * @param items the items
        */
        private void removeListeners (JMenuItem[] items) {
            int len = items.length;
            for (int i = 0; i < len; i++) {
                items[i].removePropertyChangeListener (propL);
            }
        }

        boolean needsChange = false;

        public void addNotify() {
            if (needsChange) {
                Node[] nodes = (Node[])lookup.lookup (new Lookup.Template (Node.class)).allInstances().toArray (new Node[0]);
                changeMenuItems (createMenu (popup, nodes));
                needsChange = false;
            }
            super.addNotify();
        }

        public void removeNotify() {
            removeListeners (last);
            last = NONE;
        }

        /** Property listnener to watch changes of enable state.
        */
        private class PropL implements PropertyChangeListener {
            public PropL () {}
            public void propertyChange (PropertyChangeEvent ev) {
                String name = ev.getPropertyName ();
                if (
                    name == null ||
                    name.equals (SystemAction.PROP_ENABLED) ||
                    name.equals (Registry.PROP_ACTIVATED_NODES)
                ) {
                    // change items later
                    needsChange = true;
                }
            }
        }
    }
    
}
