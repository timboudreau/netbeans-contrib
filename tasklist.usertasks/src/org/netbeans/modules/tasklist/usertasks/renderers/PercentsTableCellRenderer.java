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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.openide.util.NbBundle;

/**
 * PropertyEditor for done field.
 */
public class PercentsTableCellRenderer extends JPanel implements TableCellRenderer {
    private static final String COMPUTED =
        NbBundle.getMessage(PercentsTableCellRenderer.class, "Computed"); // NOI18N
    
    protected static Border noFocusBorder = new EmptyBorder(2, 2, 2, 2); 
    private static String[] TAGS = {
        COMPUTED,
        "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", // NOI18N
        "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" // NOI18N
    };
    
    /**
     * Used to combine 2 properties: percentComplete and progressComputed
     */
    public static final class Value {
        /** 0..100 progress of a task in percents */
        public int progress;
        
        /** true = value is computed*/
        public boolean computed;
    }
    
    private JProgressBar progressBar;
    private DefaultTableCellRenderer def = new DefaultTableCellRenderer();

    /**
     * Constructor
     */
    public PercentsTableCellRenderer() {
        setOpaque(true);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setOpaque(true);
        progressBar.setBorderPainted(true);
        progressBar.setBorder(new LineBorder(UIManager.getColor("Table.background")));
        
        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.CENTER);
    }
    
    // TODO: not used
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        Value v = new Value();
        if (text.equals(COMPUTED)) {
            v.computed = true;
        } else {
            text = text.trim();
            if (text.endsWith("%")) // NOI18N
                text = text.substring(0, text.length() - 1);
            try {
                v.progress = Integer.parseInt(text);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
    
    public Component getTableCellRendererComponent(javax.swing.JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        if (value == null)
            return def.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        }
        else {
            setBackground(table.getBackground());
        }
        int n = ((Integer) value).intValue();
        progressBar.setValue(n);
        progressBar.setString(n + "%"); // NOI18N
	if (hasFocus) {
	    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
	} else {
	    setBorder(noFocusBorder);
	}
        return this;
    }
    
    // TODO: workaround for a Swing bug (?)
    protected void paintComponent(java.awt.Graphics g) {
        Rectangle oldClip = g.getClipBounds();
        g.setClip(oldClip.x, oldClip.y, 
            oldClip.width - 1, 
            oldClip.height - 1);
        super.paintComponent(g);
        g.setClip(oldClip);
    }    
}
