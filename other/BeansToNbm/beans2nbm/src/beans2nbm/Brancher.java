/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package beans2nbm;

import beans2nbm.ui.SelectTaskPanel;
import java.util.Map;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPage;

/**
 *
 * @author Timothy Boudreau
 */
public class Brancher extends WizardBranchController {
    /** Creates a new instance of Brancher */
    public Brancher() {
        super (new SelectTaskPanel());
    }

    protected Wizard getWizardForStep(String step, Map settings) {
        System.err.println("GetWizardForStep " + step + ":" + settings);
        if (Boolean.TRUE.equals(settings.get("beansModule"))) {
            System.err.println("Return beans module");
            return getBeansModuleWizard();
        } else if (Boolean.TRUE.equals(settings.get("libsModule"))) {
            System.err.println("return lib module");
            return getLibModuleWizard();
        } else {
            System.err.println("return null");
            return null;
        }
    }

    private Wizard bmWizard = null;
    private Wizard getBeansModuleWizard() {
        if (bmWizard == null) {
            bmWizard = WizardPage.createWizard(Main.getPageList());
        }
        return bmWizard;
    }

    private Wizard libWizard = null;
    private Wizard getLibModuleWizard() {
        if (libWizard == null) {
            libWizard = WizardPage.createWizard(LibGenMain.getPageList());
        }
        return libWizard;
    }

    
}
