/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.remote.project.finder;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service = OptionProcessor.class)
public final class Options extends OptionProcessor {

    private static final int WORKSPACE_DEPTH = 0;
    private static final int DEFAULT_DEPTH_IN_WORKSPACE = 2;

    private static final Option WORK_SPACE = Option.requiredArgument(
        Option.NO_SHORT_NAME,
        "workspace"); //NOI18N

    private static final Option DEPTH = Option.requiredArgument(
        Option.NO_SHORT_NAME,
        "depth");

    public Options() {
    }    

    @Override
    @NonNull
    protected Set<Option> getOptions() {
        return Collections.unmodifiableSet(new HashSet<Option>(
            Arrays.asList(
                WORK_SPACE,
                DEPTH,
                Option.always())));
    }

    @Override
    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        String[] folders = optionValues.get(WORK_SPACE);
        if (folders == null || folders.length != 1) {
            if (System.getProperty("netbeans.full.hack") != null) { // NOI18N
                PrintStream err = env.getErrorStream();
                err.println("WARNING: No workspace given. But seems to be running in tests, going on...");
                return;
            }
            error(env, -1, "No workspace given.");
        }
        final File f = FileUtil.normalizeFile(new File(folders[0]));
        if (!f.isDirectory() || !f.canRead()) {
            error(env, -2, "Workspace does not exist.");
        }
        int depth = DEFAULT_DEPTH_IN_WORKSPACE;
        String[] depthStr = optionValues.get(DEPTH);
        if (depthStr != null) {
            if (depthStr.length != 1) {
                error(env, -3, "Wrong workspace depth.");
            }
            try {
                depth = Integer.parseInt(depthStr[0]);
                if (depth < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                error(env, -3, "Wrong workspace depth.");
            }
        }
        WorkSpaceUpdater.getDefault().configure(f, WORKSPACE_DEPTH + depth);
    }

    private static void error(
        @NonNull final Env env,
        final int retCode,
        @NonNull final String message) throws CommandException {
        env.usage();
        throw new CommandException(retCode, message);
    }
}
