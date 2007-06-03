package org.netbeans.modules.javatextcopypaste;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.editor.java.JMIUtils;
import org.netbeans.modules.javacore.api.JavaModel;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SourceCookie;
import org.openide.nodes.Node;
import org.openide.src.DefaultElementPrinter;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;
import org.openide.windows.TopComponent;

public final class CopyAsTextNewlinesToSpaces extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        
        Node node = activatedNodes[0];
        Lookup lup = node.getLookup();
        EditorCookie c = (EditorCookie) lup.lookup(EditorCookie.class);
        //StyledDocument doc = c.getDocument();
        TopComponent luptc = new TopComponent(lup);
        TopComponent tc = luptc.getRegistry().getActivated();
        Lookup.Template query = new Lookup.Template(Object.class);
        
        Lookup.Result r = lup.lookup(query);
        Collection instances = r.allInstances();
        Iterator it = instances.iterator();
        IOProvider iop = IOProvider.getDefault();
        OutputWriter out = iop.getStdOut();
        
        JEditorPane[] panes = c.getOpenedPanes();
        JEditorPane epane = null;
        if(panes!=null&&panes.length>0){
            for(int i = 0;i<panes.length;i++){
                if(panes[i].isShowing()){
                    epane = panes[i];
                }
            }
        }
        
        if(epane!=null){
            
            int istart = epane.getSelectionStart();
            int iend = epane.getSelectionEnd();
            String text = epane.getSelectedText();
            if(text!=null||text.length()<1){
                try{
                    String newText = StringParser.convertJavaToText(text, true, false);
                    StringSelection content = new StringSelection(newText);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(content, content);
                }catch(Throwable e){
                    org.openide.ErrorManager.getDefault().notify(e);
                }
                
            }
            if(istart<0){
                int x = iend;
            }else{
                int x = iend;
            }
            SourceCookie.Editor se = (SourceCookie.Editor)lup.lookup(SourceCookie.Editor.class);
            if(se!=null){
                org.openide.src.Element e = se.findElement(istart);
                
            }
            
            Document doc = epane.getDocument();
            JavaModel.getJavaRepository().beginTrans(false);
            try {
                JMIUtils utils = JMIUtils.get((BaseDocument)doc);
                Resource resource = utils.getResource();
                JavaModel.setClassPath(resource);
                Element el = resource.getElementByOffset(istart);
                DefaultElementPrinter ep = new DefaultElementPrinter(out);
                String startElSource = el.getResource().getSourceText();
                int breakp = 0;
            } finally {
                JavaModel.getJavaRepository().endTrans();
            }
        }
        
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(CopyAsTextNewlinesToSpaces.class, "CTL_CopyAsSQLNewlinesToSpaces");
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

