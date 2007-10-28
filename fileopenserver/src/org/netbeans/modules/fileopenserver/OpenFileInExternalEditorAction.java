/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.fileopenserver;

import javax.swing.text.StyledDocument;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class OpenFileInExternalEditorAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        if (dataObject != null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject.isValid() && fileObject.isData()) {
                EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
                if (editorCookie != null) {
                    StyledDocument styledDocument = editorCookie.getDocument();
                    int caretPosition = editorCookie.getOpenedPanes()[0].getCaretPosition();
                    int lineNumber = NbDocument.findLineNumber(styledDocument, caretPosition);
                    int columnNumber = NbDocument.findLineColumn(styledDocument, caretPosition);
                    openFile(FileUtil.toFile(fileObject).getAbsolutePath(), lineNumber, columnNumber);
                }
            }           
        }    
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(OpenFileInExternalEditorAction.class, "CTL_OpenFileInExternalEditorAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{
            DataObject.class,
            EditorCookie.class,
        };
    }

    @Override
    protected String iconResource() {
        return "org/netbeans/modules/fileopenserver/resources/OpenFileInExternalEditorIcon.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    /**
     * Opens a system editor on the given file resource.
     *
     * @param file the file resource
     */
    void openFile(String filePath, int line, int col) {
        FileOpenRequestEvent fileOpenRequestEvent = new FileOpenRequestEvent(
                this, filePath, line, col, true);
        FileOpenServer.getFileOpenServer().fireFileOpenRequestEvent(fileOpenRequestEvent);
    }
}

