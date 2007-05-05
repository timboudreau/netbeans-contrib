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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.tasklist.usertasks.table.grouping;

import org.netbeans.modules.tasklist.usertasks.table.grouping.Group;
import org.netbeans.modules.tasklist.usertasks.table.grouping.Group;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;
import org.openide.util.NbBundle;

/**
 * Defines group for summary (uppercased). 
 * 
 * @author tl
 */
public class FirstLetterGroup extends Group {
    /** Group for texts that do not start with a letter. */
    public static final FirstLetterGroup NON_LETTER_TEXT_GROUP = 
            new FirstLetterGroup((char) 0);
    
    private char letter;

    /**
     * Constructor.
     * 
     * @param letter first letter (upper case) or 0 for empty strings 
     */
    public FirstLetterGroup(char letter) {
        this.letter = letter;
    }

    public String getDisplayName() {
        if (letter == 0)
            return NbBundle.getMessage(FirstLetterGroup.class, 
                    "NonLetter"); // NOI18N
        else
            return String.valueOf(letter);
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FirstLetterGroup other = (FirstLetterGroup) obj;

        if (this.letter != other.letter)
            return false;
        return true;
    }
}

