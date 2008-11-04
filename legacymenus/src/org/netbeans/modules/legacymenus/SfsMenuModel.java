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
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.swing.menus.spi.MenuTreeModel.class)
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
