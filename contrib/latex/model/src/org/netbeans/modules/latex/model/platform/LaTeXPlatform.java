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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

    public List<Viewer> getViewers();

}
