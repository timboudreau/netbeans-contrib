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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.codetemplatetools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateInsertRequest;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateParameter;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateProcessor;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateProcessorFactory;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class SelectionCodeTemplateProcessor implements CodeTemplateProcessor {
    private Clipboard clipboard;
    
    public static final String SELECTION_PARAMETER         = "selection"; // NOI18N
    public static final String CLIPBOARD_CONTENT_PARAMETER = "clipboard-content"; // NOI18N
    public static final String INPUT_PARAMETER_PREFIX      = "input-"; // NOI18N
    public static final String INPUT_PARAMETER_VALUE_SEPARATOR = ","; // NOI18N
    
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
            String parameterName = master.getName();
            if (parameterName.equals(SELECTION_PARAMETER)) {
                String selectedText = component.getSelectedText();
                if (selectedText == null) {
                    master.setValue("");
                } else {
                    master.setValue(selectedText);
                }
            } else if (parameterName.equals(CLIPBOARD_CONTENT_PARAMETER)) {
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
            } else if (parameterName.startsWith(INPUT_PARAMETER_PREFIX)) {
                boolean useValues = false;
                String promptLabel = parameterName.substring(INPUT_PARAMETER_PREFIX.length()) + " :";
                String defaultValue = parameterName.substring(INPUT_PARAMETER_PREFIX.length());
                String[] values = null;
                boolean enumeratedValues = false;
                String valuesSeparator = ",";
                Map hints = master.getHints();
                if (hints != null) {
                    String promptLabelHint = (String) hints.get("prompt"); // NOI18N
                    if (promptLabelHint != null) {
                        promptLabel = promptLabelHint;
                    }
                    String defaultValueHint = (String) hints.get("defaultValue"); // NOI18N
                    if (defaultValueHint != null) {
                        defaultValue = defaultValueHint;
                    }
                    
                    String valuesSeparatorHint = (String) hints.get("valuesSeparator"); // NOI18N
                    if (valuesSeparatorHint != null) {
                        valuesSeparator = valuesSeparatorHint;
                    }
                    
                    String valuesHint = (String) hints.get("suggestedValues"); // NOI18N
                    if (valuesHint != null) {
                        values = valuesHint.split(valuesSeparator);
                    } else {
                        valuesHint = (String) hints.get("enumeratedValues"); // NOI18N
                        if (valuesHint != null) {
                            enumeratedValues = true;
                            values = valuesHint.split(valuesSeparator);
                        }
                    }
                }
                if (values == null) {
                    JTextField value = new JTextField(defaultValue.length() + 10);
                    value.setText(defaultValue);
                    if (JOptionPane.showConfirmDialog(
                            WindowManager.getDefault().getMainWindow(),
                            new Object[] {promptLabel, value},
                            NbBundle.getMessage(SelectionCodeTemplateProcessor.class,
                            "InputParameterDialogTitle", // NOI18N
                            promptLabel),
                            JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        master.setValue(value.getText());
                    }
                } else {
                    JComboBox possibleValues = new JComboBox(values);
                    possibleValues.setEditable(!enumeratedValues);
                    possibleValues.setSelectedItem(defaultValue);
                    if (JOptionPane.showConfirmDialog(
                            WindowManager.getDefault().getMainWindow(),
                            new Object[] {promptLabel, possibleValues},
                            NbBundle.getMessage(SelectionCodeTemplateProcessor.class,
                            "InputParameterDialogTitle", // NOI18N
                            promptLabel),
                            JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        master.setValue((String) possibleValues.getSelectedItem());
                    }
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
