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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Map;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import org.netbeans.editor.BaseAction;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsNames;
import org.netbeans.modules.editor.NbEditorKit;
import org.openide.util.NbBundle;
import javax.swing.text.*;
import org.openide.loaders.DataObject;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.editor.Syntax;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.editor.Formatter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.lib.editor.util.PriorityListenerList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import java.util.EventListener;

/**
 *
 * @author answer
 */
public class JavaFXEditorKit extends NbEditorKit{

    public static final String toggleFXPreviewExecution = "toggle-fx-preview-execution"; //NOI18N
    public static final String toggleFXSyntaxErrorDetection = "toggle-fx-error-syntax"; //NOI18N
    public static final String buttonResetFXPreviewExecution = "toggle-reset-fx-preview-execution"; //NOI18N
    public static final String FX_MIME_TYPE = "text/x-fx";
    
    public JavaFXEditorKit() {
        super();
        
            Settings.addInitializer (new Settings.Initializer () {
                public String getName() {
                    return FX_MIME_TYPE;
                }

                @SuppressWarnings("unchecked")
                public void updateSettingsMap (Class kitClass, Map settingsMap) {
                        settingsMap.put (SettingsNames.CODE_FOLDING_ENABLE, Boolean.TRUE);
                }

            });
    }
    
    @Override
    public String getContentType() {
        return FX_MIME_TYPE;
    }
   
 
    @Override
    public Document createDefaultDocument(){
        Document doc = new JavaFXDocument(this.getClass());
        Object mimeType = doc.getProperty("mimeType");
        if (mimeType == null){
            doc.putProperty("mimeType", getContentType());
        }
        return doc;
    }
    
    @Override
    protected Action[] createActions() {
        Action[] superActions = super.createActions();
        ResetFXPreviewExecution resetAction = new ResetFXPreviewExecution();
        Action[] javafxActions = new Action[] {
            new CommentAction("//"),
            new UncommentAction("//"),
            new ToggleFXPreviewExecution(resetAction),
            resetAction,
            new ToggleFXSyntaxErrorDetection(),
            new JavaDefaultKeyTypedAction(),
            new JavaDeleteCharAction(deletePrevCharAction, false),
            new JavaInsertBreakAction(),
            new JavaFXGoToDeclarationAction()

        };
        return TextAction.augmentList(superActions, javafxActions);
    }
    
    public static class ToggleFXPreviewExecution extends BaseAction implements org.openide.util.actions.Presenter.Toolbar {
        PreviewButton b = null;
        ResetFXPreviewExecution resetAction = null;
        
