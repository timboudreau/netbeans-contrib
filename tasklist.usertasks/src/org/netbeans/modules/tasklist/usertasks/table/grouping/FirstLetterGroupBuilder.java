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

import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;

/**
 * Groups objects by their first letters of toString() 
 * (empty strings will land in a special group)
 *
 * @author tl
 */
public class FirstLetterGroupBuilder implements UnaryFunction {
    public FirstLetterGroup compute(Object obj) {
        String v;
        if (obj == null)
            v = "";
        else 
            v = obj.toString();
        v = v.trim();
        if (v.length() == 0 || !Character.isLetter(v.charAt(0)))
            return FirstLetterGroup.NON_LETTER_TEXT_GROUP;
        else
            return new FirstLetterGroup(
                    Character.toUpperCase(v.charAt(0)));
    }
}
