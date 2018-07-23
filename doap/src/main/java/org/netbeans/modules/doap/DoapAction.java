package org.netbeans.modules.doap;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JLabel;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;

public final class DoapAction extends CallableSystemAction implements Presenter.Toolbar, DropTargetListener {

    public DoapAction () {
        putValue (NAME, getName());
        System.err.println("CREATING AN INSTANCE");
    }
    
    public String getName() {
        return NbBundle.getMessage(DoapAction.class, "CTL_DoapAction"); //NOI18N        
    }
    
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException();
    }

    JLabel jb;
    public Component getToolbarPresenter() {
        System.err.println("get toolbar presenter");
        if (jb == null) {
            System.err.println("\n\nCreating button\n\n");
            jb = new JLabel ("    DOAP     ");
            DropTarget dt = new DropTarget(jb, this);
            jb.setMinimumSize (new Dimension (300, 16));
//            jb.setBackground (Color.BLUE);
//            jb.setPreferredSize (new Dimension (300, 20));
//            dt.addDropTargetListener(this);
        }
        return jb;
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void dragOver(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void drop(DropTargetDropEvent dtde) {
        Transferable t = dtde.getTransferable();
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        DataFlavor[] d = t.getTransferDataFlavors();
        for (int i = 0; i < d.length; i++) {
            System.err.println("Type: '" + d[i].getMimeType() + " name " + d[i].getHumanPresentableName());
        }
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try         {
                String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                System.err.println("\nTHE TRANSFER DATA:\n" + s);
                URL url = new URL (s);
                System.err.println("Created a url: " + url);
                RequestProcessor.getDefault().post (new DoapFetcher(url));
            } catch (MalformedURLException mre) {
            } catch (UnsupportedFlavorException ex) {
            } catch (IOException ex) {
            }
        }
    }

    public void performAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
