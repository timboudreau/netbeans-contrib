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
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
//import org.netbeans.modules.javafx.preview.AutoResizableDesktopPane;
import org.netbeans.modules.javafx.preview.JavaFXModel;
import org.openide.loaders.DataObject;

/**
 *
 * @author answer
 */
public class JavaFXDocument extends NbEditorDocument implements FXDocument{
    
    private JPanel panel = null;
     private JEditorPane pane = null;
    private JPanel panelEmpty = null;
    private PreviewSplitPane split = null;
    private Component editor = null;
    private JScrollPane scroll = null;
    private static final JPanel nothingPanel = getSloganPanel(loadIcon("org/netbeans/modules/javafx/editor/resources/blank.png"), "Nothing to show...");;
    private static final JPanel compilePanel = getSloganPanel(loadIcon("org/netbeans/modules/javafx/editor/resources/clock.gif"), "Compile...");;

    boolean executionEnabled = false;
    boolean errorAndSyntaxEnabled = false;
    
    public JavaFXDocument(Class kitClass) {
        super(kitClass);
    }
    
    @Override
    public void write(Writer writer, int pos, int len) throws IOException, BadLocationException {
        super.write(writer, pos, len);
        JavaFXModel.fireDependenciesChange(this);
    }

    public JComponent getEditor() {
        return (JComponent) pane;
    }
    
    @Override
    public Component createEditor(JEditorPane pane) {
        JavaFXModel.addDocument(this);
        
        DocumentListener changeListener = new DocumentListener(){
            public void removeUpdate(DocumentEvent e) {
                sourceChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                sourceChanged();
            }
            
            public void changedUpdate(DocumentEvent e) {
                int i = 0;
            }
        };
        addDocumentListener(changeListener);
        
        editor = super.createEditor(pane);
        this.pane = pane;
        
        final JavaFXDocument doc = this;
        
        FocusListener focusListener = new FocusListener() {
            public void focusGained(FocusEvent e) {
                doc.createDocumentEvent(0, 0, EventType.CHANGE);
            }

            public void focusLost(FocusEvent e) {
            }
        };
        
        pane.addFocusListener(focusListener);
        
        Class clQEP = null;
        try {  
            clQEP = Class.forName("org.openide.text.QuietEditorPane");
        } catch (ClassNotFoundException e) {}
        
        if (!pane.getClass().equals(clQEP)) {
            return editor;
        } else {
            panelEmpty = new JPanel();
            panelEmpty.setMinimumSize(new Dimension(0, 0));
            panelEmpty.setMaximumSize(new Dimension(0, 0));
            
            panel = new PreviewPanel(this);
            panel.setBackground(Color.WHITE);

            scroll = new JScrollPane();
            scroll.setWheelScrollingEnabled(true);
            scroll.setAutoscrolls(true);
//            split = new PreviewSplitPane(this, JSplitPane.VERTICAL_SPLIT, scroll, editor);
            split = new PreviewSplitPane(this);
            split.setOrientation(JSplitPane.VERTICAL_SPLIT);

            if (executionEnabled) {
                split.setTopComponent(scroll);
                split.setDividerSize(divSize);
                split.setDividerLocation(divLoc);
            } else {
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

    public DataObject getDataObject(){
        return NbEditorUtilities.getDataObject(this);
    }
    
    public void  sourceChanged(){
        synchronized(this){
            JavaFXModel.sourceChanged(this);
        }
    }
    
    public String getSourceToRender(){
        try{
            return (getText(0, getLength()));
        }catch(BadLocationException e){
            return "";
        }
    }
    
    public void setCompile() {
        renderPreview(compilePanel);
    }
    
    public void setNothing() {
        renderPreview(nothingPanel);
    }
    
    public static JPanel getNothingPane() {
        return nothingPanel;
    }
    
    static public JPanel getSloganPanel(ImageIcon icon, String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(icon);
        label.setText(text);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    static private ImageIcon loadIcon(String resource) {
        try {
            InputStream is = JavaFXModel.class.getClassLoader().getResourceAsStream(resource);
            int b = is.read();
            ArrayList <Byte> list = new ArrayList <Byte> ();
            while (b != -1) {
                list.add(Byte.valueOf((byte)b));
                b = is.read();
            }
            byte[] array = new byte[list.size()];
            int i = 0;
            for (Byte B : list) {
                array[i++] = B.byteValue();
            }
            return new ImageIcon(Toolkit.getDefaultToolkit().createImage(array));
        } catch (Exception ex) {
            return null;
        }
    }
            
    public void renderPreview(final JComponent comp){
        JavaFXModel.setResultComponent(this, comp);
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                if (panel!=null){
                    if (panel.getComponentCount() > 0){
                        panel.remove(0);
                    }else{
                        panel.setLayout(new BorderLayout());
                    }
                    if (comp != null){
                        panel.add(comp);
                        /*if (comp instanceof AutoResizableDesktopPane)
                            ((AutoResizableDesktopPane)comp).checkDesktopSize();*/
                    }
                    split.revalidate();
                    split.validate();
                    split.repaint();
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
        
        public JavaFXDocument getDocument(){
            return doc;
        }
    }
    
    public class PreviewPanel extends JPanel{
        public PreviewPanel(JavaFXDocument doc){
            super();
            putClientProperty(java.awt.print.Printable.class, NbEditorUtilities.getFileObject(doc).getNameExt());
        }
    }
}
