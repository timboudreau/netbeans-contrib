/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 */
package org.netbeans.modules.doap;

import java.awt.EventQueue;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ListIterator;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;
import javax.swing.JFileChooser;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
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
            TurtleWriter wr = new TurtleWriter(System.out);
            sailconn.export(wr);
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
                } else  if (info.type.equals(vf.createURI(doap,"SVNRepository"))) {
                    res = sailconn.getStatements(info.repo, vf.createURI(doap,"module"), null, false);
                    while(res.hasNext()) {
                        info.modules.add(res.next().getObject().toString());
                    }
                    res = sailconn.getStatements(info.repo, vf.createURI(doap,"location"), null, false);
                    while(res.hasNext()) {
                        info.location = res.next().getObject().toString();
                    }
                }
                if (info.location == null) li.remove();
            }
            
        } catch (RepositoryException ex) {
            Exceptions.printStackTrace(ex);
        } catch (RDFHandlerException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        System.err.println("found |results|="+results.size());
        for (RepoInfo info: results) {
            if (info.type.equals(vf.createURI(doap,"CVSRepository"))) {
                System.err.println("creating a CVSCheckoutHandler");
                System.err.println("anonroot="+info.anonroot);
                String module =null;
                if (info.modules != null && info.modules.size() > 0) {
                    module = info.modules.get(0);
                    System.err.println("module="+module);
                }
                System.err.println("destinationdir="+destination);
                return new CvsCheckoutHandler(info.anonroot,info.modules.get(0),"HEAD",destination);
            } else  if (info.type.equals(vf.createURI(doap,"SVNRepository"))) {
                System.err.println("creating a CVSCheckoutHandler");
                System.err.println("anonroot="+info.anonroot);
                if (info.modules != null && info.modules.size() > 0) {
                    System.err.println("modules="+info.modules.get(0));
                }
                System.err.println("destinationdir="+destination);
                //todo: the svn checkout handler should allow more than one module to be checked out...
                SvnCheckoutHandler h = new SvnCheckoutHandler(info.location, null, null, info.modules,destination);
                
                return h;
            }
        }
        return null;
        
    }
    
    private boolean tryToGetTheDoap() {
        System.err.println("trying to get the doap at "+url);
        boolean success = false;
        try {
            assert !java.awt.EventQueue.isDispatchThread();
            //there is a bug in rio: it does not parse rdf files that are not wrapped with <RDF ...></RDF>
            //sailconn.add(url, (String)null, RDFFormat.RDFXML);
            URLConnection conn = url.openConnection();
            conn.connect();
            //todo: should get the content encoding
            RDFXMLParser parser = new RDFXMLParser();
            parser.setParseStandAloneDocuments(true);
            RDFInserter inserter = new RDFInserter(sailconn);

            parser.setRDFHandler(inserter);

            parser.setVerifyData(true);
            parser.setStopAtFirstError(true);
            parser.setDatatypeHandling(RDFParser.DatatypeHandling.VERIFY);

            InputStream str = conn.getInputStream();
            URL url = conn.getURL();
            String base = conn.getURL().toString().substring(0,url.toString().length()-url.getFile().length());
            parser.parse(str, base);

            success = !sailconn.isEmpty();
            if (success)
                System.err.println("got the doap.");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (RDFHandlerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (RDFParseException ex) {
            NotifyDescriptor.Message msg = new NotifyDescriptor.Message(
                    NbBundle.getMessage(DoapFetcher.class, "MSG_BAD_DOPE"));
            DialogDisplayer.getDefault().notify(msg);
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
    
    File destination;
    private void useTheDoapFile() {
        assert EventQueue.isDispatchThread();
        assert sailconn != null;
        //Ask the user the project sources should be downloaded, etc.
        //if they say yes
        boolean userSaidYes = askUserIfProjectShouldBeOpened();
        if (userSaidYes) {
            destination = askUserForDestinationDir();
            if (destination != null) {
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
        String dir = NbPreferences.forModule(DoapFetcher.class).get("destdir", null);
        if (dir != null) {
            File f = new File(dir);
            if (f.exists() && f.isDirectory()) {
                jfc.setSelectedFile(f);
            }
        }
        File result = null;
        if (jfc.showOpenDialog(Frame.getFrames()[0]) == jfc.APPROVE_OPTION) {
            result = jfc.getSelectedFile();
            NbPreferences.forModule(DoapFetcher.class).put("destdir", result.getPath());
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
