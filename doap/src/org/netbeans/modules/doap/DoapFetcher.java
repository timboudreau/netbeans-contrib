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
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.SailInitializationException;
import org.openrdf.sail.memory.MemoryStore;

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
    private final java.net.URL url;
    MemoryStore mem;
    SailRepositoryConnection sailconn;
    final static String doap = "http://usefulinc.com/ns/doap#";
    final static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    ValueFactory vf;
    
    public DoapFetcher(java.net.URL url) {
        System.err.println("creating doap fetcher");
        this.url = url;
        try {
            org.openrdf.sail.memory.MemoryStore mem = new org.openrdf.sail.memory.MemoryStore();
            mem.initialize();
            org.openrdf.repository.sail.SailRepository sail = new org.openrdf.repository.sail.SailRepository(mem);
            sailconn = sail.getConnection();
            vf = sail.getValueFactory();
        } catch (RepositoryException ex) { //mhh. the whole thing should stop right now! So I should throw the exception...
            Exceptions.printStackTrace(ex);
        } catch (SailInitializationException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.err.println("finished creating doap fetcher");        
    }
    
    //clearly using http://sommer.dev.java.net would be a lot easier. But I need to update it for Sesame 2 beta 3
    class RepoInfo {
        Resource repo;
        String location;
        Resource type;
        ArrayList<String> modules = new ArrayList<String>(); //only for cvs repos
        String anonroot; //only for cvs repos
        
    }
    
    //HENRY:  Stuff for you:
    //  - Implement tryToGetTheDoap() to make a connection, fetch the DOAP file,
    //    parse it, and if valid, return its text (or whatever you want)
    //  - Implement createCheckoutHandler() - either create a CVS or SVN
    //    one as appropriate (code is copied from remote projects modules and mostly
    //    works), using the data d/l'd earlier in tryToGetTheDoap())
    
    private CheckoutHandler createCheckoutHandler() {
        System.err.println("using the doap file");
        java.util.ArrayList<org.netbeans.modules.doap.DoapFetcher.RepoInfo> results = new java.util.ArrayList<org.netbeans.modules.doap.DoapFetcher.RepoInfo>();
        try {
            org.openrdf.repository.RepositoryResult<org.openrdf.model.Statement> res = sailconn.getStatements(null, vf.createURI(doap, "repository"), null, false);
            org.openrdf.model.Resource repo = null;
            while (res.hasNext()) {
                repo = (org.openrdf.model.Resource) res.next().getObject();
                org.netbeans.modules.doap.DoapFetcher.RepoInfo info = new org.netbeans.modules.doap.DoapFetcher.RepoInfo();
                info.repo = repo;
                results.add(info);
            }
            for (ListIterator<RepoInfo> li = results.listIterator(); li.hasNext();) {
                RepoInfo info =li.next();
                res = sailconn.getStatements(info.repo, vf.createURI(doap, "location"), null, false);
                while (res.hasNext()) {
                    info.location = res.next().getObject().toString();
                }
                res = sailconn.getStatements(info.repo, vf.createURI(rdf, "type"), null, false);
                while (res.hasNext()) {
                    info.type = (org.openrdf.model.Resource) res.next().getObject();                     
                }
                if (info.type.equals(vf.createURI(doap,"CVSRepository"))) {
                    res = sailconn.getStatements(info.repo, vf.createURI(doap,"module"), null, false);
                    while(res.hasNext()) {
                        info.modules.add(res.next().getObject().toString());
                    }
                    res = sailconn.getStatements(info.repo, vf.createURI(doap,"anon-root"), null, false);
                    while(res.hasNext()) {
                        info.anonroot = res.next().getObject().toString();
                    }                    
                }
                if (info.location == null) li.remove();
            }
            
        } catch (RepositoryException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.err.println("found |results|="+results.size());
        for (RepoInfo info: results) {
            if (info.type.equals(vf.createURI(doap,"CVSRepository"))) { 
               File destination= askUserForDestinationDir();
               System.err.println("creating a CVSCheckoutHandler");
               System.err.println("anonroot="+info.anonroot);
               System.err.println("modules="+info.modules.get(0));
               System.err.println("destinationdir="+destination);
               return new CvsCheckoutHandler(info.anonroot,info.modules.get(0),"HEAD",destination);                
            }  
        }
        return null;
        
    }
    
    private boolean tryToGetTheDoap() {
        System.err.println("trying to get the doap at "+url);
        boolean success = false;
        try {
            assert !java.awt.EventQueue.isDispatchThread();
            sailconn.add(url, (String)null, RDFFormat.RDFXML);
            success = true;
            System.err.println("got the doap");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (RDFParseException ex) {
            Exceptions.printStackTrace(ex);
        } catch (RepositoryException ex) {
            Exceptions.printStackTrace(ex);
        } 
        return success;
    }
    
    boolean gotDoap = false;
    public void run() {
        if (!EventQueue.isDispatchThread()) {
            try {
                gotDoap = tryToGetTheDoap();
            } finally {
                if (gotDoap) {
                    EventQueue.invokeLater(this);
                }
            }
        } else {
            useTheDoapFile();
        }
    }
    
    private void useTheDoapFile() {
        assert EventQueue.isDispatchThread();
        assert sailconn != null;
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
                    RequestProcessor.getDefault().post(new CheckerOuter(handler,
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
        CheckerOuter(CheckoutHandler handler, ProgressHandle progress) {
            this.handler = handler;
            this.progress = progress;
        }
        public void run() {
            handler.checkout(progress);
        }
    }
    
}
