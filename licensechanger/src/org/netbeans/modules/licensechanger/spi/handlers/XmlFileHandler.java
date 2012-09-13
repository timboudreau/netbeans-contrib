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

package org.netbeans.modules.licensechanger.spi.handlers;

import java.awt.Component;
import java.io.CharConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.licensechanger.api.Customizable;
import org.netbeans.modules.licensechanger.api.FileHandler;
import org.netbeans.modules.licensechanger.spi.wizard.utils.Offsets;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.xml.XMLUtil;

/**
 *
 * @author Tim Boudreau
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.licensechanger.api.FileHandler.class)
public class XmlFileHandler extends FileHandler{// implements Customizable {

    @Override
    public boolean match(FileObject file) {
        String ext = file.getExt();
        String mime = file.getMIMEType();
        return "xml".equals (file.getExt()) || "text/xml".equals(mime) ||
                "application/xml".equals(mime) || mime.endsWith("+xml");
    }

    @Override
    public boolean shouldSkipFile(FileObject file) {
        String n = file.getNameExt();
        return "project.xml".equals(n) ||
                "private.xml".equals(n) || "build.xml".equals(n) ||
                "build-impl.xml".equals(n) || "pom.xml".equals(n);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(XmlFileHandler.class, "NAME_XML_FILES");
    }

    @Override
    protected Offsets getReplaceOffsets(CharSequence seq) {
        String[] lines = splitIntoLines(seq);
        LineVisitor v = new LineVisitor();
        for (int i=0; i < lines.length; i++) {
            String line = lines[i];
            if (!v.visitLine(line)) {
                break;
            }
        }
        return new Offsets (v.start, v.delCount);
    }

    @Override
    protected String escape(String licenseText) {
        try {
            licenseText = XMLUtil.toElementContent(licenseText);
        } catch (CharConversionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "<!--\n" + licenseText + "\n-->\n";
    }

    static Pattern xmlDeclaration = Pattern.compile ("(<\\?xml.*?\\?>)");

//    @Override
//    public Component getCustomizer() {
//        return new XmlHandlerCustomizer();
//    }

    public static boolean isInsertAtTopOfFile() {
        return NbPreferences.forModule(XmlFileHandler.class).getBoolean("atTop", false);
    }

    public static void setInsertAtTopOfFile (boolean val) {
        NbPreferences.forModule(XmlFileHandler.class).putBoolean("atTop", val);
    }

    @Override
    protected String licenseFirst() {
        return "<!--";
    }

    @Override
    protected String licensePrefix() {
        return "";
    }

    @Override
    protected String licenseLast() {
        return "-->";
    }

    private static final class XmlDeclarationLocator {
        int declarationStartIndex;
        int declarationEndIndex;
        boolean isMidLine;
        public boolean visitLine(String line) {
            Matcher m = xmlDeclaration.matcher (line);
            if (m.find()) {
                declarationStartIndex += m.start(1);
                declarationEndIndex = declarationStartIndex + m.end(1);
                isMidLine = m.end(1) < line.length() - 1;
                return false;
            } else {
                declarationStartIndex += line.length() + 1;
                return true;
            }
        }

        boolean foundDeclaration() {
            return declarationEndIndex != 0;
        }
    }

    @Override
    public String transform(String origText, String licenseText) {
        String[] lines = splitIntoLines(origText);
        XmlDeclarationLocator v = new XmlDeclarationLocator();
        for (int i=0; i < lines.length; i++) {
            if (!v.visitLine(lines[i])) {
                break;
            }
        }
        if (v.foundDeclaration()) {
            StringBuilder sb = new StringBuilder (origText);
            sb.delete(0, v.declarationStartIndex);
            origText = sb.toString();
        }
//        String license = escape (licenseText);
        String license = resolveLicenseTemplate(licenseText);
        license = "\n" + license;
        StringBuilder sb = new StringBuilder (origText);
        Offsets o = getReplaceOffsets(sb);
        sb.delete(o.getStart(), o.getEnd());
        lines = splitIntoLines(origText);
        v = new XmlDeclarationLocator();
        for (int i=0; i < lines.length; i++) {
            if (!v.visitLine(lines[i])) {
                break;
            }
        }
        if (v.foundDeclaration()) {
            sb.insert(v.declarationEndIndex, license);
        } else {
            sb.insert(isInsertAtTopOfFile() ? 0 : o.getStart(), license);
        }
        return sb.toString();
    }



    private static final class LineVisitor {
        private boolean inComment;
        int delCount;
        int start;
        public boolean visitLine (String line) {
            if (start == 0) {
                if (line.trim().startsWith("<?xml ")) {
                    start += line.length() + 1;
                    delCount += start;
                    return true;
                }
            }
            String trimmed = line.trim();
            if (trimmed.startsWith("<!--") && trimmed.endsWith("-->")) {
                return visitCommentLine(line);
            } else if (trimmed.length() == 0) {
                return visitEmptyLine (line);
            } else if (inComment) {
                return visitLineInComment (line);
            } else {
                return visitLineAndTestForComment (line);
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
            if (line.trim().startsWith("<!--")) {
                inComment = true;
                delCount += line.length() + 1;
                return true;
            }
            return false;
        }

        private boolean visitLineInComment(String line) {
            if (line.trim().endsWith("-->")) {
                inComment = false;
                delCount += line.length() + 1;
                return true;
            }
            int ix = line.indexOf("-->");
            if (ix >= 0) {
                delCount += ix + 3;
                return false;
            } else {
                delCount += line.length() + 1;
                return true;
            }
        }
    }
}
