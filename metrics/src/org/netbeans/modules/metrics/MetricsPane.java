/*
 * MetricsPane.java
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

import org.openide.TopManager;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/** 
 * Displays class metrics in a JTable.
 *
 * @author Thomas Ball
 */
class MetricsPane extends JDialog {
    JTable table;
    MetricsTableModel model;

    private final static int NAME_COL_WIDTH = 340;
    private final static int METRIC_COL_WIDTH = 50;

    MetricsPane(Set metricsList) {
        super(TopManager.getDefault().getWindowManager().getMainWindow(), 
              MetricsNode.bundle.getString("STR_MetricsPaneTitle"), false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null); // center dialog on screen

        table = new JTable();
        table.setAutoCreateColumnsFromModel(false);

        model = new MetricsTableModel(metricsList);
        model.setColumnTitles(getColumnTitles());
        TableSorter sorter = new TableSorter(model);

        // Install a mouse listener in the TableHeader as the sorter UI.
        sorter.addMouseListenerToHeaderInTable(table);

        table.setModel(sorter);

	String[] tooltips = getColumnToolTips();

        // Add classname column first.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        TableColumn column = 
            new TableColumn(0, NAME_COL_WIDTH, renderer, null);
        table.addColumn(column);

        for (int i = 1; i < tooltips.length; i++) {
            renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(JLabel.RIGHT);
            MetricDetailsInvoker detailsInvoker = new MetricDetailsInvoker();
            column = new TableColumn(i, METRIC_COL_WIDTH, renderer, detailsInvoker);
	    column.setHeaderRenderer(createHeaderRenderer(tooltips[i]));
            table.addColumn(column);
        }

        // faster column resizing
        JTableHeader header = table.getTableHeader();
        header.setUpdateTableInRealTime(false);

        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(table);
        getContentPane().add(sp, BorderLayout.CENTER);
    }

    private TableCellRenderer createHeaderRenderer(String tooltip) {
	DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
	    public Component getTableCellRendererComponent(JTable table, Object value,
                         boolean isSelected, boolean hasFocus, int row, int column) {
	        if (table != null) {
	            JTableHeader header = table.getTableHeader();
	            if (header != null) {
	                setForeground(header.getForeground());
	                setBackground(header.getBackground());
	                setFont(header.getFont());
	            }
                }

                setText((value == null) ? "" : value.toString());
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        return this;
            }
	};
	label.setHorizontalAlignment(JLabel.CENTER);
	label.setToolTipText(tooltip);
	return label;
    }

    private static String[] columnTitles;
    private static String[] getColumnTitles() {
	if (columnTitles == null)
	    initTitlesAndToolTips();
	return columnTitles;
    }

    private static String[] columnToolTips;
    private static String[] getColumnToolTips() {
	if (columnToolTips == null)
	    initTitlesAndToolTips();
	return columnToolTips;
    }

    private static void initTitlesAndToolTips() {
	Metric[] metrics = MetricsLoader.createMetricsSet(null);
	int n = metrics.length;

	columnTitles = new String[n + 1];
	columnToolTips = new String[n + 1];
	String clsName = MetricsNode.bundle.getString("STR_ClassName");
        columnTitles[0] = clsName;
        columnToolTips[0] = clsName;

	for (int i = 0; i < n; i++) {
	    columnTitles[i + 1] = metrics[i].getDisplayName();
	    columnToolTips[i + 1] = metrics[i].getShortDescription();
	}
    }    
}
