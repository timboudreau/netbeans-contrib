/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.editor.bibtex;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.HashMap;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.editor.ext.plain.PlainSyntax;
import org.netbeans.modules.editor.NbEditorKit;
import org.netbeans.modules.lexer.editorbridge.LanguageDescriptor;
import org.netbeans.modules.lexer.editorbridge.LexerLayerInitializer;


/**
* Editor kit used to edit the plain text.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class BiBTeXKit extends NbEditorKit {
    
    static {
        Settings.addInitializer(new LexerLayerInitializer(
            new LanguageDescriptor(BiBTeXLanguage.get(), BiBTeXKit.class,
            "bibtex-", createTokenId2ColoringMap()))
        );
    }
    
    private static Map createTokenId2ColoringMap() {
        Map m = new HashMap();

        m.put(BiBTeXLanguage.COMMENT, new Coloring(new Font("Monospaced", Font.ITALIC, 12), Coloring.FONT_MODE_APPLY_STYLE, Color.gray, null));
        m.put(BiBTeXLanguage.CL_BRAC, new Coloring(null, null, null));
        m.put(BiBTeXLanguage.OP_BRAC, new Coloring(null, null, null));
        m.put(BiBTeXLanguage.COMMA, new Coloring(null, null, null));
        m.put(BiBTeXLanguage.EQUALS, new Coloring(null, null, null));
        m.put(BiBTeXLanguage.UNKNOWN_CHARACTER, new Coloring(null, null, null));
        
        m.put(BiBTeXLanguage.STRING, new Coloring(null, new Color(153, 0, 107), null));
        m.put(BiBTeXLanguage.TEXT, new Coloring(null, null, null));
        m.put(BiBTeXLanguage.TYPE, new Coloring(null, new Color(0, 0, 153), null));

        return m;
    }

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
        System.err.println(doc.getDocumentProperties());
        doc.putProperty("mime-type", getContentType());
    }

}
