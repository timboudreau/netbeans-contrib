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
        assert nonBuggyWizard(wizard);
        
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