        public ToggleFXPreviewExecution(ResetFXPreviewExecution resetAction) {
            super(toggleFXPreviewExecution);
            this.resetAction = resetAction;
            putValue(Action.SMALL_ICON, new ImageIcon(org.openide.util.Utilities.loadImage(
                    "org/netbeans/modules/javafx/editor/resources/preview.png"))); // NOI18N
            putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("enable-fx-preview-execution"));
        }
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            JavaFXDocument doc = getJavaFXDocument(target);
            if (doc != null){
                if(doc.executionAllowed()) {
                    resetAction.setActionButtonEnabled(false);
                    doc.enableExecution(false);
                    putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("enable-fx-preview-execution"));
                }else {
                    resetAction.setActionButtonEnabled(true);
                    doc.enableExecution(true);
                    putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("disable-fx-preview-execution"));
                    JavaFXPier.showPreview(doc);
                }
            }else{
                b.setSelected(!b.isSelected());
            }
        }
        
        private JavaFXDocument getJavaFXDocument(JTextComponent comp){
            Component c = comp;
            while(c != null){
                if (c instanceof JavaFXDocument.PreviewSplitPane){
                    return ((JavaFXDocument.PreviewSplitPane)c).getDocument();
                }
                c = c.getParent();
            }
            return null;
        }
        
        public java.awt.Component getToolbarPresenter() {
            b = new PreviewButton();
            b.setSelected(false);
            b.setAction(this);
            b.putClientProperty("hideActionText", Boolean.TRUE); //NOI18N
            b.setText("");
            return b;
        }
        
         private static final class PreviewButton extends JToggleButton implements ChangeListener{
            
            
            public void stateChanged(ChangeEvent evt) {
                boolean selected = isSelected();
                super.setContentAreaFilled(selected);
                super.setBorderPainted(selected);
            }
             
            @Override
            public void setBorderPainted(boolean arg0) {
                    if(!isSelected()){
                        super.setBorderPainted(arg0);
                    }
            }

            @Override
            public void setContentAreaFilled(boolean arg0) {
                if(!isSelected()){
                    super.setContentAreaFilled(arg0);
                }
            }

         }
    }
    
    static class DocEvent implements DocumentEvent {
        Document doc = null;
        
        public DocEvent(Document doc){
            this.doc = doc;
        }

        public int getOffset() {
            return 0;
        }

        public int getLength() {
            return 0;
        }

        public Document getDocument() {
            return doc;
        }

        public EventType getType() {
            return EventType.INSERT;
        }

        public ElementChange getChange(Element elem) {
            return null;
        }
      
    }
    
    
    public static class ToggleFXSyntaxErrorDetection extends BaseAction implements org.openide.util.actions.Presenter.Toolbar {
        PreviewButton b = null;
        
        public ToggleFXSyntaxErrorDetection() {
            super(toggleFXSyntaxErrorDetection);
            putValue(Action.SMALL_ICON, new ImageIcon(org.openide.util.Utilities.loadImage(
                    "org/netbeans/modules/javafx/editor/resources/detection.png"))); // NOI18N
            //putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("disable-fx-error-syntax"));
            putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("enable-fx-error-syntax"));
            
        }
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            final JavaFXDocument doc = getJavaFXDocument(target);
            if (doc != null){
                if(doc.errorAndSyntaxAllowed()) {
                    doc.enableErrorAndSyntax(false);
                    putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("enable-fx-error-syntax"));
                }else {
                    doc.enableErrorAndSyntax(true);
                    DocumentEvent event = new DocEvent(doc);
                    for (DocumentListener documentListener : doc.getDocumentListeners()) {
                        for (int i = 0; i < ((PriorityListenerList)documentListener).getListenersArray().length; i++) {
                            EventListener[] listenersArray = ((PriorityListenerList)documentListener).getListenersArray()[i];
                            for (int j = 0; j < listenersArray.length; j++) {
                                EventListener eventListener = listenersArray[j];
                                if (eventListener.getClass().getName().contains("Proxy")) {
                                    try {
                                        ((DocumentListener)eventListener).insertUpdate(event);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    /*try {
                        doc.insertString(doc.getLength(), " ", null);
                        doc.remove(doc.getLength() - 1, 1);
                    } catch (Exception e) {
                    }*/
                    putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("disable-fx-error-syntax"));
                }
            }else{
                b.setSelected(!b.isSelected());
            }
        }
        
        private JavaFXDocument getJavaFXDocument(JTextComponent comp){
            Component c = comp;
            while(c != null){
                if (c instanceof JavaFXDocument.PreviewSplitPane){
                    return ((JavaFXDocument.PreviewSplitPane)c).getDocument();
                }
                c = c.getParent();
            }
            return null;
        }
        
        public java.awt.Component getToolbarPresenter() {
            b = new PreviewButton();
            b.setSelected(false);
            b.setAction(this);
            b.putClientProperty("hideActionText", Boolean.TRUE); //NOI18N
            b.setText("");
            return b;
        }
        
         private static final class PreviewButton extends JToggleButton implements ChangeListener{
            
            
            public void stateChanged(ChangeEvent evt) {
                boolean selected = isSelected();
                super.setContentAreaFilled(selected);
                super.setBorderPainted(selected);
            }
             
            @Override
            public void setBorderPainted(boolean arg0) {
                    if(!isSelected()){
                        super.setBorderPainted(arg0);
                    }
            }

            @Override
            public void setContentAreaFilled(boolean arg0) {
                if(!isSelected()){
                    super.setContentAreaFilled(arg0);
                }
            }

         }
    }
    
    public static class ResetFXPreviewExecution extends BaseAction implements org.openide.util.actions.Presenter.Toolbar {
        JButton b = null;
        
        public ResetFXPreviewExecution() {
            super(buttonResetFXPreviewExecution);
            putValue(Action.SMALL_ICON, new ImageIcon(org.openide.util.Utilities.loadImage(
                    "org/netbeans/modules/javafx/editor/resources/reset_preview.png"))); // NOI18N
            putValue(SHORT_DESCRIPTION,NbBundle.getBundle(JavaFXEditorKit.class).getString("reset-fx-preview-execution"));
        }
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            JavaFXDocument doc = getJavaFXDocument(target);
            
            if(doc != null && doc.executionAllowed()){
                JavaFXPier.showPreview(doc);
            }
        }
        
        private JavaFXDocument getJavaFXDocument(JTextComponent comp){
            Component c = comp;
            while(c != null){
                if (c instanceof JavaFXDocument.PreviewSplitPane){
                    return ((JavaFXDocument.PreviewSplitPane)c).getDocument();
                }
                c = c.getParent();
            }
            return null;
        }
        
        public void setActionButtonEnabled(boolean enabled){
            b.setEnabled(enabled);
            b.validate();
        }
        
        public java.awt.Component getToolbarPresenter() {
            b = new JButton();
            b.setAction(this);
            b.putClientProperty("hideActionText", Boolean.TRUE); //NOI18N
            b.setText("");
            b.setEnabled(false);
            return b;
        }
    }    
    
    public static class JavaDefaultKeyTypedAction extends ExtDefaultKeyTypedAction {

        @Override
        protected void insertString(BaseDocument doc, int dotPos,
                                    Caret caret, String str,
                                    boolean overwrite) throws BadLocationException {
            char insertedChar = str.charAt(0);
            if (insertedChar == '\"' || insertedChar == '\''){
                boolean inserted = BracketCompletion.completeQuote(doc, dotPos, caret, insertedChar);
                if (inserted){
                    caret.setDot(dotPos+1);
                }else{
                    super.insertString(doc, dotPos, caret, str, overwrite);
                    
                }
            } else {
                super.insertString(doc, dotPos, caret, str, overwrite);
                BracketCompletion.charInserted(doc, dotPos, caret, insertedChar);
            }
        }
        
        @Override
        protected void replaceSelection(JTextComponent target,
                int dotPos,
                Caret caret,
                String str,
                boolean overwrite)
                throws BadLocationException {
            char insertedChar = str.charAt(0);
            Document doc = target.getDocument();
            if (insertedChar == '\"' || insertedChar == '\''){
                if (doc != null) {
                    try {
                        boolean inserted = false;
                        int p0 = Math.min(caret.getDot(), caret.getMark());
                        int p1 = Math.max(caret.getDot(), caret.getMark());
                        if (p0 != p1) {
                            doc.remove(p0, p1 - p0);
                        }
                        int caretPosition = caret.getDot();
                        if (doc instanceof BaseDocument){
                            inserted = BracketCompletion.completeQuote(
                                    (BaseDocument)doc,
                                    caretPosition,
                                    caret, insertedChar);
                        }
                        if (inserted){
                            caret.setDot(caretPosition+1);
                        } else {
                            if (str != null && str.length() > 0) {
                                doc.insertString(p0, str, null);
                            }
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                super.replaceSelection(target, dotPos, caret, str, overwrite);
                if (doc instanceof BaseDocument){
                    BracketCompletion.charInserted((BaseDocument)doc, caret.getDot()-1, caret, insertedChar);
                }
            }
        }
    }
    
    public static class JavaInsertBreakAction extends InsertBreakAction {
        
        static final long serialVersionUID = -1506173310438326380L;
        
        @Override
        protected Object beforeBreak(JTextComponent target, BaseDocument doc, Caret caret) {
            int dotPos = caret.getDot();
            if (BracketCompletion.posWithinString(doc, dotPos)) {
                try {
                    doc.insertString(dotPos, "\" + \"", null); //NOI18N
                    dotPos += 3;
                    caret.setDot(dotPos);
                    return new Integer(dotPos);
                } catch (BadLocationException ex) {
                }
            } else {
                try {
                    if (BracketCompletion.isAddRightBrace(doc, dotPos)) {
                        int end = BracketCompletion.getRowOrBlockEnd(doc, dotPos);
                        doc.insertString(end, "}", null); // NOI18N
                        doc.getFormatter().indentNewLine(doc, end);                        
                        caret.setDot(dotPos);
                        return Boolean.TRUE;
                    }
                } catch (BadLocationException ex) {
                }
            }
            return null;
        }
        
        @Override
        protected void afterBreak(JTextComponent target, BaseDocument doc, Caret caret, Object cookie) {
            if (cookie != null) {
                if (cookie instanceof Integer) {
                    // integer
                    int nowDotPos = caret.getDot();
                    caret.setDot(nowDotPos+1);
                }
            }
        }

      }
    
    public static class JavaDeleteCharAction extends ExtDeleteCharAction {
        
        public JavaDeleteCharAction(String nm, boolean nextChar) {
            super(nm, nextChar);
        }

        @Override
        protected void charBackspaced(BaseDocument doc, int dotPos, Caret caret, char ch)
        throws BadLocationException {
            BracketCompletion.charBackspaced(doc, dotPos, caret, ch);
        }
    }
    
    @Override
    public Syntax createSyntax(Document doc) {
        return new JavaFXSyntax(getSourceLevel((BaseDocument)doc));
    }
    
    public String getSourceLevel(BaseDocument doc) {
        DataObject dob = NbEditorUtilities.getDataObject(doc);
        return dob != null ? SourceLevelQuery.getSourceLevel(dob.getPrimaryFile()) : null;
    }
    
    @Override
    public Formatter createFormatter() {
        return new JavaFXFormatter(this.getClass());
    }
}