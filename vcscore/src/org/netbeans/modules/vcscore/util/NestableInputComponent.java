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

package org.netbeans.modules.vcscore.util;

import java.util.Map;

/**
 * Defines contract that all embedded JCOMPONENTS must
 * implement. IT can be attached to multiple variables.
 * The VID statemeent is like:
 * <p>
 * <codE>JCOMPONENT(VAR1 VAR2 VAR3, fullpackage.ClassName)</code>
 *
 * <p>Note: current framework implements only one var bindings.
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
     */
    public void updatedVars(Map variables);
    
    /**
     * Set a historical value. The component should adapt it's state
     * accodring to the provided value.
     * @param historicalValue The historical value.
     */
    public void setHistoricalValue(String historicalValue);

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
