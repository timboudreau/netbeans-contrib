/*
 * TestingTreeModel.java
 *
 * Created on May 21, 2004, 8:17 PM
 */

package org.netbeans.swing.menus;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.swing.menus.spi.*;
import org.netbeans.swing.menus.api.*;
import org.netbeans.swing.menus.impl.*;

/**
 * A simple, fixed-content MenuTreeModel for testing TreeMenuBar with.
 *
 * @author  Tim Boudreau
 */
class TestingTreeModel implements MenuTreeModel, TreeModel {
    DefaultComponentProvider prov = new DefaultComponentProvider(this);
    ArrayList rootChildren = new ArrayList();
    /** Creates a new instance of TestingTreeModel */
    public TestingTreeModel() {
        buildTree();
        sanityCheck();
    }
    
    protected void buildTree() {
        for (int i=0; i < 5; i++) {
            String nm = "Menu " + i;
            FolderProxy p = new FolderProxy (nm);
            rootChildren.add (p);
            for (int j=0; j < 5; j++) {
                String curr = nm + " ACTION " + j;
                if (j != 4) {
                    p.children().add (new ActionProxy(curr));
                } else {
                    FolderProxy childMenu = new FolderProxy (nm + " submenu ");
                    childMenu.children().add (new ActionProxy("Submenu item for " + curr));
                    p.children().add(childMenu);
                }
            }
        }
    }
    
    protected void sanityCheck() {
        try {
            if (rootChildren.isEmpty()) {
                throw new Exception();
            }
            for (Iterator it=rootChildren.iterator(); it.hasNext();) {
                FolderProxy fld = (FolderProxy) it.next();
                ArrayList al = fld.children();
                if (al.isEmpty()) {
                    throw new Exception();
                }
                sanityCheckChildren (fld, al);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error ("Testing tree model failed sanity check.  The test suite is broken.");
        }
    }
    
    protected void sanityCheckChildren (FolderProxy par, List children) throws Exception {
        int idx = 0;
        for (Iterator i=children.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof FolderProxy) {
                sanityCheckChildren ((FolderProxy) o, ((FolderProxy) o).children);
            }
            if (getChild (par, idx) != o) {
                throw new Exception ("getChild() not returning correct children by index");
            }
            if (getChildCount(par) != children.size()) {
                throw new Exception ("inconsistent child index count");
            }
            if (isFolder(o) && (!(o instanceof FolderProxy))) {
                throw new Exception ("isFolder() not returning sane results");
            }
            if (o instanceof FolderProxy && isLeaf(o) && children.size() > 0) {
                throw new Exception ("isLeaf not returning correct results");
            }
            if (getIndexOfChild (par, o) != idx) {
                throw new Exception ("getIndexOfChild not sane");
            }
            if (getRoot() != this) {
                throw new Exception ("getRoot is broken");
            }
            if (o instanceof ActionProxy) {
                ((ActionProxy) o).actionPerformed(null);
                if (!checkPerformed(o)) {
                    throw new Exception ("CheckPerformed did not detect an action that was definitely performed");
                }
            }
            idx++;
        }
    }
    
    public MenuTreeModel.ComponentProvider getComponentProvider() {
          return prov;
    }
    
    public Object addChild (Object par, String name, boolean submenu) {
        Object child = submenu ? (Object) new FolderProxy (name) : (Object) new ActionProxy(name);
        if (!isFolder(par)) {
            throw new IllegalStateException ("Not a folder: " + par + " but tried to add " + child);
        }
        if (par == this) {
            if (rootChildren.contains(child)) {
                throw new IllegalArgumentException ("Root already contains " + child);
            }
            rootChildren.add(child);
            TreePath path = new TreePath (this);
            TreeModelEvent tme = new TreeModelEvent (this, path);
            fire(tme, Util.INSERTED);
        } else {
            ((FolderProxy) par).children().add(child);
            TreePath path = new TreePath (par);
            TreeModelEvent tme = new TreeModelEvent (this, path);
            fire(tme, Util.INSERTED);
        }
        return child;
    }
    
