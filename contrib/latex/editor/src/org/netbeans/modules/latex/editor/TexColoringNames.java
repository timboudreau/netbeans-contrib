/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
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
