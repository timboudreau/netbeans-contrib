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

package org.netbeans.modules.tasklist.core.translators;

import java.awt.Image;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.translators.IconManager;
import org.netbeans.modules.tasklist.core.ColumnProperty;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskList;
import org.openide.NotifyDescriptor;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.awt.HtmlBrowser;
import org.openide.DialogDisplayer;


/**
 * This class exports a given TaskListView to HTML, using the current
 * visible columns, sorting column, etc.
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class HTMLSupport extends org.netbeans.modules.tasklist.core.translators.AbstractTranslator {
    // these values are stored temporary during export
    private ColumnProperty[] columns;
    private String[] headers;
    private Filter filter;
    private TaskListView view;
    private Writer writer;
    private boolean sortAscending = true;
    private boolean sortedByName = false;
    private boolean noSorting = true;
    private Node.Property sortedByProperty = null;

    public String getDefaultExtension() {
        return "html";
    }

    public String getImportName() {
        return null;
    }

    public String getExportName() {
        return NbBundle.getMessage(HTMLSupport.class, "HTML"); // NOI18N
    }

    public boolean supportsImport() {
        return false;
    }

    public boolean supportsExport() {
        return true;
    }

    // Extends AbstractTranslator
    protected String getExportDialogTitle() {
        return NbBundle.getMessage(HTMLSupport.class, "ExportHTML"); // NOI18N
    }
    
    protected String getImportDialogTitle() {
        return null;
    }

    /** Do the actual export of the list into the stream
        @param list The tasklist to read into
        @param file The FileObject for the file to be read 
        @param interactive Whether or not the user should be kept informed
        @param dir May be null, or a directory where the writer is
                 going to write the tasklist. Exporters may for example
                 write additional files in this directory.
        @return true iff the list was successfully written
    */
    protected boolean writeList(TaskList list, OutputStream out,
                             boolean interactive, File dir) throws IOException {
        Writer writer = new OutputStreamWriter(out, "utf8");  // NOI18N
        this.view = TaskListView.getCurrent();
        if (view == null) {
            return false;
        }
        this.filter = view.getFilter();
        this.writer = writer;

        if (interactive && (dir != null)) {
            IconManager icm = IconManager.getDefault();
            if (icm != null) {
                // Show the result?
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation (
                     NbBundle.getMessage(HTMLSupport.class,
                                       "ExportIcons"), // NOI18N
                     NotifyDescriptor.YES_NO_OPTION
                );
                Object result = DialogDisplayer.getDefault().notify(nd);
                if (NotifyDescriptor.YES_OPTION == result) {
                    iconMap = icm;
                    iconMap.setBase(dir); // XXX get right directory
                }
            }
        }
        
        exportHTML();
        writer.flush();

        iconMap = null;
        return true;
    }

    protected void exportDone(FileObject file) {
        URL url = null;
        try {
            url = file.getURL();
        } catch (FileStateInvalidException e) {
            // Can't show URL
            ErrorManager.getDefault().notify(e);
            return;
        }

        // Show the result?
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation (
                     NbBundle.getMessage(HTMLSupport.class,
                                       "ViewExportedHTML"), // NOI18N
                     NotifyDescriptor.YES_NO_OPTION
        );
        Object result = DialogDisplayer.getDefault().notify(nd);
        if (NotifyDescriptor.YES_OPTION == result) {
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        }
    }

   protected void exportDone(File file) {
        URL url;
        try {
            url = file.toURI().toURL(); // NOI18N
        } catch (MalformedURLException e) {
            // Can't show URL
            ErrorManager.getDefault().notify(e);
            return;
        }

        // Show the result?
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation (
            NbBundle.getMessage(HTMLSupport.class, "ViewExportedHTML"), // NOI18N
            NotifyDescriptor.YES_NO_OPTION);
        Object result = DialogDisplayer.getDefault().notify(nd);
        if (NotifyDescriptor.YES_OPTION == result) {
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        }
    }    
   
    /**
     * Creates CSS commands.
     */
    protected void writeCSS(Writer writer) throws IOException {
        writer.write(
            "    body { background-color:white; color:black }\n" + // NOI18N
            "    td { text-align:left; background-color:#eeeeee }\n" + // NOI18N
            "    th { text-align:center; background-color:#222288; color:white }\n" + // NOI18N
            "    td.sum { text-align:left }\n" + // NOI18N
            "    td.sumdone { text-align:left; background-color:#cccccc }\n" + // NOI18N
            "    td.done { background-color:#cccccc }\n" + // NOI18N
            "    td.subhead { text-align:center; background-color:#ccccff }\n" + // NOI18N
            "    td.datehead { text-align:center; background-color:#ccccff }\n" + // NOI18N
            "    td.space { background-color:white }\n" + // NOI18N
            "    td.date { text-align:left }\n" + // NOI18N
            "    td.dateholiday { text-align:left; color:red }\n"); // NOI18N
    }
    
    private void exportHTML() throws IOException {
        // TODO Conditionally do expandAll first? Ask user what
        // they want. Sometimes they may WANT the conditional
        // exposure of subtasks as indicated by their selection in
        // the GUI. (Need collapseAll too.)
        
        TableColumnModel headerModel = view.getColumnModel();

        // Based on Konqueror's output (KDE2's calendar app) 
        writer.write(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"" + // NOI18N
            " \"http://www.w3.org/TR/REC-html40/loose.dtd\">\n" + // NOI18N
            "<HTML><HEAD>\n  <META http-equiv=\"Content-Type\"" + // NOI18N
            " content=\"text/html; charset=UTF-8\">\n" + // NOI18N
            "  <TITLE>" + NbBundle.getMessage(HTMLSupport.class, "TaskListHeader") + "</TITLE>\n" + // NOI18N
            "  <style type=\"text/css\">\n"); // NOI18N
        writeCSS(writer);
        writer.write(
            "  </style>\n</HEAD><BODY>\n<H1>" +  // NOI18N
            NbBundle.getMessage(HTMLSupport.class, "TaskListHeader") +  // NOI18N
            "</H1>\n" + // NOI18N
            "<TABLE WIDTH=\"100%\" BORDER=0 CELLPADDING=3 CELLSPACING=3>\n"); // NOI18N
        
        // Write out header columns
        int headercols = headerModel.getColumnCount();
        writer.write("<TR>\n"); // NOI18N
        
        // Compute total width of the table, so we can produce
        // header proportion percentages
        int width = 0;
        for (int j = 0; j < headercols; j++) {
            TableColumn column = headerModel.getColumn(j);
            width += column.getWidth();
        }
        
        headers = new String[headercols];

        for (int j = 0; j < headercols; j++) {
            TableColumn column = headerModel.getColumn(j);
            int relativeWidth = column.getWidth()*100/width;
            if (j == 0) {
                writer.write("   <TH WIDTH=\"" + relativeWidth + // NOI18N
                             "%\">"); // NOI18N
            } else {
                writer.write("   <TH WIDTH=\"" + relativeWidth + // NOI18N
                             "%\">"); // NOI18N
            }
            headers[j] = column.getHeaderValue().toString();
            writer.write(toHTML(headers[j]));
            writer.write("</TH>\n"); // NOI18N
        }
        writer.write("</TR>\n"); // NOI18N
        
        // Write out task items
        
        // XXX How do I generalize the case that some exporters
        // want to nest subtasks, others want to put them at the
        // end (or at the beginning) ???
        // Perhaps task iteration should be left to each export
        // filter... yeah, that's probably best. In that case the
        // current methods can be viewed as a specific HTML export
        // filter needing to be replaced by an export type registry
        // abstraction
        
        // Determine sorting properties
        sortedByProperty = null;
        sortAscending = true;
        sortedByName = false;
        noSorting = true;
        
        columns = view.getColumns();
        
        // Look up sorting column
        for (int i = 0; i < columns.length; i++) {
            Boolean sorting =
                (Boolean)columns[i].getValue( "SortingColumnTTV"); // NOI18N
            if ((sorting != null) && (sorting.booleanValue())) {
                if (i == 0) {
                    sortedByName = true;
                } else {
                    sortedByProperty = columns[i];
                }
                Boolean desc = (Boolean)columns[i].
                    getValue( "DescendingOrderTTV"); // NOI18N
                sortAscending = ((desc == null) || !(desc.booleanValue()));
            }
        }
        
        // -1 == root node is hidden
        Node n = view.getExplorerManager().getRootContext();
        exportOneNode(n, -1);
        
        writer.write(
            "</TR>\n</TABLE>\n<P>" + // NOI18N
            NbBundle.getMessage(HTMLSupport.class, "PageCreatedBy",  // NOI18N
                "<A HREF=\"http://www.netbeans.org\">NetBeans</A> " +  // NOI18N
                "<A HREF=\"http://tasklist.netbeans.org\">tasklist</A></P>") + // NOI18N
            DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG).
               format(new Date()) +
            "<P>\n</BODY></HTML>\n" // NOI18N
        ); 

    }
    
    /**
     * Exports one node to HTML.
     *
     * @param n a task
     * @param level indent level (negative levels will not be exported)
     */
    private void exportOneNode(Node n, int level)
    throws IOException {
        if(filter != null && level != -1 && !filter.accept(n)) 
            return;
            
        writer.write("<TR>\n"); // NOI18N

        if (level >= 0) {
            writeOneTask(n, level);
        }
        
        List l = view.getSortedChildren(n, sortedByProperty, sortAscending,
            sortedByName, noSorting);
        
        ListIterator it = l.listIterator();
        while (it.hasNext()) {
            Node node = (Node)it.next();
            exportOneNode(node, level + 1);
        }
    }
    
    /**
     * Exports one task to HTML (one row in the table).
     *
     * @param n a task
     * @param level indent level >= 0
     */
    private void writeOneTask(Node n, int level)
    throws IOException {
        Boolean invisible = null;
        for (int i = 0; i < headers.length; ++i) {
            int colidx;

            for (colidx = 0; colidx < columns.length; ++colidx) {
               if (headers[i].equalsIgnoreCase(columns[colidx].getDisplayName())) {
                  invisible = (Boolean)columns[colidx].getValue("InvisibleInTreeTableView"); // NOI18N
                  break;
               }
            }

            if (colidx >= columns.length || (invisible != null && invisible.booleanValue())) {
                // XXX This should _never_ happen!!!!
                // However, it does. The cause seems to be that the TreeTableView
                // occasionally takes the VALUE of the display name of the
                // root note, and sticks it in the table header for the tree
                // column!! Thus, when this scenario occurs, we should simply
                // use the display name/tree column
                //  OLD: System.out.println("Sorry but I did not find <" + headers[i] + ">"); // NOI18N
                //  OLD: writer.write("   <TD>ERROR</TD>\n"); // NOI18N
                //  OLD: continue;
                colidx = 0;
            }

            // Map the node data...
            Node.PropertySet[] propsets = n.getPropertySets();
            for (int j = 0; j < propsets.length; ++j) {
                Node.Property[] props = propsets[j].getProperties();
                int k;

                for (k = 0; k < props.length; ++k) {
                    String name = props[k].getName();

                    // Locate the name among the columns
                    if (columns[colidx].getName().equals(name)) {
                        break;
                    }
                }

                if (k == props.length) {
                    // Not found in this set, search next
                    break;
                }
                
                writeOneField(writer, j == 0, n, props[k], level);                
            }
        }
        writer.write("</TR>\n"); // NOI18N
    }

    /**
     * Writes one field in the table.
     *
     * @param writer output stream
     * @param prop this property should be written
     * @param level indent level >= 0
     */
    protected void writeOneField(Writer writer, boolean first,
                                 Node node, Node.Property prop, int level)
    throws IOException {
        // Only use a "sub" table when we have subtasks.
        // The Swing HTML browser didn't handle these tables within
        // tables well (all text is centered) so avoid it when
        // it's not required.  (As a side benefit, it makes the
        // HTML document smaller and it renders faster.)
        boolean subtable = level > 0;

        if (prop.getName().equals(TaskListView.PROP_TASK_SUMMARY)) { // NOI18N
            writer.write("<TD>\n"); // NOI18N
            if (subtable) {
                writer.write("<TABLE BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\" WIDTH=\"100%\">\n"); // NOI18N
                    writer.write("<TR>\n"); // NOI18N
                        writer.write("<TD WIDTH=\"" + 20 * level + "\"></TD>\n"); // NOI18N
                        writer.write("<TD CLASS=\"sum\">\n"); // NOI18N
            }

                            exportIcon(writer, first, node);
                            try {
                                writer.write(toHTML(prop.getValue().toString()));
                            } catch (java.lang.IllegalAccessException e) {
                                writer.write("ERROR"); // NOI18N
                                ErrorManager.getDefault().notify(e);
                            } catch (InvocationTargetException e) {
                                writer.write("ERROR"); // NOI18N
                                ErrorManager.getDefault().notify(e);
                            }
            if (subtable) {
                        writer.write("</TD>\n"); // NOI18N
                    writer.write("</TR>\n"); // NOI18N
                writer.write("</TABLE>\n"); // NOI18N
            }
            writer.write("</TD>\n"); // NOI18N
        } else {
            writer.write("<TD>\n"); // NOI18N
                try {
                    Object value = prop.getValue();
                    String s = value == null ? "" : value.toString();
                    if (s == null) s = "";
                    writer.write(toHTML(s));
                } catch (java.lang.IllegalAccessException e) {
                    writer.write("ERROR"); // NOI18N
                    ErrorManager.getDefault().notify(e);
                } catch (InvocationTargetException e) {
                    writer.write("ERROR"); // NOI18N
                    ErrorManager.getDefault().notify(e);
                }
            writer.write("</TD>\n"); // NOI18N
        }
    }

    /** Export an icon reference. Return false if that was not done,
        either by user choice or IO fault. */
    protected boolean exportIcon(Writer writer, boolean first,
                                 Node node) throws IOException {
        if (first && (iconMap != null)) {
            Image icon = node.getIcon(java.beans.BeanInfo.ICON_COLOR_16x16);
            String name = iconMap.getIcon(icon);
            if (name != null) {
                writer.write("<IMG SRC=\"" + name + "\">&nbsp;\n"); // NOI18N
                return true;
            }
        }
        return false;
    }

    
    /** 
     * Convert a string to HTML, escaping &, <, >, etc
     */
    public static String toHTML(String text) {
        StringBuffer sb = new StringBuffer(2*text.length());
        // Same as TLUtils.appendHTMLChar, but we don't want to
        // translate ' ' into &nbsp;
        int n = text.length();
        for (int i = 0; i < n; i++) {
            switch (text.charAt(i)) {
            case '&': sb.append("&amp;"); break; // NOI18N
            case '<': sb.append("&lt;"); break; // NOI18N
            case '>': sb.append("&gt;"); break; // NOI18N
            case '"': sb.append("&quot;"); break; // NOI18N
            case '\n': sb.append("<br>"); break; // NOI18N
            default: sb.append(text.charAt(i)); break;
            }
        }
        return sb.toString();
    }

    protected IconManager iconMap = null;
}
