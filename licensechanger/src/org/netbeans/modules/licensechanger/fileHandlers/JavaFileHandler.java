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
package org.netbeans.modules.licensechanger.fileHandlers;

import org.netbeans.modules.licensechanger.api.FileHandler;
import org.netbeans.modules.licensechanger.api.Offsets;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
@org.openide.util.lookup.ServiceProvider(service = org.netbeans.modules.licensechanger.api.FileHandler.class)
public class JavaFileHandler extends FileHandler {

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(JavaFileHandler.class, "NAME_JAVA_FILES"); //NOI18N
    }

    @Override
    public boolean match(FileObject file) {
        return "text/x-java".equals(file.getMIMEType()); //NOI18N
    }

    @Override
    public boolean shouldSkipFile(FileObject file) {
        return false;
    }

    @Override
    protected Offsets getReplaceOffsets(CharSequence seq) {
        String[] lines = splitIntoLines(seq);
        LineVisitor v = new LineVisitor();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!v.visitLine(line)) {
                break;
            }
        }
        return new Offsets(0, v.delCount);
    }

    @Override
    protected String licenseFirst() {
        return "/* ";
    }

    @Override
    protected String licensePrefix() {
        return " * ";
    }

    @Override
    protected String licenseLast() {
        return " */";
    }

    private static final class LineVisitor {

        private boolean inComment;
        int delCount;

        public boolean visitLine(String line) {
            String trimmed = line.trim();
            if (trimmed.startsWith("//") || trimmed.startsWith("/*") && trimmed.endsWith("*/")) {
                return visitCommentLine(line);
            } else if (trimmed.length() == 0) {
                return visitEmptyLine(line);
            } else if (inComment) {
                return visitLineInComment(line);
            } else {
                return visitLineAndTestForComment(line);
            }
        }

        private boolean visitCommentLine(String line) {
            delCount += line.length() + 1;
            return true;
        }

        private boolean visitEmptyLine(String line) {
            delCount += line.length() + 1;
            return true;
        }

        private boolean visitLineAndTestForComment(String line) {
            if (line.trim().startsWith("/*")) {
                inComment = true;
                delCount += line.length() + 1;
                return true;
            }
            return false;
        }

        private boolean visitLineInComment(String line) {
            if (line.trim().endsWith("*/")) {
                inComment = false;
                delCount += line.length() + 1;
                return true;
            }
            int ix = line.indexOf("*/");
            if (ix >= 0) {
                delCount += ix + 2;
                return false;
            } else {
                delCount += line.length() + 1;
                return true;
            }
        }
    }

//    @Override
//    protected Offsets getReplaceOffsets(CharSequence seq) {
//        int end2 = 0;
//        boolean inComment = false;
//        boolean maybeLast = false;
//        int lineLen;
//        for (StringTokenizer tok = new StringTokenizer(seq.toString(), "\n", true); tok.hasMoreTokens();) {
//            String line = tok.nextToken();
//            lineLen = line.length();
//            System.err.println("InComment? " + inComment + " Check line: " + line);
//            String trimmed = line.trim();
//            boolean lineWasComment = trimmed.length() == 0 || trimmed.startsWith("//");
//            System.err.println("  was comment? " + lineWasComment);
//            if (maybeLast && !lineWasComment && !trimmed.startsWith("/*")) {
//                break;
//            }
//            if (!lineWasComment && !inComment) {
//                int cmtStartIx = trimmed.indexOf("/*");
//                if (cmtStartIx >= 0) {
//                    int cmtEndIx = trimmed.indexOf("*/");
//                    if (cmtEndIx < 0) {
//                        inComment = true;
//                    } else if (trimmed.endsWith("*/")) {
//                        inComment = false;
//                        lineWasComment = true;
//                        continue;
//                    }
//                }
//            } else if (!lineWasComment && inComment) {
//                int cmtEndIx = line.indexOf("*/");
//                if (cmtEndIx >= 0 && !trimmed.endsWith("*/")) {
//                    // e.g.   blah blah */package foo.bar;
//                    break;
//                } else if (cmtEndIx > 0) {
//                    maybeLast = true;
//                    inComment = false;
//                }
//            }
//            if (!lineWasComment && !inComment && !maybeLast) {
//                break;
//            }
//            end2 += lineLen;
//        }
//        return new Offsets(0, end2);
//    }
    @Override
    protected String escape(String licenseText) {
        StringBuilder sb = new StringBuilder(licenseText.length() + 30);
        String[] lines = splitIntoLines(licenseText);
        sb.append("/*\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            boolean empty = line.trim().length() == 0;
            if (empty) {
                sb.append(" *");
            } else {
                sb.append(" * ");
                sb.append(line);
            }
            sb.append("\n");
        }
        sb.append(" */\n");
        return sb.toString();
    }
}
