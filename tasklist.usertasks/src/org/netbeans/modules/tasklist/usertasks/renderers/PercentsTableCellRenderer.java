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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

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
    protected static Border noFocusBorder = new EmptyBorder(2, 2, 2, 2); 
    
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
        progressBar.setBorderPainted(false);

        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.CENTER);
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
	    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") ); // NOI18N
	} else {
	    setBorder(null);
	}
        return this;
    }
}
