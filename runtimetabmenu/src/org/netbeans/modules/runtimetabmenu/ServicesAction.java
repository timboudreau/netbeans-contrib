package org.netbeans.modules.runtimetabmenu;

import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;

public final class ServicesAction extends CallableSystemAction implements Presenter.Menu {
    
    public void performAction() {
        // TODO implement action body
    }
    
    public String getName() {
        return NbBundle.getMessage(ServicesAction.class, "CTL_ServicesAction");
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
    
    public JMenuItem getMenuPresenter() {
        JMenu result = new JMenu();
        result.setText (getName());
        FileObject fld = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("UI/Runtime");
        if (fld != null) {
            try {
                DataObject dob = DataObject.find (fld);
                ImageIcon icon = new ImageIcon (dob.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16));
                result.setIcon(icon);
                FileObject[] kids = fld.getChildren();
                for (int i = 0; i < kids.length; i++) {
                    DataObject d = DataObject.find (kids[i]);
                    Node n = d.getNodeDelegate();
                    icon = new ImageIcon (d.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16));
                    JMenuItem item = new JMenuItem ();
                    item.setAction (new TCAction (kids[i].getPath()));
                    item.setIcon (icon);
                    item.setText (n.getDisplayName());
                    result.add (item);
                }

            } catch (DataObjectNotFoundException e) {
                Exceptions.printStackTrace(e);
            }
            
        }
        return result;
    }
    
    static Map <String, WeakReference <TopComponent>> cache = new HashMap
            <String, WeakReference <TopComponent>> ();
    
    private static final class TCAction extends AbstractAction {
        private final String path;
        TCAction (String path) {
            //Only retain the path to the FileObject we will show, so we
            //don't leak Nodes that don't need to be held in memory
            this.path = path;
        }

        public void actionPerformed(ActionEvent e) {
            TopComponent result = null;
            WeakReference <TopComponent> ref = cache.get(path);
            if (ref != null) {
                result = ref.get();
            }
            if (result == null) {
                result = new RuntimeNodeTC(path);
                cache.put (path, new WeakReference<TopComponent>(result));
            }
            result.open();
            result.requestActive();
        }
    }
    
}
