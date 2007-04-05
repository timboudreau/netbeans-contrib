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

package org.netbeans.modules.jackpot;

import java.io.File;
import org.netbeans.api.jackpot.test.TestUtilities;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.jackpot.ScriptParsingException;

/** Having a variable on RHS which is not on left shall yeild an error.
 *
 * @author Jaroslav Tulach
 */
public class ExtraRHSArgsShallYieldAnErrorTest extends NbTestCase {
    
    public ExtraRHSArgsShallYieldAnErrorTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
    }

    protected void tearDown() throws Exception {
    }

    public void testCanMatchProperUsage() throws Exception {
        File api = new File(getWorkDir(), "API.java");
        TestUtilities.copyStringToFile(api, 
            "package x.y;\n" +
            "class API {\n" +
            "  public void annotate(Throwable t, String msg, String locMsg, Throwable t2, java.util.Date d) {\n" +
            "  }\n" +
            "  public static void newMethod(Throwable t, String msg, String locMsg, Throwable t2, java.util.Date d) {\n" +
            "  }\n" +
            "}\n"
            );
        String code = 
            "package x.y;\n" +
            "class CallsTheMethod {\n" +
            "  static {\n" +
            "    API a = null;\n" +
            "    a.annotate(new java.io.IOException(), null, \"ahoj\", null, null);\n" +
            "  }\n" +
            "}\n";
        
        String rule = 
            "$API.annotate($throw, null, $locmsg, null, null) =>\n" +
            "  $API.newMethod($throw, $msg, $locmsg, $cause, $date)" +
            "  :: $throw instanceof java.lang.Throwable, " +
            "     $locmsg instanceof java.lang.String || isNull($cause);" +
            "\n";
        
        File java = new File(getWorkDir(), "CallsTheMethod.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);

        try {
            TestUtilities.applyRules(getWorkDir(), ruleFile.toURI().toURL());
        } catch (ScriptParsingException ex) {
            // ok, the $cause and $date are undefined on RHS and thus the 
            // rule is wrong
            return;
        }

        fail("The rule is wrong, and the build of it shall fail");
    }

}
