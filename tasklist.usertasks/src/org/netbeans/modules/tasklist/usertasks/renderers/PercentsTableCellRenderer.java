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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
