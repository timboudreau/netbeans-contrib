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
 * WizardException.java
 *
 * Created on February 22, 2005, 3:56 PM
 */

package org.netbeans.spi.wizard;

/**
 * Some arguments a user enters in a wizard may be too expensive to validate
 * as the user is going through the wizard.  Therefore, Wizard.finish() throws
 * WizardException.
 * <p>
 * Exceptions of this type always have a localized message, and optionally
 * provide a step in the wizard that to return to, so that the user can
 * enter corrected information.
 *
 * @author Tim Boudreau
 */
public final class WizardException extends Exception {
    private final String localizedMessage;
    private final String step;
    /** Creates a new instance of WizardException */
    public WizardException(String localizedMessage, String stepToReturnTo) {
        super ("wizardException");
        this.localizedMessage = localizedMessage;
        this.step = stepToReturnTo;
    }
    
    public String getLocalizedMessage() {
        return localizedMessage;
    }
    
    public String getStepToReturnTo() {
        return step;
    }
}
