/*
 * MetricDetailsInvoker.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.openide.windows.WindowManager;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyEditor;
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
