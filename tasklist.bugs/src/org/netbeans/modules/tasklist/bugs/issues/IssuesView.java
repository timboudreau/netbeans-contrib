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

package org.netbeans.modules.tasklist.bugs.issues;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.filesystems.FileObject;
import org.openide.windows.TopComponent;
import org.xml.sax.SAXException;

/**
 * View for issues
 */
public class IssuesView extends TopComponent {
    // TODO: i18n
    private static final String[] COLUMN_NAMES = {
        "summary",
        "component",
        "subcomponent",
        "priority",
        "status",
        "resolution"
    };
    
    private static final Class COLUMN_CLASSES[] = {
        String.class,
        Integer.class,
        Integer.class,
        Integer.class,
        Integer.class,
        Integer.class
    };
    
    private FileObject fo;
    private JTable table;
    
    /**
     * Creates a new instance of BugsView
     */
    public IssuesView(FileObject fo) {
        this.fo = fo;
        
        javax.xml.parsers.DocumentBuilderFactory builderFactory = 
            javax.xml.parsers.DocumentBuilderFactory.newInstance();
        
        // TODO: turn the validation on
        builderFactory.setValidating(false);
        builderFactory.setExpandEntityReferences(false);
        
        javax.xml.parsers.DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace(); // TODO
            return;
        }
        org.w3c.dom.Document document;
        try {
            document = builder.parse(new org.xml.sax.InputSource(fo.getInputStream()));
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // TODO
            return;
        } catch (IOException e) {
            e.printStackTrace(); // TODO
            return;
        } catch (SAXException e) {
            e.printStackTrace(); // TODO
            return;
        }
        IssuesScanner scanner = new IssuesScanner(document);
        final IssuesList list = scanner.visitDocument();

        // TODO: remove
        System.out.println("list.issues.size " + list.issues.size());

        table = new JTable();
        table.setModel(new AbstractTableModel() {
            public int getRowCount() {
                return list.issues.size();
            }
            public int getColumnCount() {
                return 6;
            }
            public String getColumnName(int columnIndex) {
                return COLUMN_NAMES[columnIndex];
            }
            public Class getColumnClass(int columnIndex) {
                return COLUMN_CLASSES[columnIndex];
            }
            public Object getValueAt(int rowIndex, int columnIndex) {
                Issue issue = (Issue) list.issues.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return issue.summary;
                    case 1:
                        return new Integer(issue.component);
                    case 2:
                        return new Integer(issue.subcomponent);
                    case 3:
                        return new Integer(issue.priority);
                    case 4:
                        return new Integer(issue.status);
                    case 5:
                        return new Integer(issue.resolution);
                }
                return null;
            }
        });
        
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }
    
    protected String preferredID() {
        return "issues";
    }    
}
