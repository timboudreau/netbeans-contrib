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

