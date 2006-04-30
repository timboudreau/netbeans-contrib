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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.netbeans.modules.latex.guiproject.Utilities;
import org.netbeans.modules.latex.model.platform.LaTeXPlatform;
import org.netbeans.modules.latex.model.platform.Viewer;

/**
 *
 * @author Jan Lahoda
 */
public final class BuildConfigurationProvider {
    
    private List<BuildConfiguration> configurations;
    private List<ShowConfiguration> showConfigurations;

    private LaTeXGUIProject project;
    
    /** Creates a new instance of BuildConfigurationProvider */
    public BuildConfigurationProvider(LaTeXGUIProject project) {
        this.project = project;
        configurations = new ArrayList();
        configurations.add(new BuildConfiguration("latex", "latex", new String[] {
            LaTeXPlatform.TOOL_LATEX,
        }));
        configurations.add(new BuildConfiguration("latexdvips", "latex->dvips", new String[] {
            LaTeXPlatform.TOOL_LATEX,
            LaTeXPlatform.TOOL_DVIPS,
        }));
        configurations.add(new BuildConfiguration("latexdvips2pdf", "latex->dvips->ps2pdf", new String[] {
            LaTeXPlatform.TOOL_LATEX,
            LaTeXPlatform.TOOL_DVIPS,
            LaTeXPlatform.TOOL_PS2PDF,
        }));
        configurations.add(new BuildConfiguration("latexdvips2pdf", "latex->dvipdf", new String[] {
            LaTeXPlatform.TOOL_LATEX,
            LaTeXPlatform.TOOL_DVIPDF,
        }));
        showConfigurations = new ArrayList();

        LaTeXPlatform platform = Utilities.getPlatform(project);

        for (Viewer v : platform.getViewers()) {
            showConfigurations.add(new ShowConfiguration(v));
        }
    }
    
    public List<BuildConfiguration> getBuildConfigurations() {
        return Collections.unmodifiableList(configurations);
    }
    
    public BuildConfiguration getBuildConfiguration(String name) {
        if (name == null)
            throw new IllegalStateException();
        
        for (BuildConfiguration b : getBuildConfigurations()) {
            if (name.equals(b.getName()))
                return b;
        }

        return null;
    }
    
    public List<ShowConfiguration> getShowConfigurations() {
        return Collections.unmodifiableList(showConfigurations);
    }
    
    public ShowConfiguration getShowConfiguration(String name) {
        if (name == null)
            throw new IllegalStateException();
        
        for (ShowConfiguration b : getShowConfigurations()) {
            if (name.equals(b.getName()))
                return b;
        }

        return null;
    }

}
