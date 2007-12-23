package org.netbeans.modules.codetemplatetools.ui.view;

import javax.swing.AbstractListModel;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;

public class CodeTemplateListModel extends AbstractListModel {

    private CodeTemplate[] codeTemplates;

    public CodeTemplateListModel(CodeTemplate[] codeTemplates) {
        super();
        this.codeTemplates = codeTemplates;
    }

    public int getSize() {
        return codeTemplates.length;
    }

    public Object getElementAt(int index) {
        return codeTemplates[index];
    }
}
