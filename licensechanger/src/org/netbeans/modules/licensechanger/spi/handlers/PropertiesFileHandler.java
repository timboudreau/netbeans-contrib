/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.licensechanger.spi.handlers;

import org.netbeans.modules.licensechanger.api.FileHandler;
import org.netbeans.modules.licensechanger.spi.wizard.utils.Offsets;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * License transformer for properties files
 *
 * @author Tim Boudreau
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.licensechanger.api.FileHandler.class)
public class PropertiesFileHandler extends FileHandler {

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage (JavaFileHandler.class, "NAME_PROPERTIES_FILES"); //NOI18N
    }

    @Override
    public boolean match(FileObject file) {
        return "properties".equals (file.getExt());
    }

    @Override
    public boolean shouldSkipFile(FileObject file) {
        String n = file.getNameExt();
        return "project.properties".equals(n) || "private.properties".equals(n) || //NOI18N
                "platform-private.properties".equals(n) || "genfiles.properties".equals(n)
                || "config.properties".equals(n) || "nblibraries-private.properties".equals(n) ||
                "nblibraries.properties".equals(n); //NOI18N
    }

    @Override
    protected Offsets getReplaceOffsets(CharSequence seq) {
        String[] lines = splitIntoLines(seq);
        int end = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().length() == 0) {
                end += line.length() + 1;
            } else if (line.trim().startsWith("#")) {
                end += line.length() + 1;
            } else {
                break;
            }
        }
        return new Offsets (0, end);
    }

    @Override
    protected String escape(String licenseText) {
        String[] lines = splitIntoLines(licenseText);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().length() == 0) {
                sb.append ("#");
            } else {
                sb.append ("# ");
                sb.append (line);
            }
            sb.append ("\n");
        }
        return sb.toString();
    }

    @Override
    protected String licenseFirst() {
        return licensePrefix();
    }

    @Override
    protected String licensePrefix() {
        return "# ";
    }

    @Override
    protected String licenseLast() {
        return licensePrefix();
    }
}
