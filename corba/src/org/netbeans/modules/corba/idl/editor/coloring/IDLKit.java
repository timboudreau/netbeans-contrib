/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.idl.editor.coloring;

import org.netbeans.editor.Syntax;
import javax.swing.text.Document;
import org.netbeans.modules.editor.NbEditorKit;

/**
* Editor kit implementation for Idl content type
*
* @author Miloslav Metelka, Karel Gardas
* @version 0.01
*/

public class IDLKit extends NbEditorKit {

    static final long serialVersionUID =-64995352874400403L;
    
    public static final String IDL_CONTENT_TYPE = "text/x-idl";

    /** Create new instance of syntax coloring parser */
    public Syntax createSyntax (Document document) {
        return new IDLSyntax ();
    }
    
    public String getContentType () {
        return IDL_CONTENT_TYPE;
    }

}

/*
 * <<Log>>
 *  5    Jaga      1.3.1.0     3/15/00  Miloslav Metelka Structural change
 *  4    Gandalf   1.3         2/8/00   Karel Gardas    
 *  3    Gandalf   1.2         1/18/00  Miloslav Metelka extending 
 *       NbEditorBaseKit
 *  2    Gandalf   1.1         11/27/99 Patrik Knakal   
 *  1    Gandalf   1.0         11/9/99  Karel Gardas    
 * $
 */

