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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import java.awt.BorderLayout;
import javax.swing.JButton;

import org.openide.nodes.Node;

/**
 *
 * @author  Tim Boudreau
 */

class TargetsToolbarPresenter extends JComponent implements DropTargetListener, Presenter.Toolbar {
    static final long serialVersionUID = 6021472310161712674L;
    private static TargetsPanel component = null;
    
    TargetsToolbarPresenter() {
        setName(NbBundle.getMessage(TargetsPanel.class, "LBL_Tab_Title00"));
        DropTarget dt = new DropTarget (this, this);
        setFont (ToolbarTargetButton.getIconFont());
    }
    
    public Component getToolbarPresenter() {
        return this;
    }
    
    int width = -1;
    public void paintComponent (Graphics g) {
        super.paintComponent (g);
        
        if (getComponentCount() == 0) {
            Insets ins = getInsets();
            Color c = new Color (128, 0, 0);
            g.setColor(c);
            g.drawRect (ins.left + 2, ins.top + 2, getWidth() - (ins.left + ins.right + 3), getHeight() - (ins.top + ins.bottom + 6));
            String dnd = NbBundle.getMessage (
                TargetsToolbarPresenter.class, "MSG_dnd"); //NOI18N
            
            int fh = g.getFontMetrics(getFont()).getHeight();
            int stringwidth = g.getFontMetrics(getFont()).stringWidth (dnd);
            int availwidth = getWidth() - (ins.left + ins.top);
            int left = ins.left;
            if (stringwidth < availwidth) {
                left = ins.left + ((availwidth / 2) - (stringwidth / 2));
            }
            int top = g.getFontMetrics(getFont()).getMaxAscent() + (getHeight() / 2) - (fh / 2);
            g.drawString (dnd, left, top);
        }
    }
    
    public void doLayout() {
        Component[] c = getComponents();
        Insets ins = getInsets();
        if (getWidth() > getHeight()) {
            int pos = ins.left;
            int h = getHeight() - (ins.top + ins.bottom + 4);
            for (int i=0; i < c.length; i++) {
                Dimension d = c[i].getPreferredSize();
                c[i].setBounds (pos, ins.top, d.width, h);
                pos += d.width;
            }
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
    
    public Dimension getPreferredSize() {
        if (getComponentCount() == 0) {
            return new Dimension (120, 28);
        } else {
            Component[] c = getComponents();
            Insets ins = getInsets();
            int h = 0;
            int w = ins.left + ins.right;
            for (int i=0; i < c.length; i++) {
                Dimension d = c[i].getPreferredSize();
                h = Math.max (d.height + ins.top + ins.bottom, h);
                w += d.width;
            }
            return new Dimension (w, h);
        }
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
        e.acceptDrop(DnDConstants.ACTION_LINK);
        
        Transferable t = e.getTransferable();
        DataFlavor flavor = checkDataFlavors(t.getTransferDataFlavors());
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
        if (ToolbarTargetButton.accept (n)) {
            add (new ToolbarTargetButton(n));
        } 
    }
    
    private static DataFlavor checkDataFlavors (DataFlavor[] f) {
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
        ToolbarTargetButton[] buttons = ToolbarTargetButton.load();
        for (int i=0; i < buttons.length; i++) {
            add (buttons[i]);
        }
    }
    
    private void save() {
        Component[] c = getComponents();
        for (int i=0; i < c.length; i++) {
            if (c[i] instanceof ToolbarTargetButton) {
                ToolbarTargetButton.save((ToolbarTargetButton) c[i]);
            }
        }
    }
}
