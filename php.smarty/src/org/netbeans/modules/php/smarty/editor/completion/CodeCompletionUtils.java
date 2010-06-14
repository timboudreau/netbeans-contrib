/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.smarty.editor.completion;

import java.util.Locale;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.smarty.SmartyFramework;
import org.netbeans.modules.php.smarty.editor.lexer.TplTopTokenId;
import org.netbeans.modules.php.smarty.editor.utlis.LexerUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin Fousek
 */
public class CodeCompletionUtils {

    private static final int COMPLETION_MAX_FILTER_LENGHT = 20;

    public static String getTextPrefix(Document doc, int offset) {
        int readLength = (COMPLETION_MAX_FILTER_LENGHT > offset) ? offset : COMPLETION_MAX_FILTER_LENGHT;
        try {
            int lastWS = getLastWS(doc.getText(offset - readLength, readLength), SmartyFramework.getOpenDelimiter(NbEditorUtilities.getFileObject(doc)));
            return doc.getText(offset - lastWS, lastWS);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    public static int getSubstitutionLenght(Document doc, int offset) {
        return getTextPrefix(doc, offset).length();
    }

    public static int getLastWS(String area, String openDelimiter) {
        for (int i = area.length() - 1; i >= 0; i--) {
            if (LexerUtils.isWS(area.charAt(i))) {
                return area.length() - i - 1;
            } else if (area.substring(i).startsWith(openDelimiter)) {
                return area.length() - i - openDelimiter.length();
            }
        }
        return area.length();
    }

    public static boolean startsWithIgnoreCase(String text, String prefix) {
        return text.toLowerCase(Locale.ENGLISH).startsWith(prefix.toLowerCase(Locale.ENGLISH));
    }

    public static boolean insideSmartyCode(Document doc, int offset) {
        TokenHierarchy tokenHierarchy = TokenHierarchy.get(doc);
        TokenSequence tokenSequence = tokenHierarchy.tokenSequence();

        tokenSequence.move(offset);
        if (tokenSequence.moveNext() || tokenSequence.movePrevious()) {
            Object tokenID = tokenSequence.token().id();
            if (tokenID == TplTopTokenId.T_HTML || tokenID == TplTopTokenId.T_SMARTY_CLOSE_DELIMITER) {
                return false;
            }
        }
        return true;
    }

}
