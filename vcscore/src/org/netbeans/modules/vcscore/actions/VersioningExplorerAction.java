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

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;
import org.netbeans.modules.vcscore.versioning.VersioningRepository;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;
//import org.netbeans.modules.vcscore.versioning.VcsFileObject;

/**
 * This action openes the Versioning Explorer tab.
 *
 * @author  Martin Entlicher
 */
public class VersioningExplorerAction extends GeneralCommandAction {

    private static final long serialVersionUID = -4949229720968764504L;
    
    /** Creates new VersioningExplorerAction */
    public VersioningExplorerAction() {
    }

    public String getName() {
        return org.openide.util.NbBundle.getMessage(VersioningExplorerAction.class, "LBL_VersioningExplorer");
    }
    
    protected String iconResource () {
        return "org/netbeans/modules/vcscore/versioning/impl/versioningExplorer.gif";
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction(Node[] nodes) {
        VersioningExplorer.Panel explorer = VersioningExplorer.getRevisionExplorer();
        explorer.open();
        HashMap filesByFS = getFilesByFS(nodes);
        if (Boolean.getBoolean("netbeans.vcsdebug")) {
            System.out.println("\nVersioningExplorer.performAction()");
            //System.out.println("SELECTED NODES = "+(new java.util.HashSet(java.util.Arrays.asList(explorer.getExplorerManager().getSelectedNodes()))));
            System.out.println("SELECTED NODES = "+(new java.util.HashSet(java.util.Arrays.asList(nodes))));
            System.out.println("FILES BY FS (size = "+filesByFS.size()+"):");
            for (Iterator it = filesByFS.keySet().iterator(); it.hasNext(); ) {
                String foPath = (String) it.next();
                String fsSystemName = (String) filesByFS.get(foPath);
                System.out.println("  file '"+foPath+"' from '"+fsSystemName+"'");
            }
        //explorer.setActivatedNodes(getVersioningNodes(filesByFS));
            VersioningRepository repository = VersioningRepository.getRepository();
            java.util.List vfsl = repository.getVersioningFileSystems();
            System.out.println("\nList of versioning filesystems (size = "+vfsl.size()+"):");
            for (Iterator it = vfsl.iterator(); it.hasNext(); ) {
                VersioningFileSystem vfs = (VersioningFileSystem) it.next();
                System.out.println(vfs+" with system name = '"+vfs.getSystemName()+"'");
            }
        }
        selectVersioningFiles(explorer, filesByFS);
        explorer.requestActive();
    }
 
    private HashMap getFilesByFS(Node[] nodes) {
        HashMap filesByFS = new HashMap();
        HashMap map = getSupporterMap(nodes);
        Iterator it = map.values().iterator();
        while (it.hasNext()) {
            Set foSet = (Set)it.next();
            FileObject[] origFos = new FileObject[foSet.size()];
            if (origFos == null || origFos.length == 0) {
                continue;
            }
            origFos = (FileObject[])foSet.toArray(origFos);
            FileObject[] correctFos = VcsUtilities.convertFileObjects(origFos);
            for (int i = 0; i < correctFos.length; i++) {
                FileObject fo = correctFos[i];
                try {
                    FileSystem fs = fo.getFileSystem();
                    if (fs != null) {
                        filesByFS.put(fo.getPackageNameExt('/', '.'), fs.getSystemName());
                    }
                } catch (FileStateInvalidException exc) {
                    continue;
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
            if (Boolean.getBoolean("netbeans.vcsdebug")) {
                System.out.println("Versioning FS of name '"+fsName+"' = "+vs);
            }
            if (vs != null) {
                FileObject fo = vs.findResource(fileName);
                if (Boolean.getBoolean("netbeans.vcsdebug")) {
                    System.out.println("  resource of name '"+fileName+"' = "+fo);
                }
                if (fo != null) {
                    //try {
                        //Node versioningRoot = org.netbeans.modules.vcscore.versioning.impl.VersioningDataSystem.getVersioningDataSystem();
                        Node fsRoot = manager.getRootContext().getChildren().findChild(vs.getSystemName());
                        if (Boolean.getBoolean("netbeans.vcsdebug")) {
                            System.out.println("  filesystem root = "+fsRoot);
                        }
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
        if (Boolean.getBoolean("netbeans.vcsdebug")) {
            System.out.println("  node for versioning file '"+fileName+"' = "+node);
        }
        return node;
    }
    
}
