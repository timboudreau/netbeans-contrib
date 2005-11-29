/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the CVSROOT Selector (RFE #65366).
 * The Initial Developer of the Original Code is Michael Nascimento Santos.
 * Portions created by Michael Nascimento Santos are Copyright (C) 2005.
 * All Rights Reserved.
 */

package net.java.dev.cvsrootselector;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;

public class CvsRootRewriter {
    private static final String CVS_FOLDER = "CVS";
    private static final String ROOT_FILE = "Root";
    
    private final File file;
    private final String newRoot;
    private ProgressHandle handle;
    
    public CvsRootRewriter(File file, String newRoot) {
        this.file = file;
        this.newRoot = newRoot;
    }
    
    public void rewrite() {
        StatusDisplayer.getDefault().setStatusText("");
        
        try {
            final Collection toRewrite = scanForFiles();
            rewriteFolders(toRewrite);
            
            StatusDisplayer.getDefault().setStatusText("CVS Root rewritten for " +
                    toRewrite.size() + " folders");
        } catch (IOException ex) {
            StatusDisplayer.getDefault().setStatusText("CVS Root failed with: " + ex.getMessage());
        };
    }
    
    private Collection scanForFiles() throws IOException {
        Collection toRewrite = new HashSet();
        Collection toScan = new HashSet();
        toScan.add(file);
        
        handle = ProgressHandleFactory.createHandle("Rewriting roots...");
        
        int i = 0;
        int totalSize = 1;
        
        handle.start(100);
        int lastValue = 0;
        
        do {
            Collection toAdd = new HashSet();
            
            for (Iterator it = toScan.iterator(); it.hasNext(); i++) {
                File folder = (File)it.next();
                it.remove();
                
                File rootFile = getCvsRootFile(folder);
                
                if (rootFile == null) {
                    continue;
                }
                
                handle.progress("Scanning " + folder.getPath() + " ...");
                
                toRewrite.add(rootFile);
                toAdd.addAll(Arrays.asList(folder.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                })));
            }
            
            toScan.addAll(toAdd);
            totalSize += toScan.size();
            
            lastValue = Math.max(lastValue, (int)((i / (double)totalSize) * 50));
            handle.progress(lastValue);
        } while (!toScan.isEmpty());
        
        return toRewrite;
    }
    
    private void rewriteFolders(final Collection toRewrite) {
        int i = 0;
        
        for (Iterator it = toRewrite.iterator(); it.hasNext();) {
            File rootFile = (File)it.next();
            
            if (rootFile.exists() && rootFile.canWrite()) {
                PrintStream ps = null;
                
                try {
                    ps = new PrintStream(new BufferedOutputStream(
                            new FileOutputStream(rootFile)));
                    ps.println(newRoot);
                } catch (IOException ioe) {
                    notify(ioe);
                } finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
                
            }
            
            handle.progress("Rewriting " + rootFile.getPath() + "...",
                    ((int)((++i / (double)toRewrite.size()) * 50)) + 50);
        }
        
        handle.finish();
    }
    
    static File getCvsRootFile(File folder) throws IOException {
        if (folder == null || !folder.isDirectory()) {
            return null;
        }
        
        File rootFile = new File(folder, CVS_FOLDER + File.separator + ROOT_FILE);
        
        // TODO In light of issue #68881 the code should be improved to recalculate
        // CVS/Repository files (using absolute path format) too.
        
        File repositoryFile = new File(folder, CVS_FOLDER + File.separator + "Repository");
        
        if  (repositoryFile.canRead()) {
            InputStream in = null;
            try {
                in = new FileInputStream(repositoryFile);
                if (in.read() == '/') {
                    throw new IOException("#68881 Absolute CVS/Repository paths are unsupported.");
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException alreadyClosed) {
                    }
                }
            }
        }
        
        return (rootFile.exists() && !rootFile.isDirectory()) ? rootFile : null;
    }
    
    static String getCvsRoot(File folder) throws IOException {
        File rootFile = getCvsRootFile(folder);
        
        if (rootFile == null) {
            return null;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(rootFile));
            
            try {
                try {
                    return reader.readLine();
                } catch (IOException ex) {
                    notify(ex);
                    return null;
                }
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    notify(ex);
                }
            }
        } catch (FileNotFoundException ex) {
            notify(ex);
            
            return null;
        }
    }
    
    private static void notify(Throwable t) {
        ErrorManager.getDefault().notify(ErrorManager.ERROR, t);
    }
}