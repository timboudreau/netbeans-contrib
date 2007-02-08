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

package beans2nbm;

import beans2nbm.ui.SelectTaskPanel;
import java.util.Map;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

/**
 *
 * @author Timothy Boudreau
 */
public class Brancher extends WizardBranchController {
    /** Creates a new instance of Brancher */
    public Brancher(WizardResultProducer wrp) {
        super (new SelectTaskPanel());
        this.wrp = wrp;
    }

    private final WizardResultProducer wrp;
    protected Wizard getWizardForStep(String step, Map settings) {
        if (Boolean.TRUE.equals(settings.get("beansModule"))) {
            return getBeansModuleWizard();
        } else if (Boolean.TRUE.equals(settings.get("libsModule"))) {
            return getLibModuleWizard();
        } else {
            return null;
        }
    }

    private Wizard bmWizard = null;
    private Wizard getBeansModuleWizard() {
        if (bmWizard == null) {
            bmWizard = WizardPage.createWizard(Main.getPageList(), wrp);
        }
        return bmWizard;
    }

    private Wizard libWizard = null;
    private Wizard getLibModuleWizard() {
        if (libWizard == null) {
            libWizard = WizardPage.createWizard(LibGenMain.getPageList(), wrp);
        }
        return libWizard;
    }
}
