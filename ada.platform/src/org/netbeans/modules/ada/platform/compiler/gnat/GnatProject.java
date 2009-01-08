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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.ada.platform.compiler.gnat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import org.netbeans.modules.ada.platform.compiler.gnat.commands.GnatCommand;

public class GnatProject {

    private final GnatCompiler gnat;
    private String gprFilePath;

    public GnatProject(final GnatCompiler gnat) {
        this.gnat = gnat;
    }

    public void write() {
        cleanup();
        writeGprfileImpl();
    }

    private void cleanup() {
        // Remove all *.gpr files
        File folder = new File(gnat.getProjectPath() + '/' + "nbproject"); // UNIX path // NOI18N
        File[] children = folder.listFiles();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                if (children[i].getName().startsWith("Gprfile-impl")) { // NOI18N
                    children[i].delete();
                }
            }
        }
    }

    private void writeGprfileImpl() {
        String resource = "/org/netbeans/modules/ada/platform/resources/MasterGprfile-impl.gpr"; // NOI18N
        InputStream is = null;
        FileOutputStream os = null;
        try {
            URL url = new URL("nbresloc:" + resource); // NOI18N
            is = url.openStream();
        } catch (Exception e) {
            is = GnatCommand.class.getResourceAsStream(resource);
        }

        gprFilePath = gnat.getProjectPath() + '/' + "nbproject" + '/' + gnat.getProjectName() + ".gpr"; // UNIX path // NOI18N
        try {
            os = new FileOutputStream(gprFilePath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (is == null || os == null) {
            // FIXUP: ERROR
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

        // Project name
        String projectName = gnat.getProjectName();
        String mainFile = gnat.getMainFile();
        String execFile = gnat.getExecutableFile();

        try {
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.indexOf("<PN>") >= 0) { // NOI18N
                    line = line.replaceFirst("<PN>", projectName); // NOI18N
                }
                if (line.indexOf("<MAINFILE>") >= 0) { // NOI18N
                    line = line.replaceFirst("<MAINFILE>", mainFile); // NOI18N
                }
                if (line.indexOf("<EXECFILE>") >= 0) { // NOI18N
                    line = line.replaceFirst("<EXECFILE>", execFile); // NOI18N
                }
                bw.write(line + "\n"); // NOI18N
            }
            br.close();
            bw.flush();
            bw.close();
        } catch (Exception e) {
        }

    }

    public String getGprFilePath() {
        return this.gprFilePath;
    }
}
