/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s): Ilia Gromov
 */
package org.netbeans.modules.ide.analysis.modernize.impl;

import static org.netbeans.modules.ide.analysis.modernize.impl.YamlParser.Diagnostics.UNDEFINED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 *
 * @author Ilia Gromov
 */
public class YamlParser {
    // TODO check for no-replacements case

    private static final YamlParser INSTANCE = new YamlParser();

    public static YamlParser getDefault() {
        return INSTANCE;
    }

    private static final String OPEN_TAG = "---"; //NOI18N
    private static final String CLOSE_TAG = "..."; //NOI18N

    private static final String MAIN_SOURCE_FILE = "MainSourceFile"; //NOI18N
    private static final String CHECK_NAME = "CheckName"; //NOI18N
    private static final String LEVEL = "Level"; //NOI18N
    private static final String MESSAGE_FILE_PATH = "MessageFilePath"; //NOI18N
    private static final String MESSAGE_FILE_OFFSET = "MessageFileOffset"; //NOI18N
    private static final String MESSAGE = "Message"; //NOI18N
    private static final String REPLACEMENTS = "Replacements"; //NOI18N
    private static final String DIAGNOSTICS = "Diagnostics"; //NOI18N

    private static final String FILE_PATH = "FilePath"; //NOI18N
    private static final String OFFSET = "Offset"; //NOI18N
    private static final String LENGTH = "Length"; //NOI18N
    private static final String REPLACEMENT_TEXT = "ReplacementText"; //NOI18N

    // clang-modernize:
// ---
// MainSourceFile:  /home/ilia/NetBeansProjects/CppApplication_46/main.cpp
// Replacements:    
//   - FilePath:        /home/ilia/NetBeansProjects/CppApplication_46/newfile.h
//     Offset:          101
//     Length:          1
//     ReplacementText: nullptr
//   - FilePath:        /home/ilia/NetBeansProjects/CppApplication_46/newfile1.h
//     Offset:          380
//     Length:          1
//     ReplacementText: nullptr
//   - FilePath:        /home/ilia/NetBeansProjects/CppApplication_46/main.cpp
//     Offset:          152
//     Length:          1
//     ReplacementText: nullptr
// ...
    // clang-tidy:
// ---
// MainSourceFile:  ''
// Diagnostics:     
//   CheckName:       misc-macro-parentheses
//   Replacements:    
//     - FilePath:        /media/SSD_/code/zdoom/main_1.cpp
//       Offset:          1352
//       Length:          0
//       ReplacementText: '('
//     - FilePath:        /media/SSD_/code/zdoom/main_1.cpp
//       Offset:          1353
//       Length:          0
//       ReplacementText: ')'
// Diagnostics:     
//   CheckName:       misc-macro-parentheses
//   Replacements:    
//     - FilePath:        /media/SSD_/code/zdoom/main_1.cpp
//       Offset:          1446
//       Length:          0
//       ReplacementText: '('
//     - FilePath:        /media/SSD_/code/zdoom/main_1.cpp
//       Offset:          1447
//       Length:          0
//       ReplacementText: ')'
// ...
    public List<Diagnostics> parseYaml(String output) {
        try {
            if (output.isEmpty()) {
                // No warnings in header = no 'Main Source File' element
                return Collections.EMPTY_LIST;
            }
            StringTokenizer st = new StringTokenizer(output, "\n"); //NOI18N
            String line;

            // ---
            line = st.nextToken();

            line = st.nextToken();
            String mainSourceFile = getValue(MAIN_SOURCE_FILE, line);

            // Replacements:    
            line = st.nextToken();
            ClangTidyVersion version = line.contains(REPLACEMENTS)
                    ? ClangTidyVersion.OLD_FORMAT
                    : ClangTidyVersion.NEW_FORMAT;

            List<Diagnostics> list = version.parser.parse(st);

            return list;
        } catch (Exception ex) {
            ModernizeErrorProvider.LOG.log(Level.INFO, "Can't parse output line");
            return Collections.EMPTY_LIST;
        }
    }

    private static List<Replacement> parseReplacements(StringTokenizer st) {
        List<Replacement> replacements = new ArrayList<Replacement>();
        String line;

        // eat Replacements:
        line = st.nextToken();

        if (!line.contains(REPLACEMENTS)) {
            return replacements;
        }

        while (st.hasMoreTokens()) {
            line = st.nextToken();

            if (line.equals(CLOSE_TAG) || !line.contains(FILE_PATH)) {
                break;
            }

            String filePath = getValue(FILE_PATH, line);

            line = st.nextToken();
            int offset = Integer.parseInt(getValue(OFFSET, line));

            line = st.nextToken();
            int length = Integer.parseInt(getValue(LENGTH, line));

            line = st.nextToken();
            String replacementText = getValue(REPLACEMENT_TEXT, line);
            while (replacementText == null && st.hasMoreElements()) {
                line = line + "\n" + st.nextElement(); //NOI18N
                replacementText = getValue(REPLACEMENT_TEXT, line);
            }

            Replacement replacement = new Replacement(filePath, offset, length, replacementText);
            replacements.add(replacement);

        }
        return replacements;
    }

