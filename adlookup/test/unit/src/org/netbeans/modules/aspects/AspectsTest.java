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

package org.netbeans.modules.aspects;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.aspects.*;
import org.openide.util.Lookup;

/** Tests for Aspects. `
 *
 * @author Jaroslav Tulach
 */
public class AspectsTest extends org.netbeans.junit.NbTestCase {
    public AspectsTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new org.netbeans.junit.NbTestSuite(AspectsTest.class);
        
        return suite;
    }

    public void testAlwaysReturnsALookup () {
        Lookup l = Aspects.getLookup(this, null);
        assertNotNull ("There is an lookup for any object", l);
    }
  
}
