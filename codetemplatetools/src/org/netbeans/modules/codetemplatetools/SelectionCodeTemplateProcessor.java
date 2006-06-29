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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.codetemplatetools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateInsertRequest;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateParameter;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateProcessor;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateProcessorFactory;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExClipboard;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class SelectionCodeTemplateProcessor implements CodeTemplateProcessor {
    private Clipboard clipboard;
    
    public static final String SELECTION_PARAMETER         = "selection"; // NOI18N
    public static final String CLIPBOARD_CONTENT_PARAMETER = "clipboard-content"; // NOI18N
    
    private CodeTemplateInsertRequest request;
    
    SelectionCodeTemplateProcessor(CodeTemplateInsertRequest request) {
        this.request = request;
        clipboard = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
    }
    
    public void parameterValueChanged(CodeTemplateParameter masterParameter, boolean typingChange) {
        if (!typingChange) {
            
        }
    }
    
    public void updateDefaultValues() {
        JTextComponent component = request.getComponent();
        int offset = component.getCaretPosition();
        List typeHints = new ArrayList();
        for (Iterator masterParamsIt = request.getMasterParameters().iterator(); masterParamsIt.hasNext();) {
            CodeTemplateParameter master = (CodeTemplateParameter)masterParamsIt.next();
            if (master.getName().equals(SELECTION_PARAMETER)) {
                String selectedText = component.getSelectedText();
                if (selectedText == null) {
                    master.setValue("");
                } else {
                    master.setValue(selectedText);
                }
            } else if (master.getName().equals(CLIPBOARD_CONTENT_PARAMETER)) {
                try {
                    String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);
                    if (clipboardText == null) {
                        master.setValue("");
                    } else {
                        master.setValue(clipboardText);
                    }
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                } catch (UnsupportedFlavorException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
    }
    
    public void release() {
        
    }
    
    public static final class Factory implements CodeTemplateProcessorFactory {
        public CodeTemplateProcessor createProcessor(CodeTemplateInsertRequest request) {
            return new SelectionCodeTemplateProcessor(request);
        }
    }
}

