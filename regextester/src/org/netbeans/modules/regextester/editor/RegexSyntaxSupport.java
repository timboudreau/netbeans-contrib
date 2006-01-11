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

package org.netbeans.modules.regextester.editor;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.ext.ExtSyntaxSupport;

/*
 * Based on sqleditor
 *
 * @author Martin Adamek
 */
public class RegexSyntaxSupport extends ExtSyntaxSupport {
    
    public RegexSyntaxSupport(BaseDocument doc) {
        super(doc);
        System.out.println("### RegexSyntaxSupport CONSTRUCTOR");
    }

    /**
     * Get the array of token IDs that denote the comments.
     */
    public TokenID[] getCommentTokens() {
        return new TokenID[0];
    }
}
