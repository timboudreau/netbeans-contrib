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

package org.netbeans.modules.vcscore.util;

import java.util.Map;

/**
 * Defines contract that all embedded JCOMPONENTS must
 * implement. IT can be attached to multiple variables.
 * The VID statemeent is like:
 * <p>
 * <codE>JCOMPONENT(VAR1 VAR2 VAR3, fullpackage.ClassName)</code>
 *
 * <p>Note: current VID framework implements only one var bindings.
 *
 * @author Petr Kuzel
 */
public interface NestableInputComponent {

    /**
     * Request to bind the component to container context.
     * It's called once before the component is made visible.
     */
    public void joinNest(VariableInputNest nest);

    /**
     * Get current component value
     * @param variable name of variable in question
     */
    public String getValue(String variable);
    
    /**
     * Called with the updated map of variables.
     * @param variables The map of variable values by their names
     * @deprecated there is already VariableInputNest.getCommandHashtable()
     */
    public void updatedVars(Map variables);
    
    /**
     * Set a historical value. The component should adapt it's state
     * accodring to the provided value.
     * @param historicalValue The historical value.
     * @deprecated does not support multivalued NICes. Also there is already
     * VariableInputNest.getValue()
     */
    public void setHistoricalValue(String historicalValue);

    ///** Nest have changed, Adapt state to it. */
    //public void reload();

    /**
     * Tests current value validity and for valid
     * values it returns <code>null</code>
     * @param variable name of variable in question
     */
    public String getVerificationMessage(String variable);

    /**
     * Request to terminate all connections with container
     * and die (release all listeners, kill threads...).
     */
    public void leaveNest();


}
