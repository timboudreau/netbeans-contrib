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

import java.awt.Component;
import java.net.URL;
import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.MenuElement;
import javax.swing.event.MenuListener;
import org.netbeans.modules.vcscore.VcsFSCommandsAction;
import org.netbeans.spi.vcs.VcsCommandsProvider;

import org.openide.filesystems.Repository;
import org.openide.ErrorManager;
import org.openide.awt.JInlineMenu;
import org.openide.awt.Actions;
import org.openide.awt.JMenuPlus;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.Presenter;

/**
 * Action, that contains all VCS actions.
 * It merges the global commands together with context commands.
 *
 * @author  Martin Entlicher
 */
public class VcsAllCommandsAction extends SystemAction implements Presenter.Menu, Presenter.Popup, ContextAwareAction, ContextAwareDelegateAction.Delegatable {

    static final long serialVersionUID = -2345126396734900262L;

    private LookupListener lastProvidersLookupListener;
    private static final String GLOBAL_MENU_FOLDER = "vcs/versioningMenu"; // NOI18N
    private static final String OS_NAME = "os.name"; // NOI18N   
    private FileObject menuRoot;
    
    
    public boolean enable(Node[] nodes) {
        return true;
    }

    /* @return menu presenter.
    */
    public JMenuItem getMenuPresenter () {
        return getPresenter(true, org.openide.util.Utilities.actionsGlobalContext ());
    }

    /* @return popup presenter.
    */
    public JMenuItem getPopupPresenter () {
        return getPresenter(false, org.openide.util.Utilities.actionsGlobalContext ());
    }
    
    public JMenuItem getPresenter(boolean inMenu, Lookup lookup) { 
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem ();
        menuRoot = dfs.findResource(GLOBAL_MENU_FOLDER);
        FileObject[] filob;
        if (menuRoot == null) { // No global menu
            filob = new FileObject[0];
        } else {
            filob = menuRoot.getChildren();
        }
        JInlineMenu menu = new JInlineMenu();        
        ArrayList items = new ArrayList();
        String osname = System.getProperty(OS_NAME);
        String os_name; 
        Icon icon;
        JMenu item;
        for(int i = 0; i < filob.length; i++){
            os_name = (String) filob[i].getAttribute(OS_NAME);         
            if(os_name != null && osname.indexOf(os_name) == -1)
                continue;
            String bundleName = (String)filob[i].getAttribute("SystemFileSystem.localizingBundle"); //NOI18N             
            item = new JMenu();
            Actions.setMenuText(item,NbBundle.getBundle(bundleName).getString(filob[i].getNameExt()),true);
            item.addMenuListener(new VcsMenuListener(inMenu, lookup));
            URL url = (URL)filob[i].getAttribute("SystemFileSystem.icon"); //NOI18N
            if(url != null){
                icon = new ImageIcon(url);
                item.setIcon(icon);
            }            
            items.add(item);
        }
        JMenuItem[] itemsArr = (JMenuItem[])items.toArray(new JMenuItem[items.size()]);
        Arrays.sort(itemsArr, new Comparator() {
            public int compare(Object o1, Object o2) {
                JMenu m1 = (JMenu) o1;
                JMenu m2 = (JMenu) o2;
                return m1.getText().compareTo(m2.getText());
            }
        });
        menu.setMenuItems(itemsArr);
        return menu;
    } 
    
    /* Getter for name
    */
    public String getName () {
        return org.openide.util.NbBundle.getBundle(VcsAllCommandsAction.class).getString("CTL_VcsFSActionName");
    }

