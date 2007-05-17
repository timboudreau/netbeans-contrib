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

package org.netbeans.modules.tasklist.export;

import org.openide.WizardDescriptor;

/**
 * Interface for a supported export or import format
 * TODO Use lookup to find the filters such that
 * other modules can register their own.
 */
public interface ExportImportFormat {
    /**
     * Returns the name of this format
     *
     * @return name visible to the user
     */
    public String getName();

    /**
     * Creates a wizard for the export/import operation
     *
     * @return created wizard
     */
    public WizardDescriptor getWizard();
    
    /**
     * Import/Export
     *
     * @param provider Export/Import provider class (the view)
     * @param wd WizardDescriptor returned by getWizard()
     */
    public void doExportImport(ExportImportProvider provider, WizardDescriptor wd);
}
