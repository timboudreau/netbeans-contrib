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

import org.openide.ErrorManager;
import org.openide.TopManager;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.actions.SystemAction;
import org.openide.util.io.SafeException;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/** 
 * Displays class metrics in a JTable.
 *
 * @author Thomas Ball
 */
class MetricsPane extends TopComponent implements PropertyChangeListener {
    JTable table;
    MetricsTableModel model;

    private final static int NAME_COL_WIDTH = 340;
    private final static int METRIC_COL_WIDTH = 50;

    MetricsPane(Set metricsList) {
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

	// TopComponent management
	putClientProperty("PersistenceType", "Never"); // don't serialize
	setLayout(new BorderLayout());
	add(sp, BorderLayout.CENTER);
	setName(MetricsNode.bundle.getString("STR_MetricsPaneTitle")); // NOI18N
	getAccessibleContext().setAccessibleName(
            MetricsNode.bundle.getString("STR_MetricsPaneTitle")); // NOI18N
	getAccessibleContext().setAccessibleDescription(
            MetricsNode.bundle.getString("STR_MetricsPaneDescription")); // NOI18N
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

    protected Mode getDockingMode(Workspace workspace) {
	Mode mode = workspace.findMode(CloneableEditorSupport.EDITOR_MODE);
	if (mode == null) {
	    mode = workspace.createMode(
                CloneableEditorSupport.EDITOR_MODE, getName(),
                CloneableEditorSupport.class.getResource(
                "/org/openide/resources/editorMode.gif" // NOI18N
                ));
	}
	return mode;
    }
        
    public void open(Workspace workspace) {
	if (workspace == null)
	    workspace = WindowManager.getDefault().getCurrentWorkspace();
	Mode editorMode = getDockingMode(workspace);
	editorMode.dockInto(this);
	super.open(workspace);
	requestFocus();
    }

    protected void componentClosed() {
	SystemAction saveAction = SystemAction.get(SaveAsAction.class);
	saveAction.removePropertyChangeListener(this);
    }

    /** Overrides superclass method. Gets actions for this top component. */
    public SystemAction[] getSystemActions() {
	SystemAction saveAction = SystemAction.get(SaveAsAction.class);
	saveAction.addPropertyChangeListener(this);
        SystemAction[] oldValue = super.getSystemActions();
        return SystemAction.linkActions(
	    new SystemAction[] { saveAction, null},
            oldValue);
    }

    public void propertyChange(PropertyChangeEvent evt) {
	try {
	    File file = (File)(File)evt.getNewValue();
	    model.saveTableAsXML(file);

	    MessageFormat mf = new MessageFormat(
		MetricsNode.bundle.getString("MSG_SavedTo"));
	    String msg = mf.format(new Object[] { file.getPath() });
	    TopManager.getDefault().setStatusText(msg);
	} catch (IOException e) {
	    ErrorManager.getDefault().notify(e);
	}
    }

    /* This pane isn't serializable, but setting its persistence type
     * to "never" is documented as only a hint.  The following should 
     * allow the persistence mechanism to work but not store or 
     * retrieve anything.
     */
    MetricsPane() {
	// only called during serialization, ignore...
    }

    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
	throw new SafeException(
            new UnsupportedOperationException("MetricsPane isn't loadable."));
    }
}
