/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.editor;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

import org.netbeans.api.lexer.Token;
import org.netbeans.editor.BaseAction;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.MultiKeyBinding;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.ext.Completion;
import org.netbeans.editor.ext.CompletionJavaDoc;
import org.netbeans.editor.ext.ExtEditorUI;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.editor.ext.ExtSyntaxSupport;
import org.netbeans.modules.editor.NbEditorKit;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.modules.lexer.editorbridge.LanguageDescriptor;
import org.netbeans.modules.lexer.editorbridge.LexerLayerInitializer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.text.Line;

/**
* Editor kit used to edit the plain text.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class TexKit extends NbEditorKit {
    
    private static LanguageDescriptor lDescriptor;
    
    static {
//        try {
        Settings.addInitializer(new TexSettingsInitializer(/*TexKit.class*/));
        Settings.reset();
        
        lDescriptor = new LanguageDescriptor(TexLanguage.get(), TexKit.class,
        "tex-", null);
//        } catch (Throwable t) {
//            t.printStackTrace(System.err);
//        }
    }
    
    protected Action[] createActions() {
        Action[] texActions = new Action[] {
            new ActionsFactory.GoToDeclarationAction(),
            new ActionsFactory.WordCountAction(),
            new ActionsFactory.CiteAction(ActionsFactory.CiteAction.CITE),
            new ActionsFactory.CiteAction(ActionsFactory.CiteAction.REF),
            new CommentAction("%"),
            new UncommentAction("%"),
            new ActionsFactory.BuildApproximateWordList(),
        };
        return TextAction.augmentList(super.createActions(), texActions);
    }

    public static final String TEX_MIME_TYPE = "text/x-tex"; // NOI18N

    public String getContentType() {
//        System.err.println("TexKit getContentType.");
        return TEX_MIME_TYPE;
    }

    public Syntax createSyntax(Document doc) {
        return new TexSyntax();
    }

    public SyntaxSupport createSyntaxSupport(BaseDocument doc) {
        return new TexSyntaxSupport(doc);
    }
    
//    public Completion createCompletion(ExtEditorUI extEditorUI) {
//        return new TexCompletion(extEditorUI);
//    }
//    
//    public CompletionJavaDoc createCompletionJavaDoc(ExtEditorUI extEditorUI) {
//        return new TexCompletionJavaDoc(extEditorUI);
//    }
    
    public void install(JEditorPane pane) {
        super.install(pane);
        Server.getDefault().openHook(pane);
        
        //Set locale, test:
//        Locale csCZ = new Locale("cs", "CZ");
        
//        System.err.println("Setting input method: " + pane.getInputContext().selectInputMethod(csCZ));
//        pane.setLocale(csCZ);
    }
    
    public void deinstall(JEditorPane pane) {
        super.deinstall(pane);
        Server.getDefault().closeHook(pane);
    }
    
    protected void initDocument(BaseDocument doc) {
        super.initDocument(doc);
        doc.putProperty("mime-type", TEX_MIME_TYPE);
/*        try {
            System.err.println("Creating spelling layer:");*/
//            if (!Dictionary.getDefault().isEmpty())
//                doc.addLayer(new SpellingLayer(lDescriptor, doc), 1010);
            
            doc.addLayer(new LexerLayer(lDescriptor, doc), 1009);
/*            System.err.println("done");
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            throw e;
        }*/
    }

}
