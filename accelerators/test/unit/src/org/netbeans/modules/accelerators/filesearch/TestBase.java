/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Accelerators module. 
 * The Initial Developer of the Original Code is Andrei Badea. 
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 * 
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Andrei Badea
 */
public class TestBase extends NbTestCase {
    
    public TestBase(String name) {
        super(name);
    }

    public static void checkResult(FileSearchResult result, int count, String prefix) {
        FileObject[] fos = result.getResult();
        assertEquals(count, fos.length);
        for (int i = 0; i < fos.length; i++) {
            assertTrue(fos[i].getNameExt().startsWith(prefix));
        }
    }
}