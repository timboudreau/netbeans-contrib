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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.javafx.editor;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import org.netbeans.editor.BaseAction;
import org.netbeans.modules.javafx.parser.SemanticAnalysis;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.text.Line;
import org.netbeans.modules.javafx.parser.FXParser;

/**
 * Open CC source according to the given expression.
 *
 * @author Vladimir Voskresensky
 * @version 1.0
 */
public class JavaFXGoToDeclarationAction extends BaseAction {

    public JavaFXGoToDeclarationAction() {
        super("goto-declaration");
        String name = NbBundle.getBundle(JavaFXEditorKit.class).getString("goto-declaration-trimmed");
        putValue(BaseAction.NO_KEYBINDING, "false");
    }

    private static FileObject getFileObject(Document doc) {
        DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
        return od != null ? od.getPrimaryFile() : null;
    }

    private static String extractToken(String text, String separators, int searchPos) {
        int pos = searchPos - 1;
        int leftPos = 0;
        int rightPos = text.length();
        while (pos > 0) {
            String str = text.substring(pos, pos + 1);
            if (separators.contains(str)) {
                leftPos = pos;
                break;
            }
            pos--;
        }
        for (pos = searchPos; pos < rightPos; pos++) {
            String str = text.substring(pos, pos + 1);
            if (separators.contains(str)) {
                rightPos = pos;
                break;
            }
        }
        if (leftPos + 1 <= rightPos)
            return text.substring(leftPos + 1, rightPos);
        else
            return null;
    }
    
    private static boolean doOpen(FileObject fo, int line, int column) {
        try {
            DataObject od = DataObject.find(fo);
            EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
            LineCookie lc = (LineCookie) od.getCookie(LineCookie.class);
            
            if (ec != null && lc != null && line != -1 && column != -1) {                
                StyledDocument doc = ec.openDocument();                
                if (doc != null) {
                    Line l = lc.getLineSet().getCurrent(line);
                    if (l != null) {
                        l.show(Line.SHOW_GOTO, column);
                        return true;
                    }
                }
            }
            
            OpenCookie oc = (OpenCookie) od.getCookie(OpenCookie.class);
            
            if (oc != null) {
                oc.open();                
                return true;
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        
        return false;
    }
    
    public static boolean gotoDeclaration(JTextComponent target) {
        BaseDocument doc = (BaseDocument)target.getDocument();
        int caretPos = target.getCaretPosition();
        gotoDeclaration(doc, caretPos);
        return false;
    }

    public static boolean gotoDeclaration(Document doc, int caretPos) {
        final FileObject fo = getFileObject(doc);
        
        String text = null;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String token = extractToken(text, ";. *:,{}=()[]~+-&|\\/?^%$<>", caretPos);
        
        if (token == null)
            return false;
        
        ArrayList<FXParser.Declaration> declarations = FXParser.getDeclarations(fo);
        if (declarations != null)
            for (FXParser.Declaration declaration : declarations) {
                if (declaration.getName().contentEquals(token))
                    doOpen(declaration.getFileObject(), declaration.getLine(), declaration.getColumn());
            }   
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent evt, JTextComponent target) {
        if (target != null) 
            gotoDeclaration(target);
    }
}