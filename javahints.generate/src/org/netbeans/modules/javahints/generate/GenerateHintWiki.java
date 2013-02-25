/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javahints.generate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;
import org.netbeans.modules.java.hints.providers.spi.HintMetadata;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spiimpl.RulesManager;
import org.netbeans.spi.java.hints.Hint.Kind;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Refactoring", id = "org.netbeans.modules.javahints.GenerateHintWiki")
@ActionRegistration(displayName = "#CTL_GenerateHintWiki")
@ActionReferences({
    @ActionReference(path = "Menu/Refactoring", position = 1950, separatorAfter = 1975)
})
@Messages("CTL_GenerateHintWiki=Generate Hint Wiki")
public final class GenerateHintWiki implements ActionListener {

    private  static final String HINTS_FOLDER = "org-netbeans-modules-java-hints/rules/hints/";  // NOI18N

    public void actionPerformed(ActionEvent e) {
        System.out.println(generateWiki());
    }
    
    public static String generateWiki() {
        Map<String, String> since = readSinceData("rules");
        Map<String, List<HintMetadata>> category2Hint = new TreeMap<String, List<HintMetadata>>();

        for (HintMetadata hm : RulesManager.getInstance().readHints(null, null, null).keySet()) {
            FileObject catFO = FileUtil.getConfigFile(HINTS_FOLDER + hm.category);
            String categoryDisplayName = catFO != null ? getFileObjectLocalizedName(catFO) : hm.category;
            List<HintMetadata> categorized = category2Hint.get(categoryDisplayName);

            if (categorized == null) {
                category2Hint.put(categoryDisplayName, categorized = new ArrayList<HintMetadata>());
            }

            categorized.add(hm);
        }

        StringWriter baseOut = new StringWriter();
        PrintWriter out = new PrintWriter(baseOut);
        int totalHints = 0;
        int devHints = 0;
        int totalSuggestions = 0;
        int devSuggestions = 0;

        for (Entry<String, List<HintMetadata>> categoryEntry : category2Hint.entrySet()) {
            out.println("===" + categoryEntry.getKey() + "===");

            Collections.sort(categoryEntry.getValue(), new Comparator<HintMetadata>() {
                @Override public int compare(HintMetadata o1, HintMetadata o2) {
                    return o1.displayName.compareTo(o2.displayName);
                }
            });

            for (HintMetadata hm : categoryEntry.getValue()) {
                out.print(";'''" + hm.displayName + "'''");
                if (!hm.enabled) {
                    out.print("<span style='padding-left: 3em;font-size: 80%;color: #204a87'>[ Disabled by default ]</span>");
                }
                out.println();
                out.print(":" + hm.description);
                out.print("<span style='padding-left: 1em; font-size: 90%'>");
                if (since.containsKey(hm.id)) {
                    String sinceVersion = since.get(hm.id);

                    if (sinceVersion != null) {
                        out.print(" ''Since " + sinceVersion + "''");
                    } else {
                        out.print(" ''In NetBeans 6.8 or earlier''");
                    }
                } else {
                    out.print(" ''In current development version''");
                    if (hm.kind == Kind.INSPECTION) {
                        devHints++;
                    } else {
                        devSuggestions++;
                    }
                }
                out.print("</span>");
                out.println();
                out.println();
                if (hm.kind == Kind.INSPECTION) {
                    totalHints++;
                } else {
                    totalSuggestions++;
                }
            }
        }

        out.close();
        
        return "There are " + totalHints + " total hints/inspections, " + devHints + " of them in current development version only.\n\n" +
               "There are " + totalSuggestions + " total suggestions/actions, " + devSuggestions + " of them in current development version only.\n\n" +
               baseOut.toString();
    }
    
    public static String generateErrorsWiki() {
        Map<String, String> since = readSinceData("errors");
        List<ErrorRule> sortedErrors = new ArrayList<ErrorRule>(listErrorFixes());
        
        Collections.sort(sortedErrors, new Comparator<ErrorRule>() {
            @Override public int compare(ErrorRule o1, ErrorRule o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        StringWriter baseOut = new StringWriter();
        PrintWriter out = new PrintWriter(baseOut);
        int totalFixes = 0;
        int devFixs = 0;
        
        ResourceBundle rb = ResourceBundle.getBundle("com.sun.tools.javac.resources.compiler");

        for (ErrorRule rule : sortedErrors) {
            out.print(";'''" + rule.getDisplayName() + "'''");
            out.println();
            out.println(": handles the following errors:"); //there is no description for error rules:
            for (Object errorCode : rule.getCodes()) {
                try {
                    out.println(": * " + rb.getString(errorCode.toString()).replace("\n", " "));
                } catch (MissingResourceException ex) {
                    //would be better to fix the fixes not to list obsolette keys, but better to be robust here
                }
            }
            out.print(": <span style='padding-left: 1em; font-size: 90%'>");
            if (since.containsKey(rule.getId())) {
                String sinceVersion = since.get(rule.getId());

                if (sinceVersion != null) {
                    out.print(" ''Since " + sinceVersion + "''");
                } else {
                    out.print(" ''In NetBeans 7.2 or earlier''");
                }
            } else {
                out.print(" ''In current development version''");
                devFixs++;
            }
            out.print("</span>");
            out.println();
            out.println();
            totalFixes++;
        }

        out.close();
        
        return "There are " + totalFixes + " total error fixes, " + devFixs + " of them in current development version only.\n\n" +
               baseOut.toString();
    }

    static String getFileObjectLocalizedName( FileObject fo ) {
        Object o = fo.getAttribute("SystemFileSystem.localizingBundle"); // NOI18N
        if ( o instanceof String ) {
            String bundleName = (String)o;
            try {
                ResourceBundle rb = NbBundle.getBundle(bundleName);
                String localizedName = rb.getString(fo.getPath());
                return localizedName;
            }
            catch(MissingResourceException ex ) {
                // Do nothing return file path;
            }
        }
        return fo.getPath();
    }

    private static Map<String, String> readSinceData(String code) {
        FileObject lists = FileUtil.getConfigFile("org-netbeans-modules-java-hints/" + code + "/lists");

        if (lists == null) return Collections.emptyMap();

        Map<String, String> result = new HashMap<String, String>();

        for (FileObject c : FileUtil.getOrder(Arrays.asList(lists.getChildren()), true)) {
            if (!"hints".equals(c.getExt())) continue;

            try {
                String displayName = (String) c.getAttribute("displayName");

                for (String id : c.asLines("UTF-8")) {
                    if (result.containsKey(id)) continue;
                    result.put(id, displayName);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return result;
    }
    
    public static Collection<? extends ErrorRule> listErrorFixes() {
        Set<ErrorRule> errors = Collections.newSetFromMap(new IdentityHashMap<ErrorRule, Boolean>());
        for (List<ErrorRule> rules : org.netbeans.modules.java.hints.legacy.spi.RulesManager.getInstance().getErrors("text/x-java").values()) {
            errors.addAll(rules);
        }
        return errors;
    }
}
