/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package org.netbeans.modules.genericnavigator;

import junit.framework.*;
import java.io.File;

/**
 *
 * @author Tim Boudreau
 */
public class NavigationItemTest extends TestCase {
    public NavigationItemTest(String testName) { super(testName); }
    public static Test suite() {
        TestSuite suite = new TestSuite(NavigationItemTest.class);
        return suite;
    }

    public void testToString() {
        NavigationItem item = cni ("Hello world");
        assertEquals (item.txt, item.toString());
        item = cni ("This <b>is some <i>html</i> isn't </b> it?");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt +
                "'","This is some html isn't it?", item.toString());
        item = cni ("This text has a < character in it");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
        item = cni ("This is <<< very pathological <<< text but it should work");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
        item = cni ("This text is <<<< also pathological but it should work>");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
        item = cni ("This text is <<<< also even more >> pathological but it " +
                "should work>");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
    }

    private NavigationItem cni (String s) {
        NavigationItem item = new NavigationItem (null, -1, -1, s, true);
        return item;
    }
}
