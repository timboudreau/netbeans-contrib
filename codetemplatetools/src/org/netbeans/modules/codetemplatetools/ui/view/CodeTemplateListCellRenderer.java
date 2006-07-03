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
package org.netbeans.modules.codetemplatetools.ui.view;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.modules.codetemplatetools.SelectionCodeTemplateProcessor;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class CodeTemplateListCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus);
        if (value instanceof CodeTemplate) {
            CodeTemplate codeTemplate = (CodeTemplate) value;
            label.setText(codeTemplate.getAbbreviation());
            if (isForSelection(codeTemplate)) {
                if (isForClipboardContent(codeTemplate)) {
                    label.setIcon(Icons.TEMPLATE_FOR_CLIPBOARDCONTENT_AND_SELECTION_ICON);
                } else {
                    label.setIcon(Icons.TEMPLATE_FOR_SELECTION_ICON);
                }
                label.setToolTipText(codeTemplate.getDescription() + " [selection]");
            } else {
                if (isForClipboardContent(codeTemplate)) {
                    label.setIcon(Icons.TEMPLATE_FOR_CLIPBOARDCONTENT_ICON);
                } else {
                    label.setIcon(Icons.TEMPLATE_ICON);
                }
                label.setToolTipText(codeTemplate.getDescription());
            }
        }
        return label;
    }
    private static String selectionParameterString = "${" + SelectionCodeTemplateProcessor.SELECTION_PARAMETER;
    private static String clipboardContentParameterString = "${" + SelectionCodeTemplateProcessor.CLIPBOARD_CONTENT_PARAMETER;
    
    private static boolean isForSelection(CodeTemplate codeTemplate) {
        return codeTemplate.getParametrizedText().indexOf(selectionParameterString) != -1;
    }
    
    private static boolean isForClipboardContent(CodeTemplate codeTemplate) {
        return codeTemplate.getParametrizedText().indexOf(clipboardContentParameterString) != -1;
    }
}
