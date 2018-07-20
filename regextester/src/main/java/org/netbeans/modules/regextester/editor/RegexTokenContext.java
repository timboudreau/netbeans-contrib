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

package org.netbeans.modules.regextester.editor;

import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.Utilities;

/*
 * Based on sqleditor
 *
 * @author Martin Adamek
 */

public class RegexTokenContext extends TokenContext {

    // Token categories

    // Numeric-ids for token-ids
    public static final int WHITESPACE_ID = 0; // inside white space
    public static final int LINE_COMMENT_ID = 1; // inside line comment --
    public static final int BLOCK_COMMENT_ID = 2; // inside block comment /* ... */
    public static final int STRING_ID = 3; // inside string constant
    public static final int INCOMPLETE_STRING_ID = 4; // inside string constant after '
    public static final int IDENTIFIER_ID = 5; // inside identifier
    public static final int OPERATOR_ID = 6; // slash char
    public static final int INVALID_CHARACTER_ID = 7; // after '='
    public static final int INVALID_COMMENT_END_ID = 8; // after '0'
    public static final int INT_LITERAL_ID = 9; // integer number
    public static final int DOUBLE_LITERAL_ID = 10; // double number
    public static final int DOT_ID = 11; // after '.'
    public static final int KEYWORD_ID = 12;

    // Token-ids
    public static final BaseTokenID WHITESPACE = 
            new BaseTokenID( "whitespace", WHITESPACE_ID ); // NOI18N
    public static final BaseTokenID LINE_COMMENT = 
            new BaseTokenID( "line-comment", LINE_COMMENT_ID ); // NOI18N
    public static final BaseTokenID BLOCK_COMMENT = 
            new BaseTokenID( "block-comment", BLOCK_COMMENT_ID ); // NOI18N
    public static final BaseTokenID STRING = 
            new BaseTokenID( "string-literal", STRING_ID ); // NOI18N
    public static final BaseTokenID INCOMPLETE_STRING = 
            new BaseTokenID( "incomplete-string-literal", INCOMPLETE_STRING_ID ); // NOI18N
    public static final BaseTokenID IDENTIFIER = 
            new BaseTokenID( "identifier", IDENTIFIER_ID ); // NOI18N
    public static final BaseTokenID OPERATOR = 
            new BaseTokenID( "operator", OPERATOR_ID ); // NOI18N
    public static final BaseTokenID INVALID_CHARACTER = 
            new BaseTokenID( "invalid-character", INVALID_CHARACTER_ID ); // NOI18N
    public static final BaseTokenID INVALID_COMMENT_END = 
            new BaseTokenID( "invalid-comment-end", INVALID_COMMENT_END_ID ); // NOI18N
    public static final BaseTokenID INT_LITERAL = 
            new BaseTokenID( "int-literal", INT_LITERAL_ID ); // NOI18N
    public static final BaseTokenID DOUBLE_LITERAL = 
            new BaseTokenID( "double-literal", DOUBLE_LITERAL_ID ); // NOI18N
    public static final BaseTokenID DOT = 
            new BaseTokenID( "dot", DOT_ID ); // NOI18N
    public static final BaseTokenID KEYWORD = 
            new BaseTokenID( "keyword", KEYWORD_ID ); // NOI18N
        
    // Context instance declaration
    public static final RegexTokenContext context = new RegexTokenContext();
    public static final TokenContextPath contextPath = context.getContextPath();

    /**
     * Constructs a new RegexTokenContext
     */
    private RegexTokenContext() {
        super("regex-"); // NOI18N

         try {
            addDeclaredTokenIDs();
        } catch (Exception e) {
            Utilities.annotateLoggable(e);
        }

    }
}
