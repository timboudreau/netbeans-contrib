/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
