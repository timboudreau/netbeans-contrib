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

package org.netbeans.modules.regextester.editor;

import javax.swing.text.Document;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.NbEditorKit;

/*
 * Based on sqleditor
 *
 * @author Martin Adamek
 */
public class RegexEditorKit extends NbEditorKit {

    public static final String MIME_TYPE = "text/x-regex"; // NOI18N

    /**
     * Creates a new instance of RegexEditorKit
     */
    public RegexEditorKit() {
    }

    /**
     * Create a syntax object suitable for highlighting Regex syntax
     */
    public Syntax createSyntax(Document doc) {
        return new RegexSyntax();
    }
    
    
    
//    /** Create syntax support */
//    public SyntaxSupport createSyntaxSupport(BaseDocument doc) {
//        return new RegexSyntaxSupport(doc);
//    }
    
    /**
     * Retrieves the content type for this editor kit
     */
    public String getContentType() {
        return MIME_TYPE;
    }
}
