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

package org.netbeans.modules.helpbuilder;

import org.netbeans.modules.helpbuilder.ui.*;
import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
/**
 *
 * @author  Richard Gregor
 */
public class ProjectSetup extends AbstractDescriptorPanel {
    private JPanel projectSetupPanel = null;

/**
     * Creates a new instance of ProjectSetup
     */
    public ProjectSetup() {
    }
    
    public Component getComponent() {
        if(projectSetupPanel == null)
            projectSetupPanel = new ProjectSetupPanel(this);
        return projectSetupPanel;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx(ProjectSetup.class);        
    }
    
    public boolean isValid() {                
        // If it is always OK to press Next or Finish, then:
        return ((ProjectSetupPanel)getComponent()).isValid();
        // If it depends on some condition (form filled out...), then:
        // return someCondition ();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent ();
        // and uncomment the complicated stuff below.
    }
    
}
