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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Converts indented lines into a normalized tree of XML elements
 *
 * @author Tim Boudreau
 */
final class Outline {

    private final List<Item> items = new ArrayList<Item>();

    public Outline(String content) {
        String[] lines = content.split("\n"); //NOI18N
        List<String> l = new LinkedList<String>(Arrays.asList(lines));
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().length() == 0) continue;
            Item item = new Item(lines[i]);
            items.add(item);
            int skip = item.claimSubitems(item.depth, l, i);
            i += skip;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item i : items) {
            i.toString(sb, 0);
        }
        return sb.toString();
    }

    public String toXml (TagProvider prov, int indent) {
        StringBuilder sb = new StringBuilder();
        toXml (sb, prov, indent);
        return sb.toString();
    }

    public List<Item> toXml (StringBuilder sb, TagProvider prov, int indent) {
        List<Item> result = new LinkedList<Item>();
        for (Item i : items) {
            result.addAll(i.toXml(sb, 0, prov, indent));
        }
        return result;
    }

    static final class Item {

        public final int depth;
        public final String title;
        private final List<Item> subitems = new ArrayList<Item>();

        public Item(String title) {
            int d = 0;
            for (char c : title.toCharArray()) {
                if ('\t' == c) { //NOI18N
                    d += 4;
                    continue;
                }
                if (Character.isWhitespace(c)) {
                    d += 1;
                } else {
                    break;
                }
            }
            this.depth = d;
            this.title = title.trim();
        }

        private int claimSubitems(int depth, List<String> lines, int start) {
            int result = 0;
            if (start + 1 < lines.size()) {
                for (int i = start + 1; i < lines.size(); i++) {
                    if (lines.get(i).trim().length() == 0) {
                        result++;
                        continue;
                    }
                    Item sub = new Item(lines.get(i));
                    if (sub.depth > depth) {
                        result++;
                        subitems.add(sub);
                        int skip = sub.claimSubitems(sub.depth, lines, i);
                        i += skip;
                        result += skip;
                    } else {
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString (sb, 0);
            return sb.toString();
        }

        private void toString (StringBuilder out, int depth) {
            char[] c = new char[depth * 4];
            Arrays.fill (c, ' '); //NOI18N
            out.append (new String(c));
            out.append(title);
            out.append ('\n'); //NOI18N
            for (int i=0; i < subitems.size(); i++) {
                subitems.get(i).toString(out, depth + 1);
            }
        }

        String toXml (TagProvider prov, int indent) {
            StringBuilder sb = new StringBuilder();
            toXml(sb, 0, prov, indent);
            return sb.toString();
        }

        List<Item> toXml(StringBuilder out, int depth, TagProvider prov, int indent) {
            char[] c = new char[indent * 4];
            Arrays.fill (c, ' '); //NOI18N
            String indentation = new String(c);
            if (prov.skip(this, depth)) {
                out.append (indentation);
                out.append ('&');
                out.append (ProjectKind.toFilename(title));
                out.append (';');
                out.append ('\n');
                return Arrays.asList(this);
            }
            out.append (indentation);
            out.append ("<"); //NOI18N
            out.append (prov.getTag(depth));
            out.append (" id=\""); //NOI18N
            out.append (ProjectKind.toId(title));
            out.append ("\">\n"); //NOI18N
            out.append (indentation);
            out.append ("    <title>"); //NOI18N
            out.append (title);
            out.append ("</title>\n"); //NOI18N
            List<Item> result = new LinkedList<Item>();
            if (subitems.isEmpty()) {
                out.append (indentation);
                out.append ("    <para>\n"); //NOI18N
                out.append (indentation);
                out.append ("    "); //NOI18N
                out.append (prov.getPlaceholderText(depth));
                out.append ('\n');
                out.append (indentation);
                out.append ("    </para>\n"); //NOI18N
            } else {
                for (Item i : subitems) {
                    if (!prov.skip(i, depth + 1)) {
                        i.toXml(out, depth + 1, prov, indent + 1);
                    } else {
                        result.add (i);
                        out.append('&');
                        out.append(ProjectKind.toFilename(i.title));
                        out.append(';');
                        out.append('\n');
                    }
                }
            }
            out.append (indentation);
            out.append ("</"); //NOI18N
            out.append (prov.getTag(depth));
            out.append (">\n"); //NOI18N
            return result;
        }
    }

    interface TagProvider {
        String getTag (int depth);
        String getPlaceholderText (int depth);
        boolean skip (Item item, int depth);
    }
}
