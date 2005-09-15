/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;

import junit.framework.Test;
import org.netbeans.modules.tasklist.core.util.*;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

/**
 * Tests for org.netbeans.modules.tasklist.core.util.TextPositionsMapper
 */
public class TextPositionsMapperTest extends NbTestCase {
    public TextPositionsMapperTest(String name) {
        super(name);
    }
    
    public static void main (String args []) {
        junit.textui.TestRunner.run(TextPositionsMapperTest.class);
    }
    
    public static Test suite () {
        return new NbTestSuite(TextPositionsMapperTest.class);
    }

    /**
     * Tests the class
     */
    public void testPositions() {
        TextPositionsMapper m = new TextPositionsMapper(
            "\n" + 
            "This is the first line\n" + 
            "This is the second line\r\n" + 
            "This is the third line\n" +
            "\r" + 
            "The fifth line");
        int[] pos = new int[2];

        m.findPosition(0, pos);
        assertEquals(0, pos[0]);
        assertEquals(0, pos[1]);

        m.findPosition(1, pos);
        assertEquals(1, pos[0]);
        assertEquals(0, pos[1]);

        m.findPosition(20, pos);
        assertEquals(1, pos[0]);
        assertEquals(19, pos[1]);

        m.findPosition(24, pos);
        assertEquals(2, pos[0]);
        assertEquals(0, pos[1]);

        m.findPosition(49, pos);
        assertEquals(3, pos[0]);
        assertEquals(0, pos[1]);

        m.findPosition(73, pos);
        assertEquals(5, pos[0]);
        assertEquals(0, pos[1]);
    }
}
