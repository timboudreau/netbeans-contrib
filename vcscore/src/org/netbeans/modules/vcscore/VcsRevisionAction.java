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

package org.netbeans.modules.vcscore;

import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.*;

import org.openide.awt.JMenuPlus;
import org.openide.awt.JInlineMenu;
import org.openide.util.actions.*;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.WeakList;
//import org.netbeans.modules.vcscore.util.FileEditorSupport;
//import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
//import org.netbeans.modules.vcscore.versioning.VcsFileObject;

/**
 *
 * @author  Martin Entlicher
 */
public class VcsRevisionAction extends NodeAction implements ActionListener {

    protected WeakReference fileSystem = new WeakReference(null);
    protected WeakReference fileObject = new WeakReference(null);
    protected Collection selectedRevisionItems = null;

    private static final long serialVersionUID = 8803248742536265293L;
    
    //private VcsFileSystem fileSystem = null;
    //private FileObject fo = null;
    
    //private Hashtable additionalVars = new Hashtable();
    
    //private VcsCommand openRevisionCommand = null;

    /** Creates new RevisionAction 
     * Gets revision actions from filesystem and acts on a file object.
     * Both the filesystem and file object are obtained from the revision node.
     */
    public VcsRevisionAction() {
    }
    
    /* Creates new RevisionAction 
     * Gets revision actions from filesystem and acts on the givn file object.
     * @param fileSystem the file system to get the actions from
     * @param fo the file object to act on
     *
    public RevisionAction(VcsFileSystem fileSystem, FileObject fo) {
        this.fileSystem = fileSystem;
        this.fo = fo;
    }
     */

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = new WeakReference(fileSystem);
    }
    
    public void setFileObject(FileObject fileObject) {
        this.fileObject = new WeakReference(fileObject);
    }
    
    public void setSelectedRevisionItems(Collection items) {
        if (items == null) {
            this.selectedRevisionItems = null;
            return ;
        }
        this.selectedRevisionItems = new WeakList(items);
    }
    
    public String getName(){
        return NbBundle.getMessage(VcsRevisionAction.class, "CTL_Revision_Action"); // NOI18N
    }

    //-------------------------------------------
    public void performAction(Node[] nodes){
        //D.deb("performAction()"); // NOI18N
    }

    //-------------------------------------------
    public boolean enable(Node[] nodes){
        //D.deb("enable()"); // NOI18N
        return nodes.length > 0;
    }

    //-------------------------------------------
    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }

    protected JMenuItem createItem(VcsCommand cmd) {
        JMenuItem item = null;
        /*
        if (cmd == null) {
            //E.err("Command "+name+" not configured."); // NOI18N
            item = new JMenuItem("'"+name+"' not configured.");
            item.setEnabled(false);
            return item;
        }
        */
        //Hashtable vars = fileSystem.getVariablesAsHashtable();
        String label = cmd.getDisplayName();
        /*
        if (label.indexOf('$') >= 0) {
            label = Variables.expandFast(vars, label, true);
        }
         */
        item = new JMenuItem(label);
        String[] props = cmd.getPropertyNames();
        if (props != null && props.length > 0) {
            item.setActionCommand(cmd.getName());
            item.addActionListener(this);
        }
        return item;
    }

    /**
     * Add a popup submenu.
     */
    private void addMenu(Node commands, JMenu parent, int numSelected) {
        Children children = commands.getChildren();
        for (Enumeration subnodes = children.nodes(); subnodes.hasMoreElements(); ) {
            Node child = (Node) subnodes.nextElement();
            VcsCommand cmd = (VcsCommand) child.getCookie(VcsCommand.class);
            if (cmd == null) {
                parent.addSeparator();
                continue;
            }
            int numRev = VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS);
            if (numRev != numSelected
                || VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)
                || cmd.getDisplayName() == null) continue;
            if (!child.isLeaf()) {
                JMenu submenu;
                String[] props = cmd.getPropertyNames();
                if (props == null || props.length == 0) {
                    submenu = new JMenuPlus(cmd.getDisplayName());
                } else {
                    submenu = new JMenuPlus();
                }
                addMenu(child, submenu, numSelected);
                parent.add(submenu);
            } else {
                JMenuItem item = createItem(cmd);
                parent.add(item);
            }
        }
    }

    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        JInlineMenu inlineMenu = new JInlineMenu();
        ArrayList menuItems = new ArrayList();
        /*
        Node[] nodes = getActivatedNodes();
        RevisionList rList = null;
        for (int i = 0; i < nodes.length; i++) {
            RevisionList list = (RevisionList) nodes[i].getCookie(RevisionList.class);
            if (list == null) continue;
            RevisionItem item = (RevisionItem) nodes[i].getCookie(RevisionItem.class);
            if (item == null) continue;
            //if (nodes[i] instanceof RevisionNode) {
            //    VcsFileSystem nodeFS = (VcsFileSystem) ((RevisionNode) nodes[i]).getFileSystem();
            if (rList == null) rList = list;
            else if (rList != list) return null;
            }
        }
        if (rList == null) return null;
         */
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        if (fileSystem == null) return null;
        Node commands = fileSystem.getCommands();
        Children children = commands.getChildren();
        Node[] commandRoots = children.getNodes();
        int numRevisions = selectedRevisionItems.size();
        for (int i = 0; i < commandRoots.length; i++) {
            VcsCommand cmd = (VcsCommand) commandRoots[i].getCookie(VcsCommand.class);
            if (cmd != null
                && VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS) == numRevisions
                && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)
                && cmd.getDisplayName() != null) {
                    
                JMenuItem menuItem = getPopupPresenter(commandRoots[i], cmd, numRevisions);
                if (menuItem != null) menuItems.add(menuItem);
            }
        }
        inlineMenu.setMenuItems((JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]));
        return inlineMenu;
    }

    private JMenuItem getPopupPresenter(Node commandRoot, VcsCommand cmd, int numSelected) {
        String name = commandRoot.getDisplayName();
        JMenuItem menu = new JMenuPlus(name);
        addMenu(commandRoot, (JMenu) menu, numSelected);
        if (menu.getSubElements().length == 0) {
            menu = createItem(cmd);
        }
        return menu;
    }
    
    public void actionPerformed(final java.awt.event.ActionEvent e){
        //D.deb("actionPerformed("+e+")"); // NOI18N
        String cmd = e.getActionCommand();
        //D.deb("cmd="+cmd); // NOI18N
        //Node[] nodes = getActivatedNodes();
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        FileObject fo = (FileObject) this.fileObject.get();
        if (fileSystem == null || fo == null) return ;
        RevisionItem[] items = (RevisionItem[]) selectedRevisionItems.toArray(new RevisionItem[0]);
        /*
        if (nodes.length == 0) {
            //E.err("internal error nodes.length<1 TODO");
            return ;
        } else {
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] instanceof RevisionNode) {
                    VcsFileSystem nodeFS = (VcsFileSystem) ((RevisionNode) nodes[i]).getFileSystem();
                    FileObject nodeFO = ((RevisionNode) nodes[i]).getFileObject();
                    if (fileSystem == null) fileSystem = nodeFS;
                    else if (fileSystem != nodeFS) return ;
                    if (fo == null) fo = nodeFO;
                    else if (fo != nodeFO) return ;
                }
            }
            if (fileSystem == null) return ;
        }
         */
        Table files = new Table();
        String mimeType = fo.getMIMEType();
        String fileName = fo.getPackageNameExt('/', '.');
        files.put(fileName, fo);
        Hashtable additionalVars = new Hashtable();
        if (mimeType != null) additionalVars.put("MIMETYPE", mimeType); // NOI18N
        if (items.length > 0) {
            additionalVars.put("REVISION", items[0].getRevision());
        }
        for(int i = 0; i < items.length; i++) {
            //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
            additionalVars.put("REVISION"+(i+1), items[i].getRevision());
        }
        additionalVars.put("BRANCH", getBranch(items));
        //D.deb("files="+files); // NOI18N

        //doCommand (files, cmd, fileSystem);
        VcsAction.doCommand(files, fileSystem.getCommand(cmd), additionalVars, fileSystem);
    }
    
    private String getBranch(RevisionItem[] items) {
        String branchTag = "";
        for (int i = 0; i < items.length; i++) {
            if (items[0] != null && items[0].isBranch()) {
                String[] tags = items[0].getTagNames();
                if (tags.length > 0) branchTag = tags[0];
            }
        }
        return branchTag;
    }
    
}
