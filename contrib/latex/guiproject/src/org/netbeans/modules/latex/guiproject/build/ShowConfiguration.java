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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.netbeans.modules.latex.guiproject.Utilities;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;
import org.netbeans.modules.latex.model.platform.LaTeXPlatform;
import org.openide.ErrorManager;
import org.openide.LifecycleManager;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.MapFormat;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Jan Lahoda
 */
public final class ShowConfiguration {

    private static final ErrorManager ERR = ErrorManager.getDefault().getInstance(BuildConfiguration.class.getName());

    private String   name;
    private String   displayName;
    private String   tool;
    
    /** Creates a new instance of BuildConfiguration */
    ShowConfiguration(String name, String displayName, String tool) {
        this.name        = name;
        this.displayName = displayName;
        this.tool        = tool;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean build(final LaTeXGUIProject p, final InputOutput inout) {
        if (!isSupported(p))
            throw new IllegalArgumentException();
        
        FileObject file = (FileObject) p.getSource().getMainFile();
        File wd = FileUtil.toFile(file.getParent());
        LaTeXPlatform platform = Utilities.getPlatform(p);
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
        
        return BuildConfiguration.run(desc, format, wd, inout.getOut(), inout.getErr());
    }

    public boolean isSupported(LaTeXGUIProject p) {
        String buildConfiguration = ProjectSettings.getDefault(p).getBuildConfigurationName();
        BuildConfiguration conf = BuildConfigurationProvider.getDefault().getBuildConfiguration(buildConfiguration);

        return isSupported(p, conf);
    }

    public boolean isSupported(LaTeXGUIProject p, BuildConfiguration conf) {
        LaTeXPlatform platform = Utilities.getPlatform(p);
        
        if (!platform.isToolConfigured(tool))
            return false;

        if (conf == null)
            return false;

        if (LaTeXPlatform.TOOL_XDVI.equals(tool)) {
            return Arrays.asList(conf.getTools()).contains(LaTeXPlatform.TOOL_LATEX);
        }

        if (LaTeXPlatform.TOOL_GV.equals(tool)) {
            List<String> tools = new ArrayList(Arrays.asList(conf.getTools()));

            tools.retainAll(Arrays.asList(LaTeXPlatform.TOOL_DVIPDF, LaTeXPlatform.TOOL_DVIPS, LaTeXPlatform.TOOL_PS2PDF));

            return !tools.isEmpty();
        }

        return false;
    }
    
}
