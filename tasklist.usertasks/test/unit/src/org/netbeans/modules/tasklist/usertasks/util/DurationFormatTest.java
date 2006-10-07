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

package org.netbeans.modules.tasklist.usertasks.util;

import junit.framework.TestCase;
import junit.framework.*;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.openide.util.NbBundle;

/**
 * Tests for DurationFormat.
 * 
 * @author tl
 */
public class DurationFormatTest extends TestCase {    
    public DurationFormatTest(String testName) {
        super(testName);
    }

    /**
     * Test of parse method, of class 
     * org.netbeans.modules.tasklist.usertasks.util.DurationFormat.
     */
    public void testParse() throws ParseException {
        DurationFormat instance = new DurationFormat(DurationFormat.Type.SHORT);
        assertEquals("01:00", instance.format(instance.parse("1:00")));
        assertEquals("1d 01:03", instance.format(instance.parse("1d 1:03")));
        assertEquals("1w 2d 21:03", instance.format(instance.parse("1w 2d 21:03")));
        assertEquals("3w 20:03", instance.format(instance.parse("3w 20:03")));
        assertEquals("1w", instance.format(instance.parse("1w")));
        instance = new DurationFormat(DurationFormat.Type.LONG);
        assertEquals("1 hour", instance.format(instance.parse("1 hour")));
        assertEquals("2 days", instance.format(instance.parse("2 days")));
        assertEquals("2 weeks 1 hour", instance.format(instance.parse("2 weeks 1 hour")));
        assertEquals("2 weeks 1 day 7 hours 4 minutes", instance.format(instance.parse("2 weeks 1 day 7 hours 4 minutes")));
    }
}
