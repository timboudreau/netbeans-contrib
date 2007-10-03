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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.util.ArrayList;
import java.util.List;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.spi.project.LookupMerger;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.openide.util.Lookup;

/**
 * Merges AntArtifactProviders - duplicates all artifacts that have type
 * ARTIFACT_TYPE_JAR and changes the type to ARTIFACT_TYPE_J2EE_MODULE_IN_EAR_ARCHIVE
 *
 * @author Milan Kubec
 */
public class LookupMergerImpl implements LookupMerger {
    
    public LookupMergerImpl() { }
    
    public Class getMergeableClass() {
        return AntArtifactProvider.class;
    }

    public Object merge(Lookup lookup) {
        return new AntArtifactProviderImpl(lookup);
    }
    
    private static class AntArtifactProviderImpl implements AntArtifactProvider {
        
        private Lookup lkp;
        
        public AntArtifactProviderImpl(Lookup lookup) {
            this.lkp = lookup;
        }
        
        public AntArtifact[] getBuildArtifacts() {
            AntArtifactProvider aap = (AntArtifactProvider) lkp.lookup(AntArtifactProvider.class);
            AntArtifact artifacts[] = aap.getBuildArtifacts();
            List ejbArtifactList = new ArrayList();
            for (int i = 0; i < artifacts.length; i++) {
                if (artifacts[i].getType().equals(JavaProjectConstants.ARTIFACT_TYPE_JAR)) {
                    ejbArtifactList.add(new EJBFreeformAntArtifact(artifacts[i]));
                }
            }
            AntArtifact allArtifacts[] = new AntArtifact[artifacts.length + ejbArtifactList.size()];
            AntArtifact ejbArtifacts[] = (AntArtifact[]) ejbArtifactList.toArray(new AntArtifact[ejbArtifactList.size()]);
            System.arraycopy(artifacts, 0, allArtifacts, 0, artifacts.length);
            System.arraycopy(ejbArtifacts, 0, allArtifacts, artifacts.length, ejbArtifacts.length);
            return allArtifacts;
        }
        
    }
    
}
