/*
 * MetricsTableModel.java
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
