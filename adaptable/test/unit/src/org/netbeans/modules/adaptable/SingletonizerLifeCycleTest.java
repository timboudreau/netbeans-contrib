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

package org.netbeans.modules.adaptable;

import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.spi.adaptable.Adaptors;
import org.netbeans.spi.adaptable.Singletonizer;

/** Tests for adaptables with lifecycle management. Inherits the basic test
 * to make sure everything works and add few new test methods.
 *
 * @author Jaroslav Tulach
 */
public class SingletonizerLifeCycleTest extends SingletonizerTest {
    public SingletonizerLifeCycleTest(java.lang.String testName) {
        super(testName);
    }
    
    /** Subclassable method to create an Adaptors.singletonizer
     */
    protected Adaptor createSingletonizer (Class[] supported, Singletonizer impl) {
        return Adaptors.singletonizer (supported, impl, null, null, null);
    }

  
}
