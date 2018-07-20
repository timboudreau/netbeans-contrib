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

package org.netbeans.modules.keybindings.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class BindingsPanel extends JPanel {

    private JPanel fieldsPanel;
    
    private KeySequenceInputField _keySequencePrefixField;
    private KeySequenceInputField _keyCharsSequecePrefixField;
    private JTextField _actionPrefixField;
    
    private TableSorter tableSorter;
    private JTable _bindingsTable;
    
    private JPanel buttonsPanel;
    private JButton _outputToHtmlButton;
    private JButton _outputToXmlButton;
    
    private int _initialSortingColumn;
    
    /** Creates a new instance of BindingsPanel */
    public BindingsPanel(List bindings, int sortingColumn) {
        _initialSortingColumn = sortingColumn;
        _createGUI(bindings, sortingColumn);
    }
    
    private void _createGUI(List bindings, int sortingColumn) {
        setLayout(new BorderLayout());
        
        fieldsPanel = new JPanel();
        
        _keySequencePrefixField = new KeySequenceInputField();
        _keySequencePrefixField.setToolTipText(NbBundle.getMessage(BindingsPanel.class, "TOOLTIP_keySequencePrefixField"));
        fieldsPanel.add(_keySequencePrefixField);
        
        _keyCharsSequecePrefixField = new KeySequenceInputField(true);
        _keyCharsSequecePrefixField.setToolTipText(NbBundle.getMessage(BindingsPanel.class, "TOOLTIP_keyCharsSequecePrefixField"));
        fieldsPanel.add(_keyCharsSequecePrefixField);
        
        
        _actionPrefixField = new JTextField();
        _actionPrefixField.setToolTipText(NbBundle.getMessage(BindingsPanel.class, "TOOLTIP_actionPrefixField"));
        fieldsPanel.add(_actionPrefixField);
        
        buttonsPanel = new JPanel(new FlowLayout());
        JPanel buttonsGridPanel = new JPanel(new GridLayout(1, 0));
        
        _outputToHtmlButton = new JButton(new ImageIcon(getClass().getResource("html.gif"))); // NOI18N
        _outputToHtmlButton.setMargin(new Insets(1, 1, 1, 1));
        buttonsGridPanel.add(_outputToHtmlButton);
        
        _outputToHtmlButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                outputHtml();
            }
        });
        
        _outputToXmlButton = new JButton(new ImageIcon(getClass().getResource("xml.gif"))); // NOI18N
        _outputToXmlButton.setMargin(new Insets(1, 1, 1, 1));
        buttonsGridPanel.add(_outputToXmlButton);
        
        _outputToXmlButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                outputXml();
            }
        });
        
        buttonsPanel.add(buttonsGridPanel);
        fieldsPanel.add(buttonsPanel);
        
        add(fieldsPanel, BorderLayout.NORTH);
        
        tableSorter = new TableSorter(new BindingsTableModel(bindings)) {

            protected Comparator getComparator(int column) {
                if (column == 2) {
                    return String.CASE_INSENSITIVE_ORDER;
                }
                return super.getComparator(column);
            }            
        };
        _bindingsTable = new JTable(tableSorter) {
            public java.awt.Point getToolTipLocation(java.awt.event.MouseEvent event) {
                Point point = event.getPoint();
                int row = rowAtPoint(point);
                int col = columnAtPoint(point);
                Rectangle rect = getCellRect(row, col, true);
                return new Point(rect.x, rect.y);
            }
            
            public String getToolTipText(MouseEvent event) {
                Point point = event.getPoint();
                int row = rowAtPoint(point);
                int col = columnAtPoint(point);
                Object value = getValueAt(row, col);
                return (value == null) ? "" : value.toString();
            }
        };
        _bindingsTable.setDefaultRenderer(String.class, new BindingsTableCellRenderer());
        tableSorter.setTableHeader(_bindingsTable.getTableHeader());
        _bindingsTable.getColumnModel().addColumnModelListener(
                new TableColumnModelListener() {
            public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {
            }
            
            public void columnMoved(javax.swing.event.TableColumnModelEvent e) {
            }
            
            public void columnAdded(javax.swing.event.TableColumnModelEvent e) {
            }
            
            public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {
            }
            
            public void columnMarginChanged(javax.swing.event.ChangeEvent e) {
                _adjustTextFieldWidth();
            }
        }
        );
        tableSorter.setSortingStatus(sortingColumn, TableSorter.ASCENDING);
        add(new JScrollPane(_bindingsTable), BorderLayout.CENTER);
        ToolTipManager.sharedInstance().registerComponent(_bindingsTable);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        
        _keySequencePrefixField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                _keyCharsSequecePrefixField.clear();
                _actionPrefixField.setText("");
                
                tableSorter.setSortingStatus(BindingsTableModel.KEY_STROKES_CHARS_ONLY, TableSorter.NOT_SORTED);
                tableSorter.setSortingStatus(BindingsTableModel.ACTION, TableSorter.NOT_SORTED);
                tableSorter.setSortingStatus(BindingsTableModel.KEY_STROKES, TableSorter.ASCENDING);
            }
        });
        
        _keySequencePrefixField.getDocument().addDocumentListener(
                new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                textModified();
            }
            
            public void removeUpdate(DocumentEvent e) {
                textModified();
            }
            
            public void changedUpdate(DocumentEvent e) {
                textModified();
            }
            
            private void textModified() {
                scrollBindingsTableToPrefixForColumn(_keySequencePrefixField, BindingsTableModel.KEY_STROKES);
            }
        }
        );
        
        _keyCharsSequecePrefixField.getDocument().addDocumentListener(
                new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                textModified();
            }
            
            public void removeUpdate(DocumentEvent e) {
                textModified();
            }
            
            public void changedUpdate(DocumentEvent e) {
                textModified();
            }
            
            private void textModified() {
                scrollBindingsTableToPrefixForColumn(_keyCharsSequecePrefixField, BindingsTableModel.KEY_STROKES_CHARS_ONLY);
            }
        }
        );
        
        _actionPrefixField.getDocument().addDocumentListener(
                new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                textModified();
            }
            
            public void removeUpdate(DocumentEvent e) {
                textModified();
            }
            
            public void changedUpdate(DocumentEvent e) {
                textModified();
            }
            
            private void textModified() {
                scrollBindingsTableToPrefixForColumn(_actionPrefixField, BindingsTableModel.ACTION);
            }
        }
        );
        
        _keyCharsSequecePrefixField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                _keySequencePrefixField.clear();
                _actionPrefixField.setText("");
                tableSorter.setSortingStatus(BindingsTableModel.KEY_STROKES, TableSorter.NOT_SORTED);
                tableSorter.setSortingStatus(BindingsTableModel.ACTION, TableSorter.NOT_SORTED);
                tableSorter.setSortingStatus(BindingsTableModel.KEY_STROKES_CHARS_ONLY, TableSorter.ASCENDING);
            }
        });
        
        _actionPrefixField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                _keyCharsSequecePrefixField.clear();
                _keySequencePrefixField.clear();
                tableSorter.setSortingStatus(BindingsTableModel.KEY_STROKES, TableSorter.NOT_SORTED);
                tableSorter.setSortingStatus(BindingsTableModel.KEY_STROKES_CHARS_ONLY, TableSorter.NOT_SORTED);
                tableSorter.setSortingStatus(BindingsTableModel.ACTION, TableSorter.ASCENDING);
            }
        });
    }
    
    public void addNotify() {
        super.addNotify();
        
        switch(_initialSortingColumn) {
            case BindingsTableModel.KEY_STROKES:
                _keySequencePrefixField.requestFocus();
                break;
            case BindingsTableModel.KEY_STROKES_CHARS_ONLY:
                _keyCharsSequecePrefixField.requestFocus();
                break;
            case BindingsTableModel.ACTION:
                _actionPrefixField.requestFocus();
                break;
        }
        _adjustTextFieldWidth();
    }
    
    private void _adjustTextFieldWidth() {
        TableColumnModel tableColumnModel = _bindingsTable.getColumnModel();
        int count = tableColumnModel.getColumnCount();
        int offset = 0;
        Rectangle bounds;
        for (int i = 0; i < count; i++) {
            TableColumn tableColumn = tableColumnModel.getColumn(i);
            switch (i) {
                case BindingsTableModel.KEY_STROKES:
                    bounds = _keySequencePrefixField.getBounds();
                    bounds.x = offset;
                    bounds.width = tableColumn.getWidth();
                    _keySequencePrefixField.setBounds(bounds);
                    break;
                case BindingsTableModel.KEY_STROKES_CHARS_ONLY:
                    bounds = _keyCharsSequecePrefixField.getBounds();
                    bounds.x = offset;
                    bounds.width = tableColumn.getWidth();
                    _keyCharsSequecePrefixField.setBounds(bounds);
                    break;
                case BindingsTableModel.ACTION:
                    bounds = _actionPrefixField.getBounds();
                    bounds.x = offset;
                    bounds.width = tableColumn.getWidth();
                    _actionPrefixField.setBounds(bounds);
                    break;
                case BindingsTableModel.SCOPE:
                    bounds = buttonsPanel.getBounds();
                    bounds.x = offset;
                    bounds.width = tableColumn.getWidth();
                    buttonsPanel.setBounds(bounds);
                    break;
            }
            offset += tableColumn.getWidth();
        }
    }
    
    private void scrollBindingsTableToPrefixForColumn(final JTextField textField, final int columnNumber) {
        SwingUtilities.invokeLater(
                new Runnable() {
            public void run() {
                textField.setForeground(null);
                String prefix = textField.getText();
                if (prefix.endsWith(" ")) {
                    prefix = prefix.substring(0, prefix.length() -1);
                }                
                int flags = 0;
                if (prefix.length() > 0) {
                    if (columnNumber == BindingsTableModel.ACTION) {
                        if (!prefix.endsWith("$")) {
                            prefix = prefix + ".*";
                        }
                        if (prefix.toLowerCase().equals(prefix)) {
                            flags = Pattern.CASE_INSENSITIVE;
                        }
                    } else {
                        prefix = Pattern.quote(prefix);
                    }
                }
                try {
                    Pattern pattern = Pattern.compile(prefix, flags);
                    int rowCount = _bindingsTable.getRowCount();
                    for (int i = 0; i < rowCount; i++) {
                        String cellValue = _bindingsTable.getValueAt(i, columnNumber).toString();
                        if (pattern.matcher(cellValue).matches()) {
                            _bindingsTable.getSelectionModel().setSelectionInterval(i, i);
                            _bindingsTable.scrollRectToVisible(_bindingsTable.getCellRect(Math.min(i + 10, rowCount - 1) ,  0, true));
                            break;
                        }
                    }
                } catch (PatternSyntaxException pse) {
                    textField.setForeground(Color.red);
                }
            }
        });
    }
    
    private class BindingsTableCellRenderer extends DefaultTableCellRenderer {
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label;
            label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return label;
        }
    }
    
    protected void outputHtml() {
        JFileChooser fd = new JFileChooser();
        fd.setSelectedFile(new File("keybindings.html"));
        
        switch (fd.showSaveDialog(this)) {
            case JFileChooser.APPROVE_OPTION:
                String filePath = fd.getSelectedFile().getAbsolutePath();
                Writer out = null;
                try {
                    FileOutputStream fos = new FileOutputStream(filePath);
                    out = new OutputStreamWriter(fos, "UTF8");
                    out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
                    out.write("<html>\n");
                    out.write("\t<head>\n\t\t<title>Eclipse Key Bindings</title>\n");
                    out.write("<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
                    out.write("\t\t<style>\n");
                    out.write("\t\t\tth {background-color:#0080C0;  color:white;}\n");
                    out.write("\t\t\ttr {white-space:nowrap; }\n");
                    out.write("\t\t</style>\n");
                    out.write("\t</head>\n");
                    out.write("\t<body>\n\t\t<table cellspacing=\"0\" cellpadding=\"2\" border=\"1\">\n");
                    out.write("\t\t\t<tr>");
                    
                    int numCols = _bindingsTable.getColumnCount();
                    for (int c = 0; c < numCols; c++) {
                        out.write("<th>");
                        out.write(nbsp(_bindingsTable.getColumnName(c)));
                        out.write("</th>");
                    }
                    out.write("</tr>\n");
                    int numRows = _bindingsTable.getRowCount();
                    for (int r = 0; r < numRows; r++) {
                        out.write("\t\t\t<tr>");
                        for (int c = 0; c < numCols; c++) {
                            out.write("<td>");
                            out.write(nbsp(_bindingsTable.getValueAt(r, c).toString()));
                            out.write("</td>");
                        }
                        
                        out.write("</tr>\n");
                    }
                    
                    out.write("\t\t</table>\n\t</body>\n</html>\n");
                    out.flush();
                } catch (IOException ioe) {
                    ErrorManager.getDefault().annotate(ioe, "Error writing bindings file");
                } finally {
                    try {
                        out.close();
                    } catch (Throwable _ex) {
                    }
                }
        }
    }
    
    private String nbsp(String text) {
        if (text == null || text.trim().equals("")) {
            return "&nbsp;";
        }
        return text;
    }
    
    private void outputXml() {
        JFileChooser fd = new JFileChooser();
        fd.setSelectedFile(new File("keybindings.xml"));
        switch (fd.showSaveDialog(this)) {
            case JFileChooser.APPROVE_OPTION:
                String filePath = fd.getSelectedFile().getAbsolutePath();
                BufferedWriter out = null;
                try {
                    FileOutputStream fos = new FileOutputStream(filePath);
                    out = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
                    out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    out.write("<keybindings>\n");
                    
                    int numRows = _bindingsTable.getRowCount();
                    for(int r = 0; r < numRows; r++) {
                        out.write("  <keybinding>\n");
                        outputTag(out, "sequence", _bindingsTable.getValueAt(r, BindingsTableModel.KEY_STROKES).toString(), false);
                        outputTag(out, "natural-key", _bindingsTable.getValueAt(r, BindingsTableModel.KEY_STROKES_CHARS_ONLY).toString(), false);
                        outputTag(out, "action", _bindingsTable.getValueAt(r, BindingsTableModel.ACTION).toString(), true);
                        outputTag(out, "scope", _bindingsTable.getValueAt(r, BindingsTableModel.SCOPE).toString(), true);
                        out.write("  </keybinding>\n");
                    }
                    
                    out.write("</keybindings>\n");
                    out.flush();
                } catch(IOException ioe) {
                    ErrorManager.getDefault().annotate(ioe, "Error writing bindings file");
                } finally {
                    try {
                        out.close();
                    } catch(Throwable _ex) { }
                }
                return;
        }
    }
    
    private void outputTag(BufferedWriter out, String tagName, String value, boolean cdataEscape) throws IOException {
        out.write("    <" + tagName + ">");
        if(cdataEscape)
            out.write("<![CDATA[" + value + "]]>");
        else
            out.write(value);
        out.write("</" + tagName + ">\n");
    }
}
