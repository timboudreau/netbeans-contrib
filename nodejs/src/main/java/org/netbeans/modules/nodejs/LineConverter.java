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
package org.netbeans.modules.nodejs;

import java.awt.Toolkit;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor.LineConvertorFactory;
import org.netbeans.api.extexecution.print.ConvertedLine;
import org.netbeans.api.extexecution.print.LineConvertor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.text.Line.ShowOpenType;
import org.openide.text.Line.ShowVisibilityType;
import org.openide.util.Exceptions;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 *
 * @author Tim Boudreau
 */
final class LineConverter implements LineConvertorFactory {

    private FileObject sourceRoot;

    LineConverter() {
        DefaultExectable e = DefaultExectable.get();
        String s = e.getSourcesLocation();
        if (s != null) {
            File f = new File(s);
            sourceRoot = FileUtil.toFileObject(f);
        }
    }

    @Override
    public LineConvertor newLineConvertor() {
        return new LineConvertor() {

            @Override
            public List<ConvertedLine> convert(String line) {
                Matcher m = ERR_PATTERN.matcher(line);
                OutputListener ol = null;
                try {
                    if (m.find()) {
                        String clazz = m.group(1);
                        String path = m.group(2);
                        int lineNumber = Integer.parseInt(m.group(3));
                        int charPos = Integer.parseInt(m.group(4));
                        ol = new Link(clazz, path, lineNumber, charPos);
                    } else {
                        m = SYNTAX_ERR_PATTERN.matcher(line);
                        if (m.find()) {
                            String clazz = null;
                            String path = m.group(1);
                            int lineNumber = Integer.parseInt(m.group(2));
                            int charPos = 0;
                            ol = new Link(clazz, path, lineNumber, charPos);
                        }
                    }
                } catch (NumberFormatException nfe) {
                    //do nothing - some output looked like a stack element by accident
                }
                return Collections.singletonList(ConvertedLine.forText(line, ol));
            }
        };
    }
    private static final Pattern ERR_PATTERN =
            Pattern.compile("at\\s(.*?)\\s\\((.*?.js):(\\d+):(\\d+)\\)");
    //e.g. at Server.<anonymous> (/home/tim/Fooger/src/Fooger.js:7:5)
    private static final Pattern SYNTAX_ERR_PATTERN =
            Pattern.compile("(\\/.*?\\.js):(\\d+)");
    //e.g. /home/tim/work/personal/captcha/captcha.js:38

    private static class Link implements OutputListener {

        private final String path;
        private final String clazz;
        private final int line;
        private final int charPos;

        Link(String clazz, String path, int line, int charPos) {
            this.clazz = clazz;
            this.path = path;
            this.line = line;
            this.charPos = charPos;
        }

        @Override
        public void outputLineSelected(OutputEvent ev) {
            //do nothing
        }

        @Override
        public void outputLineAction(OutputEvent ev) {
            String pathLocal = this.path;
            if (pathLocal.indexOf('/') < 0) { //NOI18N
                String sourcePath = DefaultExectable.get().getSourcesLocation();
                if (sourcePath != null) {
                    File f = new File(sourcePath);
                    f = new File(f, pathLocal);
                    if (!f.exists() && new File(f, "lib").exists()) { //NOI18N
                        f = new File(f, "lib");
                        f = new File(f, pathLocal);
                    }
                    pathLocal = f.getAbsolutePath();
                }
            }
            File f = new File(pathLocal);
            if (f.exists()) {
                FileObject fo = FileUtil.toFileObject(f);
                try {
                    DataObject dob = DataObject.find(fo);
                    EditorCookie ck = dob.getLookup().lookup(EditorCookie.class);
                    if (ck != null) {
                        LineCookie l = dob.getLookup().lookup(LineCookie.class);
                        if (l != null) {
                            Line goTo = l.getLineSet().getCurrent(line);
                            if (l != null) {
                                goTo.show(ShowOpenType.REUSE_NEW, ShowVisibilityType.FOCUS, charPos);
                            }
                        }
                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void outputLineCleared(OutputEvent ev) {
            //do nothing
        }

        public String toString() {
            return path + " line " + line + " pos " + charPos;
        }
    }
}
