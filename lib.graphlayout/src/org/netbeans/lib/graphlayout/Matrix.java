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

package org.netbeans.lib.graphlayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/** Presents a graph by using a matrix of dependencies.
 *
 * @author Jaroslav Tulach
 */
final class Matrix extends JPanel 
implements TableColumnModelListener, MouseListener, ChangeListener {
    private Graph graph;
    private JTable table;
    private DefaultTableModel model;
    private HashMap colors = new HashMap();
    private int colorindex;
    private Set cyclicDeps = new HashSet();
    
    Matrix (Graph g) {
        super(new BorderLayout());
        this.graph = g;
        this.table = new GraphTable(g);
        g.addChangeListener(this);
        
        init();
    }

    public void stateChanged(ChangeEvent e) {
        init();
    }
    
    private void init() {
        Component[] arr = getComponents();
        for (int i = 0; i < arr.length; i++) {
            remove(arr[i]);
        }
        
        this.table.addMouseListener(this);
        
        ColorRend r = new ColorRend();
        
        
        model = new DefaultTableModel();
        
        cyclicDeps.clear();
        try {
            graph.topologicallySortVertexes();
        } catch (TopologicalSortException ex) {
            // ok
        }
        graph.sortByGroups();
        try {
            graph.topologicallySortVertexes();
        } catch (TopologicalSortException ex) {
            Set[] un = ex.unsortableSets();
            for (int i = 0; i < un.length; i++) {
                cyclicDeps.add(un[i]);
            }
        }
        
        {
            int size = graph.vertexes.size();
            model.setRowCount(size);
            model.setColumnCount(size + 1);
        }

        JTableHeader head = this.table.getTableHeader();
        this.table.setModel(model);
        TableColumnModel headModel = head.getColumnModel();
        headModel.addColumnModelListener(this);
        head.setResizingAllowed(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        {
            Iterator it = graph.edges.iterator();
            while (it.hasNext()) {
                Edge e = (Edge)it.next();
                int from = graph.vertexes.indexOf(e.v1);
                int to = graph.vertexes.indexOf(e.v2);
                model.setValueAt(new Integer(e.strength), to, from + 1);
            }
        }
        {
            headModel.getColumn(0).setHeaderValue("");
            headModel.getColumn(0).setMinWidth(200);
            headModel.getColumn(0).setCellRenderer(r);
            Iterator it = graph.vertexes.iterator();
            int cnt = 0;
            while (it.hasNext()) {
                Vertex v = (Vertex)it.next();
                model.setValueAt(v, cnt, 0);
                headModel.getColumn(cnt + 1).setMaxWidth(2 * table.getRowHeight());
                headModel.getColumn(cnt + 1).setHeaderValue("" + (cnt + 1));
                headModel.getColumn(cnt + 1).setCellRenderer(r);
                model.setValueAt("X", cnt, cnt + 1);
                cnt++;
            }
        }
        
        
        
        add(new javax.swing.JScrollPane(this.table), BorderLayout.CENTER);
        invalidate();
        validate();
    }

    private Color findColor (Vertex v) {
        Color c = (Color)colors.get (v.info);
        if (c == null) {
            if (colorindex == Renderer.basecolors.length) {
                colorindex = 0;
            }
            c = Renderer.basecolors[colorindex++];
            colors.put (v.info, c);
        }
        return c;
    }
    
    public void columnAdded(TableColumnModelEvent e) {
    }

    public void columnRemoved(TableColumnModelEvent e) {
    }

    public void columnMoved(TableColumnModelEvent e) {
        model.moveRow(e.getFromIndex() - 1, e.getFromIndex() - 1, e.getToIndex() - 1);
    }

    public void columnMarginChanged(ChangeEvent e) {
    }

    public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {
    }

    private void popup (java.awt.event.MouseEvent e) {
        final int[] rows = table.getSelectedRows();

        Vertex vertex = (Vertex)graph.vertexes.get(0);
        if (rows.length == 1) {
            vertex = (Vertex)graph.vertexes.get(rows[0]);
        }
        if (rows.length == 0) {
            return;
        }
        
        
        final Vertex v = vertex;
        
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu (v.name);
        final javax.swing.JMenuItem remove = menu.add ("Remove " + v.name);
        remove.setEnabled(rows.length == 1);
        final javax.swing.JMenuItem removeGroup = menu.add ("Remove All " + v.info);
        remove.setEnabled(rows.length == 1);
        menu.addSeparator ();
        final javax.swing.JMenuItem merge = menu.add ("Merge All " + v.info);
        merge.setEnabled (rows.length == 1);
        final javax.swing.JMenuItem mergeSel = menu.add ("Merge Selected");
        menu.addSeparator ();
        mergeSel.setEnabled (rows.length > 1);

        class Action implements java.awt.event.ActionListener {
            public void actionPerformed (java.awt.event.ActionEvent ev) {
                repaint ();
                if (ev.getSource () == remove) {
                    graph.removeVertex (v);
                    init();
                    return;
                }
                if (ev.getSource () == removeGroup) {
                    graph.removeGroup (v.info);
                    init();
                    return;
                }
                if (ev.getSource () == merge) {
                    Iterator it = graph.vertexes.iterator();
                    ArrayList names = new ArrayList();
                    while (it.hasNext()) {
                        Vertex vert = (Vertex)it.next();
                        
                        if (vert.info.equals(v.info)) {
                            names.add(vert.name);
                        }
                    }
                    
                    graph.mergeVertexes(v.info, (String[])names.toArray(new String[0]));
                    init();
                    return;
                }
                if (ev.getSource () == mergeSel) {
                    String[] names = new String[rows.length];
                    for (int i = 0; i < rows.length; i++) {
                        Vertex v = (Vertex)graph.vertexes.get(rows[i]);
                        names[i] = v.name;
                    }
                    
                    graph.mergeVertexes(v.info, names);
                    init();
                    return;
                }
            }
        }
        Action a = new Action ();
        remove.addActionListener (a);
        removeGroup.addActionListener (a);
        mergeSel.addActionListener (a);
        merge.addActionListener (a);
        
        menu.show (table, e.getX (), e.getY ());
    }

    public void mouseClicked (java.awt.event.MouseEvent e) {
        if (e.isPopupTrigger ()) {
            popup (e);
            return;
        }
    }    
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger ()) {
            popup (e);
            return;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger ()) {
            popup (e);
            return;
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
    
    private static final class GraphTable extends JTable {
        private Graph g;
        
        public GraphTable(Graph g) {
            this.g = g;
        }

        public boolean isCellEditable(int row, int clm) {
            return false;
        }
        
        public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            //int realColumnIndex = convertColumnIndexToModel(colIndex);
            int realColumnIndex = colIndex;
            
            if (getModel().getValueAt(rowIndex, realColumnIndex) == null || realColumnIndex == 0) {
                return null;
            }
            
            Vertex depended = (Vertex)getModel().getValueAt(rowIndex, 0);
            Vertex depending = (Vertex)getModel().getValueAt(realColumnIndex - 1, 0);
            return "Module " + depending.name + " depends on " + depended.name;
        }
    } // end of GraphTable
    
    private final class ColorRend extends DefaultTableCellRenderer {
        private Color def;
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel retValue = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (def == null && !isSelected) {
                def = retValue.getBackground();
            }
            if (def != null && !isSelected) {
                retValue.setBackground(def);
            }
            
            retValue.setOpaque(true);
            
            if (row == column - 1) {
                retValue.setBackground(retValue.getForeground());
                return retValue;
            }
            
            Vertex v = (Vertex)table.getModel().getValueAt(row, 0);
            if (column == 0) {
                if (!isSelected) {
                    retValue.setBackground(findColor(v));
                }
                retValue.setText(table.convertColumnIndexToModel(row + 1) + ". " + v.name);
            } else {
                Vertex u = (Vertex)table.getModel().getValueAt(column, 0);
                if ((cyclicDeps.contains(v) || cyclicDeps.contains(u)) && isSelected) {
                    setBackground(Color.GRAY.brighter().brighter());
                }
            }
                
            return retValue;
        }
    } // end of ColorRend
    
}
