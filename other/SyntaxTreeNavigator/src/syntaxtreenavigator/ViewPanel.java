/*DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/*Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/*The contents of this file are subject to the terms of either the GNU
/*General Public License Version 2 only ("GPL") or the Common
/*Development and Distribution License("CDDL") (collectively, the
/*"License"). You may not use this file except in compliance with the
/*License. You can obtain a copy of the License at
/*http://www.netbeans.org/cddl-gplv2.html
/*or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/*specific language governing permissions and limitations under the
/*License.  When distributing the software, include this License Header
/*Notice in each file and include the License file at
/*nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/*particular file as subject to the "Classpath" exception as provided
/*by Sun in the GPL Version 2 section of the License file that
/*accompanied this code. If applicable, add the following below the
/*License Header, with the fields enclosed by brackets [] replaced by
/*your own identifying information:
/*"Portions Copyrighted [year] [name of copyright owner]"
/*
/*Contributor(s):  */
package syntaxtreenavigator;

import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author  Tim Boudreau
 */
public class ViewPanel extends javax.swing.JPanel implements TreeSelectionListener, ListSelectionListener {
    
    /** Creates new form ViewPanel */
    public ViewPanel() {
    }
    
    public void addNotify() {
        super.addNotify();
        initComponents();
        jSplitPane1.setDividerLocation(0.5D);
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.getSelectionModel().addTreeSelectionListener(this);
        Font f = classNameLbl.getFont();
        f = f.deriveFont(Font.BOLD);
        classNameLbl.setFont (f);
        classes.setModel (new DefaultListModel());
        objNameLbl.setFont (f);
        classes.getSelectionModel().addListSelectionListener(this);
        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
    }
    

    boolean inSetObject = false;
    public void setObject (Object o) {
        if (o == null) {
            tree.setModel (new DefaultTreeModel(new DefaultMutableTreeNode()));
            classNameLbl.setText (" ");
            objNameLbl.setText (" ");
            body.setText (" ");
            classes.setModel (new DefaultListModel());
            return;
        }
        if (inSetObject) {
            return;
        }
        inSetObject = true;
        try {
        MutableTreeNode root;
        if (o instanceof Collection) {
            handleList ((Collection) o);
            DefaultMutableTreeNode rt= new DefaultMutableTreeNode();
            for (Iterator i=((Collection)o).iterator(); i.hasNext();) {
                rt.add (new IntrospectionTreeNode(i.next()));
            }
            root = rt;
        } else {
            root = new IntrospectionTreeNode (o);
        }
        tree.setModel (new DefaultTreeModel (root));
        Method[] m = o.getClass().getMethods();
        Object name = null;
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals("getName") && m[i].getTypeParameters().length == 0) {
                try {
                    name = m[i].invoke(o);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }
        body.setText (o.toString());
        if (name != null) {
            objNameLbl.setText (name.toString());
        } else {
            objNameLbl.setText(" ");
        }
        updateInterface (o.getClass());
        } finally {
            inSetObject = false;
        }
    }
    
    private void updateInterface (Class clazz) {
        Set <Class> s = new HashSet<Class> ();
        getAllInterfaces (clazz, s);
        DefaultListModel lm = new DefaultListModel();
        for (Class c : s) {
            String str = c.getName();
            if (str.startsWith("com.sun.source.tree") || str.startsWith("javax.tools") ||
                    str.startsWith("javax.lang")) {
                lm.addElement("<html><b>" + str);
            } else if (str.startsWith("java.lang") || str.startsWith("java.util")) {
                lm.addElement("<html><font color=#999999>" + str);
            } else {
                lm.addElement(str);
            }
        }
        classes.setModel (lm);
        classNameLbl.setText (strippedName(clazz.getName()));
    }
        
    static String strippedName(String s) {
        int ix1 = s.lastIndexOf('.');
        int ix2 = s.lastIndexOf('$');
        int ix = Math.max(ix1, ix2);
        if (ix != s.length() - 1) {
            return s.substring(ix + 1);
        } else {
            return s;
        }
    }
    
