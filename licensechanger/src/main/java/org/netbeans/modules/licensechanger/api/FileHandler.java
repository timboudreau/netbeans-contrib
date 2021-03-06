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
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.licensechanger.wizard.utils.WizardProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 * @author Nils Hoffmann (Refactoring, Freemarker Variables,
 * resolveLicenseTemplate)
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
     * <p>
     * Sets the <code>name</code>, <code>nameAndExt</code>,
     * <code>package</code>, and <code>encoding</code> properties.</p>
     *
     * <p>
     * The <code>package</code> binding may be null for files that do not have a
     * class path associated to them.</p>
     *
     * @param file the file object to retrieve the properties for
     * @param properties the current properties, existing properties will be
     * replaced
     */
    public void setFileProperties(FileObject file, Map<String, Object> properties) {
        //set name and nameAndExt properties
        properties.put("name", file.getName());
        properties.put("nameAndExt", file.getNameExt());
        //set package property
        ClassPath classPath = ClassPath.getClassPath(file, ClassPath.SOURCE);
        if (classPath != null) {
            String packageName = classPath.getResourceName(file, '.', false);
            properties.put("package", packageName);
        } else {
            //set to null to allow conditional handling in license template
            properties.put("package", null);
        }
        //set encoding property
        Charset enc = FileEncodingQuery.getEncoding(file);
        properties.put("encoding", enc.name());
    }

    /**
     * <p>
     * Uses the freemarker template engine to resolve the licenseText.</p>
     *
     * <p>
     * If no freemarker script engine is found, a {@link RuntimeException} is
     * thrown.</p>
     *
     * <p>
     * This method will access user information under
     * {@code Templates/Properties/User.properties} in order to resolve various
     * tokens, e.g. 'user'. If that file does not exist, a
     * {@link RuntimeException} will be thrown, wrapping the original
     * {@link IOException}. Should the freemarker engine encounter a
     * {@link ScriptException}, this will also be wrapped in a
     * {@link RuntimeException}.</p>
     *
     * <p>
     * The additionalBindings parameter may be used to supplement or override
     * the information retrieved from <code>User.properties</code>.</p>
     *
     * <p>
     * The implementation will look for tokens <code>${licenseFirst}</code>,
     * <code>${licensePrefix}</code>, and <code>${licenseLast}</code> to
     * determine whether the licenseText has already been interpolated or if it
     * needs escaping.
     *
     * @param licenseText the license text with freemarker template tokens
     * @param additionalBindings additional binding values for freemarker
     * interpolation
     * @return the resolved licenseText
     * @throws RuntimeException
     */
    public String resolveLicenseTemplate(String licenseText, Map<String, Object> additionalBindings) throws RuntimeException {
        if (licenseText.contains("${") || (licenseText.contains("${licenseFirst}") && licenseText.contains("${licensePrefix}") && licenseText.contains("${licenseLast}"))) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.FINE, "License is a freemarker template!");
            Project selectedProject = (Project) additionalBindings.get(WizardProperties.KEY_PROJECT);
            //freemarker template
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine se = sem.getEngineByName("freemarker");
            if (se != null) {
                Bindings bindings = se.createBindings();
                //parse settings from User.properties
                FileObject userProperties = FileUtil.getConfigFile("Templates/Properties/User.properties");
                Properties props = null;
                if (userProperties != null) {
                    props = new Properties();
                    InputStream in = null;
                    try {
                        in = userProperties.getInputStream();
                        props.load(in);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                        throw new RuntimeException(ex);
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                            throw new RuntimeException(ex);
                        }
                    }
                    //set default bindings from User.properties
                    for (String key : props.stringPropertyNames()) {
                        bindings.put(key, props.getProperty(key));
                    }
                } else {
                    Logger.getLogger(FileHandler.class.getName()).warning("Could not retrieve config file Templates/Properties/User.properties! Using empty properties!");
                    props = new Properties();
                }
                //add license template specific bindings
                bindings.put("licenseFirst", licenseFirst());
                bindings.put("licensePrefix", licensePrefix());
                bindings.put("licenseLast", licenseLast());
                Date date = new Date();
                //add date, if it does not exist
                if (!bindings.containsKey("date")) {
                    bindings.put("date", date);
                } else {
                    Object o = bindings.get("date");
                    if (o instanceof Date) {
                        bindings.put("date", (Date) o);
                    } else if (o instanceof String) {
                        try {
                            bindings.put("date", DateFormat.getInstance().parse((String) o));
                        } catch (ParseException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                //add time from date
                if (bindings.containsKey("time")) {
                    Object o = bindings.get("time");
                    if (o instanceof Date) {
                        bindings.put("time", (Date) date);
                    } else if (o instanceof String) {
                        try {
                            bindings.put("time", DateFormat.getInstance().parse((String) o));
                        } catch (ParseException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                } else {
                    bindings.put("time", bindings.get("date"));
                }
                for (Map.Entry<String, Object> e : additionalBindings.entrySet()) {
                    Logger.getLogger(FileHandler.class.getName()).log(Level.FINE, "Adding binding: {0}={1}", new Object[]{e.getKey(), e.getValue()});
                    bindings.put(e.getKey(), e.getValue());
                }
                String user = additionalBindings.containsKey(WizardProperties.KEY_COPYRIGHT_HOLDER) ? (String) additionalBindings.get(WizardProperties.KEY_COPYRIGHT_HOLDER) : props.getProperty("user", System.getProperty("user.name"));
                Logger.getLogger(FileHandler.class.getName()).log(Level.FINE, "Using user: {0}", user);
                if (props.containsKey("project.organization")) {
                    org.netbeans.modules.licensechanger.freemarker.Project project = new org.netbeans.modules.licensechanger.freemarker.Project(props.getProperty("project.organization"), selectedProject);
                    Logger.getLogger(FileHandler.class.getName()).log(Level.FINE, "Using project.organization: {0}", props.getProperty("project.organization"));
                    bindings.put("project", project);
                    bindings.put("user", user);
                } else {
                    bindings.put("project", new org.netbeans.modules.licensechanger.freemarker.Project(null, selectedProject));
                    if (!bindings.containsKey("user")) {
                        bindings.put("user", user);
                    }
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
            Logger.getLogger(FileHandler.class.getName()).log(Level.FINE, "License is already escaped!");
            //probably already escaped
            return licenseText;
        } else {
            Logger.getLogger(FileHandler.class.getName()).log(Level.FINE, "License header is plain!");
            //unescaped
            return escape(licenseText);
        }
    }

    public String transform(String origText, String licenseText, Map<String, Object> bindings) {
        Offsets offsets = getReplaceOffsets(origText);
        Logger.getLogger(FileHandler.class.getName()).log(Level.FINE, "Will delete from {0} to {1}", new Object[]{offsets.getStart(), offsets.getEnd()});
        StringBuilder after = new StringBuilder(origText);
        String escaped = resolveLicenseTemplate(licenseText, bindings);
        after.delete(offsets.getStart(), offsets.getEnd());
        after.insert(offsets.getStart(), escaped);
        return after.toString();
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
