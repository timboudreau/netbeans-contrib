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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.editor.bibtex;

import javax.swing.text.Document;
import org.netbeans.api.lexer.Language;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.editor.ext.plain.PlainSyntax;
import org.netbeans.modules.editor.NbEditorKit;


/**
* Editor kit used to edit the plain text.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class BiBTeXKit extends NbEditorKit {
    
//    static {
//        Settings.addInitializer(new LexerLayerInitializer(
//            new LanguageDescriptor(BiBTeXLanguage.get(), BiBTeXKit.class,
//            "bibtex-", createTokenId2ColoringMap()))
//        );
//    }
//    
//    private static Map createTokenId2ColoringMap() {
//        Map m = new HashMap();
//
//        m.put(BiBTeXLanguage.COMMENT, new Coloring(new Font("Monospaced", Font.ITALIC, 12), Coloring.FONT_MODE_APPLY_STYLE, Color.gray, null));
//        m.put(BiBTeXLanguage.CL_BRAC, new Coloring(null, null, null));
//        m.put(BiBTeXLanguage.OP_BRAC, new Coloring(null, null, null));
//        m.put(BiBTeXLanguage.COMMA, new Coloring(null, null, null));
//        m.put(BiBTeXLanguage.EQUALS, new Coloring(null, null, null));
//        m.put(BiBTeXLanguage.UNKNOWN_CHARACTER, new Coloring(null, null, null));
//        
//        m.put(BiBTeXLanguage.STRING, new Coloring(null, new Color(153, 0, 107), null));
//        m.put(BiBTeXLanguage.TEXT, new Coloring(null, null, null));
//        m.put(BiBTeXLanguage.TYPE, new Coloring(null, new Color(0, 0, 153), null));
//
//        return m;
//    }

    public String getContentType() {
        return "text/x-bibtex";
    }

    public Syntax createSyntax(Document doc) {
        return new PlainSyntax();
    }

    public SyntaxSupport createSyntaxSupport(BaseDocument doc) {
        return new ExtSyntaxSupport(doc);
    }

    protected void initDocument(BaseDocument doc) {
        super.initDocument(doc);
        doc.putProperty("mime-type", getContentType());
        doc.putProperty(Language.class, BiBTeXLanguage.description());
    }

}
