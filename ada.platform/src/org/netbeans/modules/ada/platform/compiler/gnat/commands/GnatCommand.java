/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.platform.compiler.gnat.commands;

import org.netbeans.api.ada.platform.AdaException;
import org.netbeans.api.ada.platform.AdaPlatform;

/**
 * 
 * @author Andrea Lucarelli
 */
public abstract class GnatCommand {

    // List of GNAT available commands
    public static final String GNAT_BIND = "gnatbind"; // NOI18N
    public static final String GNAT_CHOP = "gnatchop"; // NOI18N
    public static final String GNAT_CLEAN = "gnatclean"; // NOI18N
    public static final String GNAT_COMPILE = "gnatmake -f -u -c"; // NOI18N
    public static final String GNAT_ELIM = "gnatelim"; // NOI18N
    public static final String GNAT_FIND = "gnatfind"; // NOI18N
    public static final String GNAT_KRUNCH = "gnatkr"; // NOI18N
    public static final String GNAT_LINK = "gnatlink"; // NOI18N
    public static final String GNAT_LIST = "gnatls"; // NOI18N
    public static final String GNAT_MAKE = "gnatmake"; // NOI18N
    public static final String GNAT_NAME = "gnatname"; // NOI18N
    public static final String GNAT_PREPROCESS = "gnatprep"; // NOI18N
    public static final String GNAT_PRETTY = "gnatpp"; // NOI18N
    public static final String GNAT_STUB = "gnatstub"; // NOI18N
    public static final String GNAT_XREF = "gnatxref"; // NOI18N

    private final AdaPlatform platform;
    private final String projectPath;
    private final String objectFolder;
    private final String sourceFolder;
    private final String mainFile;
    private final String executableFile;
    private final String displayName;

    public GnatCommand(AdaPlatform platform, String projectPath, String objectFolder, String sourceFolder, String mainFile, String executableFile, String displayName) {
        this.platform = platform;
        this.projectPath = projectPath;
        this.objectFolder = objectFolder;
        this.sourceFolder = sourceFolder;
        this.mainFile = mainFile;
        this.executableFile = executableFile;
        this.displayName = displayName;
    }

    public abstract String getCommandId();

    public abstract void invokeCommand() throws IllegalArgumentException, AdaException;

    public AdaPlatform getPlatform() {
        return platform;
    }

    public String getExecutableFile() {
        return executableFile;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getMainFile() {
        return mainFile;
    }

    public String getObjectFolder() {
        return objectFolder;
    }

    public String getSourceFolder() {
        return sourceFolder;
    }

    public String getDisplayName() {
        return displayName;
    }

}
