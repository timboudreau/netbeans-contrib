/*
 * MetricsPane.java
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

import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.windows.WindowManager;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
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
        super(WindowManager.getDefault().getMainWindow(), 
              MetricsNode.bundle.getString("STR_MetricsPaneTitle"), false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null); // center dialog on screen
	getAccessibleContext().setAccessibleName(
            MetricsNode.bundle.getString("STR_MetricsPaneTitle")); // NOI18N
	getAccessibleContext().setAccessibleDescription(
            MetricsNode.bundle.getString("STR_MetricsPaneDescription")); // NOI18N

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
	getContentPane().add(makeButtonBox(), BorderLayout.SOUTH);
    }

    private Box makeButtonBox() {
	JButton closeButton = 
	    new JButton(MetricsNode.bundle.getString("STR_Close"));
	closeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    close();
		}
	    });

	JButton saveAsButton = 
	    new JButton(MetricsNode.bundle.getString("STR_SaveAs"));
	saveAsButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    saveAs();
		}
	    });

	Box buttonBox = Box.createHorizontalBox();
	buttonBox.add(Box.createGlue());
	buttonBox.add(saveAsButton);
	buttonBox.add(Box.createHorizontalStrut(HGAP));
	buttonBox.add(closeButton);
	buttonBox.add(Box.createHorizontalStrut(HGAP));

	Box panelBox = Box.createVerticalBox();
	panelBox.add(Box.createVerticalStrut(VGAP));
	panelBox.add(buttonBox);
	panelBox.add(Box.createVerticalStrut(VGAP));

	return panelBox;
    }

    private static final int HGAP = 10;
    private static final int VGAP = 10;

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

    private void saveAs() {
	try {
	    JFileChooser chooser = new JFileChooser();
	    chooser.addChoosableFileFilter(new FileFilter() {
		public boolean accept(File f) {
		    return ((f.isDirectory() && !f.isHidden()) || 
			    f.getName().endsWith(".xml"));
		}

		public String getDescription() {
		    return MetricsNode.bundle.getString("STR_XML_Files");
		}
	    });
	    int returnVal = chooser.showSaveDialog(this);
	    if (returnVal != JFileChooser.APPROVE_OPTION)
		return;
	    File file = chooser.getSelectedFile();
	    model.saveTableAsXML(file);

	    MessageFormat mf = new MessageFormat(
		MetricsNode.bundle.getString("MSG_SavedTo"));
	    String msg = mf.format(new Object[] { file.getPath() });
	    StatusDisplayer.getDefault().setStatusText(msg);
	} catch (IOException e) {
	    ErrorManager.getDefault().notify(e);
	}
    }

    private void close() {
	setVisible(false);
	dispose();
    }
}
