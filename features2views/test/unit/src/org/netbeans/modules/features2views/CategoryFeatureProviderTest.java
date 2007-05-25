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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.features2views;

import java.util.Map;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.autoupdate.UpdateItem;

/**
 *
 * @author Jirka Rechtacek
 */
public class CategoryFeatureProviderTest extends NbTestCase {
    
    public CategoryFeatureProviderTest (String testName) {
        super (testName);
    }

    public void testGetName () {
        CategoryFeatureProvider instance = new CategoryFeatureProvider ();
        String expResult = "category2feature-provider";
        String result = instance.getName ();
        assertEquals (expResult, result);
    }

    public void testGetUpdateItems () throws Exception {
        CategoryFeatureProvider instance = new CategoryFeatureProvider ();
        Map<String, UpdateItem> features = instance.getUpdateItems ();
        assertNotNull ("Features not null.", features);
        //assertFalse ("Some features found.", features.isEmpty ());
        //assertEquals ("Only one feature there.", 1, features.size ());
    }

    public void testRefresh () throws Exception {
        CategoryFeatureProvider instance = new CategoryFeatureProvider ();
        assertTrue ("Force refresh passed.", instance.refresh (true));
        assertTrue ("Soft refresh passed.", instance.refresh (false));
    }
    
}
