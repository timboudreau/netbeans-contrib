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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import org.netbeans.modules.latex.model.platform.Viewer;
import org.netbeans.modules.latex.ui.ModuleSettings;
import org.netbeans.modules.latex.ui.viewer.ViewerImpl;
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
        tool2DefaultArgs.put(TOOL_LATEX, "-interaction=nonstopmode -src-specials {" + ARG_INPUT_FILE_BASE + "}");
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
    
    private List<Viewer> viewers = new ArrayList<Viewer>();
    
    /** Creates a new instance of LaTeXPlatformImpl */
    public LaTeXPlatformImpl() {
        viewers.add(new ViewerImpl());
        viewers.add(new ProcessViewerImpl(this, TOOL_XDVI, "xdvi", "DVI Viewer", new String[] {".dvi"}));
        viewers.add(new ProcessViewerImpl(this, TOOL_XDVI, "gv", "PS/PDF Viewer", new String[] {".ps", ".pdf"}));
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
        String pathName = /*!!!*/uri.getPath();

        if (!pathName.endsWith("." + oldExtension)) // NOI18N
            return uri;

        pathName = pathName.substring(0, pathName.length() - oldExtension.length() - 1) + "." + newExtension; // NOI18N

        URI u = new URI(uri.getScheme(), uri.getHost(), pathName, uri.getFragment());

        return u;
    }

    public List<URI> getTargetFiles(String tool, FileObject input) {
        if (!isToolConfigured(tool))
            throw new IllegalArgumentException("tool= " + tool + " not supported"); // NOI18N

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

    public List<Viewer> getViewers() {
        return viewers;
    }

}
