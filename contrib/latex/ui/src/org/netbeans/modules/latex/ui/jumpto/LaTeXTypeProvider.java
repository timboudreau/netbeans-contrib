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

package org.netbeans.modules.latex.ui.jumpto;

import java.beans.BeanInfo;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;
import org.netbeans.modules.latex.model.structural.GoToSourceAction;
import org.netbeans.modules.latex.model.structural.PositionCookie;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.netbeans.modules.latex.model.structural.label.LabelStructuralElement;
import org.netbeans.modules.latex.model.structural.section.SectionStructuralElement;
import org.netbeans.spi.jumpto.type.SearchType;
import org.netbeans.spi.jumpto.type.TypeDescriptor;
import org.netbeans.spi.jumpto.type.TypeProvider;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXTypeProvider implements TypeProvider {

    public String name() {
        return "latex";
    }

    public String getDisplayName() {
        return "LaTeX Files Provider";
    }

    public List<? extends TypeDescriptor> getTypeNames(Project project, final String text, SearchType type) {
        //XXX: simplified & !cancellable!:
        final List<TypeDescriptor> result = new LinkedList<TypeDescriptor>();
        Set<FileObject> mainFiles = new HashSet<FileObject>();
        
        for (LaTeXSourceFactory f : Lookup.getDefault().lookupAll(LaTeXSourceFactory.class)) {
            for (FileObject file : (Iterable<FileObject>) f.getAllKnownFiles()) {
                if (file.getNameExt().startsWith(text))
                    result.add(new FileTypeDescriptor(file));
                
                if (f.isMainFile(file)) {
                    mainFiles.add(file);
                }
            }
        }
        
        //section names&labels:
        for (final FileObject file : mainFiles) {
            Source s = Source.forFileObject(file);
            
            try {
            s.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {}
                public void run(CompilationController parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    
                    LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                    
                    for (StructuralElement e : lpr.getStructuralRoot().getSubElements()) {
                        gatherStructuralDescriptions(file, e, result, null, text);
                    }
                }
            }, true);
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }
        
        return result;
    }

    private String getName(StructuralElement e) {
        if (e instanceof LabelStructuralElement) {
            return ((LabelStructuralElement) e).getLabel();
        } else {
            return StructuralNodeFactory.createNode(e).getDisplayName();
        }
    }
    private void gatherStructuralDescriptions(FileObject mainFile, StructuralElement e, List<TypeDescriptor> result, String path, String prefix) {
        String name = getName(e);
        
        if (name.startsWith(prefix))
            result.add(new StructuralTypeDescriptor(e, name, path, mainFile));
        
        path = (path != null ? path + "/" : "") + name;
        
        for (StructuralElement c : e.getSubElements()) {
            gatherStructuralDescriptions(mainFile, c, result, path, prefix);
        }
    }
    
    public void cancel() {
    }

    public void cleanup() {
    }

    private static final class FileTypeDescriptor extends TypeDescriptor {

        private FileObject file;

        public FileTypeDescriptor(FileObject file) {
            this.file = file;
        }
        
        public String getSimpleName() {
            return file.getNameExt();
        }

        public String getOuterName() {
            return null;
        }

        public String getTypeName() {
            return getSimpleName();
        }

        public String getContextName() {
            return " (" + FileUtil.getFileDisplayName(file.getParent()) + ")";
        }

        public Icon getIcon() {
            try {
                DataObject od = DataObject.find(file);
                Node n = od.getNodeDelegate();

                if (n != null) {
                    return new ImageIcon(n.getIcon(BeanInfo.ICON_COLOR_16x16));
                }
            } catch (DataObjectNotFoundException e) {
                Logger.getLogger(LaTeXTypeProvider.class.getName()).log(Level.FINE, null, e);
            }
            
            return null;
        }

        public String getProjectName() {
            Project p = FileOwnerQuery.getOwner(file);
            
            if (p != null) {
                return ProjectUtils.getInformation(p).getDisplayName();
            }
            
            return null;
        }

        public Icon getProjectIcon() {
            Project p = FileOwnerQuery.getOwner(file);
            
            if (p != null) {
                return ProjectUtils.getInformation(p).getIcon();
            }
            
            return null;
        }

        public FileObject getFileObject() {
            return file;
        }

        public int getOffset() {
            return 0;
        }

        public void open() {
            try {
                DataObject od = DataObject.find(file);
                EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
                
                ec.open();
            } catch (DataObjectNotFoundException e) {
                Logger.getLogger(LaTeXTypeProvider.class.getName()).log(Level.FINE, null, e);
            }
        }
        
    }

    private static final class StructuralTypeDescriptor extends TypeDescriptor {

        private StructuralElement e;
        private String name;
        private String context;
        private FileObject mainFile;

        public StructuralTypeDescriptor(StructuralElement e, String name, FileObject mainFile) {
            this(e, name, null, mainFile);
        }
        
        public StructuralTypeDescriptor(StructuralElement e, String name, String context, FileObject mainFile) {
            this.e = e;
            this.name = name;
            this.context = context;
            this.mainFile = mainFile;
        }

        public String getSimpleName() {
            return name;
        }

        public String getOuterName() {
            return null;
        }

        public String getTypeName() {
            return getSimpleName();
        }

        public String getContextName() {
            return context == null ? "" : " (" + context + ")";
        }

        public Icon getIcon() {
            return new ImageIcon(StructuralNodeFactory.createNode(e).getIcon(BeanInfo.ICON_COLOR_16x16));
        }

        public String getProjectName() {
            Project p = FileOwnerQuery.getOwner(mainFile);
            
            if (p != null) {
                return ProjectUtils.getInformation(p).getDisplayName();
            }
            
            return null;
        }

        public Icon getProjectIcon() {
            Project p = FileOwnerQuery.getOwner(mainFile);
            
            if (p != null) {
                return ProjectUtils.getInformation(p).getIcon();
            }
            
            return null;
        }

        public FileObject getFileObject() {
            PositionCookie pc = StructuralNodeFactory.createNode(e).getLookup().lookup(PositionCookie.class);
            
            if (pc == null)
                return null;
            
            return (FileObject) pc.getPosition().getFile();
        }

        public int getOffset() {
            PositionCookie pc = StructuralNodeFactory.createNode(e).getLookup().lookup(PositionCookie.class);
            
            if (pc == null)
                return 0;
            
            return pc.getPosition().getOffsetValue();
        }

        public void open() {
            GoToSourceAction.get(GoToSourceAction.class).performAction(new Node[] {StructuralNodeFactory.createNode(e)});
        }
        
    }
}
