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

package complete.vss_profile;

import junit.framework.*;
import org.netbeans.junit.*;
import org.openide.util.Utilities;

/** XTest / JUnit test class performing testing of whole VSS support.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class AllTogether extends NbTestCase {

    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public AllTogether(String testName) {
        super(testName);
    }

    /** Method used for explicit test suite definition.
     * @return AllTogether test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        String zipFile = "C:\\Program Files\\Microsoft Visual Studio\\vss.zip";
        if (!new java.io.File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTestSuite(RepositoryCreation.class);
        suite.addTestSuite(RegularDevelopment.class);
        suite.addTestSuite(AdditionalCommands.class);
        suite.addTestSuite(AdditionalFeatures.class);
        return suite;
    }
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new java.io.File(workingDir).mkdirs();
        java.io.File outputFile = new java.io.File(workingDir + "/output.txt");
        outputFile.createNewFile();
        java.io.File errorFile = new java.io.File(workingDir + "/error.txt");
        errorFile.createNewFile();
        java.io.PrintWriter outputWriter = new java.io.PrintWriter(new java.io.FileWriter(outputFile));
        java.io.PrintWriter errorWriter = new java.io.PrintWriter(new java.io.FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }

    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}