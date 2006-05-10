package org.netbeans.modules.toolsintegration;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;


public class ExternalToolDataObject extends MultiDataObject {
    
    public ExternalToolDataObject (FileObject pf, ExternalToolDataLoader loader) throws DataObjectExistsException, IOException {
        super (pf, loader);
        CookieSet cookies = getCookieSet ();
        cookies.add ((Node.Cookie) DataEditorSupport.create (this, getPrimaryEntry (), cookies));
        cookies.add (new InstanceCookie () {
            public Class instanceClass () {
                return Action.class;
            }
            public Object instanceCreate () {
                return new ETAction ();
            }
            public String instanceName () {
                return "???";
            }
        });
    }
    
    protected Node createNodeDelegate () {
        return new ExternalToolDataNode (this);
    }
    
    class ETAction extends AbstractAction {
        
        ETAction () {
            ExternalTool tool = Model.read (getPrimaryFile ());
            putValue (NAME, tool.getName ());
        }
        
        public void actionPerformed (ActionEvent e) {
            ExternalTool tool = Model.read (getPrimaryFile ());
            tool.exec ();
        }
    }
}
