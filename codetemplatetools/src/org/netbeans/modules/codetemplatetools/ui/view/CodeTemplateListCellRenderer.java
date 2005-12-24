/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
                label.setIcon(Icons.TEMPLATE_FOR_SELECTION_ICON);
                label.setToolTipText(codeTemplate.getDescription() + " [selection]");
            } else {
                label.setIcon(Icons.TEMPLATE_ICON);
                label.setToolTipText(codeTemplate.getDescription());
            }
        }
        return label;
    }
    private static String selectionParameterString = "${" + SelectionCodeTemplateProcessor.SELECTION_PARAMETER;
    
    private static boolean isForSelection(CodeTemplate codeTemplate) {
        return codeTemplate.getParametrizedText().indexOf(selectionParameterString) != -1;
    }
}