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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.javafx.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.loaders.DataObject;
import org.netbeans.editor.Formatter;

/**
 *
 * @author answer
 */
public class JavaFXDocument extends NbEditorDocument{
    
    private JPanel panel = null;
    private JPanel panelEmpty = null;
    private PreviewSplitPane split = null;
    private Component editor = null;
    private JScrollPane scroll = null;

    boolean executionEnabled = false;
    boolean errorAndSyntaxEnabled = false;
    boolean compReqNew = false;
    boolean compReqNewP = false;
    
    public JavaFXDocument(Class kitClass) {

        super(kitClass);
        DocumentListener changeListener = new DocumentListener(){
            public void removeUpdate(DocumentEvent e) {
                sourceChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                sourceChanged();
            }
            
            public void changedUpdate(DocumentEvent e) {
            }
        };
        addDocumentListener(changeListener);
    }

    @Override
    public Formatter getFormatter() {
        return Formatter.getFormatter(getKitClass());
    }

    @Override
    public Component createEditor(JEditorPane pane){
        editor = super.createEditor(pane);
        
        final JavaFXDocument doc = this;
        
        FocusListener focusListener = new FocusListener() {
            public void focusGained(FocusEvent e) {
                doc.compReqNew(true);
                doc.compReqNewP(true);
            }

            public void focusLost(FocusEvent e) {
            }
        };
        
        pane.addFocusListener(focusListener);
        
        Class clQEP = null;
        try{  
            clQEP = Class.forName("org.openide.text.QuietEditorPane");
        }catch(ClassNotFoundException e){}
        
        if (!pane.getClass().equals(clQEP)){
            return editor;
        }else{
            panelEmpty = new JPanel();
            panelEmpty.setMinimumSize(new Dimension(0, 0));
            panelEmpty.setMaximumSize(new Dimension(0, 0));
            
            panel = new JPanel();
            panel.setBackground(Color.WHITE);

            scroll = new JScrollPane();
            scroll.setWheelScrollingEnabled(true);
            scroll.setAutoscrolls(true);
//            split = new PreviewSplitPane(this, JSplitPane.VERTICAL_SPLIT, scroll, editor);
            split = new PreviewSplitPane(this);
            split.setOrientation(JSplitPane.VERTICAL_SPLIT);

            if (executionEnabled){
                split.setTopComponent(scroll);
                split.setDividerSize(divSize);
                split.setDividerLocation(divLoc);
            }else{
                split.setDividerSize(0);
                split.setDividerLocation(0);
                split.setTopComponent(panelEmpty);
            }
            split.setActionMap(pane.getActionMap());
            
            scroll.setViewportView(panel);
            split.setRightComponent(editor);
            
            return split;
            
        }
    }
    
    public void compReqNew(boolean value){
        this.compReqNew = value;
    }

    public boolean isCompReqNew(){
        return compReqNew;
    }
    
    public void compReqNewP(boolean value){
        this.compReqNewP = value;
    }

    public boolean isCompReqNewP(){
        return compReqNewP;
    }
    
    public DataObject getDataObject(){
        return NbEditorUtilities.getDataObject(this);
    }
    
    public void  sourceChanged(){
        synchronized(this){
            JavaFXPier.sourceChanged(this);
        }
    }
    
    public String getSourceToRender(){
        try{
            return (getText(0, getLength()));
        }catch(BadLocationException e){
            return "";
        }
    }
    
    public void renderPreview(final JComponent comp){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                if (panel!=null){
                    if (panel.getComponentCount() > 0){
                        panel.remove(0);
                    }else{
                        panel.setLayout(new BorderLayout());
                    }
                    panel.add(comp, BorderLayout.CENTER);
                    split.validate();
                }
            }
        });
    }
    
    private int divLoc = 150;
    private int divSize = 8;
    
    public void enableErrorAndSyntax(boolean enabled){
        errorAndSyntaxEnabled = enabled;
    }
    
    public boolean errorAndSyntaxAllowed(){
        return errorAndSyntaxEnabled;
    }
            
    public void enableExecution(boolean enabled){
        executionEnabled = enabled;
        if (enabled){
            
            split.setTopComponent(scroll);
            split.setDividerSize(divSize);
            split.setDividerLocation(divLoc);
        }else{
            
            divLoc = split.getDividerLocation();
            split.setDividerSize(0);
            split.setDividerLocation(0);
            split.setTopComponent(panelEmpty);
        }
        split.validate();
    }
    
    public boolean executionAllowed(){
        return executionEnabled;
    }
    
    public class PreviewSplitPane extends JSplitPane{
        private JavaFXDocument doc;

        public PreviewSplitPane(JavaFXDocument doc){
            super();
            this.doc = doc;
        }
/*        
        public PreviewSplitPane(JavaFXDocument doc, int newOrientation, Component newLeftComponent, Component newRightComponent){
            super(newOrientation, newLeftComponent, newRightComponent);
            this.doc = doc;
        }
*/     
        public JavaFXDocument getDocument(){
            return doc;
        }
    }
}
