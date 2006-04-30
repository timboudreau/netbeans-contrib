/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.ui.platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.latex.model.platform.FilePosition;
import org.netbeans.modules.latex.model.platform.LaTeXPlatform;
import org.netbeans.modules.latex.model.platform.Viewer;
import org.openide.ErrorManager;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.MapFormat;

/**
 *
 * @author Jan Lahoda
 */
public class ProcessViewerImpl implements Viewer {
    
    private LaTeXPlatformImpl platform;
    private String            tool;
    private String            name;
    private String            displayName;
    private String[]          extensions;
    
    /** Creates a new instance of ProcessViewerImpl */
    public ProcessViewerImpl(LaTeXPlatformImpl platform, String tool, String name, String displayName, String[] extensions) {
        this.platform = platform;
        this.tool = tool;
        this.name = name;
        this.displayName = displayName;
        this.extensions = extensions;
    }

    public void show(FileObject file, FilePosition startPosition) throws NullPointerException {
        File wd = FileUtil.toFile(file.getParent());
        Map format = new HashMap();
        boolean result = true;
        
        format.put(LaTeXPlatform.ARG_INPUT_FILE_BASE, file.getName());
        
        if (LaTeXPlatform.TOOL_GV.equals(tool)) {
            FileObject ps = FileUtil.findBrother(file, "ps");
            FileObject pdf = FileUtil.findBrother(file, "pdf");
            FileObject target = ps;
            
            if (ps == null) {
                target = pdf;
            }
            
            if (ps != null && pdf != null) {
                if (pdf.lastModified().compareTo(ps.lastModified()) > 0) {
                    target = pdf;
                }
            }
            
            if (target != null) {
                format.put(LaTeXPlatform.ARG_INPUT_FILE, target.getNameExt());
            }
        }
        
        NbProcessDescriptor desc = platform.getTool(tool);
        
        run(desc, format, wd);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSupported() {
        return platform.isToolConfigured(tool);
    }

    public boolean accepts(URI uri) {
        for (String e : extensions) {
            if (uri.getPath().endsWith(e))
                return true;
        }
        
        return false;
    }
    
    static boolean run(NbProcessDescriptor descriptor, Map format, File wd) {
        try {
            Process process = descriptor.exec(new MapFormat(format), null, true, wd);
            
            CopyMaker scOut = new CopyMaker(process.getInputStream(), System.err);
            CopyMaker scErr = new CopyMaker(process.getErrorStream(), System.err);
            
            scOut.start();
            scErr.start();
            
            scOut.join();
            scErr.join();

            return process.waitFor() == 0;
        } catch (InterruptedException ex) {
            ErrorManager.getDefault().notify(ex);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }
        
        return true;
    }
    
    private static class CopyMaker extends Thread {
        
        final OutputStream out;
        final InputStream is;
        /** while set to false at streams that writes to the OutputWindow it must be
         * true for a stream that reads from the window.
         */
        final boolean autoflush;
        
        CopyMaker(InputStream is, OutputStream out) {
            this.out = out;
            this.is = is;
            autoflush = true;
        }
        
        /* Makes copy. */
        public void run() {
            try {
                int read;
                
                while ((read = is.read()) != (-1)) {
                    out.write(read);
                }
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            } finally {
                try {
                    out.flush();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
        
        
    }
    
}
