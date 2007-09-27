/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package org.netbeans.modules.genericnavigator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Regexp adder/editor
 *
 * @author  Tim Boudreau
 */
public class AddExpressionPanel extends javax.swing.JPanel implements DocumentListener, ActionListener, KeyListener, FocusListener {
    public AddExpressionPanel() {
        initComponents();
        expression.getDocument().addDocumentListener(this);
        sampleText.getDocument().addDocumentListener(this);
        getNameEditorDoc().addDocumentListener(this);
        getMimeEditorDoc().addDocumentListener(this);
        mimeBox.addActionListener(this);
        name.addActionListener(this);
        sampleText.addKeyListener(this);
        sampleText.addFocusListener(this);
        expression.addFocusListener(this);

        Set s = new TreeSet (PatternItem.getSupportedMimeTypes());
        s.add ("text/plain");
        s.add ("text/x-java");
        s.add ("text/html");
        s.add ("text/x-properties");
        mimeBox.setModel (new DefaultComboBoxModel (new Vector(s)));
        EdRen edren = new EdRen();
        matches.getTableHeader().setDefaultRenderer(edren);
        matches.getTableHeader().addMouseListener(edren);

    }

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        mimeLabel = new javax.swing.JLabel();
        mimeBox = new javax.swing.JComboBox();
        expressionLabel = new javax.swing.JLabel();
        expression = new javax.swing.JTextField();
        problem = new javax.swing.JLabel();
        sampleLabel = new javax.swing.JLabel();
        samplesPane = new javax.swing.JScrollPane();
        sampleText = new javax.swing.JEditorPane();
        nameLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        matches = new javax.swing.JTable();
        canonicalEquivalenceBox = new javax.swing.JCheckBox();
        caseInsensitiveBox = new javax.swing.JCheckBox();
        allowCommentsBox = new javax.swing.JCheckBox();
        literalBox = new javax.swing.JCheckBox();
        multilineBox = new javax.swing.JCheckBox();
        unicodeCaseBox = new javax.swing.JCheckBox();
        unixLinesBox = new javax.swing.JCheckBox();
        dotallBox = new javax.swing.JCheckBox();
        name = new javax.swing.JComboBox();
        htmlBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        mimeLabel.setLabelFor(mimeBox);
        mimeLabel.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.mimeLabel.text")); // NOI18N

