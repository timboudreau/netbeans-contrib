/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.autoproject.java;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;

class SubprojectProviderImpl implements SubprojectProvider {

    private final Project p;

    public SubprojectProviderImpl(Project p) {
        this.p = p;
    }

    public Set<? extends Project> getSubprojects() {
        Set<Project> kids = new LinkedHashSet<Project>();
        for (SourceGroup g : ProjectUtils.getSources(p).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            ClassPath cp = ClassPath.getClassPath(g.getRootFolder(), ClassPath.COMPILE);
            if (cp != null) {
                for (ClassPath.Entry entry : cp.entries()) {
                    for (FileObject sourceRoot : SourceForBinaryQuery.findSourceRoots(entry.getURL()).getRoots()) {
                        Project kid = FileOwnerQuery.getOwner(sourceRoot);
                        if (kid != null && kid != p) {
                            // XXX might also want to check that sourceRoot is among SOURCES_TYPE_JAVA of kid?
                            kids.add(kid);
                        }
                    }
                }
            }
        }
        return kids;
    }

    public void addChangeListener(ChangeListener listener) {
        // XXX important to implement? probably not
    }

    public void removeChangeListener(ChangeListener listener) {}

}
