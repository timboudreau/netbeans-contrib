/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.explorer.ExplorerPanel;
import org.openide.nodes.Node;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.actions.NodeAction;

import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;
import org.netbeans.modules.vcscore.versioning.VersioningRepository;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
//import org.netbeans.modules.vcscore.versioning.VcsFileObject;

/**
 * This action openes the Versioning Explorer tab.
 *
 * @author  Martin Entlicher
 */
public class VersioningExplorerAction extends NodeAction {

    /** Creates new VersioningExplorerAction */
    public VersioningExplorerAction() {
    }

    protected boolean enable(Node[] nodes) {
        return true;
        /*
        if (nodes.length < 1) return false;
        for (int i = 0; i < nodes.length; i++) {
            DataObject dataObj = (DataObject) nodes[i].getCookie(DataObject.class);
            if (dataObj == null) continue;
            Set files = dataObj.files();
            for (Iterator it = files.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                FileSystem fs;
                try {
                    fs = fo.getFileSystem();
                } catch (FileStateInvalidException exc) {
                    continue;
                }
                if (VcsCommandsAction.isVcsFileSystem(fs)) return true;
            }
        }
        return false;
         */
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getMessage(VersioningExplorerAction.class, "LBL_VersioningExplorer");
    }
    
    protected String iconResource () {
        return "/org/netbeans/modules/vcscore/versioning/impl/versioningExplorer.gif";
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    protected void performAction(Node[] nodes) {
        VersioningExplorer.Panel explorer = VersioningExplorer.getRevisionExplorer();
        explorer.open();
        HashMap filesByFS = getFilesByFS(nodes);
        //System.out.println("SELECTED NODES = "+(new java.util.HashSet(java.util.Arrays.asList(explorer.getExplorerManager().getSelectedNodes()))));
        //explorer.setActivatedNodes(getVersioningNodes(filesByFS));
        selectVersioningFiles(explorer, filesByFS);
        explorer.requestFocus();
    }
    
    private HashMap getFilesByFS(Node[] nodes) {
        HashMap filesByFS = new HashMap();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dataObj = (DataObject) nodes[i].getCookie(DataObject.class);
            if (dataObj == null) continue;
            Set files = dataObj.files();
            for (Iterator it = files.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                FileSystem fs;
                try {
                    fs = fo.getFileSystem();
                } catch (FileStateInvalidException exc) {
                    continue;
                }
                if (VcsCommandsAction.isVcsFileSystem(fs)) {
                    filesByFS.put(fo.getPackageNameExt('/', '.'), fs.getSystemName());
                }
            }
        }
        return filesByFS;
    }
    
    private Node[] getVersioningNodes(Map filesByFS) {
        VersioningRepository repository = VersioningRepository.getRepository();
        LinkedList nodes = new LinkedList();
        for (Iterator it = filesByFS.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String fileName = (String) entry.getKey();
            String fsName = (String) entry.getValue();
            VersioningFileSystem vs = repository.getSystem(fsName);
            //System.out.println("getVersioningNodes("+fileName+", "+fsName+")");
            //System.out.println("  VersioningSystem = "+vs);
            if (vs != null) {
                FileObject fo = vs.findResource(fileName);
                //System.out.println("  Resource ="+fileName);
                if (fo != null) {
                    //Node root;
                    //root.getChildren().
                    try {
                        nodes.add(DataObject.find(fo).getNodeDelegate());
                    } catch (DataObjectNotFoundException exc) {}
                    //System.out.println("  Node Delegate = "+nodes.get(nodes.size() - 1));
                }
            }
        }
        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }
    
    private static void selectVersioningFiles(final ExplorerPanel explorer, final Map filesByFS) {
        VersioningRepository repository = VersioningRepository.getRepository();
        final org.openide.explorer.ExplorerManager manager = explorer.getExplorerManager();
        final LinkedList nodes = new LinkedList();
        for (Iterator it = filesByFS.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String fileName = (String) entry.getKey();
            String fsName = (String) entry.getValue();
            VersioningFileSystem vs = repository.getSystem(fsName);
            if (vs != null) {
                FileObject fo = vs.findResource(fileName);
                if (fo != null) {
                    //try {
                        //Node versioningRoot = org.netbeans.modules.vcscore.versioning.impl.VersioningDataSystem.getVersioningDataSystem();
                        Node fsRoot = manager.getRootContext().getChildren().findChild(vs.getSystemName());
                        nodes.add(selectVersioningFile(explorer, fsRoot, fileName));
                        //nodes.add(DataObject.find(fo).getNodeDelegate());
                    //} catch (DataObjectNotFoundException exc) {}
                }
            }
        }
        final Node[] nodeArray = (Node[]) nodes.toArray(new Node[nodes.size()]);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < nodeArray.length; i++) {
                    manager.setExploredContext(nodeArray[i]);
                }
                //explorer.setActivatedNodes((Node[]) nodes.toArray(new Node[nodes.size()]));
                try {
                    manager.setSelectedNodes(nodeArray);
                } catch (java.beans.PropertyVetoException exc) {
                }
            }
        });
        //return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }
    
    private static Node selectVersioningFile(ExplorerPanel explorer, Node root, String fileName) {
        StringTokenizer files = new StringTokenizer(fileName, "/");
        Node node = root;
        while (files.hasMoreTokens()) {
            String file = files.nextToken();
            Node subNode = node.getChildren().findChild(file);
            if (subNode != null) node = subNode;
            else break;
        }
        //System.out.println("setting explored context to "+node);
        //explorer.getExplorerManager().setExploredContext(node);
        return node;
    }
    
}
