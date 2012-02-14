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
import org.netbeans.modules.java.hints.jackpot.impl.RulesManager;
import org.netbeans.modules.java.hints.jackpot.spi.HintMetadata;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
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
        Map<String, String> since = readSinceData();
        Map<String, List<HintMetadata>> category2Hint = new TreeMap<String, List<HintMetadata>>();

        for (HintMetadata hm : RulesManager.getInstance().allHints.keySet()) {
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

        for (Entry<String, List<HintMetadata>> categoryEntry : category2Hint.entrySet()) {
            out.println("===" + categoryEntry.getKey() + "===");

            Collections.sort(categoryEntry.getValue(), new Comparator<HintMetadata>() {
                @Override public int compare(HintMetadata o1, HintMetadata o2) {
                    return o1.displayName.compareTo(o2.displayName);
                }
            });

            for (HintMetadata hm : categoryEntry.getValue()) {
                out.println(";" + hm.displayName);
                out.print(":" + hm.description);
                if (since.containsKey(hm.id)) {
                    String sinceVersion = since.get(hm.id);

                    if (sinceVersion != null) {
                        out.print(" '''Since " + sinceVersion + "'''");
                    }
                } else {
                    out.print(" '''In current development version'''");
                }
                out.println();
                out.println();
            }
        }

        out.close();
        System.err.println(baseOut.toString());
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

    private static Map<String, String> readSinceData() {
        FileObject lists = FileUtil.getConfigFile("org-netbeans-modules-java-hints/rules/lists");

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
}
