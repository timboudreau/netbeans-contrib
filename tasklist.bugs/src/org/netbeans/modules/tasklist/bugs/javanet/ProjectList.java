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

package org.netbeans.modules.tasklist.bugs.javanet;

import org.netbeans.modules.tasklist.bugs.ProjectDesc;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Lists all projects on java.net.
 *
 * @author Petr Kuzel
 */
public final class ProjectList {

    public static ProjectDesc[] listProjects() {
        List components = new ArrayList(23);
        try {
            URL list = new URL("http://community.java.net/projects/alpha.csp?only=hosted");
            URLConnection io = list.openConnection();
            io.connect();
            InputStream in = new BufferedInputStream(io.getInputStream());
            int next = in.read();
            StringBuffer sb = new StringBuffer();
            while (next != -1) {
                sb.append((char) next);
                next = in.read();
            }

            // parse output looking for componet names by MAGIC

            String sample = sb.toString();
            String MAGIC = "<p><b><a href=\"https://";  // NOi18N

            int entry = 0;
            int end = -1;
            while (true) {
                entry = sample.indexOf(MAGIC, entry);
                if (entry == -1) break;
                end = sample.indexOf(".", entry);  // .dev.java.net
                if (entry == -1) break;
                String component = sample.substring(entry + MAGIC.length(), end);
                entry = end;
                ProjectDesc desc = new ProjectDesc();
                desc.name = component;
                components.add(desc);
            }
            return (ProjectDesc[]) components.toArray(new ProjectDesc[components.size()]);

        } catch (MalformedURLException e) {
            return new ProjectDesc[0];
        } catch (IOException e) {
            return new ProjectDesc[0];
        }
    }
}
