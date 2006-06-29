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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.whitespacetools.actions;
/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class WhiteSpaceConstants {

    // One or more space or tab before the end of line.
    static final String TRAILING_WHITESPACE_REGEXP = "[ \\t]+$"; // NOI18N

    // Zero or more spaces, followed by a tab followed by Zero or more space or tab.
    static final String LEADING_TABS_REGEXP = "^\\ *\\t[ \\t]*"; // NOI18N

    // One or more space or tab
    static final String WHITESPACE_REGEXP = "([ \\t][ \\t]+)([^ \\t])"; // NOI18N

    private WhiteSpaceConstants() {}
}