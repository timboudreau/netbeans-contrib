
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */


package com.sun.tthub.gde.ui.panels;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gde.ui.GDEWizardModel;
import com.sun.tthub.gde.logic.PortletDeployParams;
import javax.swing.JPanel;

/**
 *
 * @author Hareesh Ravindran
 */
public final class WizardController {
    
    private WizardJPanel wizardPanel;    
    
    /**
     * This is the java object which has to be filled using the wizard. The
     * wizard pages will capture different parts of information related to this
     * java class. Depending on the nature of the fields in the extended trouble
     * ticket value interface, this java class can be simple or complex.
     */ 
    private GDEWizardModel model = new GDEWizardModel();
    
    // It is assumed that all the panels in the array has implemented the
    // WizardValidator interface.
    private WizardContentJPanel[] panels = { new ExtendedTTInfoJPanel(this), 
            new TTValueFieldsDisplayInfoJPanel(this), /*new DeployParamsJPanel(this),*/ 
            new ConfirmationJPanel(this) };
    private int curStep = 0;
    
    
    /** Creates a new instance of WizardController */
    public WizardController() {
        wizardPanel = new WizardJPanel(this);     
        initialize();
    }
    
    public TTValueDisplayInfo getTTValueDisplayInfo() 
                        { return model.getTtValueDisplayInfo(); }
    
    public PortletDeployParams getPortletDeployParams() 
                        { return model.getPortletDeployParams(); }
    
    public GDEWizardModel getWizardModel() { return model; }
    
    public WizardJPanel getWizardPanel() {  return wizardPanel; } 
    
    public int getCurStep() { return curStep; }
    public WizardContentJPanel getCurPanel() { return panels[curStep]; }
    
    public void nextStep() throws GDEException {
        int finalStep = panels.length - 1;
        if(curStep >= finalStep)
            return;
        // Validate the contents of the current step before moving to the next
        // step
        WizardContentProcessor processor = panels[curStep];
        processor.preProcessWizardContents(WizardActions.ACTION_NEXT);
        try {
            processor.validateContents();              
        } catch(GDEWizardPageValidationException ex) {
            if(!processor.validationFailed(ex))
                return;
        }        
        panels[curStep].processWizardContents(WizardActions.ACTION_NEXT);
        JPanel prevPanel = panels[curStep];
        curStep++;
        panels[curStep].loadWizardContentPanel();
        wizardPanel.setWizardContent(panels[curStep], prevPanel);        
        if(curStep == finalStep) {
            wizardPanel.setControlState(WizardJPanel.STATE_FINAL);        
        } else {
            wizardPanel.setControlState(WizardJPanel.STATE_INTERMEDIATE); 
        }
    }
    
    public void previousStep() throws GDEException {
        int initialStep = 0;
        if(curStep <= initialStep)
            return;
        // Validate teh contents of the current step before moving to the next
        // step.
        WizardContentProcessor processor = (WizardContentProcessor) panels[curStep];        
        processor.preProcessWizardContents(WizardActions.ACTION_PREVIOUS);
        try {
            processor.validateContents();                    
        } catch(GDEWizardPageValidationException ex) {
            if(!processor.validationFailed(ex))
                return;
        }        
        panels[curStep].processWizardContents(WizardActions.ACTION_PREVIOUS);        
        WizardContentJPanel prevPanel = panels[curStep];                
        curStep--;
        panels[curStep].loadWizardContentPanel();        
        wizardPanel.setWizardContent(panels[curStep], prevPanel);        
        if(curStep == initialStep) {
            wizardPanel.setControlState(WizardJPanel.STATE_INITIAL);                
        } else {
            wizardPanel.setControlState(WizardJPanel.STATE_INTERMEDIATE);                
        }
        
    }
    
    public void lastStep() throws GDEException {
        // get the last panel of the wizard.
        WizardContentProcessor processor = 
                    (WizardContentProcessor) panels[panels.length - 1];                        
        processor.preProcessWizardContents(WizardActions.ACTION_FINAL);
        try {
            processor.validateContents();                    
        } catch(GDEWizardPageValidationException ex) {
            if(!processor.validationFailed(ex))
                return;
        }        
        panels[curStep].processWizardContents(WizardActions.ACTION_FINAL);        
    }
    
    public void initialize() {
        curStep = 0;
        wizardPanel.setWizardContent(panels[curStep], null);
        wizardPanel.setControlState(WizardJPanel.STATE_INITIAL);
    }
}
