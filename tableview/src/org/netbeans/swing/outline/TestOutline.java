/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Test.java
 *
 * Created on January 28, 2004, 6:15 PM
 */

package org.netbeans.swing.outline;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import org.openide.util.enum.AlterEnumeration;
import org.openide.util.enum.ArrayEnumeration;

/** A simple test of the Outline (aka TreeTable) class which implements
 * a filesystem browser.
 *
 * @author  Tim Boudreau
 */
public class TestOutline extends JFrame {
    private Outline outline;
    /** Creates a new instance of Test */
    public TestOutline() {
        setDefaultCloseOperation (EXIT_ON_CLOSE);
        getContentPane().setLayout (new BorderLayout());
        
        TreeModel treeMdl = new DefaultTreeModel(
            new FileTreeNode(File.listRoots()[0]));
        
        OutlineModel mdl = DefaultOutlineModel.createOutlineModel(treeMdl, 
            new FileAttrConverter(), false);
        
        outline = new Outline();
        outline.setRootVisible (true);
        
        outline.setModel (mdl);
        
        
        getContentPane().add(new JScrollPane(outline), BorderLayout.CENTER);
        setBounds (20, 20, 400, 200);
    }
    
    public static void main(String[] ignored) {
        try {
           // UIManager.setLookAndFeel (new javax.swing.plaf.metal.MetalLookAndFeel());
        } catch (Exception e) {}
        
        new TestOutline().show();
    }
    
    private class FileAttrConverter implements RowModel {
        
        public Class getColumnClass(int column) {
            switch (column) {
                case 0 : return Date.class;
                case 1 : return Long.class;
                default : assert false;
            }
            return null;
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public String getColumnName(int column) {
            return column == 0 ? "Date" : "Size";
        }
        
        public Object getValueFor(Object node, int column) {
            File f = ((FileTreeNode) node).getFile();
            switch (column) {
                case 0 : return new Date (f.lastModified());
                case 1 : return new Long (f.length());
                default : assert false;
            }
            return null;
        }
        
        public boolean isCellEditable(Object node, int column) {
            return false;
        }
        
        public void setValueFor(Object node, int column) {
            //do nothing for now
        }
        
    }
    
    
    private Map nodes = new HashMap();
    
    private class FileTreeNode implements TreeNode {
        private File file;
        public FileTreeNode (File file) {
            this.file = file;
            nodes.put(file, this);
        }
        
        public String toString() {
            String result = file.getName();
            if ("".equals(result)) {
                result = "/";
            }
            return result;
        }
        
        public File getFile() {
            return file;
        }
        
        public Enumeration children() {
            return new FileNodeEnumeration (new ArrayEnumeration(
                file.listFiles()));
        }
        
        public boolean getAllowsChildren() {
            return file.isDirectory();
        }
        
        public TreeNode getChildAt(int childIndex) {
            return new FileTreeNode(file.listFiles()[childIndex]);
        }
        
        public int getChildCount() {
            String[] s = file.list();
            return s == null ? 0 : s.length;
        }
        
        public int getIndex(TreeNode node) {
            File parent = ((FileTreeNode) node).getFile();
            if (parent != null) {
                File[] files = parent.listFiles();
                if (files == null) {
                    return 0;
                }
                return Arrays.asList(parent.listFiles()).indexOf(file);
            } else {
                return 0;
            }
        }
        
        public TreeNode getParent() {
            File par = file.getParentFile();
            return (TreeNode) nodes.get(par);
        }
        
        public boolean isLeaf() {
            return !file.isDirectory();
        }
    }
    
    private class FileNodeEnumeration extends AlterEnumeration {
        
        public FileNodeEnumeration (Enumeration files) {
            super (files);
        }
        
        protected Object alter(Object o) {
            File file = (File) o;
            TreeNode result = (TreeNode) nodes.get(file);
            if (result == null) {
                result = new FileTreeNode(file);
            }
            return result;
        }
    }    
}
