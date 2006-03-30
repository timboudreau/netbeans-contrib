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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.latex.model.platform.LaTeXPlatform;
import org.netbeans.modules.latex.ui.ModuleSettings;
import org.openide.ErrorManager;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXPlatformImpl implements LaTeXPlatform {
    
    private static Map<String, String> tool2DefaultArgs;
    private static Map<String, List<String>> tool2TargetExtensions;
    
    static {
        tool2DefaultArgs = new HashMap();
        tool2DefaultArgs.put(TOOL_LATEX, "-interaction=nonstopmode {" + ARG_INPUT_FILE_BASE + "}");
        tool2DefaultArgs.put(TOOL_DVIPS, "-o {" + ARG_INPUT_FILE_BASE + "}.ps  {" + ARG_INPUT_FILE_BASE + "}");
        tool2DefaultArgs.put(TOOL_DVIPDF, "-o {" + ARG_INPUT_FILE_BASE + "}.pdf  {" + ARG_INPUT_FILE_BASE + "}");
        tool2DefaultArgs.put(TOOL_PS2PDF, "{" + ARG_INPUT_FILE_BASE + "}.ps");
        tool2DefaultArgs.put(TOOL_BIBTEX, "{" + ARG_INPUT_FILE_BASE + "}");
        tool2DefaultArgs.put(TOOL_XDVI, "{" + ARG_INPUT_FILE_BASE + "}.dvi");
        tool2DefaultArgs.put(TOOL_GV, "{" + ARG_INPUT_FILE + "}");
        tool2TargetExtensions = new HashMap();
        tool2TargetExtensions.put(TOOL_LATEX, Arrays.asList("dvi", "aux", "log"));
        tool2TargetExtensions.put(TOOL_DVIPS, Arrays.asList("ps"));
        tool2TargetExtensions.put(TOOL_DVIPDF, Arrays.asList("pdf"));
        tool2TargetExtensions.put(TOOL_PS2PDF, Arrays.asList("pdf"));
        tool2TargetExtensions.put(TOOL_BIBTEX, Arrays.asList("bbl"));
        tool2TargetExtensions.put(TOOL_XDVI, Arrays.asList(new String[0]));
        tool2TargetExtensions.put(TOOL_GV, Arrays.asList(new String[0]));
    }
    
    /** Creates a new instance of LaTeXPlatformImpl */
    public LaTeXPlatformImpl() {
    }

    private String getDefaultArgs(String tool) {
        String args = tool2DefaultArgs.get(tool);
        
        if (args == null)
            return "{" + ARG_INPUT_FILE_BASE + "}";
        
        return args;
    }
    
    private static NbProcessDescriptor create(String value) {
        PropertyEditor ped = PropertyEditorManager.findEditor(NbProcessDescriptor.class);
        
        ped.setAsText(value);
        
        return (NbProcessDescriptor) ped.getValue();
    }
    
    public NbProcessDescriptor getTool(String tool) {
        if (isToolConfigured(tool)) {
            Map settings = ModuleSettings.getDefault().readSettings();
            String command = (String) settings.get(tool);
            
            if (command == null)
                return null;
            
            return create(command + " " + getDefaultArgs(tool));
        }
        
        return null;
    }
    
    public static LaTeXPlatformImpl getInstance() {
        return (LaTeXPlatformImpl) Lookup.getDefault().lookup(LaTeXPlatformImpl.class);
    }

    public boolean isToolConfigured(String tool) {
        Map settings = ModuleSettings.getDefault().readSettings();
        
        Object quality = settings.get(tool + "-quality");
        
        if (quality instanceof Boolean) {
            return ((Boolean) quality).booleanValue();
        } else {
            return false;
        }
    }

    private URI alterExtension(URI uri, String oldExtension, String newExtension) throws URISyntaxException {
        URI u = new URI(uri.getScheme(), uri.getHost(), /*!!!*/uri.getPath().replaceFirst("." + oldExtension, "." + newExtension), uri.getFragment());

        return u;
    }

    public List<URI> getTargetFiles(String tool, FileObject input) {
        if (!isToolConfigured(tool))
            throw new IllegalArgumentException("tool= " + tool + " not supported");

        try {
            URI uri = input.getURL().toURI();
            List<URI> result = new ArrayList<URI>();
            
            for (String ext : tool2TargetExtensions.get(tool)) {
                result.add(alterExtension(uri, "tex", ext));
            }

            return result;
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(e);
        } catch (URISyntaxException e) {
            ErrorManager.getDefault().notify(e);
        }

        return Collections.emptyList();
    }

}
