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
package org.netbeans.modules.latex.model.platform;

import java.net.URI;
import java.util.List;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public interface LaTeXPlatform {
    
    public static final String TOOL_LATEX  = "latex";
    public static final String TOOL_BIBTEX = "bibtex";
    public static final String TOOL_DVIPS  = "dvips";
    public static final String TOOL_DVIPDF = "dvipdf";
    public static final String TOOL_PS2PDF = "ps2pdf";
    public static final String TOOL_GS     = "gs";
    public static final String TOOL_XDVI   = "xdvi";
    public static final String TOOL_GV     = "gv";

    public static final String ARG_INPUT_FILE_BASE = "input-file-base";
    public static final String ARG_INPUT_FILE      = "input-file";
    public static final String ARG_INPUT_FILE_ABSOLUTE = "input-file-absolute";
    
    public NbProcessDescriptor getTool(String tool);
    
    public boolean isToolConfigured(String tool);

    public List<URI> getTargetFiles(String tool, FileObject inputSourceFile);

}
