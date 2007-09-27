/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package wizarddemo;

import java.awt.Rectangle;
import java.util.Map;
import javax.swing.UIManager;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPanelProvider;
import wizarddemo.panels.SpeciesPanel;


/**
 * This is the main entry point, from which the wizard is created.
 *
 * @author Timothy Boudreau
 */
public class NewPetWizard extends WizardBranchController {

    NewPetWizard(  ) {
        super( new InitialSteps(  ) );
    }
    
    public static void main (String[] ignored) throws Exception {
        //Use native L&F
        UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName());
        
        WizardDisplayer.showWizard (new NewPetWizard().createWizard(), 
                new Rectangle (20, 20, 500, 400));
        System.exit(0);
    }
    
    public static Wizard makeWizard() {
        return new NewPetWizard().createWizard();
    }

    protected WizardPanelProvider getPanelProviderForStep(String step, Map collectedData) {
        //There's only one branch point, so we don't need to test the
        //value of step
        Object species = collectedData.get(SpeciesPanel.KEY_SPECIES);
        if (SpeciesPanel.VALUE_CAT.equals(species)) {
            return getCatLoversSteps();
        } else if (SpeciesPanel.VALUE_DOG.equals(species)) {
            return getDogLoversSteps();
        } else if (SpeciesPanel.VALUE_GERBIL.equals(species)) {
            return null;//new GerbilSteps();
        } else {
            return null;
        }
    }

    private WizardPanelProvider getDogLoversSteps() {
        if (dogLoversSteps == null) {
            dogLoversSteps = new DogLoversSteps();
        }
        return dogLoversSteps;
    }

    private WizardPanelProvider getCatLoversSteps() {
        if (catLoversSteps == null) {
            catLoversSteps = new CatLoversSteps();
        }
        return catLoversSteps;
    }
    
    private CatLoversSteps catLoversSteps = null;
    private DogLoversSteps dogLoversSteps = null;
}
