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
