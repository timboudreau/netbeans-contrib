/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.wizards;

import java.awt.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class NewDocumentWizardIterator implements TemplateWizard.Iterator {
    
    private WizardDescriptor.Panel[] panels;
    private String[]                 panelNames;
    private boolean                  initialized;
    private int                      current;
    
    /** Creates a new instance of NewDocumentWizardDescriptor */
    public NewDocumentWizardIterator() {
        initialized = false;
        panels = null;
        current = 0;
    }
    
    public WizardDescriptor.Panel current() {
        if (!initialized)
            throw new IllegalStateException("");
        
        if (panels[current] instanceof JComponent)
            ((JComponent) panels[current].getComponent()).putClientProperty("WizardPanel_contentSelectedIndex", new Integer(current));
        return panels[current];
    }
    
    public boolean hasNext() {
        if (!initialized)
            throw new IllegalStateException("");
        
        return current + 1 < panels.length;
    }
    
    public boolean hasPrevious() {
        if (!initialized)
            throw new IllegalStateException("");
        
        return current > 0;
    }
    
    public void initialize(TemplateWizard wiz) {
        wiz.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        panels = new WizardDescriptor.Panel[2];
        
        panels[0] = wiz.targetChooser();
        panels[1] = new ContentPanel();
        
        panelNames = new String[2];
        
        Component chooser = panels[0].getComponent();
        
        panelNames[0] = chooser.getName();
        panelNames[1] = "LaTeX - TESTESTERSTETSTES";

        if (chooser instanceof JComponent) {
            ((JComponent) chooser).putClientProperty("WizardPanel_contentData", panelNames);
        }
        
        current = 0;
        initialized = true;
    }
    
    private String preparePreamble(LaTeXWizardData data) {
        boolean      isFontDefault = Storage.getDefault().isDefaultFontSize(data.getDocclass(), data.getInputEnc());
        boolean      isPaperDefault = Storage.getDefault().isDefaultPaperSize(data.getDocclass(), data.getPaperSize());
        boolean      hasOptionalOptions = data.getOptions().length > 0;
        StringBuffer result = new StringBuffer();
        
        result.append("\\\\documentclass");
        
        if (!isFontDefault || !isPaperDefault || hasOptionalOptions) {
            result.append("[");
            
            if (!isFontDefault) {
                result.append(data.getFontSize());
                
                if (!isPaperDefault || hasOptionalOptions) {
                    result.append(",");
                }
            }
            
            if (!isPaperDefault) {
                result.append(data.getPaperSize());
                
                if (hasOptionalOptions) {
                    result.append(",");
                }
            }
        
            if (hasOptionalOptions) {
                for (int cntr = 0; cntr < data.getOptions().length; cntr++) {
                    result.append(data.getOptions()[cntr]);
                    
                    if (cntr + 1 <= data.getOptions().length)
                        result.append(",");
                }
            }
            result.append("]");
        }
        
        result.append("{");
        result.append(data.getDocclass());
        result.append("}\n");
        
        if (!Storage.getDefault().isDefaultEncoding(data.getInputEnc())) {
            result.append("\\\\usepackage[");
            result.append(data.getInputEnc());
            result.append("]{");
            result.append(Storage.getDefault().getInputEncPackageName());
            result.append("}\n\\\\usepackage[T1]{fontenc}\n");
        }
        
        return result.toString();
    }
    
    private String prepareEmptyPreamble() {
        return "\\documentclass{article}\n";
    }
    
    public Set instantiate(TemplateWizard wiz) throws IOException {
        if (!initialized)
            throw new IllegalStateException("");
        
        DataFolder target = wiz.getTargetFolder();
        DataObject template = wiz.getTemplate();
        String     targetName = wiz.getTargetName();
        
        DataObject instatied = template.createFromTemplate(target, targetName);
        
        EditorCookie ec = (EditorCookie) instatied.getCookie(EditorCookie.class);
        
        if (ec != null) {
            try {
                Document doc = ec.openDocument();
                String   content = doc.getText(0, doc.getLength());
                String   preamble;
                
                LaTeXWizardData data = (LaTeXWizardData) wiz.getProperty("latex_settings");
                
                if (data != null) {
                    preamble = preparePreamble(data);
                } else {
                    preamble = prepareEmptyPreamble();
                }
                
                content = content.replaceFirst("__PREAMBLE__", preamble);
                
                doc.remove(0, doc.getLength());
                doc.insertString(0, content, null);
                
                ec.saveDocument();
                ec.open();
            } catch (BadLocationException e) {
                IOException newE = new IOException();
                
                ErrorManager.getDefault().annotate(newE, e);
                
                throw newE;
            }
        }
        
        return Collections.singleton(instatied);
    }
    
    public String name() {
        return "TESTETSTETSTETSTE";
    }
    
    public void nextPanel() {
        if (!initialized)
            throw new IllegalStateException("");
        
        current++;
    }
    
    public void previousPanel() {
        if (!initialized)
            throw new IllegalStateException("");
        
        current--;
    }
    
    public void uninitialize(TemplateWizard wiz) {
        initialized = false;
        panels = null;
        panelNames = null;
        current = 0;
    }
    
    private static NewDocumentWizardIterator instance = null;
    
    public static synchronized NewDocumentWizardIterator create() {
        if (instance == null)
            instance = new NewDocumentWizardIterator();
        
        return instance;
    }
    
    public void addChangeListener(ChangeListener l) {
    }
    
    public void removeChangeListener(ChangeListener l) {
    }
    
    private static class ContentPanel implements WizardDescriptor.Panel {
        
        private PreambleForm panel;
        
        public ContentPanel() {
            panel = null;
        }
        
        public void addChangeListener(ChangeListener l) {
        }
        
        private synchronized JPanel getPanel() {
            if (panel == null) {
                panel = new PreambleForm();
                panel.setName(NbBundle.getBundle("org/netbeans/modules/latex/wizards/Bundle").getString("LaTeX_document_preamble"));
            }
            
            return panel;
        }
        
        public Component getComponent() {
            return getPanel();
        }
        
        public HelpCtx getHelp() {
            return null;
        }
        
        public boolean isValid() {
            return true;
        }
        
        public void readSettings(Object settings) {
            if (settings instanceof WizardDescriptor) {
                WizardDescriptor descr = (WizardDescriptor) settings;
                
                LaTeXWizardData data = (LaTeXWizardData) descr.getProperty("latex_settings");
                
                if (data == null)
                    return ;
                
                panel.setDocumentClass(data.getDocclass());
                panel.setFontSize(data.getFontSize());
                panel.setPaperSize(data.getPaperSize());
                panel.setInputEnc(data.getInputEnc());
                panel.setAuthor(data.getAuthor());
                panel.setTitle(data.getTitle());
                panel.setOptions(data.getOptions());
            }
        }
        
        public void removeChangeListener(ChangeListener l) {
        }
        
        public void storeSettings(Object settings) {
            if (settings instanceof WizardDescriptor) {
                WizardDescriptor descr = (WizardDescriptor) settings;
                
                LaTeXWizardData data = (LaTeXWizardData) descr.getProperty("latex_settings");
                
                if (data == null) {
                    data = new LaTeXWizardData();
                    descr.putProperty("latex_settings", data);
                }
                
                data.setDocclass(panel.getDocumentClass());
                data.setFontSize(panel.getFontSize());
                data.setPaperSize(panel.getPaperSize());
                data.setInputEnc(panel.getInputEnc());
                data.setAuthor(panel.getAuthor());
                data.setTitle(panel.getTitle());
                data.setOptions(panel.getOptions());
            }
        }
        
    }
}
