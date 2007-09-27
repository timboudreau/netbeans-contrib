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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.regextester;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


import javax.swing.JEditorPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.EditorKit;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.netbeans.modules.regextester.editor.RegexEditorKit;

/**
 * Top component which displays something.
 */
final class RegexTopComponent extends TopComponent {
    
    private static final long serialVersionUID = 1L;
    private Color defaultForegroundColor;
    private static RegexTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "RegexTopComponent";
    
    private RegexTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(RegexTopComponent.class, "CTL_RegexTopComponent"));
        setToolTipText(NbBundle.getMessage(RegexTopComponent.class, "HINT_RegexTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        new EventHandler(input);
        new EventHandler(pattern);
        defaultForegroundColor = matchTextField.getForeground();
        ((AbstractDocument) pattern.getDocument()).setDocumentFilter(new DocumentFilter() {
            
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                } else {
                    replace(fb, offset, 0, string, attr);
                }
            }
            
            public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
                replace(fb, offset, length, "", null);
            }
            
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.equals("\n")) {
                    System.out.println("### No way!");
                } else {
                    fb.replace(offset, length, text, attrs);
                }
            }
            
        });
    }
    
    protected EditorKit createDefaultEditorKit() {
        return new RegexEditorKit();
    }
    static EditorKit createEditorKitForContentType(String type) {
        return new RegexEditorKit();
    }
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized RegexTopComponent getDefault() {
        if (instance == null) {
            instance = new RegexTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the RegexTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized RegexTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find Regex component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof RegexTopComponent) {
            return (RegexTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return RegexTopComponent.getDefault();
        }
    }
    
    private void test() {
        Matcher matcher = null;
        try {
            matcher = getMatcher(pattern.getText(), input.getText());
        } catch (PatternSyntaxException ex) {
            matchTextField.setText("Wrong pattern syntax: " + ex.getDescription() + " near index " + ex.getIndex());
            matchTextField.setForeground(Color.RED);
            return;
        }

        StyledDocument doc = resultTextPane.getStyledDocument();
        addStylesToDocument(doc);

        List initStyles = new ArrayList();
        initStyles.add("red");
        initStyles.add("green");
        initStyles.add("blue");
        boolean found = false;
        String result = "";
        Iterator stylesIterator = initStyles.iterator();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, input.getText(), null);

            while(matcher.find()) {
                if (!stylesIterator.hasNext()) {
                    stylesIterator = initStyles.iterator();
                }
                doc.remove(matcher.start(), matcher.group().length());
                doc.insertString(matcher.start(), matcher.group(), doc.getStyle((String) stylesIterator.next()));
                result += "\nText \"" + matcher.group() + "\" found at <" + matcher.start() +
                        ", " + matcher.end() + ">";
                found = true;
            }
        
            if (!found) {
                result = ("No match found");
                doc.remove(0, doc.getLength());
                doc.insertString(0, result, null);
            } else {
                doc.insertString(doc.getLength(), result, null);
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
        if (matcher.matches()) {
            matchTextField.setText("Input matches pattern.\n");
            matchTextField.setForeground(defaultForegroundColor);
        } else {
            matchTextField.setText("Input doesn't match pattern.\n");
            matchTextField.setForeground(Color.RED);
        }
    }
    
    protected void addStylesToDocument(StyledDocument doc) {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style s = doc.addStyle("red", def);
        StyleConstants.setForeground(s, Color.RED);

        s = doc.addStyle("green", def);
        StyleConstants.setForeground(s, Color.GREEN);

        s = doc.addStyle("blue", def);
        StyleConstants.setForeground(s, Color.BLUE);
    }
    
    private static Matcher getMatcher(String patternStr, String input) throws PatternSyntaxException {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        return matcher;
    }
    
    private class EventHandler extends KeyAdapter {

        private JEditorPane editor;
        
        public EventHandler(JEditorPane editorPane) {
            this.editor = editorPane;
            editorPane.addKeyListener(this);
        }
        
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                evt.consume();
                test();
            } else if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                evt.consume();
                if (editor == input) {
                    pattern.requestFocusInWindow();
                } else {
                    input.requestFocusInWindow();
                }
            }
        }
        
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        input = new javax.swing.JEditorPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultTextPane = new javax.swing.JTextPane();
        pattern = new javax.swing.JEditorPane();
        matchTextField = new javax.swing.JTextField();

        jLabel1.setText("Input:");

        jLabel2.setText("Pattern:");

        jLabel3.setText("Match:");

        input.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        input.setMaximumSize(new java.awt.Dimension(9, 17));

        jScrollPane3.setViewportView(resultTextPane);

        pattern.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pattern.setDocument(pattern.getDocument());
        pattern.setContentType("text/x-regex");
        pattern.setMaximumSize(new java.awt.Dimension(9, 17));

        matchTextField.setEditable(false);
        matchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchTextFieldActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel1)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pattern, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                            .add(matchTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                            .add(input, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1)
                    .add(input, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel2)
                    .add(pattern, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(matchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void matchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchTextFieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_matchTextFieldActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField matchTextField;
    private javax.swing.JEditorPane pattern;
    private javax.swing.JTextPane resultTextPane;
    // End of variables declaration//GEN-END:variables
    
}
