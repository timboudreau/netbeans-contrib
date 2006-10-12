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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.util.ArrayList;
import java.util.List;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ant.AntArtifact;

import org.netbeans.spi.project.ant.AntArtifactProvider;

import org.netbeans.modules.ant.freeform.spi.LookupMerger;

import org.openide.util.Lookup;

/**
 * Merges AntArtifactProviders - duplicates all artifacts that have type
 * ARTIFACT_TYPE_JAR and changes the type to ARTIFACT_TYPE_J2EE_MODULE_IN_EAR_ARCHIVE
 *
 * @author Milan Kubec
 */
public class LookupMergerImpl implements LookupMerger {
    
    public LookupMergerImpl() { }
    
    public Class[] getMergeableClasses() {
        return new Class[] { AntArtifactProvider.class };
    }
    
    public Object merge(Lookup lookup, Class clazz) {
        if (clazz == AntArtifactProvider.class) {
            return new AntArtifactProviderImpl(lookup);
        }
        throw new IllegalArgumentException("Merging of " + clazz + " is not supported"); // NOI18N
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
