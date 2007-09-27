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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder.
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
