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
package org.netbeans.modules.latex.editor;

import org.netbeans.modules.latex.model.lexer.TexTokenId;

/**
 *
 * @author Jan Lahoda
 */
public class TexColoringNames {

    /** Creates a new instance of TexColoringNames */
    private TexColoringNames() {
    }

    //Basic token colorings:
    public static final String COMMAND_GENERAL   = TexTokenId.COMMAND.name();
    public static final String COMMENT           = TexTokenId.COMMENT.name();
    public static final String WHITESPACE        = TexTokenId.WHITESPACE.name();
    public static final String UNKNOWN_CHARACTER = TexTokenId.UNKNOWN_CHARACTER.name();
    public static final String WORD              = TexTokenId.WORD.name();
    public static final String PARAGRAPH_END     = TexTokenId.PARAGRAPH_END.name();
    
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
    
    public static final String UNUSED             = "mod-unused";
    
}
