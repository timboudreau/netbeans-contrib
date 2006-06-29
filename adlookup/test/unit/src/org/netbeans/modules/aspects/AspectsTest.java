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

package org.netbeans.modules.aspects;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adlookup.AdaptableLookup;
import org.openide.util.Lookup;

/** Tests for Aspects. `
 *
 * @author Jaroslav Tulach
 */
public class AspectsTest extends org.netbeans.junit.NbTestCase {
    public AspectsTest(java.lang.String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new org.netbeans.junit.NbTestSuite(AspectsTest.class);
        
        return suite;
    }

    public void testAlwaysReturnsALookup () {
        Lookup l = AdaptableLookup.getLookup(null, this);
        assertNotNull ("There is an lookup for any object", l);

        if (!(l instanceof Adaptable)) {
            fail("We also need to implement adaptable: " + l);
        }
    }
  
}
