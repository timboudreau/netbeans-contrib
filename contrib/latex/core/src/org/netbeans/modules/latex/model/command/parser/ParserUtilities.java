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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.parser;

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.lexer.TexTokenId;

/**
 *
 * @author Jan Lahoda
 */
public class ParserUtilities {

    /** Creates a new instance of ParserUtilities */
    private ParserUtilities() {
    }

    public static boolean updateBracketStack(StringBuffer stack, Token bracket) {
        assert isBracket(bracket);
        
        switch ((TexTokenId) bracket.id()) {
            case COMP_BRACKET_LEFT: stack.append('{'); return true;
            case RECT_BRACKET_LEFT: stack.append('['); return true;
            case MATH:
                if (stack.charAt(stack.length() - 1) == '$')
                    stack = stack.delete(stack.length() - 1, stack.length());
                else
                    stack.append("$");
                return true;
                
            case COMP_BRACKET_RIGHT:
                if ((stack.length() > 0) && (stack.charAt(stack.length() - 1) == '{')) {
                    stack = stack.delete(stack.length() - 1, stack.length());
                    return true;
                } else {
                    return false;
                }
                
            case RECT_BRACKET_RIGHT:
                if ((stack.length() > 0) && (stack.charAt(stack.length() - 1) == '[')) {
                    stack = stack.delete(stack.length() - 1, stack.length());
                    return true;
                } else {
                    return false;
                }
        }
        
        return false;
    }

    public static boolean isOpeningBracket(Token token) {
        return    token.id() == TexTokenId.COMP_BRACKET_LEFT
               || token.id() == TexTokenId.RECT_BRACKET_LEFT;
    }
    
    public static boolean isClosingBracket(Token token) {
        return    token.id() == TexTokenId.COMP_BRACKET_RIGHT
               || token.id() == TexTokenId.RECT_BRACKET_RIGHT;
    }
    
    public static boolean isBracket(Token token) {
        return isOpeningBracket(token) || isClosingBracket(token) || token.id() == TexTokenId.MATH;
    }
    
    public static boolean matches(Token left, Token right) {
        return    (   left.id() == TexTokenId.COMP_BRACKET_LEFT
                   && right.id() == TexTokenId.COMP_BRACKET_RIGHT)
               || (   left.id() == TexTokenId.RECT_BRACKET_LEFT
                   && right.id() == TexTokenId.RECT_BRACKET_RIGHT);
    }
    
    public static boolean isWhitespace(Token token) {
        return    token.id() == TexTokenId.WHITESPACE
               || token.id() == TexTokenId.COMMENT;
    }
    
    private static CharSequence removeBrackets(CharSequence text, char open, char close) {
        CharSequence result = text;
        
        if (result.charAt(0) == open) {
            result = result.subSequence(1, result.length());
        }
        
        if (result.charAt(result.length() - 1) == close) {
            result = result.subSequence(0, result.length() - 1);
        }
        
        return result;
    }
    
    public static CharSequence getArgumentValue(ArgumentNode an) {
        CharSequence argumentText = an.getFullText();
        
        switch (an.getArgument().getType()) {
            case Command.Param.MANDATORY:
                return removeBrackets(argumentText, '{', '}');
            case Command.Param.NONMANDATORY:
                return removeBrackets(argumentText, '[', ']');
            case Command.Param.FREE:
                return argumentText;
            case Command.Param.SPECIAL:
                //do not know what to do with this:
                return argumentText;
            default:
                throw new IllegalArgumentException("Unknown argument type:" + an.getArgument().getType());
        }
    }

}
