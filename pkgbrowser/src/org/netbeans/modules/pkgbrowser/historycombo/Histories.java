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
package org.netbeans.modules.pkgbrowser.historycombo;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import javax.swing.JComboBox;
import org.openide.util.WeakSet;

/**
 *
 * @author Timothy Boudreau
 */
public final class Histories {
    private static final String SEP = "$@|@*";
    
    private Histories() {
    }

    public static JComboBox createComboBox (String key) {
        JComboBox result = new JComboBox();
        CompletionComboBoxModel mdl = new CompletionComboBoxModel (key);
        HistoryComboBoxEditor editor = new HistoryComboBoxEditor (result);
        result.setEditable(true);
        result.setEditor(editor);
        result.setModel (mdl);
        return result;
    }

    private static final Map lists = new HashMap();
    static List get (String key) {
        List result = null;
//        Object o =
//        Reference r = (Reference) lists.get(key);
//        if (r != null) {
//            result = (List) r.get();
//        }
        result = (List) lists.get(key);
        if (result == null) {
            result = parseContents (getSavedContents(key));
//            lists.put (key, new WeakReference(result));
            lists.put (key, result);
        }
        return result;
    }

    static List put (String key, String value) {
        List l = get (key);
        l.add (0, value);
        return l;
//        List toAdd = null;
//        for (Iterator i = l.iterator(); i.hasNext();) {
//            String s = (String) i.next();
//            if (s.startsWith(value)) {
//                if (toAdd == null) {
//                    toAdd = new LinkedList();
//                }
//                toAdd.add (s);
//                i.remove();
//            }
//        }
//        if (toAdd != null) {
//            l.addAll (toAdd);
//        }
//        Collections.sort(l);
//        save (key, l);
//        return l;
    }

    static List getMatchList (String key, String toMatch) {
        toMatch = toMatch.trim();
        List l = get (key);
        List result = null;
        boolean isTailSearch = toMatch.startsWith ("*");
        if (isTailSearch) {
            StringBuffer sb = new StringBuffer (toMatch);
            while (sb.length() > 0 && sb.charAt(0) == '*') {
                sb.deleteCharAt(0);
            }
            toMatch = sb.toString();
        }
        boolean isHeadSearch = toMatch.endsWith("*");
        if (isHeadSearch) {
            StringBuffer sb = new StringBuffer (toMatch.trim());
            while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '*') {
                sb.deleteCharAt(sb.length() - 1);
            }
            toMatch = sb.toString();
        }
        if ("".equals(toMatch)) {
            return l;
        }
        for (Iterator i = l.iterator(); i.hasNext();) {
            String s = (String) i.next();
            boolean isMatch;
            if (isHeadSearch && isTailSearch) {
                isMatch = s.contains (toMatch);
            } else if (isTailSearch) {
                isMatch = s.startsWith(toMatch);
            } else {
                isMatch = s.startsWith(toMatch);
            }
            System.err.println("Check " + toMatch + " against " + s + " result " + isMatch);
            if (isMatch) {
                if (result == null) {
                    result = new LinkedList();
                }
                result.add (s);
            }
        }
        return result == null ? Collections.EMPTY_LIST : result;
    }

    private static String getSavedContents(String name) {
        Preferences prefs = Preferences.userNodeForPackage(ListComboBoxModelImpl.class);
        String result = prefs.get(name, ""); //NOI18N
        return result;
    }

    private static List parseContents(String saved) {
        List contents = new LinkedList();
        if (saved.length() > 0) {
            for (StringTokenizer tok = new StringTokenizer(saved, SEP); tok.hasMoreTokens();) {
                contents.add (tok.nextToken());
            }
        }
        return contents;
    }

    private static final boolean DONT_SAVE = true;
    public static void save(String key, List l) {
        if (!DONT_SAVE) {
            Preferences prefs = Preferences.userNodeForPackage(ListComboBoxModelImpl.class);
            prefs.put(key, toString(l));
        }
        List toAdd = new LinkedList(l);
//        lists.put (key, new WeakReference(toAdd));
        lists.put (key, toAdd);
    }
//
//    private static Set models = new WeakSet();
//    static void register (CompletionComboBoxModel mdl) {
//        models.add (mdl);
//    }
//
//    static List getRegisteredModels (String key) {
//        ArrayList result = new ArrayList();
//        for (Iterator i = models.iterator(); i.hasNext();) {
//            CompletionComboBoxModel mdl = (CompletionComboBoxModel) i.next();
//            if (mdl != null && key.equals(mdl.getKey())) {
//                result.add (mdl);
//            } else {
//                i.remove();
//            }
//        }
//        return result;
//    }

    private static String toString(List contents) {
        StringBuffer result = new StringBuffer();
        for (Iterator i = contents.iterator(); i.hasNext();) {
            String item = (String) i.next();
            result.append (item);
            result.append (SEP);
        }
        return result.toString();
    }
}