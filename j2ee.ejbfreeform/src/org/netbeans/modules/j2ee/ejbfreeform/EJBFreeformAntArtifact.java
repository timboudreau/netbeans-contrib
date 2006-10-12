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

import java.io.File;
import java.net.URI;

import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;

/**
 * Simple implementation of AntArtifact that just copies another AntArtifact
 * and replaces its type by ARTIFACT_TYPE_J2EE_MODULE_IN_EAR_ARCHIVE
 *
 * @author Milan Kubec
 */
public class EJBFreeformAntArtifact extends AntArtifact {
    
    private AntArtifact aa;
    
    public EJBFreeformAntArtifact(AntArtifact aa) {
        super();
        this.aa = aa;
    }
    
    public String getType() {
        return EjbProjectConstants.ARTIFACT_TYPE_J2EE_MODULE_IN_EAR_ARCHIVE;
    }
    
    public File getScriptLocation() {
        return aa.getScriptLocation();
    }
    
    public String getTargetName() {
        return aa.getTargetName();
    }
    
    public String getCleanTargetName() {
        return aa.getCleanTargetName();
    }
    
    public URI[] getArtifactLocations() {
        return aa.getArtifactLocations();
    }
    
}
