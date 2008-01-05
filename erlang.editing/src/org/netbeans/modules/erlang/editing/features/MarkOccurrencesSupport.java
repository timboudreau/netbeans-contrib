/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.erlang.editing.features;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.Highlighting;
import org.netbeans.api.languages.Highlighting.Highlight;
import org.netbeans.api.languages.ParseException;
import org.netbeans.api.languages.ParserManager;
import org.netbeans.api.languages.ParserManager.State;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.erlang.editing.semantic.ErlRoot;
import org.netbeans.modules.erlang.editing.semantic.ErlangSemanticParser;
import org.netbeans.modules.erlang.editing.util.NbUtilities;
import org.openide.text.Annotation;
import org.openide.util.RequestProcessor;


/**
 *
 * @author Jan Jancura
 */
public class MarkOccurrencesSupport implements CaretListener {

    private static Map<JTextComponent,WeakReference<MarkOccurrencesSupport>> 
                                        editorToMOS = new WeakHashMap<JTextComponent,WeakReference<MarkOccurrencesSupport>> ();
    private JTextComponent              editor;
    private RequestProcessor.Task       parsingTask;
    private List<Highlight>             highlights;
    private List<LanguagesAnnotation>   annotations;
    
    
    public MarkOccurrencesSupport (JTextComponent editor) {
        this.editor = editor;
        editorToMOS.put (editor, new WeakReference<MarkOccurrencesSupport> (this));
    }

    public void caretUpdate (final CaretEvent e) {
        if (parsingTask != null) {
            parsingTask.cancel ();
        }
        parsingTask = RequestProcessor.getDefault ().post (new Runnable () {
            public void run () {
                refresh (e.getDot ());
            }
        }, 1000);
    }
    
    private void refresh (int offset) {
        ParserManager parserManager = ParserManager.get (editor.getDocument ());
        if (parserManager.getState () == State.PARSING) {
            return;
        }
        try {
            ASTNode astRoot= parserManager.getAST ();
            ErlRoot erlRoot = ErlangSemanticParser.getErlRoot(editor.getDocument(), astRoot);
            if (erlRoot == null) {
                return;
            }
	    Set<ASTToken> usages = erlRoot.getOccurrentUsages(offset);
	    if (usages != null && ! usages.isEmpty()) {
		removeHighlights ();
		addHighlights(usages);
	    }
        } catch (ParseException ex) {
            ex.printStackTrace ();
        }
    }
    
    private void addHighlights (final Set<ASTToken> usages) {
        if (usages.isEmpty ()) return;
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                try {
                    NbEditorDocument doc = (NbEditorDocument) editor.getDocument ();
                    Highlighting highlighting = Highlighting.getHighlighting (doc);
                    annotations = new ArrayList<LanguagesAnnotation> ();
                    highlights = new ArrayList<Highlight> ();
                    Iterator<ASTToken> it = usages.iterator ();
                    HashSet<Integer> lines = new HashSet<Integer>();
                    while (it.hasNext ()) {
                        ASTToken i = it.next ();
                        highlights.add (highlighting.highlight (i.getOffset (), i.getEndOffset (), getHighlightAS ()));
                        int lineNumber = Utilities.getLineOffset(doc, i.getOffset());
                        if (!lines.contains(lineNumber)) {
                            LanguagesAnnotation la = new LanguagesAnnotation(
                                "Usage",
                                "..."
                            );
                            doc.addAnnotation(
                                doc.createPosition(i.getOffset()),
                                i.getLength(),
                                la
                            );
                            lines.add(lineNumber);
                            annotations.add(la);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace ();
                }
            }
        });
    }
    
    static void removeHighlights (JTextComponent editor) {
        WeakReference<MarkOccurrencesSupport> wr = editorToMOS.get (editor);
        if (wr == null) return;
        MarkOccurrencesSupport mos = wr.get ();
        if (mos == null) return;
        mos.removeHighlights ();
    }

    private void removeHighlights () {
        if (highlights == null) return;
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                if (highlights == null) return;
                NbEditorDocument doc = (NbEditorDocument) editor.getDocument ();
                Iterator<Highlight> it = highlights.iterator ();
                while (it.hasNext ())
                    it.next ().remove ();
                Iterator<LanguagesAnnotation> it2 = annotations.iterator ();
                while (it2.hasNext ())
                    doc.removeAnnotation (it2.next ());
                highlights = null;
                annotations = null;
            }
        });
    }
            
    private static AttributeSet highlightAS = null;
    
    private static AttributeSet getHighlightAS () {
        if (highlightAS == null) {
            SimpleAttributeSet as = new SimpleAttributeSet ();
            as.addAttribute (StyleConstants.Background, new Color (236, 235, 163));
            highlightAS = as;
        }
        return highlightAS;
    }

    public static void checkInstallation(final Document doc) {
        // expected in AWT thread only
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
	        JTextComponent comp = NbUtilities.getOpenEditorPane(doc);
		if (comp != null && ! editorToMOS.containsKey(comp)) {
		    comp.addCaretListener(new MarkOccurrencesSupport(comp));
		}
            }
        });
    }
    
    static class LanguagesAnnotation extends Annotation {

        private String type;
        private String description;

        /** Creates a new instance of ToolsAnotation */
        public LanguagesAnnotation (
            String type,
            String description
        ) {
            this.type = type;
            this.description = description;
        }

        /** Returns name of the file which describes the annotation type.
         * The file must be defined in module installation layer in the
         * directory "Editors/AnnotationTypes"
         * @return  name of the anotation type
         */
        public String getAnnotationType () {
            return type;
        }

        /** Returns the tooltip text for this annotation.
         * @return  tooltip for this annotation
         */
        public String getShortDescription () {
            return description;
        }
        
        private Position position;
        
        void setPosition (Position position) {
            this.position = position;
        }
        
        Position getPosition () {
            return position;
        }
    }

}