        mimeBox.setEditable(true);
        mimeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "text/plain", "text/x-java", "text/html", "text/x-properties" }));

        expressionLabel.setLabelFor(expression);
        expressionLabel.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.expressionLabel.text")); // NOI18N

        expression.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.expression.text")); // NOI18N

        problem.setForeground(java.awt.Color.red);
        problem.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.problem.text")); // NOI18N

        sampleLabel.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.sampleLabel.text")); // NOI18N

        samplesPane.setViewportView(sampleText);

        nameLabel.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.nameLabel.text")); // NOI18N

        matches.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(matches);

        canonicalEquivalenceBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.canonicalEquivalenceBox.text")); // NOI18N
        canonicalEquivalenceBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        canonicalEquivalenceBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        canonicalEquivalenceBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.canonicalEquivalenceBox.name")); // NOI18N

        caseInsensitiveBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.caseInsensitiveBox.text")); // NOI18N
        caseInsensitiveBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        caseInsensitiveBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        caseInsensitiveBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.caseInsensitiveBox.name")); // NOI18N

        allowCommentsBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.allowCommentsBox.text")); // NOI18N
        allowCommentsBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        allowCommentsBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        allowCommentsBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.allowCommentsBox.name")); // NOI18N

        literalBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.literalBox.text")); // NOI18N
        literalBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        literalBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        literalBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.literalBox.name")); // NOI18N

        multilineBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.multilineBox.text")); // NOI18N
        multilineBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        multilineBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        multilineBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.multilineBox.name")); // NOI18N

        unicodeCaseBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.unicodeCaseBox.text")); // NOI18N
        unicodeCaseBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        unicodeCaseBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        unicodeCaseBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.unicodeCaseBox.name")); // NOI18N

        unixLinesBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.unixLinesBox.text")); // NOI18N
        unixLinesBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        unixLinesBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        unixLinesBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.unixLinesBox.name")); // NOI18N

        dotallBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.dotallBox.text")); // NOI18N
        dotallBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dotallBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        dotallBox.setName(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddDialog.dotallBox.name")); // NOI18N

        name.setEditable(true);
        name.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        htmlBox.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddExpressionPanel.htmlBox.text")); // NOI18N
        htmlBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        htmlBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AddExpressionPanel.class, "AddExpressionPanel.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(expressionLabel)
                                    .add(sampleLabel)
                                    .add(nameLabel)
                                    .add(mimeLabel))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(samplesPane, 0, 0, Short.MAX_VALUE)
                                    .add(mimeBox, 0, 564, Short.MAX_VALUE)
                                    .add(name, 0, 564, Short.MAX_VALUE)
                                    .add(expression, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(63, 63, 63)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(multilineBox)
                                    .add(unixLinesBox))
                                .add(63, 63, 63)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(caseInsensitiveBox)
                                    .add(canonicalEquivalenceBox))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 104, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(unicodeCaseBox)
                                    .add(allowCommentsBox))
                                .add(65, 65, 65)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(literalBox)
                                    .add(dotallBox)))
                            .add(problem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(htmlBox)
                        .add(20, 20, 20))
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addContainerGap(333, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameLabel)
                    .add(name, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mimeLabel)
                    .add(mimeBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(expressionLabel)
                    .add(expression, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sampleLabel)
                    .add(samplesPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(caseInsensitiveBox)
                            .add(unixLinesBox)
                            .add(unicodeCaseBox))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(multilineBox)
                            .add(canonicalEquivalenceBox)
                            .add(allowCommentsBox)))
                    .add(layout.createSequentialGroup()
                        .add(dotallBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(literalBox)))
                .add(25, 25, 25)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(htmlBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(problem)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {expression, mimeBox, name}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

    private boolean fromExpression (DocumentEvent e) {
        return expression.getDocument() == e.getDocument();
    }

    private ChangeListener l;
    void addChangeListener (ChangeListener cl) {
        this.l = cl;
    }

    private Document getNameEditorDoc() {
        return ((JTextComponent) name.getEditor().getEditorComponent()).getDocument();
    }

    private Document getMimeEditorDoc() {
        return ((JTextComponent) mimeBox.getEditor().getEditorComponent()).getDocument();
    }

    public void insertUpdate(DocumentEvent e) {
        boolean change;
        if (fromExpression(e)) {
            change = setExpression (expression.getText());
        } else if (e.getDocument() == getNameEditorDoc()) {
            change = true;
        } else if (e.getDocument() == getMimeEditorDoc()) {
            change = true;
        } else {
            change = setSample (sampleText.getText());
        }
        if (change) {
            check();
        }
    }

    public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

    private boolean setSample (String txt) {
        boolean result = !this.smpl.equals(txt);
        if (result) {
            this.smpl= txt;
        }
        return result;
    }

    private String exp = ""; //NOI18N
    private boolean setExpression (String exp) {
        boolean result = !this.exp.equals(exp);
        if (result) {
            this.exp= exp;
        }
        return result;
    }

    public void addNotify() {
        super.addNotify();
        check();
    }

    private Font boldFont;
    private Font getBoldFont() {
        if (boldFont == null) {
            boldFont = dotallBox.getFont().deriveFont(Font.BOLD);
        }
        return boldFont;
    }

    private String validatePattern() {
        if ((exp.trim().length() == 0)) {
            return GenericNavPanel.getString("MSG_EnterExpression"); //NOI18N
        }
        try {
            pattern = Pattern.compile(exp);
        } catch (Exception e) {
            return NbBundle.getMessage (AddExpressionPanel.class, "MSG_ExpressionInvalid"); //NOI18N
        }
        return " "; //NOI18N
    }

    private String validateName() {
        if ((((JTextComponent) name.getEditor().getEditorComponent()).getText().trim().length() == 0)) {
            return (GenericNavPanel.getString("MSG_NoName")); //NOI18N
        }
        return " "; //NOI18N
    }

    public String validateSample() {
        return smpl.trim().length() == 0 ?
            GenericNavPanel.getString("MSG_EnterSampleText") : //NOI18N
            " "; //NOI18N
    }

    public void setEnabled (boolean val) {
        super.setEnabled (val);
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            c[i].setEnabled(val);
            if (c[i] instanceof JScrollPane) {
                ((JScrollPane) c[i]).getViewport().getView().setEnabled(val);
            }
        }
    }

    public String validateMime () {
        String s = ((JTextComponent) mimeBox.getEditor().getEditorComponent()).getText().trim();
        char[] c = s.toCharArray();
        if (c.length == 0) {
            return GenericNavPanel.getString("MSG_EnterMimeType"); //NOI18N
        }
        boolean foundSlash = false;
            for (int i = 0; i < c.length; i++) {
                foundSlash |= c[i] == '/';
                if (Character.isWhitespace(c[i])) {
                    return GenericNavPanel.getString("MSG_WhitespaceInMimeType");
                }
            }
        if (!isProblem() && !foundSlash) {
            return GenericNavPanel.getString("MSG_NoSlashInMimeType");
        } else if (!isProblem() && c[c.length-1] == '/') {
            return GenericNavPanel.getString("MSG_MimeTypeStartsWithSlash");
        } else if (!isProblem() && c[0] == '/') {
            return GenericNavPanel.getString("MSG_MimeTypeCantStartWithSlash");
        }
        return " ";
    }


    private Pattern pattern = null;
    void check() {
        String p = " ";
        p = validateName();
        if (p.trim().length() == 0) {
            p = validateMime();
        }
        if (p.trim().length() == 0) {
            p = validateSample();
        }
        if (p.trim().length() == 0) {
            p = validatePattern();
        }
//        if (p.trim().length() == 0) {
//            p = validateGroups();
//        }
        setProblem (p);
        if (!isProblem()) {
            updateMatches();
        }
    }

    private String validateGroups() {
        boolean any = false;
        for (int i = 0; i < vals.length; i++) {
            any |=vals[i];
            if (any) break;
        }
        return any ? "" : GenericNavPanel.getString("MSG_NoGroupsSelected");
    }

    private boolean[] vals = new boolean[5];
    private void updateMatches() {
        final Pattern pattern = this.pattern;
        if (pattern == null) {
            DefaultTableModel mdl = new DefaultTableModel(1, 1);
            matches.setModel (mdl);
        } else {
            Matcher m = pattern.matcher(sampleText.getText());
            int gc = m.groupCount();
            DefaultTableModel tmdl = new DefaultTableModel (0, 0);
            for (int i = 0; i < gc+1; i++) {
                tmdl.addColumn(GenericNavPanel.getString("LBL_GROUP", //NOI18N
                        Integer.toString(i)));
            }
            while (m.find()) {
                String[] items = new String[gc + 1];
                for (int i = 0; i < items.length; i++) {
                    items[i] = m.group(i);
                }
                tmdl.addRow(items);
            }
            boolean[] old = vals;
            vals = new boolean[Math.max (vals.length, tmdl.getColumnCount())];
            boolean any = false;
            for (int i = 0; i < Math.min(old.length, vals.length); i++) {
                vals[i] = old[i];
            }
            matches.setModel (tmdl);
        }
    }

    private void setProblem(String val) {
        boolean before = isProblem();
        problem.setText (val);
        if (before && !isProblem()) {
            updateMatches();
        }
        boolean after = isProblem();
        if (before != after) {
            change();
        }
    }

    public boolean isProblem() {
        return problem.getText().trim().length() > 0;
    }

    private void change() {
        if (l != null) {
            l.stateChanged(new ChangeEvent(this));
        }
    }

    public int getFlags() {
        StringBuffer sb = new StringBuffer();
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JCheckBox) {
                JCheckBox box = (JCheckBox) c[i];
                if (box.isSelected()) {
                    sb.append (box.getName());
                    sb.append (',');
                }
            }
        }
        int end = sb.length() -1;
        if (end > 0 && sb.charAt(end) == ',') {
            sb.deleteCharAt(end);
        }
        return PatternItem.parseFlags(sb.toString());
    }

    public PatternItem getPatternItem() {
        if (isProblem()) {
            return null;
        } else {
            return new PatternItem (name.getEditor().getItem().toString(),
                    exp, getFlags(), getIncludeGroups(), isStripHtml());
        }
    }

    public boolean isStripHtml() {
        return htmlBox.isSelected();
    }

    private int[] getIncludeGroups() {
        int max = matches.getColumnCount();
        List <Integer> result = new ArrayList <Integer> (max);
        for (int i = 0; i < max; i++) {
            if (i < vals.length && vals[i]) {
                result.add (new Integer(i));
            }
        }
        if (result.isEmpty()) {
            //Always at least handle the default group, otherwise we never
            //match anything
            result.add (new Integer(0));
        }
        Integer[] arr = (Integer[]) result.toArray(new Integer[result.size()]);
        return (int[]) Utilities.toPrimitiveArray(arr);
    }

    private String smpl = ""; //NOI18N

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mimeBox) {
            if (otherString.equals(mimeBox.getSelectedItem())) {
                NotifyDescriptor.InputLine line =
                        new NotifyDescriptor.InputLine (
                        NbBundle.getMessage(AddExpressionPanel.class, "MSG_MimeType"), //NOI18N
                        NbBundle.getMessage(AddExpressionPanel.class, "TTL_MimeType")); //NOI18N

                if (line.OK_OPTION.equals(DialogDisplayer.getDefault().notify(line))) {
                    String type = line.getInputText().trim();
                    boolean bad = "".equals(type) ||
                                  type.indexOf('/') < 0 ||
                                  type.indexOf('\\') > 0;

                    if (bad) {
                        NotifyDescriptor.Message msg = new NotifyDescriptor.Message(
                                NbBundle.getMessage(AddExpressionPanel.class, "MSG_BadMimeType", type), //NOI18N
                                NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notify(msg);
                        return;
                    } else {
                        ((DefaultComboBoxModel) mimeBox.getModel()).addElement(type);
                        mimeBox.setSelectedItem(type);
                    }
                }
            }
            updateSampleText();
            String fld = mimeBox.getSelectedItem().toString().trim();
            if (fld.length() > 0) {
                updateNames();
            }
        } else { //name box
            String fld = mimeBox.getSelectedItem().toString().trim();
            String nm = name.getSelectedItem().toString().trim();
            if (nm.length() > 0) {
                PatternItem[] items = PatternItem.getDefaultItems(fld);
                for (int i = 0; i < items.length; i++) {
                    if (items[i].getDisplayName().equals(nm)) {
                        setPatternItem (items[i]);
                    }
                }
            }
        }
    }

    private void updateSampleText() {
        if (!userModifiedSample) {
            String txt = getTextForMimeType (getMimeType());
            if (txt != null) {
                sampleText.setText(txt);
                sampleText.setSelectionStart(0);
                sampleText.setSelectionEnd(0);
                samplesPane.scrollRectToVisible(new Rectangle (0,0,1,1));
            }
        }
        change();
    }

    private Map mime2txt = new HashMap();
    private String getTextForMimeType (String mimetype) {
        String result = (String) mime2txt.get (mimetype);
        if (result == null) {
            try {
                result = loadTextForMimeType(mimetype);
                mime2txt.put (mimetype, result);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL, ioe);
                return "";
            }
        }
        return result;
    }

    private String loadTextForMimeType(String mimetype) throws IOException {
        URL url;
        try {
            if ("text/x-java".equals(mimetype)) { //NOI18N
                url = new URL ("nbresloc:/org/netbeans/modules/" + //NOI18N
                        "genericnavigator/examplefiles/javasample.txt"); //NOI18N
            } else if ("text/x-properties".equals(mimetype)) { //NOI18N
                url = new URL ("nbresloc:/org/netbeans/modules/" + //NOI18N
                        "genericnavigator/examplefiles/propssample.txt"); //NOI18N
            } else if ("text/html".equals(mimetype)) { //NOI18N
                url = new URL ("nbresloc:/org/netbeans/modules/" + //NOI18N
                        "genericnavigator/examplefiles/sample.html"); //NOI18N
            } else {
                url = new URL ("nbresloc:/org/netbeans/modules/" + //NOI18N
                        "genericnavigator/examplefiles/sample.txt"); //NOI18N
            }
            InputStream is = new BufferedInputStream (url.openStream());
            StringBuffer result = new StringBuffer();
            int read = 0;
            byte[] b = new byte[2048];
            while ((read = is.read(b)) != -1) {
                result.append (new String (b, 0, read, "UTF-8"));
            }
            return result.toString();
        } catch (MalformedURLException mre) {
            ErrorManager.getDefault().notify (mre);
        }
        return null;
    }

    public void setPatternItem (PatternItem item) {
        expression.setText(item.getPatternString());
        name.setSelectedItem (item.getDisplayName());
        setFlags (item.getFlags());
        updateSampleText();
        updateMatches();
    }

    private void setFlags (int flags) {
        Component[] c = getComponents();
        String s = PatternItem.getFlagsString(flags);
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JCheckBox) {
                JCheckBox jcb = (JCheckBox) c[i];
                String nm = jcb.getName();
                if (nm != null) {
                    jcb.setSelected(s.indexOf(jcb.getName()) != -1);
                }
            }
        }
    }

    private void updateNames() {
        String fld = mimeBox.getSelectedItem().toString().trim();
        PatternItem[] items = PatternItem.getDefaultItems(fld);
        Object o = name.getSelectedItem();
        DefaultComboBoxModel mdl = new DefaultComboBoxModel (items);
        name.setModel (mdl);
        name.setSelectedItem(o);
    }

    public String getMimeType() {
        return (String) mimeBox.getSelectedItem();
    }

    String otherString = NbBundle.getMessage (AddExpressionPanel.class, "LBL_Other"); //NOI18N

    public void setMimeType (String type) {
        mimeBox.setSelectedItem(type);
    }

    public String getDisplayName() {
        return name.getSelectedItem().toString();
    }

    private boolean userModifiedSample = false;
    public void keyTyped(KeyEvent e) {
        userModifiedSample = true;
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void focusGained(FocusEvent e) {
        if (e.getSource() instanceof JTextComponent) {
            ((JTextComponent) e.getSource()).selectAll();
        }
    }

    public void focusLost(FocusEvent e) {
        //do nothing
    }

    private final class EdRen extends MouseAdapter implements TableCellRenderer {
        private final JCheckBox renBox = new JCheckBox();
        private final Border lower = new MatteBorder (0, 0, 1, 0, Color.BLACK);
        private final Border lowerWithRight = new MatteBorder (0, 0, 1, 1, Color.BLACK);

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            System.err.println("Render row " + row + " col " + column);
            renBox.setBackground (table.getBackground());
            boolean valAtColumn = vals[column];
            if (column == table.getColumnCount() - 1) {
                renBox.setBorder (lower);
            } else {
                renBox.setBorder (lowerWithRight);
            }
            renBox.setText ("Group " + column);
            renBox.setSelected(valAtColumn);
            return renBox;
        }

        public void mouseClicked(MouseEvent e) {
            JTableHeader jth = (JTableHeader) e.getSource();
            int col = jth.columnAtPoint(e.getPoint());
            if (col >= 0) {
                vals[col] = !vals[col];
                jth.repaint();
            }
            check();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowCommentsBox;
    private javax.swing.JCheckBox canonicalEquivalenceBox;
    private javax.swing.JCheckBox caseInsensitiveBox;
    private javax.swing.JCheckBox dotallBox;
    private javax.swing.JTextField expression;
    private javax.swing.JLabel expressionLabel;
    private javax.swing.JCheckBox htmlBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox literalBox;
    private javax.swing.JTable matches;
    private javax.swing.JComboBox mimeBox;
    private javax.swing.JLabel mimeLabel;
    private javax.swing.JCheckBox multilineBox;
    private javax.swing.JComboBox name;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel problem;
    private javax.swing.JLabel sampleLabel;
    private javax.swing.JEditorPane sampleText;
    private javax.swing.JScrollPane samplesPane;
    private javax.swing.JCheckBox unicodeCaseBox;
    private javax.swing.JCheckBox unixLinesBox;
    // End of variables declaration//GEN-END:variables

}
