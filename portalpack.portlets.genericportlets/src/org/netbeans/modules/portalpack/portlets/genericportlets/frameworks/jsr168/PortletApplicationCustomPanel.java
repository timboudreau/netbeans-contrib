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

package org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.jsr168;

import java.util.Map;
import javax.swing.JScrollPane;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;

/**
 *
 * @author Satyaranjan
 */
public class PortletApplicationCustomPanel extends JScrollPane{
    
    private PortletApplicationPanelVisual visPanel;
    
    /**
     * 
     * @param panel 
     * @param wm 
     */
    public PortletApplicationCustomPanel(PortletApplicationWizardPanel panel,WebModule wm)
    {
        super();
        visPanel = new PortletApplicationPanelVisual(panel,wm);
        this.getViewport().add(visPanel);    
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        return visPanel.valid(wizardDescriptor);
    }
    
    void store(WizardDescriptor d) {
        visPanel.store(d);
    }
    
    /**
     * 
     * @return 
     */
    public Map getData()
    {
        return visPanel.getData();
    }
    
    void read(WizardDescriptor settings) {
        visPanel.read(settings);
    }
    
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
        visPanel.validate(d);
    }   
    
    void enableComponents(boolean enable)
    {
        visPanel.enableComponents(enable);
    }
}
