/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.parser;

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.Command;

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
        
        switch (bracket.getId().getIntId()) {
            case TexLanguage.COMP_BRACKET_LEFT_INT: stack.append('{'); return true;
            case TexLanguage.RECT_BRACKET_LEFT_INT: stack.append('['); return true;
            case TexLanguage.MATH_INT:
                if (stack.charAt(stack.length() - 1) == '$')
                    stack = stack.delete(stack.length() - 1, stack.length());
                else
                    stack.append("$");
                return true;
                
            case TexLanguage.COMP_BRACKET_RIGHT_INT:
                if ((stack.length() > 0) && (stack.charAt(stack.length() - 1) == '{')) {
                    stack = stack.delete(stack.length() - 1, stack.length());
                    return true;
                } else {
                    return false;
                }
                
            case TexLanguage.RECT_BRACKET_RIGHT_INT:
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
        return    token.getId() == TexLanguage.COMP_BRACKET_LEFT
               || token.getId() == TexLanguage.RECT_BRACKET_LEFT;
    }
    
    public static boolean isClosingBracket(Token token) {
        return    token.getId() == TexLanguage.COMP_BRACKET_RIGHT
               || token.getId() == TexLanguage.RECT_BRACKET_RIGHT;
    }
    
    public static boolean isBracket(Token token) {
        return isOpeningBracket(token) || isClosingBracket(token) || token.getId() == TexLanguage.MATH;
    }
    
    public static boolean matches(Token left, Token right) {
        return    (   left.getId() == TexLanguage.COMP_BRACKET_LEFT
                   && right.getId() == TexLanguage.COMP_BRACKET_RIGHT)
               || (   left.getId() == TexLanguage.RECT_BRACKET_LEFT
                   && right.getId() == TexLanguage.RECT_BRACKET_RIGHT);
    }
    
    public static boolean isWhitespace(Token token) {
        return    token.getId() == TexLanguage.WHITESPACE
               || token.getId() == TexLanguage.COMMENT;
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
