/*
 * DumpAction.java
 *
 * Created on August 17, 2005, 12:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.folder2html;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.TopComponent;

/**
 *
 * @author Timothy Boudreau
 */
public class DumpAction extends AbstractAction implements Comparator {

    public DumpAction() {
        putValue (Action.NAME, NbBundle.getMessage(DumpAction.class, "LBL_Action"));
    }

    public void actionPerformed(ActionEvent e) {
        Node[] n = TopComponent.getRegistry().getActivatedNodes();
        if (n.length == 1) {
            DataObject ob = (DataObject) n[0].getCookie (DataObject.class);
            if (ob != null) {
                FileObject fo = ob.getPrimaryFile();
                if (fo.isFolder()) {
                    String s = proc (fo);
                    InputOutput iop = IOProvider.getDefault().getIO(fo.getPath(), false);
                    iop.select();
                    try {
                        iop.getOut().reset();
                    } catch (IOException ioe) {
                        ErrorManager.getDefault().notify (ioe);
                    }
                    iop.getOut().write(s, 0, s.length());
                    iop.getOut().close();
                }
            }
        }
    }
    
    private String proc (FileObject f) {
        StringBuffer sb = new StringBuffer();
        sb.append ("<ul>\n");
        proc (f, sb, 0);
        sb.append ("</ul>");
        return sb.toString();
    }
    
    private void proc (FileObject f, StringBuffer sb, int depth) {
        char[] c = new char[(depth + 1) * 2];
        Arrays.fill (c, ' ');
        String depthString = new String(c);
        boolean fld = f.isFolder();
        sb.append (depthString);
        sb.append ("<li><code>");
        if (fld) {
            sb.append ("<b>");
        }
        sb.append (f.getNameExt());
        if (fld) {
            sb.append ("/"); 
            sb.append ("</b>");
        }
        //Provide a place to add descriptive text
        //XXX make this optional?
        sb.append (" <font color=\"gray\"><i> </i></font>");
        
        sb.append ("</code>");
        if (fld) {
            FileObject[] kids = f.getChildren();
            if (kids.length > 0) {
                sb.append ('\n');
                sb.append (depthString);
                sb.append("<ul>\n");
                Arrays.sort (kids, this);
                for (int i=0; i < kids.length; i++) {
                    proc (kids[i], sb, depth + 1);
                }
                sb.append(depthString);
                sb.append("</ul>");
            }
        }
        sb.append ("</li>");
        sb.append ('\n');
    }

    public int compare(Object o1, Object o2) {
        FileObject f1 = (FileObject) o1;
        FileObject f2 = (FileObject) o2;
        return f1.getNameExt().compareToIgnoreCase(f2.getNameExt());
    }
    
    
    
}
