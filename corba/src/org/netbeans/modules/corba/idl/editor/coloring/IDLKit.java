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

package org.netbeans.modules.corba.idl.editor.coloring;

import org.openide.util.Lookup;
import org.openide.text.IndentEngine;
import org.netbeans.editor.Syntax;
import javax.swing.Action;
import javax.swing.text.TextAction;
import javax.swing.text.Document;
import javax.swing.text.Caret;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Acceptor;
import org.netbeans.editor.AcceptorFactory;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.ext.ExtSettingsNames;
import org.netbeans.modules.editor.NbEditorKit;
import org.netbeans.modules.corba.idl.editor.indent.IDLIndentEngine;

/**
* Editor kit implementation for Idl content type
*
* @author Miloslav Metelka, Karel Gardas, Tomas Zezula
* @version 0.01
*/

public class IDLKit extends NbEditorKit {

    static final long serialVersionUID =-64995352874400403L;
    
    public static final String IDL_CONTENT_TYPE = "text/x-idl";     // No I18N
    
    public static class IDLDefaultKeyTypedAction extends org.netbeans.editor.ext.ExtKit.ExtDefaultKeyTypedAction {
        
        public void actionPerformed (java.awt.event.ActionEvent event, javax.swing.text.JTextComponent component) {
            super.actionPerformed (event, component);
            Document doc = component.getDocument();
            if (doc instanceof BaseDocument) {
// WORKAROUD {BEGIN}
                Object o = doc.getProperty ("indentEngine");
                IndentEngine engine = null;
                if (o instanceof IndentEngine) 
                    engine = (IndentEngine) o;
                else if (o instanceof String) {
                    System.out.println("Document.getProperty(\"indentEngine\") workaround org.netbeans.modules.corba.idl.editor.coloring.IDLKit(49)");
                    Lookup.Template template = new Lookup.Template (IndentEngine.class, (String)o, null);
                    Lookup.Result result = Lookup.getDefault().lookup (template);
                    if (result != null) {
                        java.util.Collection c = result.allItems();
                        if (c != null && c.size() > 0) {
                            java.util.Iterator it = c.iterator();
                                Lookup.Item item = (Lookup.Item) it.next();
                                engine = (IndentEngine) item.getInstance();
                        }
                    }
                }
//               IndentEngine engine = (IndentEngine) doc.getProperty ("indentEngine");       // No I18N 
// WORKAROUND {END}
               if (engine != null && ( engine instanceof IDLIndentEngine)) {
                   Acceptor indentAcceptor = SettingsUtil.getAcceptor(IDLKit.class,ExtSettingsNames.INDENT_HOT_CHARS_ACCEPTOR, AcceptorFactory.FALSE);
                   if (indentAcceptor != null) {
                       String value = event.getActionCommand();
                       if (value != null && value.length() > 0) {
                        if (indentAcceptor.accept (value.charAt(0))) {
                           Caret caret = component.getCaret(); 
                           int dotPos = caret.getDot();
                           int newDotPos = engine.indentLine (doc, dotPos);
                           caret.setDot (newDotPos);
                        }
                       }
                   }
               }
            }
        }
    }

    /** Create new instance of syntax coloring parser */
    public Syntax createSyntax (Document document) {
        return new IDLSyntax ();
    }
    
    public String getContentType () {
        return IDL_CONTENT_TYPE;
    }

    protected Action[] createActions () {
        Action[] actions = new Action[] {
            new IDLDefaultKeyTypedAction ()
        };
        return TextAction.augmentList (super.createActions(),actions);
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

