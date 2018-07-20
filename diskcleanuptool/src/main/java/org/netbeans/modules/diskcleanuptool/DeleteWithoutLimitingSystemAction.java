package org.netbeans.modules.diskcleanuptool;

import java.io.File;
import java.io.PrintWriter;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;

public final class DeleteWithoutLimitingSystemAction extends CookieAction {
    
    private static class DeleteRunnable implements Runnable {
        Node[] nodes = null;
        public DeleteRunnable(Node[] activatedNodes){
            nodes = activatedNodes;
        }
        
        public void run(){
            IndeterminateProgressHandleImpl rph = null;
            DiskUtilities.LongWrapper delCounter = new DiskUtilities.LongWrapper();
            try{
                //we need to loop over all the nodes and get the data objects and delete them
                DiskUtilities.BooleanWrapper cancelled = new DiskUtilities.BooleanWrapper();
                ProgressHandle ph = ProgressHandleFactory.createHandle("Deleting directory(ies) and files", new BooleanWrapperCancellable(cancelled));
                rph = new IndeterminateProgressHandleImpl(ph);
                if(nodes!=null){
                    rph.start();
                    for(int i = 0; !cancelled.getValue()&&i<nodes.length;i++){
                        DataObject o = (DataObject)nodes[i].getLookup().lookup(DataObject.class);
                        FileObject fo = o.getPrimaryFile();
                        if(fo.isFolder()){
                            
                            File dir = FileUtil.toFile(fo);//new File(fo.getPath());
                            dir=dir.getCanonicalFile().getAbsoluteFile();
                            if(!dir.exists()){
                                continue;
                            }
                            DiskUtilities.deleteFiles(dir, rph, delCounter, cancelled, 5, 50);
                        }
                    }
                }
            }catch(Throwable e){
                OutputWriter io = IOProvider.getDefault().getStdOut();
                PrintWriter pw = new PrintWriter(io);
                pw.println("There was an error deleting the files:");
                e.printStackTrace(pw);
                pw.flush();
                pw = null;
            }finally{
                rph.finish();
                OutputWriter io = IOProvider.getDefault().getStdOut();
                PrintWriter pw = new PrintWriter(io);
                pw.println("Successfully deleted " + delCounter.getValue() + " files and directories.");
                pw.flush();
                pw = null;
            }
        }
    }
    
    protected void performAction(Node[] activatedNodes) {
        NotifyDescriptor d =
                new NotifyDescriptor.Confirmation("Are you absolutely sure you want to delete these directories and files?!", "Really delete these directories and files?",
                NotifyDescriptor.YES_NO_OPTION);
        DialogDisplayer.getDefault().notify(d);
        if(d.getValue().equals(d.YES_OPTION)){
            DeleteRunnable dr = new DeleteRunnable(activatedNodes);
            Thread th = new Thread(dr);
            th.setDaemon(true);
            th.start();
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_ALL;
    }
    
    public String getName() {
        return NbBundle.getMessage(DeleteWithoutLimitingSystemAction.class, "CTL_DeleteWithoutLimitingSystemAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
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

