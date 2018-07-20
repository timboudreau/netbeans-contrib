package org.netbeans.modules.codetemplatetools.actions;

import java.awt.Toolkit;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

public final class ExpandTemplateAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
        if (editorCookie != null) {
            JEditorPane[] panes = editorCookie.getOpenedPanes();
            if (panes != null) {
                TopComponent activetc = TopComponent.getRegistry().getActivated();
                for (int i = 0; i < panes.length; i++) {
                    if (activetc.isAncestorOf(panes[i])) {
                        expandTemplateAtCaret(panes[i]);
                        break;
                    }
                }
            }
        }
    }

    private void expandTemplateAtCaret(JEditorPane editorPane) {
        if (editorPane.isEditable()) {
            int caretPosition = editorPane.getCaretPosition();
            int wordStart;
            try {
                wordStart = Utilities.getWordStart(editorPane, caretPosition);
                String abbrev = editorPane.getText(wordStart, (caretPosition - wordStart));
                Document doc = editorPane.getDocument();
                CodeTemplateManager codeTemplateManager = CodeTemplateManager.get(doc);
                for (CodeTemplate codeTemplate : codeTemplateManager.getCodeTemplates()) {
                    if (codeTemplate.getAbbreviation().equals(abbrev)) {
                        if (doc instanceof BaseDocument) {
                            ((BaseDocument)doc).atomicLock();
                        }
                        try {
                            doc.remove(wordStart, (caretPosition - wordStart));
                            codeTemplate.insert(editorPane);
                        } finally {
                            if (doc instanceof BaseDocument) {
                                ((BaseDocument)doc).atomicUnlock();
                            }
                        }
                        break;
                    }
                }
                
            } catch (BadLocationException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }  
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ExpandTemplateAction.class, "CTL_ExpandTemplateAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}

