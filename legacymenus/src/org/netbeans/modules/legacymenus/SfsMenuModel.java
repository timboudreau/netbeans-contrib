/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * SfsMenuModel.java
 *
 * Created on May 22, 2004, 12:07 AM
 */

package org.netbeans.modules.legacymenus;

import java.util.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.netbeans.swing.menus.spi.*;

import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.*;

/**
 * MenuTreeModel implementation over the system filesystem.
 *
 * @author  Tim Boudreau
 */
public class SfsMenuModel implements MenuTreeModel, TreeModel {
    private FileObject root;
    private ComponentProvider mapper = new ComponentMapper(this);
    
    /** Creates a new instance of SfsMenuModel */
    public SfsMenuModel() {
        this (findRootFolder());
    }
    
    public ComponentProvider getComponentProvider() {
        return mapper;
    }
    
    SfsMenuModel(FileObject fld) {
        this.root = fld;
    }
    
    private static FileObject findRootFolder() {
        FileObject fo = 
            Repository.getDefault().getDefaultFileSystem().findResource("Menu"); //NOI18N
        if (fo == null) throw new Error("No Menu"); // NOI18N
        if (!fo.isFolder()) {
            throw new Error ("Menu folder is not a folder");
        }
        return fo;
    }
    
    private static FileObject tfo(Object o) {
        return (FileObject) o;
    }
    
    private static boolean isFolder(FileObject fo) {
        return fo.isFolder();
    }
    
    public Object getChild(Object obj, int idx) {
        FileObject fo = tfo(obj);
        assert isFolder(fo);
        return FolderSupport.getSortedChildren(fo)[idx];
    }
    
    public int getChildCount(Object obj) {
        FileObject fo = tfo(obj);
        if (isFolder(fo)) {
            return fo.getChildren().length;
        } else {
            return 0;
        }
    }
    
    public int getIndexOfChild(Object fld, Object child) {
        FileObject fo = tfo(fld);
        assert isFolder(fo);
        return Arrays.asList(FolderSupport.getSortedChildren(fo)).indexOf(child);
    }
    
    public Object getRoot() {
        return root;
    }
    
    public boolean isLeaf(Object obj) {
        return tfo (obj).isData() && !tfo(obj).isFolder();
    }
    
    public void valueForPathChanged(TreePath treePath, Object obj) {
        //do not implement, makes no sense for us
    }
    
    private ArrayList list = new ArrayList();
    public boolean hasListener (TreeModelListener l) {
        return list.contains(l);
    }
    
    public synchronized void addTreeModelListener(javax.swing.event.TreeModelListener treeModelListener) {
        list.add (treeModelListener);
    }
    
    public synchronized void removeTreeModelListener(javax.swing.event.TreeModelListener treeModelListener) {
        list.remove (treeModelListener);
    }
    
    public synchronized void fire (TreeModelEvent tme, int id) {
        for (Iterator i=list.iterator(); i.hasNext();) {
            TreeModelListener l = (TreeModelListener) i.next();
            switch (id) {
                case CHANGED :
                    l.treeNodesChanged(tme);
                    break;
                case INSERTED :
                    l.treeNodesInserted(tme);
                    break;
                case REMOVED :
                    l.treeNodesRemoved(tme);
                    break;
                case STRUCTURE :
                    l.treeStructureChanged(tme);
                    break;
                default :
                    throw new IllegalArgumentException (Integer.toString(id) + " - " + tme);
            }
        }
    }    
    
    public static final int CHANGED = 0;
    public static final int INSERTED = 1;
    public static final int REMOVED = 2;
    public static final int STRUCTURE = 3;    
}
