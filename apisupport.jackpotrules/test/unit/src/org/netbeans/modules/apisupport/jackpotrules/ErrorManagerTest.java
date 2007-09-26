/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.apisupport.jackpotrules;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netbeans.junit.NbTestCase;

/**
 *
 * @author Jaroslav Tulach
 */
public final class ErrorManagerTest extends NbTestCase {

    private URL script;


    public ErrorManagerTest(String s) {
        super(s);
    }

    protected Level logLevel() {
        return Level.INFO;
    }

    protected void setUp() throws Exception {
        clearWorkDir();
        script = ErrorManagerTest.class.getResource("/org/netbeans/modules/apisupport/jackpotrules/annotate-to-initcause.rules");
        assertNotNull(script);
    }

    public void testRenameNotifyToPst() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "   Exception e = new Exception();\n" +
            "   org.openide.ErrorManager.getDefault().notify(e);\n" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }
    }

    public void testErrMgrAnnoMsg() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "import javax.swing.JButton;" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException npe = new NullPointerException();\n" +
            "       org.openide.ErrorManager.getDefault().annotate(" +
            "       npe, \"localizedText\");" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }

        if (txt.indexOf("attachLocalizedMessage") < 0) {
            fail("There should be a localized message usage:\n" + txt);
        }
    }
    public void testErrMgrUnknown() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "import javax.swing.JButton;" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException npe = new NullPointerException();\n" +
            "       org.openide.ErrorManager.getDefault().annotate(" +
            "           npe, org.openide.ErrorManager.UNKNOWN," +
            "           \"msg\", null, null, null);" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }
    }

    public void testErrMgrException() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "import javax.swing.JButton;" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       JButton r = new JButton();\n" +
            "       String icon = null;\n" +
            "       NullPointerException npe = new NullPointerException();\n" +
            "       org.openide.ErrorManager.getDefault().annotate(" +
            "       npe, org.openide.ErrorManager.EXCEPTION," +
            "       \"Probably an ImageIcon with a null source image: \" + icon + \" - \" + //NOI18N\n" +
            "       r.getText(), null, null, null\n" +
            "); //NOI18N\n" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }
    }
    public void testConvertToWarning() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException e = new NullPointerException();\n" +
            "       org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.EXCEPTION, e);" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }
        if (!txt.replace('\n', ' ').matches(".*Logger.getLogger *\\( *\"global *\"\\).log *\\(Level.WARNING, *null, *e *\\).*")) {
            fail("Logger.global shall be there:\n" + txt);
        }
    }

    public void testConvertToLogging() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException e = new NullPointerException();\n" +
            "       org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, e);" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }
        if (!txt.replace('\n', ' ').matches(".*Logger.getLogger *\\( *\"global\" *\\).log *\\(Level.INFO, *null, *e *\\).*")) {
            fail("Logger.global shall be there:\n" + txt);
        }
    }

    public void testGetRidOfCopyAnnotation() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException npe = new NullPointerException();\n" +
            "       java.io.IOException io = null;\n" +
            "       org.openide.ErrorManager.getDefault().copyAnnotation(npe, io);" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }

        if (!txt.replace("\n", " ").matches(".*npe.initCause *\\( *io *\\).*")) {
            fail("initCause shall be used:\n" + txt);
        }
    }

    public void testNonLocalizedAnnotation() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException npe = new NullPointerException();\n" +
            "       String msg = \"nonlocmsg\";\n" +
            "       org.openide.ErrorManager.getDefault().annotate(npe, org.openide.ErrorManager.UNKNOWN, null, msg, null, null);" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }

        if (!txt.replace("\n", " ").matches(".*Exceptions.attachLocalizedMessage.*")) {
            fail("initCause shall be used:\n" + txt);
        }
    }


    public void testFriendContractWithOwnLevel() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException npe = new NullPointerException();\n" +
            "       String msg = \"nonlocmsg\";\n" +
            "       org.openide.ErrorManager.getDefault().annotate(npe, " +
            "           org.openide.ErrorManager.USER, msg == null ? \"\" : npe.getMessage()," +
            "           msg, npe, new java.util.Date()); //NOI18N\n" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }

        if (!txt.replace("\n", " ").matches(".*UIException.annotateUser *\\( *npe.*")) {
            fail("UIException shall be used:\n" + txt);
        }
    }

    public void testWarningGoesToLogging() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException npe = new NullPointerException();\n" +
            "       org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.WARNING, npe);" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }

        if (!txt.replace('\n', ' ').matches(".*Logger.getLogger *\\( *\"global\" *\\).log *\\(Level.WARNING, *null, *npe *\\).*")) {
            fail("Logger.global shall be there:\n" + txt);
        }
    }

    public void testAnnotateWithInfo() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       NullPointerException cnfe = new NullPointerException();\n" +
            "       String msg = \"ahoj\";\n" +
            "       org.openide.ErrorManager.getDefault().annotate(cnfe, org.openide.ErrorManager.INFORMATIONAL, msg, null, null, null);" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }

        if (!txt.replace('\n', ' ').matches(".*Exceptions.attachMessage *\\(*cnfe, *msg\\).*")) {
            fail("Exceptions.attachMessage shall be there:\n" + txt);
        }

    }

    public void testRenamesEditor() throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "class A {\n" +
            " public static void main(String[] args) {\n" +
            "       try {" +
            "          System.exit(0);\n" +
            "        } catch( IllegalArgumentException iaE ) {" + 
            "           org.openide.ErrorManager.getDefault().annotate(iaE, org.openide.ErrorManager.USER, null, iaE.getLocalizedMessage(), null, null);\n" +
            "           throw iaE;\n" + 
            "        }" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        if (txt.indexOf("ErrorManager") >= 0) {
            fail("No ErrorManager shall be there:\n" + txt);
        }

        if (!txt.replace('\n', ' ').matches(".*UIException.*")) {
            fail("UIException shall be there:\n" + txt);
        }
        if (txt.replace('\n', ' ').indexOf("$") >= 0) {
            fail("NO $ shall be there:\n" + txt);
        }
    }
    
    
    public void testUsingAttachMessageAndThenInitCauseIsNotGoodIdea() throws Exception {
        doUsingAttachMessageAndThenInitCauseIsNotGoodIdea("attachMessage");
    }
    
    public void testUsingAttachLocMessageAndThenInitCauseIsNotGoodIdea() throws Exception {
        doUsingAttachMessageAndThenInitCauseIsNotGoodIdea("attachLocalizedMessage");
    }
    
    private void doUsingAttachMessageAndThenInitCauseIsNotGoodIdea(String method) throws Exception {
        File f = JackpotUtils.extractString(new File(getWorkDir(), "A.java"),
            "package test;\n" +
            "import java.io.IOException;\n" +
            "class A {\n" +
            " public static void main(String[] args) throws IOException {\n" +
            "       try {" +
            "          System.exit(0);\n" +
            "        } catch( IllegalArgumentException iaE ) {" + 
            "           IOException io = new IOException();" +
            "           org.openide.util.Exceptions." + method + "(io, \"msg\");\n" +
            "           iaE.printStackTrace();\n" + 
            "           io.initCause(iaE);\n" +
            "           throw io;\n" + 
            "        }" +
            " }\n" +
            "}\n"
        );

        JackpotUtils.apply(getWorkDir(), script);

        String txt = JackpotUtils.readFile(f, false);

        Matcher m = Pattern.compile("initCause.*" + method, Pattern.DOTALL).matcher(txt);
        if (!m.find()) {
            fail("First of all we should use initCause and then attachMessage:\n" + txt);
        }
    }
    
}
