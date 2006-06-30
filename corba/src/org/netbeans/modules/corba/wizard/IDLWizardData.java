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

package org.netbeans.modules.corba.wizard;

/**
 *
 * @author  tzezula
 * @version
 */
public class IDLWizardData extends Object {

    private String idlSource;
    private boolean importIdl;
    private boolean continueCorbaWizard;
    private org.openide.loaders.TemplateWizard wizard;

    /** Creates new IDLWizardData */
    public IDLWizardData(org.openide.loaders.TemplateWizard wizard) {
        this.wizard = wizard;
    }

    public IDLWizardData() {
    }

    public org.openide.loaders.TemplateWizard getWizard () {
        return this.wizard;
    }
    
    public void setWizard (org.openide.loaders.TemplateWizard wizard) {
        this.wizard = wizard;
    }
    
    public boolean importIdl () {
        return this.importIdl;
    }
    
    public void importIdl (boolean imp) {
        this.importIdl = imp;
    }
    
    public boolean continueCorbaWizard () {
        return this.continueCorbaWizard;
    }
    
    public void continueCorbaWizard (boolean cont) {
        this.continueCorbaWizard = cont;
    }
    
    public String getIdlSource () {
        return this.idlSource;
    }
    
    
    public void setIdlSource (String source) {
        this.idlSource = source;
    }

}
