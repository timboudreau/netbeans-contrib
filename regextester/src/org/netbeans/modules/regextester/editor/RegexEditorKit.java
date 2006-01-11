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

import javax.swing.text.Document;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.java.JavaKit;

/*
 * Based on sqleditor
 *
 * @author Martin Adamek
 */
public class RegexEditorKit extends JavaKit {
    
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
