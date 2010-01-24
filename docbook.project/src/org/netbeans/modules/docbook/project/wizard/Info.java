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

package org.netbeans.modules.docbook.project.wizard;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Info {
    public final String title;
    public final String subtitle;
    public final Name name;

    Info(String title, String subtitle, String name) {
        this.title = title;
        this.subtitle = subtitle;
        this.name = new Name(name);
    }

    String toXml (String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append ("    "); //NOI18N
        sb.append ('<'); //NOI18N
        sb.append (tag);
        sb.append ('>'); //NOI18N
        sb.append ('\n'); //NOI18N
        sb.append ("        <title>"); //NOI18N
        sb.append (title);
        sb.append ("</title>\n"); //NOI18N
        if (subtitle != null) {
            sb.append ("        <subtitle>"); //NOI18N
            sb.append (subtitle);
            sb.append ("</subtitle>"); //NOI18N
        }
        sb.append (name.toXml());
        sb.append ("    </"); //NOI18N
        sb.append (tag);
        sb.append (">\n"); //NOI18N
        return sb.toString();
    }

    String firstName() {
        return name.firstName;
    }

    String lastName() {
        return name.lastName;
    }

    String honorific() {
        return name.honorific;
    }

    List<String> otherNames() {
        return name.otherNames;
    }

    private static final class Name {
        private String honorific;
        private String firstName;
        private String lastName;
        private List<String> otherNames = new LinkedList<String>();
        public Name (String name) {
            Pattern a = Pattern.compile ("^\\s*(\\S.*),\\s*(.*)\\s*$"); //NOI18N
            Matcher m = a.matcher(name);
            if (m.find()) {
                init (m.group(1));
                honorific = m.group(2);
            } else {
                init(name);
            }
        }

        private void init(String name) {
            Pattern p = Pattern.compile ("^\\s*(\\S*)\\s*(.*?)\\s*(\\S*)\\s*$"); //NOI18N
            String[] s = toArr (name, p.matcher(name));
            if (s.length == 1) {
                otherNames.add(s[0]);
            } else if (s.length == 2) {
                firstName = s[0];
                lastName = s[1];
            } else if (s.length > 2) {
                firstName = s[0];
                lastName = s[s.length - 1];
                for (int i=1; i < s.length - 1; i ++) {
                    otherNames.addAll (Arrays.asList(s[i].split("\\s")));
                }
            }
        }

        private String[] toArr(String name, Matcher m) {
            List<String> l = new LinkedList<String>();
            if (m.lookingAt()) {
                for (int i=1; i <= m.groupCount(); i++) {
                    String s = m.group(i);
                    if (s.trim().length() > 0) {
                        l.add(s.trim());
                    }
                }
            } else {
                if (name.trim().length() > 0) {
                    l.add(name.trim());
                }
            }
            System.out.println("Split '" + name + " to '" + l + "'");
            return l.toArray(new String[0]);
        }

        private String toXml() {
            StringBuilder sb = new StringBuilder("        <author>\n" + //NOI18N
                    "            <personname>\n"); //NOI18N
            if (firstName != null) {
                sb.append("                <firstname>"); //NOI18N
                sb.append (firstName);
                sb.append ("</firstname>\n"); //NOI18N
            }
            for (String s : otherNames) {
                sb.append("                <othername>"); //NOI18N
                sb.append (s);
                sb.append ("</othername>\n"); //NOI18N
            }
            if (lastName != null) {
                sb.append("                <surname>"); //NOI18N
                sb.append (lastName);
                sb.append ("</surname>\n"); //NOI18N
            }
            if (honorific != null) {
                sb.append("                <honorific>"); //NOI18N
                sb.append (honorific);
                sb.append ("</honorific>\n"); //NOI18N
            }
            sb.append("            </personname>\n"); //NOI18N
            sb.append("        </author>\n"); //NOI18N
            return sb.toString();
        }
    }
}
