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
package org.netbeans.modules.licensechanger.api;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.netbeans.modules.licensechanger.spi.wizard.utils.Offsets;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 * @author Nils Hoffmann (Refactoring, Freemarker Variables, resolveLicenseTemplate)
 */
public abstract class FileHandler {

    public abstract boolean match(FileObject file);

    public abstract boolean shouldSkipFile(FileObject file);

    public abstract String getDisplayName();

    protected abstract Offsets getReplaceOffsets(CharSequence seq);

    protected abstract String escape(String licenseText);

    protected abstract String licenseFirst();

    protected abstract String licensePrefix();

    protected abstract String licenseLast();

    /**
     * Uses the freemarker template engine to resolve the licenseText. If no
     * freemarker script engine is found, a {@link RuntimeException} is thrown.
     * This method will access user information under {@code Templates/Properties/User.properties}
     * in order to resolve various tokens, e.g. 'user'. If that file does not
     * exist, a {@link RuntimeException} will be thrown, wrapping the original {@link IOException}.
     * Should the freemarker engine encounter a {@link ScriptException}, this
     * will also be wrapped in a {@link RuntimeException}.
     *
     * @param licenseText
     * @return the resolved licenseText
     * @throws RuntimeException
     */
    public String resolveLicenseTemplate(String licenseText) throws RuntimeException {
        if (licenseText.contains("${licenseFirst}") && licenseText.contains("${licensePrefix}") && licenseText.contains("${licenseLast}")) {
            System.out.println("License is a freemarker template!");
            //freemarker template
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine se = sem.getEngineByName("freemarker");
            if (se != null) {
                Bindings bindings = se.createBindings();
                FileObject licenseTemplates = FileUtil.getConfigFile("Templates/Properties/User.properties");
                Properties props = new Properties();
                try {
                    props.load(licenseTemplates.getInputStream());
                    for (String key : props.stringPropertyNames()) {
                        bindings.put(key, props.getProperty(key));
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    throw new RuntimeException(ex);
                }
                bindings.put("licenseFirst", licenseFirst());
                bindings.put("licensePrefix", licensePrefix());
                bindings.put("licenseLast", licenseLast());
                bindings.put("date", new Date());
                if (props.containsKey("project.organization")) {
                    Project project = new Project(props.getProperty("project.organization"));
                    System.out.println("Using project.organization: " + props.getProperty("project.organization"));
                    bindings.put("project", project);
                    bindings.put("user", null);
                } else {
                    String user = props.getProperty("user", System.getProperty("user.name"));
                    System.out.println("Using user: " + user);
                    bindings.put("project", new Project(null));
                    bindings.put("user", user);
                }
                StringWriter writer = new StringWriter();
                try {
                    se.getContext().setWriter(writer);
                    se.eval(licenseText, bindings);
                    return writer.toString();
                } catch (ScriptException ex) {
                    Exceptions.printStackTrace(ex);
                    throw new RuntimeException(ex);
                }
            } else {
                throw new RuntimeException("Could not find script engine 'freemarker'!");
            }
        } else if (licenseText.contains(licenseFirst()) && licenseText.contains(licensePrefix()) && licenseText.contains(licenseLast())) {
            System.out.println("License is already escaped!");
            //probably already escaped
            return licenseText;
        } else {
            System.out.println("License is plain!");
            //unescaped
            return escape(licenseText);
        }
    }

    public String transform(String origText, String licenseText) {
        Offsets offsets = getReplaceOffsets(origText);
        System.err.println("Will delete from " + offsets.getStart() + " to " + offsets.getEnd());
        StringBuilder after = new StringBuilder(origText);
        String escaped = resolveLicenseTemplate(licenseText);
        after.delete(offsets.getStart(), offsets.getEnd());
        after.insert(offsets.getStart(), escaped);
        return after.toString();
    }

    /**
     * Freemarker expects project.organization to be the field 'organization' in
     * an object. This provided a thin wrapper for that.
     */
    private class Project {

        public String organization;

        public Project(String organization) {
            this.organization = organization;
        }
    }
    static Pattern p = Pattern.compile("(.*?)\\n|\\z", Pattern.UNIX_LINES);

    public static String[] splitIntoLines(CharSequence content) {
        List<String> result = new ArrayList<String>(40);
        Matcher m = p.matcher(content);
        while (m.find()) {
            if (m.groupCount() == 1) {
                if (m.group(1) != null) {
                    result.add(m.group(1));
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
