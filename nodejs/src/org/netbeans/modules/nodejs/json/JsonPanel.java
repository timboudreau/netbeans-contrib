/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import org.netbeans.modules.nodejs.json.SimpleJSONParser.JsonException;
import org.openide.filesystems.FileObject;

public final class JsonPanel extends JPanel {

    public JsonPanel(FileObject fo) throws JsonException, IOException {
        this(new SimpleJSONParser().parse(fo), null);
    }
    
    private String capitalize(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public JsonPanel(Map<String, Object> map, String nm) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Object name = map.get("name");
        Object desc = map.get("description");
        if (nm != null) {
            name = capitalize(nm);
        }
        boolean nameUsed = name instanceof String;
        boolean descUsed = desc instanceof String;
        if (nameUsed) {
            JLabel l = new JLabel(capitalize(name.toString()));
            if (nm == null) {
                l.setFont(l.getFont().deriveFont(24F));
            } else {
                l.setFont(l.getFont().deriveFont(14F));
            }
            if (!descUsed) {
                l.setBorder(new MatteBorder(0, 0, 1, 0, UIManager.getColor("controlShadow")));
            }
            add(l);
        }
        if (descUsed) {
            JLabel l = border(new JLabel(desc.toString()));
            if (nameUsed) {
                l.setBorder(new MatteBorder(0, 0, 1, 0, UIManager.getColor("controlShadow")));
            }
            add(l);
        }
        if (nameUsed || descUsed) {
            JLabel lbl = new JLabel("   ");
            add(lbl);
        }
        for (String k : sortKeys(map)) {
            Object o = map.get(k);
            k = capitalize(k);
            if ("description".equals(k) && descUsed) {
                continue;
            }
            if ("name".equals(k) && nameUsed) {
                continue;
            }
            if (o instanceof String) {
                JLabel l = new JLabel(k + ":  " + o);
                add(l);
            } else if (o instanceof List) {
                StringBuilder sb = new StringBuilder(k).append(":  ");
                for (Iterator<?> it = ((List<?>) o).iterator(); it.hasNext();) {
                    sb.append(it.next());
                    if (it.hasNext()) {
                        sb.append(", ");
                    }
                }
                JLabel lbl = border(new JLabel(sb.toString()));
                add(lbl);
            } else if (o instanceof Map) {
                add(new JsonPanel((Map<String, Object>) o, capitalize(k)));
            }
        }
        setBorder(new EmptyBorder(12, 24, 12, 12));
    }
    private static final Border b = new EmptyBorder(5, 0, 5, 0);

    private JLabel border(JLabel lbl) {
        lbl.setBorder(b);
        return lbl;
    }
    
    List<String> sortKeys(Map<String,?> m) {
        List<String> l = new ArrayList<String>(m.keySet());
        Collections.sort(l, new C(m));
        return l;
    }
    
    private static class C implements Comparator<String> {
        private final Map<String,?> m;

        public C(Map<String, ?> m) {
            this.m = m;
        }

        @Override
        public int compare(String o1, String o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            Object a = m.get(o1);
            Object b = m.get(o2);
            if (a.getClass() == b.getClass()) {
                return 0;
            }
            if ((a.getClass() != String.class && a.getClass() != List.class) && b.getClass() == String.class) {
                return 1;
            }
            if ((a.getClass() == String.class || a.getClass() == List.class) && b.getClass() != String.class) {
                return -1;
            }
            return 0;
            
        }
        
    }
}
