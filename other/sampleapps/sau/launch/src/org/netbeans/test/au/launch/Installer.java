package org.netbeans.test.au.launch;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.LifecycleManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.modules.ModuleInstall;

/**
 */
public class Installer extends ModuleInstall implements Runnable {
    Action action;
    
    
    public void restored() {
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Actions/System/org-netbeans-modules-autoupdate-ui-actions-PluginManagerAction.instance");
            assert fo != null;
            DataObject obj = DataObject.find(fo);
            InstanceCookie ic = obj.getLookup().lookup(InstanceCookie.class);
            action = (Action)ic.instanceCreate();
            EventQueue.invokeLater(this);
        } catch (Exception ex) {
            Logger.getLogger(Installer.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
        }        
        
        
    }

    public void run() {
        action.actionPerformed(null);
        LifecycleManager.getDefault().exit();
    }
}
