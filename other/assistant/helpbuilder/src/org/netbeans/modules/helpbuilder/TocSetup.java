/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
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
public class TocSetup extends AbstractDescriptorPanel {
    private JPanel tocSetupPanel = null;
    
    /**
     * Creates a new instance of ProjectSetup
     */
    public TocSetup() {
    }
    
    public Component getComponent() {
        if(tocSetupPanel == null)
            tocSetupPanel = new TocSetupPanel(this);
        return tocSetupPanel;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx(TocSetup.class);        
    }
    
    public boolean isValid() {                
        // If it is always OK to press Next or Finish, then:
        return ((TocSetupPanel)getComponent()).isValid();
        // If it depends on some condition (form filled out...), then:
        // return someCondition ();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent ();
        // and uncomment the complicated stuff below.
    }
    
}
