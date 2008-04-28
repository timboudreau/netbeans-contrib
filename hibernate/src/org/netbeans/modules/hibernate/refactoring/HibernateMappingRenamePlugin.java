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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.hibernate.refactoring;

import java.util.List;
import java.util.Map;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.hibernate.loaders.mapping.HibernateMappingDataLoader;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.hibernate.refactoring.HibernateRefactoringUtil.OccurrenceItem;
import org.netbeans.modules.hibernate.service.HibernateEnvironment;
import org.netbeans.modules.j2ee.core.api.support.SourceGroups;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RenameRefactoring;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;
import org.openide.util.NbBundle;

/**
 * This plugin modifies the Hibernate configuration files accordingly when the referenced
 * mapping files are renamed
 * 
 * @author Dongmei Cao
 */
public class HibernateMappingRenamePlugin implements RefactoringPlugin {

    private RenameRefactoring refactoring;
    private FileObject fo;

    public HibernateMappingRenamePlugin(RenameRefactoring refactoring) {
        this.refactoring = refactoring;
        fo = refactoring.getRefactoringSource().lookup(FileObject.class);
    }

    public Problem preCheck() {
        return null;
    }

    public Problem checkParameters() {
        return null;
    }

    public Problem fastCheckParameters() {
        // TODO: verify the new name here. Make sure it is valid and unique
        
        Problem fastCheckProblem = null;
    
        String oldName = fo.getName();
        String newName = refactoring.getNewName();
        if(oldName.equals(newName)) {
            fastCheckProblem = HibernateRefactoringUtil.createProblem(fastCheckProblem, 
                    true,  NbBundle.getMessage(HibernateMappingRenamePlugin.class,"MSG_NameNotChanged"));
            return fastCheckProblem;
        }
        
         if(!HibernateRefactoringUtil.isValidMappingFileName(newName)) {
             fastCheckProblem = HibernateRefactoringUtil.createProblem(fastCheckProblem, 
                     true, NbBundle.getMessage(HibernateMappingRenamePlugin.class,"MSG_Invalid_Name"));
         }
        
        if(HibernateRefactoringUtil.nameNotUnique(newName)){
            fastCheckProblem = HibernateRefactoringUtil.createProblem(fastCheckProblem, 
                    true, NbBundle.getMessage(HibernateMappingRenamePlugin.class, "MSG_NameNotUnique"));
        }

        return fastCheckProblem;
    }

    public void cancelRequest() {
        return;
    }

    public Problem prepare(RefactoringElementsBag refactoringElements) {

        if (fo == null || !fo.getMIMEType().equals(HibernateMappingDataLoader.REQUIRED_MIME)) {
            // Nothing needs to be done
            return null;
        }

        // Get the old and new resource name
        String[] names = getOldNewResourceName();
        if (names == null || names[0] == null || names[1] == null) {
            //TODO: return a Problem
            return null;
        }

        // Get the configuration files
        Project proj = FileOwnerQuery.getOwner(fo);
        HibernateEnvironment env = proj.getLookup().lookup(HibernateEnvironment.class);
        List<FileObject> configFiles = env.getAllHibernateConfigFileObjects();
        if(configFiles.isEmpty())
            return null;
        
        Map<FileObject, List<OccurrenceItem>> occurrences =
                HibernateRefactoringUtil.getMappingResourceOccurrences(configFiles, names[0]);

        for (FileObject mappingFile : occurrences.keySet()) {
            List<OccurrenceItem> foundPlaces = occurrences.get(mappingFile);
            for (OccurrenceItem foundPlace : foundPlaces) {
                HibernateRenameRefactoringElement elem = new HibernateRenameRefactoringElement(mappingFile,
                        names[0],
                        names[1],
                        foundPlace.getLocation(),
                        foundPlace.getText());
                refactoringElements.add(refactoring, elem);
            }
        }
        refactoringElements.registerTransaction(new HibernateMappingRenameTransaction(occurrences.keySet(), names[0], names[1]));

        return null;
    }

    private String[] getOldNewResourceName() {
        String names[] = new String[2]; // [oldName, newName]
        Project project = FileOwnerQuery.getOwner(fo);
        SourceGroup[] grp = SourceGroups.getJavaSourceGroups(project);
        if (grp.length == 0) {
            return null;
        }

        String srcRoot = grp[0].getRootFolder().getPath();
        String oldPath = fo.getPath();
        String oldResource = oldPath.substring(srcRoot.length() + 1);
        String pkgPath = oldResource.substring(0, oldResource.lastIndexOf("/") + 1);

        names[0] = oldResource;
        names[1] = pkgPath + refactoring.getNewName() + ".xml"; // NOI18N

        return names;
    }
}