    private static String getValue(String key, String line) {
        int keyStart = line.indexOf(key);
        assert keyStart != -1;
        int keyEnd = keyStart + key.length() + 1;
        String trimmed = line.substring(keyEnd).trim();
        boolean isTextValue = trimmed.charAt(0) == '\'';
        if (isTextValue) {
            if (trimmed.length() == 1) {
                // '
                // abcd'
                return null;
            } else if (trimmed.charAt(trimmed.length() - 1) == '\'') {
                trimmed = trimmed.substring(1, trimmed.length() - 1);
            } else {
                return null;
            }
        }
        return trimmed;
    }

    public static class TranslationUnitDiagnostics {

        private final String mainSourceFilePath;
        private final String context;
        private final List<Diagnostics> diags;

        public TranslationUnitDiagnostics(String mainSourceFilePath, String context, List<Diagnostics> diags) {
            this.mainSourceFilePath = mainSourceFilePath;
            this.context = context;
            this.diags = diags;
        }

        public String getMainSourceFilePath() {
            return mainSourceFilePath;
        }

        public String getContext() {
            return context;
        }

        public List<Diagnostics> getDiags() {
            return diags;
        }
    }

    public static class Diagnostics {

        public static final String UNDEFINED = "undefined"; // NOI18N

        public enum Level {
            warning, error
        }

        private final String checkName;
        private final Diagnostics.Level level;
        private final String messageFilePath;
        private final int messageFileOffset;
        private final String message;
        private final List<Replacement> replacements;

        public Diagnostics(String checkName, Diagnostics.Level level, String messageFilePath, int messageFileOffset, String message, List<Replacement> replacements) {
            this.checkName = checkName;
            this.level = level;
            this.messageFilePath = messageFilePath;
            this.messageFileOffset = messageFileOffset;
            this.message = message;
            this.replacements = replacements;
        }

        public String getCheckName() {
            return checkName;
        }

        public Level getLevel() {
            return level;
        }

        public String getMessageFilePath() {
            return messageFilePath;
        }

        public int getMessageFileOffset() {
            return messageFileOffset;
        }

        public String getMessage() {
            return message;
        }

        public List<Replacement> getReplacements() {
            return replacements;
        }
    }

    public static class Replacement {

        public final String filePath;
        public final int offset;
        public final int length;
        public final String replacementText;

        public Replacement(String FilePath, int offset, int length, String replacementText) {
            this.filePath = FilePath;
            this.offset = offset;
            this.length = length;
            this.replacementText = replacementText;
        }
    }

    private static enum ClangTidyVersion {
        NEW_FORMAT(new NewFormatParser()),
        OLD_FORMAT(new OldFormatParser());

        public final TidyParser parser;

        private ClangTidyVersion(TidyParser parser) {
            this.parser = parser;
        }
    }

    private static abstract class TidyParser {

        abstract List<Diagnostics> parse(StringTokenizer st);
    }

    private static class NewFormatParser extends TidyParser {

        @Override
        public List<Diagnostics> parse(StringTokenizer st) {
            List<Diagnostics> list = new ArrayList<Diagnostics>();

            String line;
            while (st.hasMoreElements()) {
                line = st.nextToken();
                String checkName = getValue(CHECK_NAME, line);

                line = st.nextToken();
                Diagnostics.Level level = Diagnostics.Level.valueOf(getValue(LEVEL, line).toLowerCase());

                line = st.nextToken();
                String messageFilePath = getValue(MESSAGE_FILE_PATH, line);

                line = st.nextToken();
                int messageFileOffset = Integer.parseInt(getValue(MESSAGE_FILE_OFFSET, line));

                line = st.nextToken();
                String message = getValue(MESSAGE, line);

                List<Replacement> replacements = parseReplacements(st);

                list.add(new Diagnostics(checkName, level, messageFilePath, messageFileOffset, message, replacements));
            }
            return list;
        }

    }

    private static class OldFormatParser extends TidyParser {

        @Override
        public List<Diagnostics> parse(StringTokenizer st) {
            List<Replacement> parseReplacements = parseReplacements(st);
            return Collections.singletonList(new Diagnostics(UNDEFINED, Diagnostics.Level.warning, "", -1, "", parseReplacements)); //NOI18N
        }
    }
}
