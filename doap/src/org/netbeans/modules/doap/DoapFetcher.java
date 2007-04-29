/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */
package org.netbeans.modules.doap;

import java.awt.EventQueue;
import java.awt.Frame;
import java.io.File;
import java.net.URL;
import javax.swing.JFileChooser;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Fetches a DOAP file from a dropped URL, and if the user agrees, checks
 * it out from CVS or SVN.
 * 
 * The dropped URL is parsed and its resource downloaded on a background
 * thread;  if successful, on the event thread we ask the user if the project
 * should be downloaded/opened;  if so, we ask them to choose a destination 
 * directory;  if they choose one, we then go back to a background thread
 * to actually pull the content down from SVN or CVS.
 *
 * @author Tim Boudreau
 */
final class DoapFetcher implements Runnable {
    private final URL url;
    private CharSequence content;
    public DoapFetcher(URL url) {
        this.url = url;
    }
    
    //HENRY:  Stuff for you:
    //  - Implement tryToGetTheDoap() to make a connection, fetch the DOAP file,
    //    parse it, and if valid, return its text (or whatever you want)
    //  - Implement createCheckoutHandler() - either create a CVS or SVN
    //    one as appropriate (code is copied from remote projects modules and mostly
    //    works), using the data d/l'd earlier in tryToGetTheDoap())

    private CheckoutHandler createCheckoutHandler() {
        //PENDING figure out if it is CVS or SVN, get the appropriate parameters and
        //create either a CvsCheckoutHandler or an SvnCheckoutHandler.  If we
        //got a DOAP file, the content ivar will contain its text, and so we
        //just need to decide if it is cvs or svn, and if so, create the 
        //appropriate handler and return it
        return null;
    }
    
    private CharSequence tryToGetTheDoap() {
        assert !EventQueue.isDispatchThread();
        //PENDING:  Fetch the content, parse it, if it's really a DOAP file store
        //it to the content ivar.  This method will be called on a background
        //thread
        return null;
    }
    
    
    
    public void run() {
        if (!EventQueue.isDispatchThread()) {
            try {
                content = tryToGetTheDoap();
            } finally {
                if (content != null) {
                    EventQueue.invokeLater (this);
                }
            }
        } else {
            useTheDoapFile();
        }
    }
    
    private void useTheDoapFile() {
        assert EventQueue.isDispatchThread();
        assert content != null;
        //Ask the user the project sources should be downloaded, etc.
        //if they say yes
        boolean userSaidYes = askUserIfProjectShouldBeOpened();
        if (userSaidYes) {
            File f = askUserForDestinationDir();
            if (f != null) {
                CheckoutHandler handler = createCheckoutHandler();
                if (handler != null) {
                    ProgressHandle progress = ProgressHandleFactory.createHandle(
                            NbBundle.getMessage(DoapFetcher.class,
                            "MSG_DOWNLOADING")); //NOI18N

                    //Run the checkout on a background thread
                    RequestProcessor.getDefault().post (new CheckerOuter (handler, 
                            progress));
                } else {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(
                            DoapFetcher.class, "LBL_CANT_CHECKOUT")); //NOI18N                        
                }
            }
        }
    }
    
    private boolean askUserIfProjectShouldBeOpened() {
        //Show a dialog asking if the project should be opened
        NotifyDescriptor.Message md = new NotifyDescriptor.Message(NbBundle.getMessage(
                DoapFetcher.class, "LBL_FETCH", url), NotifyDescriptor.YES_NO_OPTION); //NOI18N
        return (NotifyDescriptor.YES_OPTION.equals(DialogDisplayer.getDefault().notify(md)));
    }
    
    private File askUserForDestinationDir() {
        JFileChooser jfc = new JFileChooser();
        jfc.setMultiSelectionEnabled(false);
        jfc.setDialogTitle(NbBundle.getMessage(DoapFetcher.class, 
                "TTL_DEST_DIR")); //NOI18N
        jfc.setFileSelectionMode(jfc.DIRECTORIES_ONLY);
        File result = null;
        if (jfc.showOpenDialog(Frame.getFrames()[0]) == jfc.APPROVE_OPTION) {
            result = jfc.getSelectedFile();
        }
        return result;
    }
    
    private static class CheckerOuter implements Runnable {
        private final CheckoutHandler handler;
        private final ProgressHandle progress;
        CheckerOuter (CheckoutHandler handler, ProgressHandle progress) {
            this.handler = handler;
            this.progress = progress;
        }
        public void run() {
            handler.checkout (progress);
        }
    }
    
}
