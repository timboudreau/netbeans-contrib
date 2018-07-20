/*
 * MetricDetailsInvoker.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.openide.windows.WindowManager;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

/**
 * Displays a pane describing how a metric was calculated.
 * Although it defines the TableCellEditor interface, it is
 * not an editor; that interface is used to get notification
 * when a cell has been clicked for details.
 */
class MetricDetailsInvoker extends AbstractCellEditor implements TableCellEditor {
    JLabel editorComponent;
    TextPanel textPanel;
    JDialog dialog;
    Metric metric;

    public MetricDetailsInvoker() {
        editorComponent = new JLabel("", SwingConstants.RIGHT);
    }

    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) { 
            return ((MouseEvent)anEvent).getClickCount() >= 2;
        }
        return true;
    }
    	
    public boolean shouldSelectCell(EventObject anEvent) { 
        return false;
    }

    public Object getCellEditorValue() {
        return metric.getMetricValue();
    }

    public void setMetric(Metric metric) { 
        this.metric = metric;
        textPanel = (TextPanel)metric.getDetailsViewer().getCustomEditor();
        editorComponent.setText(metric.getMetricValue().toString());
    }

    public boolean stopCellEditing() { 
        closeDialog();
        fireEditingStopped(); 
        return true;
    }

    public void cancelCellEditing() { 
        closeDialog();
        fireEditingCanceled(); 
    }

    private void openDialog() {
        dialog = new JDialog(
            WindowManager.getDefault().getMainWindow(), 
            MetricsNode.bundle.getString("STR_MetricsPaneTitle"), 
            false) {
		public Dimension getPreferredSize() {
		    Dimension screenSize = 
			java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		    Dimension containerSize = super.getPreferredSize();
		    return new Dimension(
		        Math.min(containerSize.width, screenSize.width),
		        Math.min(containerSize.height, screenSize.height));
		}
	    };
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null); // center dialog on screen
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(textPanel);
        dialog.getContentPane().add(sp, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
    }    

    private void closeDialog() {
        if (dialog != null) {
            dialog.setVisible(false);
            dialog = null;
        }
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row, int column) {
        setMetric((Metric)value);
        openDialog();
	return editorComponent;
    }

    public Component getComponent() {
	return editorComponent;
    }

}
