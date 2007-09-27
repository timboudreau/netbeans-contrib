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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.latex.guiproject.build;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.Registry;
import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.netbeans.modules.latex.guiproject.Utilities;
import org.netbeans.modules.latex.guiproject.ui.ProjectSettings;
import org.netbeans.modules.latex.model.platform.FilePosition;
import org.netbeans.modules.latex.model.platform.LaTeXPlatform;
import org.netbeans.modules.latex.model.platform.Viewer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.windows.InputOutput;

/**
 *
 * @author Jan Lahoda
 */
public final class ShowConfiguration implements Builder {

    private static final ErrorManager ERR = ErrorManager.getDefault().getInstance(BuildConfiguration.class.getName());

    private Viewer viewer;
    
    /** Creates a new instance of BuildConfiguration */
    ShowConfiguration(Viewer viewer) {
        this.viewer = viewer;
    }
    
    public String getName() {
        return viewer.getName();
    }
    
    public String getDisplayName() {
        return viewer.getDisplayName();
    }

    private FilePosition findCurrentPosition(LaTeXGUIProject p) {
        JTextComponent c = Registry.getMostActiveComponent();
        
        if (c == null)
            return null;
        
        DataObject d = (DataObject) c.getDocument().getProperty(Document.StreamDescriptionProperty);

        if (d == null)
            return null;

        FileObject file = d.getPrimaryFile();
        Project remote = FileOwnerQuery.getOwner(file);

        if (remote == null)
            return null;

        if (p != remote.getLookup().lookup(LaTeXGUIProject.class))
            return null;

        FilePosition pos = new FilePosition(file, NbDocument.findLineNumber((StyledDocument) c.getDocument(), c.getCaretPosition()), 0);

        return pos;
    }
    
    public boolean build(final LaTeXGUIProject p, final InputOutput inout) {
        if (!isSupported(p))
            throw new IllegalArgumentException();

        try {
            String buildConfiguration = ProjectSettings.getDefault(p).getBuildConfigurationName();
            BuildConfiguration conf = Utilities.getBuildConfigurationProvider(p).getBuildConfiguration(buildConfiguration);
            LaTeXPlatform platform = Utilities.getPlatform(p);
            
            URI fileToShow = getURIToShow(p, conf);
            FileObject toShow = URLMapper.findFileObject(fileToShow.toURL());
            
            if (toShow != null) {
                viewer.show(toShow, findCurrentPosition(p));
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }

        return true;

//        FileObject file = (FileObject) p.getSource().getMainFile();
//        File wd = FileUtil.toFile(file.getParent());
//        LaTeXPlatform platform = Utilities.getPlatform(p);
//        Map format = new HashMap();
//        boolean result = true;
//        
//        format.put(LaTeXPlatform.ARG_INPUT_FILE_BASE, file.getName());
//        
//        if (LaTeXPlatform.TOOL_GV.equals(tool)) {
//            FileObject ps = FileUtil.findBrother(file, "ps");
//            FileObject pdf = FileUtil.findBrother(file, "pdf");
//            FileObject target = ps;
//            
//            if (ps == null) {
//                target = pdf;
//            }
//            
//            if (ps != null && pdf != null) {
//                if (pdf.lastModified().compareTo(ps.lastModified()) > 0) {
//                    target = pdf;
//                }
//            }
//            
//            if (target != null) {
//                format.put(LaTeXPlatform.ARG_INPUT_FILE, target.getNameExt());
//            }
//        }
//
//        if (IN_IDE_PDF_VIEWER.equals(tool)) {
//            FileObject pdf = FileUtil.findBrother(file, "pdf");
//
//            if (pdf == null) {
//                ErrorManager.getDefault().log(ErrorManager.ERROR, "In IDE viewer: cannot find PDF file!");
//            }
//
//            Viewer.getDefault().show(pdf, findCurrentPosition(p), null);
//            return true;
//        }
//
//        NbProcessDescriptor desc = platform.getTool(tool);
//        
//        return BuildConfiguration.run(desc, format, wd, inout.getOut(), inout.getErr());
    }

    public boolean isSupported(LaTeXGUIProject p) {
        String buildConfiguration = ProjectSettings.getDefault(p).getBuildConfigurationName();
        BuildConfiguration conf = Utilities.getBuildConfigurationProvider(p).getBuildConfiguration(buildConfiguration);

        return isSupported(p, conf);
    }

    private List<URI> getResults(LaTeXGUIProject p, BuildConfiguration conf) {
        List<URI> result = new ArrayList<URI>();
        LaTeXPlatform platform = Utilities.getPlatform(p);

        for (String tool : conf.getTools()) {
            result.addAll(platform.getTargetFiles(tool, (FileObject) p.getMainFile()));
        }

        return result;

    }

    public URI getURIToShow(LaTeXGUIProject p, BuildConfiguration conf) {
        List<URI> uris = getResults(p, conf);
        LaTeXPlatform platform = Utilities.getPlatform(p);
        
        for (URI u : uris) {
            if (viewer.accepts(u))
                return u;
        }

        return null;
    }

    public String getErrorIfAny(LaTeXGUIProject p, BuildConfiguration conf) {
        if (!conf.isSupported(p))
            return "Selected build configuration is not supported.";

        if (getURIToShow(p, conf) == null) {
            return "The selected build configuration does not create a required file type.";
        }

        return null;
    }

    public boolean isSupported(LaTeXGUIProject p, BuildConfiguration conf) {
        return getErrorIfAny(p, conf) == null;
//        LaTeXPlatform platform = Utilities.getPlatform(p);
//        
//        if (!platform.isToolConfigured(tool) && !IN_IDE_PDF_VIEWER.equals(tool))
//            return false;
//
//        if (conf == null)
//            return false;
//
//        if (LaTeXPlatform.TOOL_XDVI.equals(tool)) {
//            return Arrays.asList(conf.getTools()).contains(LaTeXPlatform.TOOL_LATEX);
//        }
//
//        if (LaTeXPlatform.TOOL_GV.equals(tool)) {
//            List<String> tools = new ArrayList(Arrays.asList(conf.getTools()));
//
//            tools.retainAll(Arrays.asList(LaTeXPlatform.TOOL_DVIPDF, LaTeXPlatform.TOOL_DVIPS, LaTeXPlatform.TOOL_PS2PDF));
//
//            return !tools.isEmpty();
//        }
//
//        if (IN_IDE_PDF_VIEWER.equals(tool)) {
//            List<String> tools = new ArrayList(Arrays.asList(conf.getTools()));
//
//            tools.retainAll(Arrays.asList(LaTeXPlatform.TOOL_DVIPDF, LaTeXPlatform.TOOL_PS2PDF));
//
//            return !tools.isEmpty();
//        }
//
//        return false;
    }
    
}
