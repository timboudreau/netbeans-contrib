/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.MenuElement;
import org.netbeans.modules.vcscore.VcsFSCommandsAction;
import org.netbeans.spi.vcs.VcsCommandsProvider;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.JInlineMenu;
import org.openide.actions.FileSystemAction;
import org.openide.awt.JMenuPlus;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.BeanNode;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.WeakListener;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;
import org.openide.util.enum.*;
import org.openide.windows.TopComponent.Registry;
import org.openide.windows.WindowManager;

/**
 * Action, that contains all VCS actions.
 * It merges the global commands together with context commands.
 *
 * @author  Martin Entlicher
 */
public class VcsAllCommandsAction extends SystemAction implements Presenter.Menu, Presenter.Popup,
                                                                  ContextAwareAction,
                                                                  ContextAwareDelegateAction.Delegatable {

    static final long serialVersionUID = -2345126396734900262L;

    private LookupListener lastProvidersLookupListener;
    
    public boolean enable(Node[] nodes) {
        return true;
    }

    /* @return menu presenter.
    */
    public JMenuItem getMenuPresenter () {
        return getPresenter(false, org.openide.util.Utilities.actionsGlobalContext ());
    }

    /* @return popup presenter.
    */
    public JMenuItem getPopupPresenter () {
        return getPresenter(true, org.openide.util.Utilities.actionsGlobalContext ());
    }
    
    public JMenuItem getPresenter(boolean inMenu, Lookup lookup) {
        return createMergedMenu(!inMenu, lookup);
    }
    
    private JMenuItem createMergedMenu(boolean popup, Lookup lookup) {
        JMenuItem[] globalMenu = getGlobalMenu(popup, lookup);
        JMenuItem[] contextMenu = getContextMenu(popup, lookup);
        MergedMenu mergedMenu = new MergedMenu(globalMenu, contextMenu);
        Lookup.Result globalProvidersRes = Lookup.getDefault().lookup(new Lookup.Template(VcsCommandsProvider.class));
        Lookup.Result contextRes = lookup.lookup (new Lookup.Template (Node.class));
        if (lastProvidersLookupListener != null) {
            globalProvidersRes.removeLookupListener(lastProvidersLookupListener);
            contextRes.removeLookupListener(lastProvidersLookupListener);
        }
        LookupListener providersLookupListener = new GlobalProvidersLookupListener(lookup, new WeakReference(mergedMenu), popup);
        lastProvidersLookupListener = providersLookupListener;
        globalProvidersRes.addLookupListener(providersLookupListener);
        contextRes.addLookupListener(providersLookupListener);
        return mergedMenu;
    }

    /* Getter for name
    */
    public String getName () {
        return org.openide.util.NbBundle.getBundle(VcsCommandsAction.class).getString("CTL_VcsFSActionName");
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
    
    private static JMenuItem[] getGlobalMenu(boolean popup, Lookup actionContext) {
        VcsGlobalCommandsAction globalAction = (VcsGlobalCommandsAction) VcsGlobalCommandsAction.get(VcsGlobalCommandsAction.class);
        JMenuItem[] globalMenu = globalAction.createMenuItems(globalAction.getActivatedFiles(), popup);
        return globalMenu;
    }
    
    private static JMenuItem[] getContextMenu(boolean popup, Lookup actionContext) {
        VcsFSCommandsAction contextAction = (VcsFSCommandsAction) VcsFSCommandsAction.get(VcsFSCommandsAction.class);
        JMenuItem[] contextMenu = contextAction.createMenuItems(popup, actionContext);
        return contextMenu;
    }
    
    private class GlobalProvidersLookupListener extends Object implements LookupListener {
        
        private Lookup lookup;
        private Reference mergedMenuRef;
        private boolean popup;
        
        public GlobalProvidersLookupListener(Lookup lookup, Reference mergedMenuRef, boolean popup) {
            this.lookup = lookup;
            this.mergedMenuRef = mergedMenuRef;
            this.popup = popup;
        }
        
        public void resultChanged(LookupEvent ev) {
            MergedMenu mergedMenu = (mergedMenuRef != null) ? (MergedMenu) mergedMenuRef.get() : null;
            if (mergedMenu != null) {
                mergedMenu.update(getGlobalMenu(!popup, lookup), getContextMenu(!popup, lookup));
            }
        }
    
    }
    
    private class MergedMenu extends JInlineMenu {
        
        static final long serialVersionUID = 2650151487189209767L;
        
        private JMenuItem[] contextMenu;
        private JMenuItem[] globalMenu;
        private boolean needsChange = false;
        
        public MergedMenu(JMenuItem[] globalMenu, JMenuItem[] contextMenu) {
            this.globalMenu = globalMenu;
            this.contextMenu = contextMenu;
            setMenuItems(mergeMenu(globalMenu, contextMenu));
        }
        
        public void update(JMenuItem[] globalMenu, JMenuItem[] contextMenu) {
            this.globalMenu = globalMenu;
            this.contextMenu = contextMenu;
            needsChange = true;
        }
        
        public void addNotify() {
            if (needsChange) {
                needsChange = false;
                setMenuItems(mergeMenu(globalMenu, contextMenu));
            }
            super.addNotify();
        }

        // One-level merge of two menus
        private JMenuItem[] mergeMenu(JMenuItem[] m1, JMenuItem[] m2) {
            for (int i = 0; i < m1.length; i++) {
                if (!(m1[i] instanceof JMenu)) continue;
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
                    JMenuItem[] m2n = new JMenuItem[m2.length - 1];
                    for (int k = 0; k < j; k++) m2n[k] = m2[k];
                    for (int k = j + 1; k < m2.length; k++) m2n[k-1] = m2[k];
                    m2 = m2n;
                }
            }
            if (m2.length == 0) {
                for (int i = 0; i < m1.length; i++) {
                    if (!(m1[i] instanceof JMenu)) continue;
                    m1[i] = new MergedMenuItem((JMenu) m1[i], addContextPlaceHolder((JMenu) m1[i]));
                }
                return m1;
            } else {
                JMenuItem[] m = new JMenuItem[m1.length + m2.length];
                for (int k = 0; k < m1.length; k++) {
                    m[k] = m1[k];
                }
                for (int k = 0; k < m2.length; k++) {
                    m[m1.length + k] = m2[k];
                }
                return m;
            }
        }
        
        private JMenu addContextPlaceHolder(JMenu m) {
            JMenu cm = new JMenu();
            JMenuItem contextPlaceHolder = new JMenuItem(org.openide.util.NbBundle.getMessage(VcsCommandsAction.class, "CTL_ContextCommandsPlaceHolder", m.getText()));
            contextPlaceHolder.setEnabled(false);
            cm.add(contextPlaceHolder);
            return cm;
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
        
        /** Overrides superclass method. Adds lazy popup menu creation
          * if it is necessary. */
        public JPopupMenu getPopupMenu() {
            if (!popupCreated) createPopup();
            return super.getPopupMenu();
        }

        private void createPopup() {
            JPopupMenu popupM1 = m1.getPopupMenu();
            //addElements(popupM1.getSubElements());
            addElements(m1);
            add(new JSeparator());
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
            for (int i = 0; i < cs.length; i++) {
                Component c = cs[i];
                c.getParent().remove(c);
                add(c);
            }
        }
    }
}
