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
package org.netbeans.modules.latex.editor;

/**
 *
 * @author Jan Lahoda
 */
public class TexColoringNames {
    
    /** Creates a new instance of TexColoringNames */
    private TexColoringNames() {
    }

    //Basic token colorings:
    public static final String COMMAND_GENERAL   = TexLanguage.COMMAND.getName();
    public static final String COMMENT           = TexLanguage.COMMENT.getName();
    public static final String WHITESPACE        = TexLanguage.WHITESPACE.getName();
    public static final String UNKNOWN_CHARACTER = TexLanguage.UNKNOWN_CHARACTER.getName();
    public static final String WORD              = TexLanguage.WORD.getName();
    public static final String PARAGRAPH_END     = TexLanguage.PARAGRAPH_END.getName();
    
    //Modifiers:
    
    public static final String MATH              = "mod-math";
    
    public static final String COMMAND_INCORRECT = "mod-command-incorrect";
    public static final String COMMAND_CORRECT   = "mod-command-correct";
    public static final String DEFINITION        = "mod-command-definition";

    public static final String ENUM_ARG_INCORRECT = "mod-enum-arg-incorrect";
    public static final String ENUM_ARG_CORRECT   = "mod-enum-arg-correct";
    
    public static final String ARG_INCORRECT      = "mod-arg-incorrect";

    public static final String WORD_BAD           = "mod-word-bad";
    public static final String WORD_INCORRECT     = "mod-word-incorrect";
    public static final String WORD_INCOMPLETE    = "mod-word-incomplete";
    
}
