/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * WizardFactory.java
 *
 * Created on February 22, 2005, 4:39 PM
 */

package org.netbeans.api.wizard;

import java.util.Arrays;
import java.util.HashSet;
import org.netbeans.spi.wizard.Wizard;


/**
 * API to show a Wizard in a dialog.
 *
 * @author Tim Boudreau
 */
public abstract class WizardDisplayer {
    protected WizardDisplayer() {
    }
    
    /**
     * Display a wizard in a dialog, using the default implementation of
     * WizardDisplayer.
     */
    public static Object showWizard (Wizard wizard) {
        assert nonBuggyWizard (wizard);
        
//        WizardFactory factory = (WizardFactory) Lookup.getDefault().lookup(
//                WizardFactory.class);
        WizardDisplayer factory = null;
        
        if (factory == null) {
            factory = new TrivialWizardFactory();
        }
        
        return factory.show (wizard);
    }
    
    /**
     * Show a wizard.
     * @param wizard the Wizard to show
     * @return Whatever object the wizard returns from its <code>finish()</code>
     *  method, if the Wizard was completed by the user.
     */
    protected abstract Object show (Wizard wizard);
    
    
    private static boolean nonBuggyWizard (Wizard wizard) {
        String[] s = wizard.getAllSteps();
        assert new HashSet(Arrays.asList(s)).size() == s.length;
        if (s.length == 1 && Wizard.UNDETERMINED_STEP.equals(s[0])) {
            assert false : "Only ID may not be UNDETERMINED_ID";
        }
        for (int i=0; i < s.length; i++) {
            if (Wizard.UNDETERMINED_STEP.equals(s[i]) && i != s.length - 1) {
               assert false :  "UNDETERMINED_ID may only be last element in" +
                       " ids array " + Arrays.asList(s);
            }
        }
        return true;
    }
}
