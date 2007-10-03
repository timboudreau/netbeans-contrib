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
 * The Original Software is the CVSROOT Selector (RFE #65366).
 * The Initial Developer of the Original Software is Michael Nascimento Santos.
 * Portions created by Michael Nascimento Santos are Copyright (C) 2005.
 * All Rights Reserved.
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

package net.java.dev.cvsrootselector;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.actions.NodeAction;

public final class CvsRootSelectorAction extends NodeAction {

    public CvsRootSelectorAction() {
        setIcon(null);
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    public String getName() {
        return "Change CVSROOT...";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected boolean enable(Node[] node) {
        if (node.length != 1) {
            return false;
        }
        
        try {
            File file = getFile(node[0]);
            
            if (file == null) {
                return false;
            }
                        
            return CvsRootRewriter.getCvsRootFile(file) != null;
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return false;
        }
    }
    
    protected void performAction(Node[] node) {
        try {
            new CvsRootSelectorPanel(getFile(node[0])).display();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.USER, ex);
        }
    }
    
    private File getFile(Node node) throws IOException {
        File file = null;
        
        FileObject fo = null;
        Collection fileObjects = node.getLookup().lookup(
                new Lookup.Template(FileObject.class)).allInstances();
        
        if (fileObjects.size() > 0) {
            fo = (FileObject) fileObjects.iterator().next();
        } else {
            DataObject dataObject = (DataObject) node.getCookie(DataObject.class);
            if (dataObject instanceof DataShadow) {
                dataObject = ((DataShadow) dataObject).getOriginal();
            }
            if (dataObject != null) {
                fo = dataObject.getPrimaryFile();
            }
        }
        
        if (fo != null) {
            File f = FileUtil.toFile(fo);
            if (f != null && f.isDirectory()) {
                file = f;
            }
        } else {
            Project project = (Project)node.getLookup().lookup(Project.class);
            
            if (project != null) {
                Sources sources = ProjectUtils.getSources(project);
                SourceGroup[] groups = sources.getSourceGroups(Sources.TYPE_GENERIC);
                
                if (groups.length == 1) {
                    FileObject root = groups[0].getRootFolder();
                    file = FileUtil.toFile(root);
                } else {
                    File versioned = null;
                    boolean multiple = false;
                    
                    for (int i = 0; i < groups.length; i++) {
                        FileObject root = groups[0].getRootFolder();
                        File f = FileUtil.toFile(root);
                        
                        System.out.println(f);
                        if (f != null && CvsRootRewriter.getCvsRootFile(f) != null) {
                            if (versioned != null && !versioned.equals(f)) {
                                multiple = true;
                            }
                            
                            versioned = f;
                        }
                    }
                    
                    file = (multiple || versioned == null) ?
                        FileUtil.toFile(project.getProjectDirectory()) :
                        versioned;
                }
            }
        }
        
        return file;
    }
}