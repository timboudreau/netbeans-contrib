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
 * WizardController.java
 *
 * Created on March 5, 2005, 7:24 PM
 */

package org.netbeans.spi.wizard;

/**
 * Controller which can be used to modify the UI state of a wizard.  Passed
 * as an argument to methods of <code>PanelProvider</code>.  Use this interface
 * to determine whether the Next/Finish buttons should be enabled, and if some
 * problem explanation text should be displayed.
 * <p>
 * If you are implementing this interface, you are probably doing something
 * wrong.  Use instances of this interface passed to
 * {@link org.netbeans.spi.wizard.WizardPanelProvider#createPanel 
 * WizardPanelProvider.createPanel}.
 *
 * @author Tim Boudreau
 */
public interface WizardController {
    
    /**
     * Indicate that there is a problem with what the user has (or has not)
     * input, such that the Next/Finish buttons should be disabled until the
     * user has made some change.
     * <p>
     * Pass null to indicate there is no problem;  non-null indicates there is
     * a problem - the passed string should be a localized, human-readable
     * description that assists the user in correcting the situation.
     */
    void setProblem (String value);
    
    /**
     * Indicate that the Finish button of the wizard should be enabled 
     * (assuming <code>setProblem</code> has not been called with a non-null
     * value).  
     * <p>
     * <code>setCanFinish</code> means two different things, depending on the
     * type of wizard.  In a wizard created by a <code>WizardBranchController</code>,
     * it only enables the finish button if the sub-wizard in question is the last
     * in the branching structure;  if it is not, setting <code>canFinish</code>
     * to true indicates that the next steps in the wizard may now be known,
     * and it should try to find the next sub-wizard to continue.
     */
    void setCanFinish (boolean value);
}
