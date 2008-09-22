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

import org.netbeans.api.project.Project;
import org.netbeans.modules.autoproject.java.actions.ActionProviderImpl;
import org.netbeans.spi.java.project.support.LookupMergerSupport;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ui.support.UILookupMergerSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Inserts extra items into autoproject lookup suited for Java projects.
 */
public class LookupProviderImpl implements LookupProvider {

    /** public for lookup */
    public LookupProviderImpl() {}

    public Lookup createAdditionalLookup(Lookup baseContext) {
        Project p = baseContext.lookup(Project.class);
        assert p != null;
        ClassPathProviderImpl cpp = new ClassPathProviderImpl(p);
        return Lookups.fixed(
                LookupMergerSupport.createClassPathProviderMerger(cpp),
                new SourceForBinaryImpl(p),
                new ProjectInformationImpl(p),
                new SourcesImpl(p),
                new SourceLevelQueryImpl(p),
                UILookupMergerSupport.createProjectOpenHookMerger(new OpenHook(p, cpp)),
                new ActionProviderImpl(p),
                new SubprojectProviderImpl(p));
        // XXX consider adding:
        // AntArtifactProvider
        // BinaryForSourceQueryImplementation
        // FileBuiltQueryImplementation
        // JavadocForBinaryQueryImplementation
        // MultipleRootsUnitTestForSourceQueryImplementation
    }

}