    /* Getter for help.
    */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (VcsAllCommandsAction.class);
    }

    /* Do nothing.
    * This action itself does nothing, it only presents other actions.
    * @param ev ignored
    */
    public void actionPerformed (java.awt.event.ActionEvent e) {}

    public javax.swing.Action createContextAwareInstance(Lookup actionContext) {
        return new ContextAwareDelegateAction (this, actionContext);
    }
    
    private static JMenuItem[] getGlobalMenu(boolean inMenu, Lookup actionContext) {
        VcsGlobalCommandsAction globalAction = (VcsGlobalCommandsAction) VcsGlobalCommandsAction.get(VcsGlobalCommandsAction.class);
        JMenuItem[] globalMenu = globalAction.createMenuItems(globalAction.getActivatedFiles(), inMenu);       
        return globalMenu;
    }
    
    private static JMenuItem[] getContextMenu(boolean inMenu, Lookup actionContext) {
        VcsFSCommandsAction contextAction = (VcsFSCommandsAction) VcsFSCommandsAction.get(VcsFSCommandsAction.class);
        JMenuItem[] contextMenu = contextAction.createMenuItems(inMenu, actionContext, true);
        return contextMenu;
    }
    
    private class VcsMenuListener implements MenuListener{        
        private boolean inMenu;
        private Lookup lookup;
        private boolean wasSelected = false;
        private MergedMenu mergedMenu;
        
        public VcsMenuListener(boolean inMenu, Lookup lookup){            
            this.inMenu = inMenu;
            this.lookup = lookup;            
        }
        
        public void menuCanceled(javax.swing.event.MenuEvent e) {
        }
        
        public void menuDeselected(javax.swing.event.MenuEvent e) {
        }
        
        public void menuSelected(javax.swing.event.MenuEvent e) {                                   
                JMenu emptyMenu = (JMenu)e.getSource();
                mergedMenu = new MergedMenu(inMenu,lookup,emptyMenu);
                mergedMenu.addNotify();           
        }
        
    }
    private class MergedMenu extends JInlineMenu implements LookupListener {
        
        static final long serialVersionUID = 2650151487189209767L;
        
        private boolean inMenu;
        private Lookup lookup;
        private JMenu eMenu;
        private boolean needsChange = true;
        
        public MergedMenu(boolean inMenu, Lookup lookup, JMenu eMenu) {
            this.inMenu = inMenu;
            this.lookup = lookup;
            this.eMenu = eMenu;
            Lookup.Result globalProvidersRes = Lookup.getDefault().lookup(new Lookup.Template(VcsCommandsProvider.class));
            Lookup.Result contextRes = lookup.lookup (new Lookup.Template (Node.class));
            globalProvidersRes.addLookupListener((LookupListener) WeakListeners.create(LookupListener.class, this, globalProvidersRes));
            contextRes.addLookupListener((LookupListener) WeakListeners.create(LookupListener.class, this, contextRes));
        }
        
        public void addNotify() {            
            if (needsChange) {
                needsChange = false;
                JMenuItem[] globalMenu = getGlobalMenu(inMenu, lookup);
                JMenuItem[] m1 = new JMenuItem[1];
                for(int i = 0; i < globalMenu.length; i++){
                    if(globalMenu[i].getText().equals(eMenu.getText()))
                        m1[0] = globalMenu[i];
                }
                if (m1[0] == null) {
                    ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "'"+eMenu.getText()+"' not found among global commands.");
                    m1 = new JMenuItem[] {};
                }
                JMenuItem[] contextMenu = getContextMenu(inMenu, lookup);
                //setMenuItems(mergeMenu(globalMenu, contextMenu));
                JMenuItem[] mMenu = mergeMenu(m1,contextMenu);
                JPopupMenu popup = ((JMenu)mMenu[0]).getPopupMenu();
                Component[] items = popup.getComponents();
                eMenu.removeAll();
                for(int j=0; j<items.length; j++){                   
                    eMenu.add(items[j]);
                }
                // remove old key listeners
                javax.swing.event.MenuKeyListener[] keyListeners = eMenu.getMenuKeyListeners();
                for (int k = 0; k < keyListeners.length; k++) {
                    if (keyListeners[k].getClass().getName().startsWith("org.netbeans")) {
                        eMenu.removeMenuKeyListener(keyListeners[k]);
                    }
                }
                // transfer the current key listeners
                keyListeners = mMenu[0].getMenuKeyListeners();
                for (int k = 0; k < keyListeners.length; k++) {
                    if (!keyListeners[k].getClass().getName().startsWith("org.netbeans")) continue;
                    eMenu.addMenuKeyListener(keyListeners[k]);
                    //System.out.println("  Transfering key listener: "+keyListeners[k]);
                }
                
            }
            super.addNotify();
        }

        // One-level merge of two menus
        private JMenuItem[] mergeMenu(JMenuItem[] m1, JMenuItem[] m2) {
            for (int i = 0; i < m1.length; i++) {
                if (!(m1[i] instanceof JMenu)) continue;
                Icon icon = m1[i].getIcon();
                String text1 = m1[i].getText();
                int j;
                for (j = 0; j < m2.length; j++) {
                    if (!(m2[j] instanceof JMenu)) continue;
                    String text2 = m2[j].getText();
                    if (text1.equals(text2)) {
                        break;
                    }
                }
                if (j < m2.length) {
                    m1[i] = new MergedMenuItem((JMenu) m1[i], (JMenu) m2[j]);
                    if (inMenu) m1[i].setIcon(icon);
                    JMenuItem[] m2n = new JMenuItem[m2.length - 1];
                    for (int k = 0; k < j; k++) m2n[k] = m2[k];
                    for (int k = j + 1; k < m2.length; k++) m2n[k-1] = m2[k];
                    m2 = m2n;
                } else {
                    m1[i] = new MergedMenuItem((JMenu) m1[i], addContextPlaceHolder((JMenu) m1[i]));
                    if (inMenu) m1[i].setIcon(icon);
                }
            }
            if (m1.length == 0) {
                JMenuItem mm;
                String text1 = eMenu.getText();
                int j;
                for (j = 0; j < m2.length; j++) {
                    if (!(m2[j] instanceof JMenu)) continue;
                    String text2 = m2[j].getText();
                    if (text1.equals(text2)) {
                        break;
                    }
                }
                if (j < m2.length) {
                    return m2;
                } else {
                    mm = new MergedMenuItem(eMenu.getText(), eMenu.getMnemonic(),
                                            addContextPlaceHolder(eMenu));
                }
                m1 = new JMenuItem[] { mm };
            }
            JMenuItem[] m = new JMenuItem[m1.length + m2.length];
            for (int k = 0; k < m1.length; k++) {
                m[k] = m1[k];
            }
            for (int k = 0; k < m2.length; k++) {
                m[m1.length + k] = m2[k];
            }
            return m;
        }
        
        private JMenu addContextPlaceHolder(JMenu m) {
            JMenu cm = new JMenu();
            JMenuItem contextPlaceHolder = new JMenuItem(org.openide.util.NbBundle.getMessage(VcsAllCommandsAction.class, "CTL_ContextCommandsPlaceHolder", m.getText()));
            contextPlaceHolder.setEnabled(false);
            cm.add(contextPlaceHolder);
            return cm;
        }
        
        public void resultChanged(LookupEvent ev) {
            needsChange = true;
        }
        
    }
    
   private static class MergedMenuItem extends JMenuPlus {
        
        private JMenu m1;
        private JMenu m2;
        private boolean popupCreated = false;
        
        public MergedMenuItem(JMenu m1, JMenu m2) {            
            this.m1 = m1;
            this.m2 = m2;
            setText(m1.getText());
            setMnemonic(m1.getMnemonic());
        }
        
        public MergedMenuItem(String text, int mnemonic, JMenu m2) {
            this.m1 = null;
            this.m2 = m2;
            setText(text);
            setMnemonic(mnemonic);
        }

        /** Overrides superclass method. Adds lazy popup menu creation
          * if it is necessary. */
        public JPopupMenu getPopupMenu() {
            if (!popupCreated) createPopup();
            return super.getPopupMenu();
        }

        private void createPopup() {
            if (m1 != null) {
                addElements(m1);
                add(new JSeparator());
            }
            //addElements(m2.getPopupMenu().getSubElements());
            addElements(m2);
            popupCreated = true;
        }
        
        private void addElements(MenuElement[] elements) {
            for (int i = 0; i < elements.length; i++) {
                Component c = elements[i].getComponent();
                c.getParent().remove(c);
                add(c);
            }
        }
        
        private void addElements(JMenu m) {
            Component[] cs = m.getPopupMenu().getComponents();
            boolean menuKeyListenersTransfered = false;
            for (int i = 0; i < cs.length; i++) {
                Component c = cs[i];
                Component parent = c.getParent();
                ((java.awt.Container) parent).remove(c);
                add(c);
                if (!menuKeyListenersTransfered) {
                    if (parent instanceof javax.swing.JPopupMenu) {
                        parent = ((javax.swing.JPopupMenu) parent).getInvoker();
                    }
                    if (parent instanceof JMenuItem) {
                        JMenuItem parentMenu = (JMenuItem) parent;
                        javax.swing.event.MenuKeyListener[] keyListeners = parentMenu.getMenuKeyListeners();
                        for (int k = 0; k < keyListeners.length; k++) {
                            if (!keyListeners[k].getClass().getName().startsWith("org.netbeans")) continue;
                            addMenuKeyListener(keyListeners[k]);
                            //System.out.println("  Transfering key listener: "+keyListeners[k]);
                        }
                        menuKeyListenersTransfered = true;
                    }
                }
            }
        }
    }
}
