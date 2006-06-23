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
package org.netbeans.modules.latex.guiproject.build;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.netbeans.modules.latex.guiproject.Utilities;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.platform.LaTeXPlatform;
import org.openide.ErrorManager;
import org.openide.LifecycleManager;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.MapFormat;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Jan Lahoda
 */
public final class BuildConfiguration {

    private static final ErrorManager ERR = ErrorManager.getDefault().getInstance(BuildConfiguration.class.getName());

    private String   name;
    private String   displayName;
    private String[] tools;
    
    /** Creates a new instance of BuildConfiguration */
    BuildConfiguration(String name, String displayName, String[] tools) {
        this.name        = name;
        this.displayName = displayName;
        this.tools       = tools;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean build(final LaTeXGUIProject p, final InputOutput inout) {
        if (getErrorIfAny(p) != null)
            throw new IllegalArgumentException();
        
        FileObject file = (FileObject) p.getSource().getMainFile();
        File wd = FileUtil.toFile(file.getParent());
        LaTeXPlatform platform = Utilities.getPlatform(p);
        Map format = new HashMap();
        boolean result = true;
        
        format.put(LaTeXPlatform.ARG_INPUT_FILE_BASE, file.getName());
        
        for (String tool : tools) {
            NbProcessDescriptor desc = platform.getTool(tool);
            
            if (LaTeXPlatform.TOOL_LATEX.equals(tool)) {
                result &= runLaTeX(p, file, format, wd, inout);
            } else {
                result &= BuildConfiguration.run(desc, format, wd, inout.getOut(), inout.getErr());
            }
            
            if (!result) {
                break;
            }
        }
        
        file.getParent().refresh(false);
        
        for (FileObject child : file.getChildren()) {
            child.refresh();
        }
        
        return result;
    }


    private boolean runLaTeX(LaTeXGUIProject p, FileObject mainFile, Map format, File wd, InputOutput inout) {
        boolean forceReparse = false;
        LaTeXPlatform platform = Utilities.getPlatform(p);
        NbProcessDescriptor latex  = platform.getTool(LaTeXPlatform.TOOL_LATEX);
        NbProcessDescriptor bibtex = platform.getTool(LaTeXPlatform.TOOL_BIBTEX);
        
        for (int pass = 0; pass < 3; pass++) {
            boolean doLatex = true;
            
//            if (isUpToDate()) {
//                if (pass == 0) {
//                    //The first pass is assured always, but there is no point in latexing...
//                    ERR.log(ErrorManager.INFORMATIONAL, "Up-to-date mainfile, first pass, included files not checked, latexing forced.");
//                    doLatex = true;
//                } else {
//                    if (forceReparse) {
//                        ERR.log(ErrorManager.INFORMATIONAL, "Up-to-date, forceReparse == true.");
//                        doLatex = true;
//                    } else {
//                        ERR.log(ErrorManager.INFORMATIONAL, "Up-to-date, no latexing, exit");
//                        break;
//                    }
//                }
//            }
            
            if (doLatex) {
                ERR.log(ErrorManager.INFORMATIONAL, "LaTeXing, mainfile:" + mainFile);
                
                boolean result = BuildConfiguration.run(latex, format, wd, inout.getOut(), inout.getErr());

                if (!result)
                    return false;
            }
            
            if (pass == 0) {
                if (shouldRunBiBTeX(p)) {
                    ERR.log(ErrorManager.INFORMATIONAL, "Running bibtex tasks.");
                    
                    boolean result = BuildConfiguration.run(bibtex, format, wd, inout.getOut(), inout.getErr());
                    
//                    if (!result)
//                        return false;
                }
            }
            
            if (pass == 1) //TODO: maybe not necessary, need to parse it from the output...
                forceReparse = true;
        }

        return true;
    }

    private boolean shouldRunBiBTeX(LaTeXGUIProject p) {
        switch (ProjectSettings.getDefault(p).getBiBTeXRunType()) {
            case YES:
                return true;
            case NO:
                return false;
            case AUTO:
            default:
                final boolean [] result = new boolean[1];
                p.getSource().traverse(new DefaultTraverseHandler() {
                    public boolean commandStart(CommandNode node) {
                        if ("\\bibliography".equals(node.getCommand().getCommand())) {
                            result[0] = true;
                            return false;
                        }
                        
                        return true;
                    }
                }, LaTeXSource.DOCUMENT_SHOULD_EXIST_LOCK);
                return result[0];
        }
    }
    
    public boolean clean(final LaTeXGUIProject p, final InputOutput inout) {
        if (getErrorIfAny(p) != null)
            throw new IllegalArgumentException();

        FileObject file = (FileObject) p.getSource().getMainFile();
        LaTeXPlatform platform = Utilities.getPlatform(p);
        List<URI> targets = new ArrayList<URI>();
        
        for (String tool : tools) {
            targets.addAll(platform.getTargetFiles(tool, file));
        }
        
        for (URI u : targets) {
            try {
                FileObject f = URLMapper.findFileObject(u.toURL());
                
                inout.getOut().println("Going to delete: " + FileUtil.getFileDisplayName(f));
                
//                    f.delete();
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        
        return true;
    }

    public String getErrorIfAny(LaTeXGUIProject p) {
        LaTeXPlatform platform = Utilities.getPlatform(p);
        
        for (String tool : tools) {
            if (!platform.isToolConfigured(tool))
                return NbBundle.getMessage(BuildConfiguration.class, "LBL_ToolNotConfigured", new Object[] {String.valueOf(tool)});
        }
        
        return null;
    }

    public boolean isSupported(LaTeXGUIProject p) {
        return getErrorIfAny(p) == null;
    }
    
    static boolean run(NbProcessDescriptor descriptor, Map format, File wd, OutputWriter stdOut, OutputWriter stdErr) {
        try {
            Process process = descriptor.exec(new MapFormat(format), null, true, wd);
            
            LaTeXCopyMaker scOut = new LaTeXCopyMaker(wd, process.getInputStream(), stdOut);
            LaTeXCopyMaker scErr = new LaTeXCopyMaker(wd, process.getErrorStream(), stdErr);
            
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

    String[] getTools() {
        return tools;
    }
}
