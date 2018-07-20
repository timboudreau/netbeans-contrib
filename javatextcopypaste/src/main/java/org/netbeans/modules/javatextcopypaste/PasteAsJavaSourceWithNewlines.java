package org.netbeans.modules.javatextcopypaste;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.StringReader;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Formatter;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;

public final class PasteAsJavaSourceWithNewlines extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        Node node = activatedNodes[0];
        
        EditorCookie c = (EditorCookie) node.getCookie(EditorCookie.class);
        Lookup lup = node.getLookup();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable content = clipboard.getContents(this);
        IOProvider iop = IOProvider.getDefault();
        OutputWriter out = iop.getStdOut();
        
        
        
        if(content.isDataFlavorSupported(DataFlavor.stringFlavor)){
            try{
                String realc = (String)content.getTransferData(DataFlavor.stringFlavor);
                realc = StringParser.convertTextToJava(realc, false);
                JEditorPane[] panes = c.getOpenedPanes();
                for(int i = 0; i<panes.length;i++){
                    if(panes[i].isShowing()){
                        if(panes[i].getSelectedText()!=null&&panes[i].getSelectedText().length()>0){
                            Action[] actions = panes[i].getActions();
                            for(int j=0;actions!=null&&j<actions.length;j++){
                                String className = actions[j].getClass().getName();
                                out.println(className);
                                if(className.contains("DeleteCharAction")){
                                    actions[j].actionPerformed(new ActionEvent(panes[i], 1, KeyEvent.getKeyText(KeyEvent.VK_DELETE)));
                                    break;
                                }
                            }
                        }
                        int pos = panes[i].getCaretPosition();
                        StringReader sr = new StringReader(realc);
                        Document doc = panes[i].getDocument();
                        if(doc instanceof BaseDocument){
                            ((BaseDocument)doc).atomicLock();
                        }
                        try{
                            panes[i].getEditorKit().read(sr, doc, pos);
                            int endpos = pos+realc.length();
                            Formatter formatter = Formatter.getFormatter(panes[i].getEditorKit().getClass());
                            formatter.reformat((BaseDocument) doc, pos, endpos);
                        }catch(Throwable e){
                            if(doc instanceof BaseDocument){
                                ((BaseDocument)doc).atomicUndo();
                            }
                            throw e;
                        }finally{
                            if(doc instanceof BaseDocument){
                                ((BaseDocument)doc).atomicUnlock();
                            }
                        }
                        break;
                    }
                }
            }catch(Throwable e){
                ErrorManager.getDefault().notify(e);
            }
            
        }
        
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(PasteAsJavaSourceWithNewlines.class, "CTL_PasteAsJavaSourceWithNewlines");
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

