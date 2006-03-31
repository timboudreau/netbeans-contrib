/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.editor.completion.latex;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.modules.latex.editor.AnalyseBib;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionJavaDoc;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public abstract class TexCompletionItem implements CompletionItem {
    
    private int substituteOffset;
    
    /** Creates a new instance of TexCompletionItem */
    public TexCompletionItem(int substituteOffset) {
        this.substituteOffset = substituteOffset;
    }
    
    public void setSubstituteOffset(int substituteOffset) {
        this.substituteOffset = substituteOffset;
    }
    
    public int getSubstituteOffset() {
        return substituteOffset;
    }

    public void defaultAction(final JTextComponent component) {
        Completion.get().hideCompletion();
        Completion.get().hideDocumentation();
        NbDocument.runAtomic((StyledDocument) component.getDocument(), new Runnable() {
            public void run() {
                Document doc = component.getDocument();
                
                try {
                    doc.remove(substituteOffset - 1, component.getCaretPosition() - substituteOffset + 1);
                    doc.insertString(substituteOffset - 1, getText(), null);
                } catch (BadLocationException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        });
    }
    
    public void processKeyEvent(KeyEvent evt) {
        if (acceptKey(evt.getKeyChar())) {
            defaultAction((JTextComponent) evt.getSource());
        }
    }
    
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(getLeftText(), getRightText(), g, defaultFont);
    }
    
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        if (selected) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, width, height);
            g.setColor(defaultColor);
        }
        CompletionUtilities.renderHtml(getIcon(), getLeftText(), getRightText(), g, defaultFont, defaultColor, width, height, selected);
    }
    
    public CompletionTask createDocumentationTask() {
        return null;
    }
    
    public CompletionTask createToolTipTask() {
        return null;
    }
    
    public boolean instantSubstitution(JTextComponent component) {
        return true;
    }
    
    public abstract int getSortPriority();
    
    public CharSequence getSortText() {
        return getText();
    }
    
    protected abstract String getText();
    
    protected abstract String getLeftText();
    
    protected abstract String getRightText();
    
    protected abstract ImageIcon getIcon();
    
    protected boolean acceptKey(char c) {
        return false;
    }
    
    public CharSequence getInsertPrefix() {
        return getText();
    }
    
    public static final class CommandCompletionItem extends TexCompletionItem {
        
        private Command command;
        
        /** Creates a new instance of TexCompletionItem */
        public CommandCompletionItem(int substituteOffset, Command command) {
            super(substituteOffset);
            this.command = command;
        }
    
        public int getSortPriority() {
            return 0;
        }
        
        public String getText() {
            return command.getCommand();
        }
        
        public String getLeftText() {
            return "<font color=#CC0000>" + command.getCommand() + "</font>";
        }
        
        public String getRightText() {
            return null;
        }
        
        public ImageIcon getIcon() {
            return null;
        }
        
        protected boolean acceptKey(char c) {
            if (c == '{')
                return true;
            
            return false;
        }

        public CompletionTask createDocumentationTask() {
            return new TexCompletionJavaDoc().createDocumentationForName(command.getCommand());
        }
        
        public String toString() {
            return command.getCommand();
        }
    }

    public static final class ValueCompletionItem extends TexCompletionItem {
        
        private String value;
        
        /** Creates a new instance of TexCompletionItem */
        public ValueCompletionItem(int substituteOffset, String envName) {
            super(substituteOffset);
            this.value = envName;
        }
    
        public int getSortPriority() {
            return 0;
        }
        
        public String getText() {
            return value;
        }
        
        public String getLeftText() {
            return value;
        }
        
        public String getRightText() {
            return null;
        }
        
        public ImageIcon getIcon() {
            return null;
        }
        
        public String toString() {
            return value;
        }
    }
    
    public static final class EnvironmentCompletionItem extends TexCompletionItem {
        
        private Environment environment;
        
        /** Creates a new instance of TexCompletionItem */
        public EnvironmentCompletionItem(int substituteOffset, Environment environment) {
            super(substituteOffset);
            this.environment = environment;
        }
    
        public int getSortPriority() {
            return 0;
        }
        
        public String getText() {
            return environment.getName();
        }
        
        public String getLeftText() {
            return "<font color=#0000F5>" + environment.getName() + "</font>";
        }
        
        public String getRightText() {
            return null;
        }
        
        public ImageIcon getIcon() {
            return null;
        }
        
        public CompletionTask createDocumentationTask() {
            return new TexCompletionJavaDoc().createDocumentationForName(environment.getName());
        }
        
        public String toString() {
            return environment.getName();
        }
    }

    public static final class LabelCompletionItem extends TexCompletionItem {
        
        private String labelDesc;
        private String label;
        
        /** Creates a new instance of TexCompletionItem */
        public LabelCompletionItem(int substituteOffset, String label, String labelDesc) {
            super(substituteOffset);
            this.labelDesc = labelDesc;
            this.label   = label;
        }
    
        public int getSortPriority() {
            return 0;
        }
        
        public String getText() {
            return label;
        }
        
        public String getLeftText() {
            return label + "<font color=#808080>-" + labelDesc + "</font>";
        }
        
        public String getRightText() {
            return null;
        }
        
        public ImageIcon getIcon() {
            return null;
        }
        
        public String toString() {
            return label + "-" + labelDesc;
        }
    }

    public static final class DocClassCompletionItem extends TexCompletionItem {
        
        private String name;
        
        /** Creates a new instance of TexCompletionItem */
        public DocClassCompletionItem(int substituteOffset, String name) {
            super(substituteOffset);
            this.name = name;
        }
    
        public int getSortPriority() {
            return 0;
        }
        
        public String getText() {
            return name;
        }
        
        public String getLeftText() {
            return name;
        }
        
        public String getRightText() {
            return null;
        }
        
        public ImageIcon getIcon() {
            return null;
        }
        
        public String toString() {
            return name;
        }
    }
    
    public static final class BiBRecordCompletionItem extends TexCompletionItem {
        
        private AnalyseBib.BibRecord record;
        
        public BiBRecordCompletionItem(int substituteOffset, AnalyseBib.BibRecord record) {
            super(substituteOffset);
            this.record = record;
        }

        public int getSortPriority() {
            return 0;
        }

        protected String getText() {
            return record.getRef();
        }

        protected String getLeftText() {
            return record.getTitle();
        }

        protected String getRightText() {
            return record.getRef();
        }

        protected ImageIcon getIcon() {
            return null;
        }
        
        public String toString() {
            return record.getRef() + ":" + record.getTitle();
        }
    }

    public static final class NewFileCompletionItem extends TexCompletionItem {

        private static final String NEW_FILE_TEXT = "Create New File";

        private FileObject base;

        public NewFileCompletionItem(int substituteOffset, FileObject base) {
            super(substituteOffset);
            this.base = base;
        }

        public void defaultAction(final JTextComponent component) {
            try {
                NotifyDescriptor nd = new NotifyDescriptor.InputLine("New File Name:", "Craate New File");
                
                DialogDisplayer.getDefault().notify(nd);

                String nueFileName = ((NotifyDescriptor.InputLine) nd).getInputText();

                if (nueFileName.lastIndexOf('.') == (-1)) {
                    //does not have an extension:
                    nueFileName += ".tex";
                }

                FileObject nueFile = FileUtil.createData(base.getParent(), nueFileName);

                String relativeFile = FileUtil.getRelativePath(base.getParent(), nueFile);

                if (relativeFile == null) {
                    relativeFile = FileUtil.toFile(nueFile).getAbsolutePath();
                }

                final String relativeFileFinal = relativeFile;

                Completion.get().hideCompletion();
                Completion.get().hideDocumentation();
                NbDocument.runAtomic((StyledDocument) component.getDocument(), new Runnable() {
                    public void run() {
                        Document doc = component.getDocument();
                        
                        try {
                            doc.remove(getSubstituteOffset() - 1, component.getCaretPosition() - getSubstituteOffset() + 1);
                            doc.insertString(getSubstituteOffset() - 1, relativeFileFinal + "}", null);
                        } catch (BadLocationException e) {
                            ErrorManager.getDefault().notify(e);
                        }
                    }
                });

                DataObject toOpen = DataObject.find(nueFile);
                OpenCookie ec = (OpenCookie) toOpen.getCookie(OpenCookie.class);

                ec.open();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }

        public void processKeyEvent(KeyEvent evt) {
        }

        public int getPreferredWidth(Graphics g, Font defaultFont) {
            return CompletionUtilities.getPreferredWidth(NEW_FILE_TEXT, null, g, defaultFont);
        }
        
        public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
            if (selected) {
                g.setColor(backgroundColor);
                g.fillRect(0, 0, width, height);
                g.setColor(defaultColor);
            }
            CompletionUtilities.renderHtml(null, NEW_FILE_TEXT, "", g, defaultFont, defaultColor, width, height, selected);
        }

        public CompletionTask createDocumentationTask() {
            return null;
        }

        public CompletionTask createToolTipTask() {
            return null;
        }

        public boolean instantSubstitution(JTextComponent component) {
            return false;
        }

        public int getSortPriority() {
            return 50;
        }

        public CharSequence getSortText() {
            return NEW_FILE_TEXT;
        }

        public CharSequence getInsertPrefix() {
            return null;
        }

        protected String getText() {throw new IllegalArgumentException();}
        
        protected String getLeftText() {throw new IllegalArgumentException();}
        
        protected String getRightText() {throw new IllegalArgumentException();}
        
        protected ImageIcon getIcon() {throw new IllegalArgumentException();}

    }

}
