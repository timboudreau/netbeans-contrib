/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.regexplugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.tools.FileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.netbeans.modules.regexplugin.LangRefXMLTreeNode;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Top component which displays something.
 */
final class regexTopComponent extends TopComponent{

    private static regexTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "plugin_go.png";

    private static final String PREFERRED_ID = "regexTopComponent";
    private Document LangRefTree_xmlModel;    
    private String path_to_LangRef_XML = "LangRef.xml";
    private String path_to_LangRef_LeafIcon = "images/add.png";
    private String path_to_MatchResultsIcon1 = "images/match_results.png";
    private String path_to_MatchResultsIcon2 = "images/match.png";
    private String path_to_MatchResultsIcon3 = "images/group.png";
    private String path_to_RegEx2StringIcon = "images/arrow_right.png";
    private String path_to_String2RegExIcon = "images/arrow_left.png";
    private boolean doHighlight = true;
    
    public regexTopComponent() {
        initComponents();
        setName("Regular Expressions Plugin");
        setToolTipText(NbBundle.getMessage(regexTopComponent.class, "HINT_regexTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
    
        //CREATING THE LANGUAGE REFERENCE TREE..
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            
            InputStream is = this.getClass().getResourceAsStream(path_to_LangRef_XML);
            LangRefTree_xmlModel = docBuilder.parse(is);
            
            Node root = LangRefTree_xmlModel.getElementsByTagName("LangRefRoot").item(0);
                        //set some properties
            this.jt_LangRef.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            //this.jt_LangRef.setRootVisible(false);
            
            ImageIcon leafIcon = new ImageIcon(this.getClass().getResource(path_to_LangRef_LeafIcon));
            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            renderer.setLeafIcon(leafIcon);
            jt_LangRef.setCellRenderer(renderer);
                    
            //set model
            this.jt_LangRef.setModel(new DefaultTreeModel (new LangRefXMLTreeNode(root)));
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading or parsing Language Reference XML file: " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        } finally {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        
        //Set selection handler for language reference tree..
        
        jt_LangRef.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        //TREE CLICK AND DOUBLE CLICK HANDLING
        
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow =  jt_LangRef.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = jt_LangRef.getPathForLocation(e.getX(), e.getY());
                
                if(selRow != -1) {
                    if(e.getClickCount() == 1) {
                    LangRefTree_SingleClick(selRow, selPath);
                    }
                    else if(e.getClickCount() == 2) {
                    LangRefTree_DoubleClick(selRow, selPath);
                    }
                }
            }
        };

        jt_LangRef.addMouseListener(ml);
        
        //CREATING THE OPTIONS JCHECKBOXLIST
        
        //JCheckBoxList jcl_Options = new JCheckBoxList(new String[] { "IgnoreCase", "Multiline", "ExplicitCapture", "Singleline", "IgnorePatternWhitespace", "RightToLeft", "ECMAScript", "CultureInvariant"});
        //jcl_Options.setBackground(this.getBackground());
                
        //jScrollPane6.setViewportView(jcl_Options);
        
        //Setting up match results tree
        
        jtMatchedResults.setModel(null);
        jtMatchedResults.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        //POPUP MENU ON REGEX TEXTAREA
        // Add the mouse listener
        
        jta_RegExp.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                requestFocus();
                dealWithMousePress(evt);
            }
        });
    }
    
    private void dealWithMousePress(MouseEvent evt) {
                     // Only interested in the right button
        if(SwingUtilities.isRightMouseButton(evt))
        {
            //if(MenuSelectionManager.defaultManager().getSelectedPath().length>0)
            //return;
            
            ImageIcon icon1 = new ImageIcon(this.getClass().getResource(path_to_RegEx2StringIcon));
            ImageIcon icon2 = new ImageIcon(this.getClass().getResource(path_to_String2RegExIcon));
            
            JPopupMenu menu = new JPopupMenu();
            menu.add(new RegEx2StringAction(jta_RegExp, icon1));
            menu.add(new String2RegExAction(jta_RegExp, icon2));
            
            // Display the menu
            Point pt = SwingUtilities.convertPoint(evt.getComponent(), evt.getPoint(), this);
            menu.show(this, pt.x, pt.y);
        }
            }

                private void LangRefTree_DoubleClick(int selRow, TreePath selPath) {
                LangRefXMLTreeNode node = (LangRefXMLTreeNode) selPath.getLastPathComponent();

                if (node == null)
                //Nothing is selected.	
                return;

                Node nodeInfo = node.getXMLNode();
                if (nodeInfo.getNodeName().compareTo("LangElement") == 0) {
                    String txt = nodeInfo.getAttributes().getNamedItem("langtext").getTextContent();
                    int sltStart = jta_RegExp.getCaretPosition();
                    int sltEnd = sltStart + txt.length();
                    jta_RegExp.insert(txt, jta_RegExp.getCaretPosition());

                    //tofix
                    jta_RegExp.setCaretPosition(sltStart);
                    jta_RegExp.moveCaretPosition(sltEnd);
                }
            }

            private void LangRefTree_SingleClick(int selRow, TreePath selPath) {
                LangRefXMLTreeNode node = (LangRefXMLTreeNode) selPath.getLastPathComponent();

                if (node == null)
                //Nothing is selected.	
                return;

                Node nodeInfo = node.getXMLNode();
                if (nodeInfo.getNodeName().compareTo("LangElement") == 0) {
                    String txt = nodeInfo.getAttributes().getNamedItem("description").getTextContent();
                    jta_Description.setText(txt);
                }
            }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();
        button3 = new java.awt.Button();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jt_LangRef = new javax.swing.JTree();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jta_Description = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jta_RegExp = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jta_Input_Text = new javax.swing.JTextArea();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel11 = new javax.swing.JPanel();
        jcb_CASE_INSENSITIVE = new javax.swing.JCheckBox();
        jcb_MULTILINE = new javax.swing.JCheckBox();
        jcb_DOTALL = new javax.swing.JCheckBox();
        jcb_UNICODE_CASE = new javax.swing.JCheckBox();
        jcb_CANON_EQ = new javax.swing.JCheckBox();
        jcb_UNIX_LINES = new javax.swing.JCheckBox();
        jcb_LITERAL = new javax.swing.JCheckBox();
        jcb_COMMENTS = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtMatchedResults = new javax.swing.JTree();

        button3.setLabel("button3");

        org.jdesktop.layout.GroupLayout panel1Layout = new org.jdesktop.layout.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 33, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, "New Expression");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, "Match Expression");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton2)
                .add(18, 18, 18)
                .add(jButton1)
                .addContainerGap(494, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jButton1, jButton2}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane1.setDividerLocation(400);

        jPanel3.setMinimumSize(new java.awt.Dimension(50, 100));

        jSplitPane3.setDividerLocation(350);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Language Reference"));

        jScrollPane5.setViewportView(jt_LangRef);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
        );

        jSplitPane3.setTopComponent(jPanel6);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));

        jta_Description.setBackground(new java.awt.Color(225, 225, 249));
        jta_Description.setColumns(20);
        jta_Description.setEditable(false);
        jta_Description.setLineWrap(true);
        jta_Description.setRows(5);
        jta_Description.setText("Click on a language element to see its description here");
        jta_Description.setWrapStyleWord(true);
        jScrollPane6.setViewportView(jta_Description);

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane3.setRightComponent(jPanel8);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jSplitPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel3);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Building & Testing"));

        jLabel1.setLabelFor(jta_RegExp);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Regular Expression");

        jta_RegExp.setColumns(20);
        jta_RegExp.setLineWrap(true);
        jta_RegExp.setRows(2);
        jta_RegExp.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane3.setViewportView(jta_RegExp);

        jLabel2.setLabelFor(jta_RegExp);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Input Text");

        jta_Input_Text.setColumns(20);
        jta_Input_Text.setRows(5);
        jta_Input_Text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jta_Input_TextKeyTyped(evt);
            }
        });
        jScrollPane4.setViewportView(jta_Input_Text);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Text Input", jPanel9);

        jScrollPane2.setMaximumSize(new java.awt.Dimension(200, 200));

        jPanel11.setMaximumSize(new java.awt.Dimension(200, 200));
        jPanel11.setPreferredSize(new java.awt.Dimension(200, 200));

        org.openide.awt.Mnemonics.setLocalizedText(jcb_CASE_INSENSITIVE, "Case Insensitive");

        jcb_MULTILINE.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jcb_MULTILINE, "Multiline");

        org.openide.awt.Mnemonics.setLocalizedText(jcb_DOTALL, "Dotall Mode (match line terminators)");

        org.openide.awt.Mnemonics.setLocalizedText(jcb_UNICODE_CASE, "Enable Unicode-aware case folding");

        org.openide.awt.Mnemonics.setLocalizedText(jcb_CANON_EQ, "Enable canonical equivalence");

        org.openide.awt.Mnemonics.setLocalizedText(jcb_UNIX_LINES, "Enable Unix lines mode.");

        org.openide.awt.Mnemonics.setLocalizedText(jcb_LITERAL, "Enable literal parsing of the pattern");

        org.openide.awt.Mnemonics.setLocalizedText(jcb_COMMENTS, "Permit whitespace and comments in pattern.");

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jcb_CASE_INSENSITIVE)
                    .add(jcb_MULTILINE)
                    .add(jcb_DOTALL)
                    .add(jcb_UNICODE_CASE)
                    .add(jcb_CANON_EQ)
                    .add(jcb_UNIX_LINES)
                    .add(jcb_LITERAL)
                    .add(jcb_COMMENTS))
                .addContainerGap(112, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jcb_CASE_INSENSITIVE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcb_MULTILINE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcb_DOTALL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcb_UNICODE_CASE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcb_CANON_EQ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcb_UNIX_LINES, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcb_LITERAL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcb_COMMENTS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel11);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Options", jPanel10);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
        );

        jSplitPane2.setTopComponent(jPanel5);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Matched Results"));

        jtMatchedResults.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jtMatchedResultsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jtMatchedResults);

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel7);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel4);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(11, 11, 11)
                .add(panel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public class MatchResultsTreeCellRenderer extends DefaultTreeCellRenderer
    {
        @Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            ImageIcon resultsNodeIcon = new ImageIcon(this.getClass().getResource(path_to_MatchResultsIcon1));
            ImageIcon matchNodeIcon = new ImageIcon(this.getClass().getResource(path_to_MatchResultsIcon2));
            ImageIcon groupNodeIcon = new ImageIcon(this.getClass().getResource(path_to_MatchResultsIcon3));
            
            if (tree.getModel().getRoot() == value)
            {
                setIcon (resultsNodeIcon);
            }
            else if (leaf)
            {
                setIcon (groupNodeIcon);
            }
            else
            {
                setIcon (matchNodeIcon);
            }
            
            return this;		
        }
    }
    
    private void showMatchResults(List regExpMatches) {
        DefaultMutableTreeNode matches = new DefaultMutableTreeNode ("Matches");
        
        for (int i = 0; i< regExpMatches.size(); i++)
        {
            DefaultMutableTreeNode match = new DefaultMutableTreeNode("Match '" + ((RegExpMatch)regExpMatches.get(i)).getText() + "'");
            
            List match_groups = ((RegExpMatch)regExpMatches.get(i)).getGroups();
            
            for (int j = 0; j<match_groups.size(); j++)
            {
                match.add(((RegExpMatchGroup)match_groups.get(j)));
            }
            matches.add(match);
        }
        
        
        MatchResultsTreeCellRenderer renderer = new MatchResultsTreeCellRenderer();
        
        jtMatchedResults.setCellRenderer(renderer);
        jtMatchedResults.setModel(new DefaultTreeModel (matches));
        doHighlight = true;
    }
    
    class RegExpMatchGroup extends DefaultMutableTreeNode
    {
        private int start;
        private int end;
        private String text;
        private int groupno;
        
        public RegExpMatchGroup(int start, int end, String text, int groupno)
        {
            this.start = start;
            this.end = end;
            this.text = text;
            this.groupno = groupno;
        }
        
        public int getStart ()
        {
            return start;
        }
        
        public int getEnd ()
        {
            return end;
        }
        
        public String getText()
        {
            return text;
        }
        
        @Override
        public String toString() {
            return "Group " + groupno + ": '" + text + "' at (" + start + "," + end + ")";
        }
    }
    
    class RegExpMatch
    {
        private int num_matches;
        private int start;
        private int end;
        private String text;
        private List groups;
        
        public RegExpMatch(int num_matches, int start, int end, String text)
        {
            this.num_matches = num_matches;
            groups = new ArrayList();
            this.start = start;
            this.end = end;
            this.text = text;
        }
        
        public void addGroup(RegExpMatchGroup gp)
        {
            groups.add(gp);
        }
        
        public List getGroups()
        {
            return groups;
        }
        
        public int getStart ()
        {
            return start;
        }
        
        public int getEnd ()
        {
            return end;
        }
        
        public String getText()
        {
            return text;
        }
    }
    Pattern compileRegExp(String regExp) throws PatternSyntaxException {
        //read options
        boolean CASE_INSENSITIVE = jcb_CASE_INSENSITIVE.isSelected();
        boolean MULTILINE = jcb_MULTILINE.isSelected();
        boolean DOTALL = jcb_DOTALL.isSelected();
        boolean UNICODE_CASE = jcb_UNICODE_CASE.isSelected();
        boolean CANON_EQ = jcb_CANON_EQ.isSelected();
        boolean UNIX_LINES = jcb_UNIX_LINES.isSelected();
        boolean LITERAL = jcb_LITERAL.isSelected();
        boolean COMMENTS = jcb_COMMENTS.isSelected();
        
        int flags = 0;
        
        if (CASE_INSENSITIVE)
            flags = flags | Pattern.CASE_INSENSITIVE;
        if (MULTILINE)
            flags = flags | Pattern.MULTILINE;
        if (DOTALL)
            flags = flags | Pattern.DOTALL;
        if (UNICODE_CASE)
            flags = flags | Pattern.UNICODE_CASE;
        if (CANON_EQ)
            flags = flags | Pattern.CANON_EQ;
        if (UNIX_LINES)
            flags = flags | Pattern.UNIX_LINES;
        if (LITERAL)
            flags = flags | Pattern.LITERAL;
        if (COMMENTS)
            flags = flags | Pattern.COMMENTS;
        
        return Pattern.compile(regExp, flags);
    }

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        String regExp = jta_RegExp.getText();
        String text = jta_Input_Text.getText();        
        
        if (regExp == null || regExp.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Please enter a regular expression to match");
            return;
        }
        
        if (text == null || text.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Please enter the input text to match the regular expression against");
            return;
        }
        
        try
        {
            Pattern compiledRegExp = compileRegExp(regExp);
            Matcher matcher = compiledRegExp.matcher(text);
            
            List regExpMatches = new ArrayList();
            
                while (matcher.find()) {
                    RegExpMatch regmatch = new RegExpMatch (matcher.groupCount(), matcher.start(), matcher.end(), matcher.group());
                    
                    for (int i = 1 ; i < matcher.groupCount() + 1; i++) {
                        int start = matcher.start(i);
                        int end = matcher.end(i);
                        
                        if (start >= end) {
                            continue;
                        }
                        regmatch.addGroup(new RegExpMatchGroup(start, end, matcher.group(i), i));
                    }
                    
                    regExpMatches.add(regmatch);
                    }
            showMatchResults(regExpMatches);
        }
        catch (PatternSyntaxException pse) {
            JOptionPane.showMessageDialog(this, "Invalid regular expression syntax: " + pse.getDescription());
            return;
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        //clear input text
        jta_Input_Text.setText("");
        //clear regexp
        jta_RegExp.setText("");
        //clear match results tree
        jtMatchedResults.setModel(null);
        removeHighlights(jta_Input_Text);
    }//GEN-LAST:event_jButton2MouseClicked

        // An instance of the private subclass of the default highlight painter
    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.GREEN);
    
    // A private subclass of the default highlight painter
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
        // Removes only our private highlights
    public void removeHighlights(JTextComponent textComp) {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();
    
        for (int i=0; i<hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
    
        // Creates highlights around all occurrences of pattern in textComp
    public void highlight(JTextComponent textComp, int start, int end) {
        // First remove all old highlights
        removeHighlights(textComp);
    
        try {
            Highlighter hilite = textComp.getHighlighter();
            javax.swing.text.Document doc = (javax.swing.text.Document) textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
    
            hilite.addHighlight(start, end, myHighlightPainter);
            }
         catch (BadLocationException e) {
        }
    }
    
    private void jtMatchedResultsValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jtMatchedResultsValueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
        
        if (node instanceof RegExpMatchGroup)
        {
            if (doHighlight)
            {
                RegExpMatchGroup match = (RegExpMatchGroup) node;
                JTextComponent textComponent = jta_Input_Text;

                if (textComponent == null) {
                    return;
                }

                if (textComponent.getDocument().getLength() == 0) {
                    return;
                }

                highlight (textComponent, match.start, match.end);
            }
        }
    }//GEN-LAST:event_jtMatchedResultsValueChanged

    private void jta_Input_TextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jta_Input_TextKeyTyped
        removeHighlights(jta_Input_Text);
        doHighlight = false;
    }//GEN-LAST:event_jta_Input_TextKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox jcb_CANON_EQ;
    private javax.swing.JCheckBox jcb_CASE_INSENSITIVE;
    private javax.swing.JCheckBox jcb_COMMENTS;
    private javax.swing.JCheckBox jcb_DOTALL;
    private javax.swing.JCheckBox jcb_LITERAL;
    private javax.swing.JCheckBox jcb_MULTILINE;
    private javax.swing.JCheckBox jcb_UNICODE_CASE;
    private javax.swing.JCheckBox jcb_UNIX_LINES;
    private javax.swing.JTree jtMatchedResults;
    private javax.swing.JTree jt_LangRef;
    private javax.swing.JTextArea jta_Description;
    private javax.swing.JTextArea jta_Input_Text;
    private javax.swing.JTextArea jta_RegExp;
    private java.awt.Panel panel1;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized regexTopComponent getDefault() {
        if (instance == null) {
            instance = new regexTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the regexTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized regexTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(regexTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof regexTopComponent) {
            return (regexTopComponent) win;
        }
        Logger.getLogger(regexTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
    // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
    // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return regexTopComponent.getDefault();
        }
    }
}
