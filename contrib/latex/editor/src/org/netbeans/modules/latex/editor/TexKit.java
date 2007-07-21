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

package org.netbeans.modules.latex.editor;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.TextAction;
import org.netbeans.api.lexer.Language;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.modules.editor.NbEditorKit;
import org.openide.util.RequestProcessor;

/**
* Editor kit used to edit the plain text.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class TexKit extends NbEditorKit {
    
    protected Action[] createActions() {
        Action[] texActions = new Action[] {
            new ActionsFactory.GoToDeclarationActionImpl(),
            new ActionsFactory.WordCountAction(),
            new ActionsFactory.CiteAction(ActionsFactory.CiteAction.CITE),
            new ActionsFactory.CiteAction(ActionsFactory.CiteAction.REF),
            new CommentAction("%"),
            new UncommentAction("%"),
        };
        return TextAction.augmentList(super.createActions(), texActions);
    }

    public static final String TEX_MIME_TYPE = "text/x-tex"; // NOI18N

    public @Override String getContentType() {
//        System.err.println("TexKit getContentType.");
        return TEX_MIME_TYPE;
    }

    public @Override Syntax createSyntax(Document doc) {
        return new TexSyntax();
    }

    public @Override SyntaxSupport createSyntaxSupport(BaseDocument doc) {
        return new TexSyntaxSupport(doc);
    }

//    public Completion createCompletion(ExtEditorUI extEditorUI) {
//        return new TexCompletion(extEditorUI);
//    }
//    
//    public CompletionJavaDoc createCompletionJavaDoc(ExtEditorUI extEditorUI) {
//        return new TexCompletionJavaDoc(extEditorUI);
//    }
    
    public @Override void install(JEditorPane pane) {
        super.install(pane);
        
        //Set locale, test:
//        Locale csCZ = new Locale("cs", "CZ");
        
//        System.err.println("Setting input method: " + pane.getInputContext().selectInputMethod(csCZ));
//        pane.setLocale(csCZ);
    }
    
    public @Override void deinstall(JEditorPane pane) {
        super.deinstall(pane);
    }
    
    protected @Override void initDocument(final BaseDocument doc) {
        super.initDocument(doc);
        doc.putProperty("mime-type", TEX_MIME_TYPE);
        doc.putProperty(Language.class, TexLanguage.description());
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                ColoringEvaluator.getColoringEvaluator(doc);
            }
        }, 2000);
/*        try {
            System.err.println("Creating spelling layer:");*/
//            if (!Dictionary.getDefault().isEmpty())
//                doc.addLayer(new SpellingLayer(lDescriptor, doc), 1010);
            
//            doc.addLayer(new LexerLayer(lDescriptor, doc), 1009);
/*            System.err.println("done");
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            throw e;
        }*/
    }

}
