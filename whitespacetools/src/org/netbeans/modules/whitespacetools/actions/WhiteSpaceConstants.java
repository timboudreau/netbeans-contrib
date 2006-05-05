/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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