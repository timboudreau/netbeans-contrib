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

package org.netbeans.modules.tasklist.usertasks.model;

import junit.framework.Test;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

/**
 * Tests for Duration.
 * 
 * @author tl
 */
public class DurationTest extends NbTestCase {
    public DurationTest (String name) {
        super (name);
    }

    public static Test suite () {
        return new NbTestSuite(TaskListTest.class);
    }

    public void testDummy() throws Exception {
        Duration d = new Duration(8 * 60, 8 * 60, 7, true);
        assertTrue(d.equals(new Duration(0, 1, 0, 0)));
        d = new Duration(60, 8 * 60, 5, true);
        assertTrue(d.equals(new Duration(0, 0, 1, 0)));
    }
}
