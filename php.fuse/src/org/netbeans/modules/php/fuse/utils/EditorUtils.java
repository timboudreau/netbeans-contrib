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
package org.netbeans.modules.php.fuse.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.php.fuse.editor.TmplParseData;
import org.netbeans.modules.php.fuse.lexer.FuseTokenId;
import org.netbeans.modules.php.fuse.lexer.FuseTopTokenId;
import org.openide.filesystems.FileObject;

/**
 *
 * @author cawe
 */
public class EditorUtils {

    /**
     * Get index of white character in the line.
     * @param line which line should be scanned
     * @return index of first white characted
     */
    public static int indexOfWhite(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (Character.isWhitespace(c)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get first non whitespace in file from some offset.
     * @param doc which document should be scanned
     * @param offset where the scanning starts
     * @return index of first non-white character
     * @throws BadLocationException entered offset is outside of the document
     */
    public static int getRowFirstNonWhite(StyledDocument doc, int offset) throws BadLocationException {
        TokenHierarchy<?> th = TokenHierarchy.get(doc);
        TokenSequence<FuseTopTokenId> ts = th.tokenSequence(FuseTopTokenId.language());
        int diffStart = ts.move(offset);
        Token t = null;
        int lenght = 0;
        if (ts.moveNext() || ts.movePrevious()) {
            t = ts.token();
            lenght = t.length();
        }
        int start = offset - diffStart;
        while (start + 1 < start + lenght) {
            try {
                if (doc.getText(start, 1).charAt(0) != ' ') {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start + 1) +
                        ") on doc of length: " + doc.getLength(), start).initCause(ex);
            }
            start++;
        }
        return start;
    }

    /**
     * Get context depending variables for Fuse template.
     * @param doc for whcih template
     * @return list with variables which were sent from controller
     */
    public static ArrayList<String> getKeywordsForView(Document doc) {
        ArrayList<String> results = new ArrayList<String>();
        Source file = Source.create(doc);
        FileObject fo = file.getFileObject();

        String nameOfView = fo.getName();
        int indexOfDash = nameOfView.indexOf("-");
        if (indexOfDash > 0) {
            final String nameOfController = nameOfView.substring(0, indexOfDash) + "Controller.class.php";
            File cf = null;
            fo = fo.getParent().getParent();
            for (int i = 0; i < 3; i++) {
                cf = new File(fo.getPath() + "/controllers");
                if (cf.exists() && cf.isDirectory()) {
                    break;
                }
                fo = fo.getParent();
            }
            if (!cf.exists()) {
                return new ArrayList<String>();
            }
            File[] adeptsForCompletion = cf.listFiles(new FileFilter() {

                public boolean accept(File arg0) {
                    return arg0.getName().equals(nameOfController);
                }
            });

            for (File controller : adeptsForCompletion) {
                try {
                    BufferedReader bis = new BufferedReader(new FileReader(controller));
                    String line;
                    while ((line = bis.readLine()) != null) {
                        String res = parseLineForVars(line);
                        if (res != null && !results.contains(res)) {
                            results.add(res);
                        }
                    }
                    results.addAll(getGeneralVariables());
                } catch (IOException ioe) {
                    Logger.getLogger("TmplCompletionQuery").warning("scanning of unnexisting file " + controller.getAbsolutePath());
                }
            }
        }
        return results;
    }

    /**
     * Get information if the line contains parameter sent into template.
     * @param line scanned line
     * @return whole line if there is sent variable or nothing
     */
    public static String parseLineForVars(String line) {
        if (line.contains("template")) {
            String[] templateProcessing = {"add_param", "add_iterator", "add_by_reference",
                "add_db_resultset", "add_db_result", "add_resource_map", "add_resource_result_map"};
            for (String pattern : templateProcessing) {
                if (line.contains(pattern)) {
                    return parseVariable(line, pattern);
                }
            }
        }
        return null;
    }

    /**
     * Get variable from the line.
     * @param line line which will be used
     * @param param which keyword for sending variables is there
     * @return name of the variable
     */
    public static String parseVariable(String line, String param) {
        line = line.replaceAll(" ", "");
        int indexOfAdd = line.indexOf(param + "(");
        int paramLength = param.length();
        if ((paramLength + 2) > line.length()) {
            return null;
        }
        String del = line.substring(indexOfAdd + paramLength + 1, indexOfAdd + paramLength + 2);
        line = line.substring(indexOfAdd + paramLength + 2, line.length());
        line = line.substring(0, line.indexOf(del));
        return line;
    }

    private static ArrayList<String> getGeneralVariables() {
        ArrayList<String> generalVars = new ArrayList<String>();
        generalVars.add("SITE_BASE_URI");
        return generalVars;
    }

    public static TokenHierarchy<CharSequence> createTmplTokenHierarchy(CharSequence inputText, Snapshot tmplSnapshot) {
        InputAttributes inputAttributes = new InputAttributes();

        FileObject fo = tmplSnapshot.getSource().getFileObject();
        if (fo != null) {
            //try to obtain tmpl coloring info for file based snapshots
            final Document doc = tmplSnapshot.getSource().getDocument(true);

            TmplParseData tmplParseData = new TmplParseData(doc);
            inputAttributes.setValue(FuseTokenId.language(), TmplParseData.class, tmplParseData, true);

        }

        TokenHierarchy<CharSequence> th = TokenHierarchy.create(
                inputText,
                true,
                FuseTokenId.language(),
                Collections.EMPTY_SET,
                inputAttributes);

        return th;
    }
}
