/*
 * MetricsTableModel.java
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

import java.io.*;
import java.util.Collection;
import javax.swing.event.*;
import javax.swing.table.*;
import org.netbeans.modules.classfile.ClassName;

public class MetricsTableModel extends AbstractTableModel 
  implements TableModelListener {

    private static final long serialVersionUID = 6302748498401030538L;

    private ClassMetrics[] entries;
    private String[] columnTitles;

    public MetricsTableModel(Collection metrics) {
        entries = new ClassMetrics[metrics.size()];
        metrics.toArray(entries);
    }

    void setColumnTitles(String[] names) {
        columnTitles = names;
    }

    public int getRowCount() {
        return entries.length;
    }

    public int getColumnCount() {
        return columnTitles.length;
    }

    public String getColumnName(int i) {
        return columnTitles[i];
    }

    // Sorter class uses this method.
    public Class getColumnClass(int columnIndex) {
        return (columnIndex == 0) ? ClassName.class : Metric.class;
    }

    // This is a read-only table model, but double-clicking brings up
    // a details window.
    public boolean isCellEditable(int nRow, int nCol) {
        return nCol > 0;   // classnames aren't editable
    }

    public Object getValueAt(int nRow, int nCol) {
        if (nRow < 0 || nRow >= getRowCount())
            return "";

        ClassMetrics cm = entries[nRow];
        if (nCol == 0)
            return cm.getName();
        else if (nCol <= maxMetrics)
            return cm.getMetric(nCol - 1);
        else
            return "MetricTableModel.getValueAt is broken";
    }
    private static int maxMetrics = MetricsLoader.getNumberOfMetrics();

    // By default forward all events to all the listeners. 
    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
    }

    void saveTableAsXML(File file) throws IOException {
	String filename = file.getAbsolutePath();
	if (!filename.endsWith(".xml"))
	    filename += ".xml";
	PrintWriter xml = new PrintWriter(new FileWriter(filename));

	xml.println("<!-- Created by the NetBeans metrics module -->");
	xml.println("<!-- module version: " + 
		    MetricsLoader.getModuleVersion() + " -->\n");
	xml.println("<metrics_report>");

	int n = getRowCount();
	for (int i = 0; i < n; i++) {
	    ClassName name = (ClassName)getValueAt(i, 0);
	    xml.println("  <class name=\"" + name.getSimpleName() +
			"\" package=\"" + name.getPackage() + "\">");
	    for (int j = 1; j <= maxMetrics; j++) {
		Metric m = (Metric)getValueAt(i, j);
		xml.println("    <metric=\"" + m.getDisplayName() +
			    "\" value=\"" + m.getMetricValue() + "\"/>");
	    }
	    xml.println("  </class>");
	}
	xml.println("</metrics_report>");

	xml.close();
    }
}