    static String sig (Method m) {
        StringBuffer sb = new StringBuffer(strippedName (m.getReturnType().getName()));
        sb.append(' ');
        sb.append (m.getName());
        sb.append (" (");
        Class[] tps = m.getParameterTypes();
        for (int i = 0; i < tps.length; i++) {
            sb.append (strippedName(tps[i].getName()));
            if (i != tps.length - 1) {
                sb.append (',');
            }
        }
        sb.append (')');
        return sb.toString();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        bodypane = new javax.swing.JScrollPane();
        body = new javax.swing.JEditorPane();
        objNameLbl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        classes = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        classNameLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        methodLbl = new javax.swing.JLabel();
        valLbl = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jSplitPane1.setDividerLocation(50);
        jSplitPane1.setResizeWeight(0.5);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        bodypane.setFont(new java.awt.Font("Monospaced", 0, 11));
        bodypane.setViewportView(body);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 200;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(bodypane, gridBagConstraints);

        objNameLbl.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(objNameLbl, gridBagConstraints);

        classes.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(classes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        classNameLbl.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel1.add(classNameLbl, gridBagConstraints);

        jScrollPane1.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        methodLbl.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(methodLbl, gridBagConstraints);

        valLbl.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(valLbl, gridBagConstraints);

        jSplitPane1.setLeftComponent(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void handleList(Collection c) {
        String tp = c instanceof List ? "List of " : "Collection of ";
        StringBuffer txt = new StringBuffer(tp);
        txt.append (c.size());
        txt.append (" items\n");
        Set classes = new HashSet();
        for (Iterator i=c.iterator();i.hasNext();) {
            Object o = i.next();
            classes.add (o.getClass());
            txt.append (o.getClass().getName());
            txt.append ("\n");
            txt.append (o.toString());
            if (i.hasNext()) {
                txt.append ("\n--------------------------------------\n");
            }
        }
        body.setText (txt.toString());
    }

    public void valueChanged(TreeSelectionEvent e) {
        TreeNode nd = (TreeNode) e.getPath().getLastPathComponent();
        if (nd instanceof IntrospectionTreeNode) {
            IntrospectionTreeNode itn = (IntrospectionTreeNode) nd;
            methodLbl.setText (itn.getMethodString());
            valLbl.setText(itn.getObject().toString());
            body.setText (itn.getString());
            if (!itn.isInvalid()) {
                updateInterface (itn.getObject().getClass());
            } else {
                classes.setModel (new DefaultListModel());
            }
        } else {
            methodLbl.setText (" ");
            valLbl.setText (" ");
        }
    }
    
    private Set <Class> getAllInterfaces(Class clazz, Set <Class> set) {
        if (Object.class.equals(clazz)) {
            return set;
        }
        Class[] classes = clazz.getInterfaces();
        for (int i = 0; i < classes.length; i++) {
            set.addAll (getAllInterfaces (classes[i], set));
        }
        set.addAll (Arrays.asList(classes));
        set.add (clazz);
        if (clazz.getSuperclass() != null) {
            getAllInterfaces (clazz.getSuperclass(), set);
        }
        return set;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (classes.getSelectedValue() != null) {
            Class clazz;
            try {
                String s = classes.getSelectedValue().toString();
                if (s.indexOf ("<") >= 0) {
                    StringBuffer sb = new StringBuffer();
                    for (int i=s.length() - 1; i >= 0; i--) {
                        char c = s.charAt(i);
                        if (c == '>' || Character.isWhitespace(c)) {
                            break;
                        }
                        sb.insert(0, c);
                    }
                    s = sb.toString();
                }
                clazz = Class.forName(s);
                StringBuffer sb = new StringBuffer();

                Type[] t = clazz.getGenericInterfaces();
                if (t.length > 0) {
                    sb.append ("GENERIC INTERFACES:\n");
                    for (int i = 0; i < t.length; i++) {
                        sb.append (t[i]);
                        sb.append ('\n');
                    }
                }
                if (clazz.getGenericSuperclass() != null) {
                    sb.append ("GENERIC SUPERCLASS:\n");
                    sb.append (clazz.getGenericSuperclass());
                }
                if (sb.length() == 0) {
                    sb.append ("[No generic interfaces or superclass for " + 
                            clazz.getName() + ']');
                }
                body.setText (sb.toString());
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane body;
    private javax.swing.JScrollPane bodypane;
    private javax.swing.JLabel classNameLbl;
    private javax.swing.JList classes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel methodLbl;
    private javax.swing.JLabel objNameLbl;
    private javax.swing.JTree tree;
    private javax.swing.JLabel valLbl;
    // End of variables declaration//GEN-END:variables
    
}
