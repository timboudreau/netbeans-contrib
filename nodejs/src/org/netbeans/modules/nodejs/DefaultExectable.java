/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tim Boudreau
 */
@ServiceProvider(service = NodeJSExecutable.class)
public final class DefaultExectable extends NodeJSExecutable {

    private static final String NODE_EXE_KEY = "nodejs_binary";
    private static final String PORT_KEY = "port";

    private Preferences preferences() {
        return NbPreferences.forModule(NodeJSExecutable.class);
    }

    public String getNodeExecutable(boolean showDialog) {
        Preferences p = preferences();
        String loc = p.get(NODE_EXE_KEY, null);
        if (loc == null) {
            if (loc == null) {
                File f = new File("usr/local/bin/node");
                if (f.exists()) {
                    loc = f.getAbsolutePath();
                }
            }
            if (loc == null) {
                loc = lookForNodeExecutable(showDialog);
            }
        }
        return loc;
    }

    public int getDefaultPort() {
        return preferences().getInt(PORT_KEY, 9080);
    }

    public void setDefaultPort(int val) {
        assert val > 0 && val < 65536;
        preferences().putInt(PORT_KEY, val);
    }

    public void setNodeExecutable(String location) {
        if (location != null && "".equals(location.trim())) {
            location = null;
        }
        preferences().put(NODE_EXE_KEY, location);
    }

    @Override
    protected Future<Integer> doRun(FileObject file) throws IOException {
        File f = FileUtil.toFile(file);
        String executable = getNodeExecutable(true);
        if (executable == null) {
            StatusDisplayer.getDefault().setStatusText(
                    NbBundle.getMessage(DefaultExectable.class, "NO_BINARY"));
            Toolkit.getDefaultToolkit().beep();
            return null;
        }
        ExternalProcessBuilder b = new ExternalProcessBuilder(executable).addArgument(f.getAbsolutePath()).redirectErrorStream(true).workingDirectory(f.getParentFile());
        ExecutionDescriptor des = new ExecutionDescriptor().showProgress(true).frontWindow(true).outLineBased(true).controllable(true).errLineBased(true).errConvertorFactory(new LineConverter()).outLineBased(true).outConvertorFactory(new LineConverter());
        ExecutionService service = ExecutionService.newService(b, des, file.getName());
        return service.run();
    }

    private String lookForNodeExecutable(boolean showDialog) {
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(
                DefaultExectable.class, "LOOK_FOR_EXE")); //NOI18N
        String pathToBinary = runExternal("which", "node"); //NOI18N
        if (pathToBinary == null) {
            pathToBinary = runExternal("which", "nodejs"); //NOI18N
        }
        if (pathToBinary == null && showDialog) {
            pathToBinary = askUserForExecutableLocation();
        }
        if (pathToBinary != null) {
            preferences().put(NODE_EXE_KEY, pathToBinary);
        }
        return pathToBinary;
    }

    private String runExternal(String... cmdline) {
        ProcessBuilder b = new ProcessBuilder(cmdline);
        try {
            Process p = b.start();
            try {
                InputStream in = p.getInputStream();
                p.waitFor();
                if (p.exitValue() == 0) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    FileUtil.copy(in, out);
                    String result = new String(out.toByteArray()).trim(); //trim off \n
                    return result.length() == 0 ? null : result;
                }
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
                Thread.currentThread().interrupt(); //reset the flag
                return null;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        return null;
    }

    public String askUserForExecutableLocation() {
        File f = new FileChooserBuilder(DefaultExectable.class).setTitle(NbBundle.getMessage(DefaultExectable.class, "LOCATE_EXECUTABLE")).setFilesOnly(true).setApproveText(NbBundle.getMessage(DefaultExectable.class, "LOCATE_EXECUTABLE_APPROVE")).showOpenDialog();
        return f == null ? null : f.getAbsolutePath();
    }

    public void setSourcesLocation(String location) {
        if (location != null && "".equals(location.trim())) {
            location = null;
        }
        preferences().put("sources", location);
    }

    public String getSourcesLocation() {
        return preferences().get("sources", null);
    }
}
