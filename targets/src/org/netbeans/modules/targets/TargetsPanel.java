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

package org.netbeans.modules.targets;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import javax.swing.BoxLayout;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import java.awt.BorderLayout;
import javax.swing.JButton;

import org.openide.nodes.Node;

/**
 *
 * @author  Tim Boudreau
 */

class TargetsPanel extends TopComponent implements DropTargetListener {
    static final long serialVersionUID = 6021472310161712674L;
    private static TargetsPanel component = null;
    
    private TargetsPanel() {
        setName(NbBundle.getMessage(TargetsPanel.class, "LBL_Tab_Title00"));
        init();
    }
    
    private void init() {
//        setLayout (new BoxLayout(this, BoxLayout.Y_AXIS));
        DropTarget dt = new DropTarget (this, this);
    }
    
    public void doLayout() {
        Component[] c = getComponents();
        Insets ins = getInsets();
        if (getWidth() > getHeight()) {
            
        } else {
            int pos = ins.top;
            int w = getWidth() - (ins.left + ins.right);
            for (int i=0; i < c.length; i++) {
                Dimension d = c[i].getPreferredSize();
                c[i].setBounds (ins.left, pos, w, d.height);
                pos += d.height;
            }
        }
    }
    
    public static synchronized TargetsPanel findDefault() {
        if (component == null) {
            TopComponent tc = WindowManager.getDefault().findTopComponent("targets"); // NOI18N
            if (tc != null) {
                if (!(tc instanceof TargetsPanel)) {
                    IllegalStateException exc = new IllegalStateException
                    ("Incorrect settings file. Unexpected class returned." // NOI18N
                    + " Expected:" + TargetsPanel.class.getName() // NOI18N
                    + " Returned:" + tc.getClass().getName()); // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    TargetsPanel.getDefault();
                }
            } else {
                TargetsPanel.getDefault();
            }
        }
        return component;
    }
    
    public static synchronized TargetsPanel getDefault() {
        if (component == null) {
            component = new TargetsPanel();
        }
        return component;
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public Object readResolve() throws java.io.ObjectStreamException {
        return TargetsPanel.getDefault();
    }
    
    static void clearRef () {
        component = null;
    }
    
    public void requestFocus(){
        
    }
    
    public boolean requestFocusInWindow(){
        return super.requestFocusInWindow();
    }
    
    public String preferredID() {
        return "targets";
    }

    public void dragEnter(java.awt.dnd.DropTargetDragEvent e) {
        System.err.println("DRAG ENTER");
        boolean accept = checkDataFlavors (e.getCurrentDataFlavors()) != null;
        if (accept) {
            System.err.println("Accepting drag");
            e.acceptDrag(DnDConstants.ACTION_LINK);
        }
    }

    public void dragExit(java.awt.dnd.DropTargetEvent dropTargetEvent) {
    }

    public void dragOver(java.awt.dnd.DropTargetDragEvent dropTargetDragEvent) {
    }

    public void drop(java.awt.dnd.DropTargetDropEvent e) {
        System.err.println("Drop");
        e.acceptDrop(DnDConstants.ACTION_LINK);
        
        Transferable t = e.getTransferable();
        DataFlavor flavor = checkDataFlavors(t.getTransferDataFlavors());
        System.err.println("Flavor is " + flavor);
        if (flavor != null) {
            try {
                Object data = t.getTransferData(flavor);
                System.err.println("Got data " + data);
                if (data instanceof Node) {
                    maybeCreateButton ((Node) data);
                } else {
                    System.err.println("Data not a node, but " + data.getClass());
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dropTargetDragEvent) {
        
    }
    
    private void maybeCreateButton(Node n) {
        if (TargetButton.accept (n)) {
            System.err.println("Creating button");
            add (new TargetButton(n));
        } else {
            System.err.println("Not creating button");
        }
    }
    
    private static DataFlavor checkDataFlavors (DataFlavor[] f) {
        System.err.println("Flavors");
        for (int i=0; i < f.length; i++) {
            System.err.println(f[i]);
            if (Node.class.isAssignableFrom(f[i].getRepresentationClass())) {
                return f[i];
            }
        }
        return null;
    }
    
    public void addNotify() {
        super.addNotify();
        load();
    }
    
    public void removeNotify() {
        super.removeNotify();
        save();
        removeAll();
    }
    
    private void load() {
        TargetButton[] buttons = TargetButton.load();
        for (int i=0; i < buttons.length; i++) {
            add (buttons[i]);
        }
    }
    
    private void save() {
        Component[] c = getComponents();
        for (int i=0; i < c.length; i++) {
            if (c[i] instanceof TargetButton) {
                TargetButton.save((TargetButton) c[i]);
            }
        }
    }
}
