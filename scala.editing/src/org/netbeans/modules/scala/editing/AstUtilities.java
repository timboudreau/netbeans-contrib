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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing;

import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.TranslatedSource;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import xtc.tree.Node;

/**
 *
 * @author Caoyuan Deng
 */
public class AstUtilities {

    public static final String DOT_PROTOTYPE = ".prototype"; // NOI18N


    public static int getAstOffset(CompilationInfo info, int lexOffset) {
        ParserResult result = info.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);
        if (result != null) {
            TranslatedSource ts = result.getTranslatedSource();
            if (ts != null) {
                return ts.getAstOffset(lexOffset);
            }
        }

        return lexOffset;
    }

    public static OffsetRange getAstOffsets(CompilationInfo info, OffsetRange lexicalRange) {
        ParserResult result = info.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);
        if (result != null) {
            TranslatedSource ts = result.getTranslatedSource();
            if (ts != null) {
                int rangeStart = lexicalRange.getStart();
                int start = ts.getAstOffset(rangeStart);
                if (start == rangeStart) {
                    return lexicalRange;
                } else if (start == -1) {
                    return OffsetRange.NONE;
                } else {
                    // Assumes the translated range maintains size
                    return new OffsetRange(start, start + lexicalRange.getLength());
                }
            }
        }
        return lexicalRange;
    }

    public static Node getRoot(CompilationInfo info) {
        return getRoot(info, ScalaMimeResolver.MIME_TYPE);
    }

    public static ScalaParserResult getParserResult(CompilationInfo info) {
        ParserResult result = info.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);

        if (result == null) {
            return null;
        } else {
            return (ScalaParserResult) result;
        }
    }

    public static Node getRoot(CompilationInfo info, String mimeType) {
        ParserResult result = info.getEmbeddedResult(mimeType, 0);

        if (result == null) {
            return null;
        }

        return getRoot(result);
    }

    public static Node getRoot(ParserResult r) {
        assert r instanceof ScalaParserResult;

        ScalaParserResult result = (ScalaParserResult) r;

        return result.getRootNode();
    }

    /**
     * Return a range that matches the given node's source buffer range
     */
    @SuppressWarnings("unchecked")
    public static OffsetRange getRange(CompilationInfo info, Node node) {
        OffsetRange range = OffsetRange.NONE;

//        Span span = node.getSpan();
//        try {
//            BaseDocument doc = (BaseDocument) info.getDocument();
//            int begin = getOffset(doc, span.getBegin().getLine(), span.getBegin().column() + 1);
//            int end = getOffset(doc, span.getEnd().getLine(), span.getEnd().column() + 1);
//            range = new OffsetRange(begin, end);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
        return range;
    }

    /**
     * Find offset in text for given line and column
     */
    public static int getOffset(BaseDocument doc, int lineNumber, int columnNumber) {
        assert lineNumber > 0 : "Line number must be at least 1 and was: " + lineNumber;
        assert columnNumber > 0 : "Column number must be at least 1 ans was: " + columnNumber;

        int offset = Utilities.getRowStartFromLineOffset(doc, lineNumber - 1);
        offset += (columnNumber - 1);
        return offset;
    }
}
