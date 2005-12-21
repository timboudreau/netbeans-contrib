/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.codetemplatetools;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.JTextComponent;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateInsertRequest;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateParameter;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateProcessor;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateProcessorFactory;
import org.openide.ErrorManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class SelectionCodeTemplateProcessor implements CodeTemplateProcessor {
    
    public static final String SELECTION_PARAMETER         = "selection"; // NOI18N
    public static final String CLIPBOARD_CONTENT_PARAMETER = "clipboard-content"; // NOI18N
    
    private CodeTemplateInsertRequest request;
    
    SelectionCodeTemplateProcessor(CodeTemplateInsertRequest request) {
        this.request = request;
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
                //
            }
        } // for
    }
    
    public void release() {
        
    }
    
    public static final class Factory implements CodeTemplateProcessorFactory {
        public CodeTemplateProcessor createProcessor(CodeTemplateInsertRequest request) {
            return new SelectionCodeTemplateProcessor(request);
        }
    }
}