    public Object remove (Object item) {
        Object parent = findParent (item);
        if (parent == null) {
            throw new IllegalArgumentException (item + " has no parent");
        }
        if (parent == this) {
            rootChildren.remove(item);
            TreeModelEvent tme = new TreeModelEvent (this, new TreePath(this));
            fire (tme, Util.REMOVED);
        } else {
            FolderProxy fld = (FolderProxy) parent;
            fld.children().remove(item);
            TreeModelEvent tme = new TreeModelEvent (this, new TreePath(fld));
            fire (tme, Util.REMOVED);
        }
        return parent;
    }
    
    public void setIcon (Object obj, Icon ic) {
        if (obj instanceof FolderProxy) {
            throw new IllegalArgumentException ("Cannot set icon on a folder for testing");
        }
        ActionProxy ac = (ActionProxy) obj;
        ac.putValue (Action.SMALL_ICON, ic);
        Object parent = findParent (obj);
        if (parent == null) {
            throw new IllegalArgumentException (obj + " has no parent in setIcon");
        }
        TreePath path = new TreePath (parent);
        TreeModelEvent tme = new TreeModelEvent (this, path);
        fire (tme, Util.CHANGED);
    }
    
    public Object findParent (Object child) {
        if (child == this) {
            return null;
        }
        Object result = null;
        for (Iterator i=allFolders.iterator(); i.hasNext();) {
            FolderProxy p = (FolderProxy) i.next();
            if (p.children().contains(child)) {
                result = p;
                break;
            }
        }
        if (result == null && rootChildren.contains(child)) {
            result = this;
        }
        return result;
    }
    
    public Object getChild(Object par, int idx) {
        if (!isFolder(par) && par != this) {
            throw new IllegalArgumentException ("Not a folder: " + par + " but child at  " + idx + " requested");
        }
        if (par == this) {
            return rootChildren.get(idx);
        }
        return ((FolderProxy) par).children().get(idx);
    }    
    
    public int getChildCount(Object par) {
        if (!isFolder(par) && par != this) {
            throw new IllegalArgumentException ("Not a folder: " + par);
        }
        if (isFolder(par)) {
            return par == this ? rootChildren.size() : ((FolderProxy) par).children().size();
        }
        return rootChildren.size();
    }
    
    public int getIndexOfChild(Object par, Object ch) {
        if (!isFolder(par) && par != this) {
            throw new IllegalArgumentException ("Not a folder: " + par);
        }
        if (par == this) {
            int result = rootChildren.indexOf(ch);
            if (result == -1) {
                throw new IllegalArgumentException ("Not a child of root: " + ch);
            }
        }
        FolderProxy folder = (FolderProxy) par;
        int i = folder.children().indexOf(ch);
        if (i == -1) {
            throw new IllegalArgumentException ("Wrong parent for " + par + ": " + ch);
        }
        return i;
    }
    
    public Object getRoot() {
        return this;
    }
    
    private boolean isFolder (Object obj) {
        return (obj instanceof FolderProxy) || obj == this;
    }
    
    public boolean isLeaf(Object obj) {
        return !isFolder (obj) || (isFolder(obj) && ((FolderProxy) obj).children().isEmpty());
    }
    
    public void valueForPathChanged(TreePath treePath, Object obj) {
        //uh, whatever
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
                case Util.CHANGED :
                    l.treeNodesChanged(tme);
                    break;
                case Util.INSERTED :
                    l.treeNodesInserted(tme);
                    break;
                case Util.REMOVED :
                    l.treeNodesRemoved(tme);
                    break;
                case Util.STRUCTURE :
                    l.treeStructureChanged(tme);
                    break;
                default :
                    throw new IllegalArgumentException (Integer.toString(id) + " - " + tme);
            }
        }
    }
    
    private HashSet allFolders = new HashSet();
    
    private class FolderProxy {
        private ArrayList children = new ArrayList();
        private String name;
        public FolderProxy (String name) {
            this.name = name;
            allFolders.add(this);
        }
        
        private ArrayList children() {
            return children;
        }
        
        public String toString() {
            return name;
        }
    }

    private Object lastPerformed = null;
    
    public boolean checkPerformed (Object act) {
        boolean result = lastPerformed == act && (act instanceof ActionProxy);
        lastPerformed = null;
        return result;
    }
    
    private class ActionProxy extends AbstractAction {
        private String name;
        public ActionProxy (String name) {
            putValue (Action.NAME, name);
            this.name = name;
        }
        
        public String toString() {
            return (String) getValue(Action.NAME);
        }
        
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            lastPerformed = this;
        }
        
        
    }
    
        
    
}
