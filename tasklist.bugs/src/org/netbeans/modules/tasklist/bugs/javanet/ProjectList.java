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
