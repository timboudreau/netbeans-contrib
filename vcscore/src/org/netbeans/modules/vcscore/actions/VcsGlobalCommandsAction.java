/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.openide.awt.JInlineMenu;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.WeakListener;
import org.openide.util.WeakSet;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.SystemAction;
import org.openide.windows.WindowManager;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.commands.CommandsTree;

/**
 * This action presents global VCS commands, that are independent of a filesystem.
 *
 * @author  Martin Entlicher
 */
public class VcsGlobalCommandsAction extends SystemAction implements Presenter.Menu,
                                                                     Presenter.Popup,
                                                                     LookupListener,
                                                                     PropertyChangeListener {
    
    static final long serialVersionUID = -175327694047831654L;
    
    private CommandsTree globalCommands;
    private WeakSet listenedProviders = new WeakSet();
    private Reference inlineMenuReference;
    private boolean inlineMenuWasInMenu;
    private boolean globalExpertMode;
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(VcsGlobalCommandsAction.class).getString("CTL_VcsGlobalActionName");
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return new org.openide.util.HelpCtx(VcsGlobalCommandsAction.class);
    }
    
    public boolean isEnabled() {
        return getGlobalCommands().hasChildren();
    }
    
    public void performAction() {
    }
    
    /**
     * Does nothing.
     */
    public void actionPerformed(ActionEvent aev) {
    }
    
    public JMenuItem getMenuPresenter() {
        return getPresenter(true);
    }
    
    public JMenuItem getPopupPresenter() {
        return getPresenter(false);
    }
    
    private synchronized CommandsTree getGlobalCommands() {
        if (globalCommands == null) {
            Lookup.Result globalProvidersRes = Lookup.getDefault().lookup(new Lookup.Template(VcsCommandsProvider.class));
            LookupListener providersLookupListener = (LookupListener) WeakListener.create(LookupListener.class, this, globalProvidersRes);
            globalProvidersRes.addLookupListener(providersLookupListener);
            VcsCommandsProvider[] globalProviders = (VcsCommandsProvider[]) globalProvidersRes.allInstances().toArray(new VcsCommandsProvider[0]);
            globalCommands = getCommandsFromProviders(globalProviders);
        }
        return globalCommands;
    }
            
    public synchronized void resultChanged(LookupEvent lookupEvent) {
        Lookup.Result globalProvidersRes = (Lookup.Result) lookupEvent.getSource();
        VcsCommandsProvider[] globalProviders = (VcsCommandsProvider[]) globalProvidersRes.allInstances().toArray(new VcsCommandsProvider[0]);
        globalCommands = getCommandsFromProviders(globalProviders);
        refreshMenu();
    }
    
    /** This method gets called when a provider's property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("VcsGlobalCommandsAction.propertyChange("+evt+")");
        if (inlineMenuReference != null && evt.getSource().equals(inlineMenuReference.get())) return ;
        if (CommandsTree.Provider.PROP_COMMANDS.equals(evt.getPropertyName())) {
            Lookup.Result globalProvidersRes = Lookup.getDefault().lookup(new Lookup.Template(VcsCommandsProvider.class));
            VcsCommandsProvider[] globalProviders = (VcsCommandsProvider[]) globalProvidersRes.allInstances().toArray(new VcsCommandsProvider[0]);
            globalCommands = getCommandsFromProviders(globalProviders);
            refreshMenu();
        }
    }
    
    private void refreshMenu() {
        if (inlineMenuReference != null) {
            JInlineMenu inlineMenu = (JInlineMenu) inlineMenuReference.get();
            if (inlineMenu != null) {
                inlineMenu.setMenuItems(createMenuItems(new FileObject[0], inlineMenuWasInMenu));
            }
        }
    }
    
    private CommandsTree getCommandsFromProviders(VcsCommandsProvider[] providers) {
        CommandsTree commands = new CommandsTree(null);
        //System.out.println("GlobalCommandsAction.getCommandsFromProviders()");
        globalExpertMode = false;
        for (int i = 0; i < providers.length; i++) {
            if (providers[i] instanceof CommandsTree.Provider) {
                if (!listenedProviders.contains(providers[i])) {
                    ((CommandsTree.Provider) providers[i]).addPropertyChangeListener(WeakListener.propertyChange(this, providers[i]));
                    listenedProviders.add(providers[i]);
                }
                CommandsTree providerCommands = ((CommandsTree.Provider) providers[i]).getCommands();
                CommandsTree[] children = providerCommands.children();
                for (int j = 0; j < children.length; j++) {
                    commands.add(children[j]);
                    //System.out.println("  add "+children[j]+
                    //    ((children[j].getCommandSupport() != null) ?
                    //        ", name = "+children[j].getCommandSupport().getName()+
                    //        ", displayName = "+children[j].getCommandSupport().getDisplayName() :
                    //        ", null support"));
                }
                if (children.length > 0 && children[children.length - 1].getCommandSupport() != null) {
                    //System.out.println("  had "+children.length+" children, adding separator.");
                    commands.add(CommandsTree.EMPTY);
                }
                globalExpertMode = globalExpertMode && ((CommandsTree.Provider) providers[i]).isExpertMode();
            }
        }
        return commands;
        
    }
    
    private JMenuItem getPresenter(boolean inMenu) {
        FileObject[] files = getActivatedFiles();
        JInlineMenu inlineMenu = new JInlineMenu();
        inlineMenu.setMenuItems(createMenuItems(files, inMenu));
        inlineMenuReference = new WeakReference(inlineMenu);
        inlineMenuWasInMenu = inMenu;
        inlineMenu.addPropertyChangeListener(this); // Hack. This object will not be garbage-collected before the inlineMenu
        return inlineMenu;
    }
    
    static FileObject[] getActivatedFiles() {
        Node[] n = WindowManager.getDefault().getRegistry ().getActivatedNodes ();
        ArrayList foList = new ArrayList();
        if (n != null) {
            for (int i = 0; i < n.length; i++) {
                 DataObject obj = (DataObject)n[i].getCookie (DataObject.class);
                 if (obj != null) {
                     foList.addAll(obj.files());
                 }
            }
        }
        FileObject[] files = (FileObject[]) foList.toArray(new FileObject[foList.size()]);
        return files;
    }
    
    JMenuItem[] createMenuItems(FileObject[] files, boolean inMenu) {
        CommandsTree commands = getGlobalCommands();
        ArrayList menuItems = new ArrayList();
        CommandsTree[] subCommands = commands.children();
        boolean addSeparator = false;
        for (int i = 0; i < subCommands.length; i++) {
            //System.out.println("GlobAction.getPresenter() subCommands["+i+"] = "+subCommands[i]);
            JMenuItem menuItem = getPopupPresenter(subCommands[i], files, inMenu);
            //System.out.println("  menu item = "+menuItem);
            if (menuItem != null) {
                if (menuItem == SEPARATOR) {
                    if (menuItems.size() > 0) addSeparator = true;
                } else {
                    if (addSeparator) {
                        menuItems.add(null);
                        addSeparator = false;
                    }
                    menuItems.add(menuItem);
                }
            }
        }
        return (JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]);
    }
    
    private static final JMenuItem SEPARATOR = new JMenuItem(); // Dummy item denoting a separator
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    private JMenuItem getPopupPresenter(CommandsTree commands, FileObject[] files,
                                        boolean inMenu) {
        JMenuItem menu;
        //System.out.println("  has Children = "+commands.hasChildren());
        Map filesWithMessages = Collections.singletonMap(files, null);
        if (commands.hasChildren()) {
            menu = new CommandMenu(commands, filesWithMessages, true, inMenu, globalExpertMode);
            /*
            menu.addMenuKeyListener(new CtrlMenuKeyListener());
            ((JMenu) menu).addMenuListener(new javax.swing.event.MenuListener() {
                public void menuDeselected(javax.swing.event.MenuEvent e) {
                    //                deselectedMenu();
                    //                System.out.println("menu deselected");
                }
                public void menuCanceled(javax.swing.event.MenuEvent e) {
                    //                deselectedMenu();
                    //                System.out.println("menu canceled");
                }
                public void menuSelected(javax.swing.event.MenuEvent e) {
                    deselectedMenu();
                    //                System.out.println("Selected menu");
                }
            });
             */
        } else {
            CommandSupport cmd = commands.getCommandSupport();
            if (cmd == null) return SEPARATOR;
            // TODO expert mode. (Can be a global property ?!?)
            if (cmd.getDisplayName() == null) return null;
            if (cmd.getApplicableFiles(files) == null) {
                return null;
                //menu.setEnabled(false);
            }
            menu = CommandMenu.createItem(cmd, globalExpertMode,
                                          CommandMenu.DEFAULT_ADVANCED_OPTIONS_SIGN,
                                          inMenu, filesWithMessages);
        }
        if (inMenu && menu != null) {
            menu.setIcon(getIcon());
        }
        return menu;
    }
    
}
