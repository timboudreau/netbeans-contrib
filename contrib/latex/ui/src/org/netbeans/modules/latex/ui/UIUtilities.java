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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.ErrorManager;
import org.openide.text.CloneableEditor;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class UIUtilities {
    
    /** Creates a new instance of Utilities */
    public UIUtilities() {
    }
    
    public static JEditorPane getCurrentEditorPane() {
        return Utilities.getDefault().getLastActiveEditorPane();
////        System.err.println("1");
//        TopComponent comp = TopComponent.getRegistry().getActivated();
//        
////        System.err.println("comp.getClass()" + comp.getClass());
//        if (comp == null || !(comp instanceof CloneableEditor))
//            return null;//Nothing to do.
//        
////        System.err.println("2");
//        
//        CloneableEditor editor = (CloneableEditor) comp;
//        
//        try {
//            Field field = CloneableEditor.class.getDeclaredField("pane");
//            
////            System.err.println("3");
//            
//            field.setAccessible(true);
//            
////            System.err.println("4");
//            
//            JEditorPane pane = (JEditorPane) field.get(editor);
////            System.err.println("5");
//            
//            return pane;
//        } catch (NoSuchFieldException nsfe) {
//            IllegalStateException ex = new IllegalStateException("Cannot access pane field.");
//            ErrorManager.getDefault().annotate(ex, nsfe);
//            throw ex;
//        } catch (IllegalArgumentException nsfe) {
//            IllegalStateException ex = new IllegalStateException("Cannot access pane field.");
//            ErrorManager.getDefault().annotate(ex, nsfe);
//            throw ex;
//        } catch (IllegalAccessException nsfe) {
//            IllegalStateException ex = new IllegalStateException("Cannot access pane field.");
//            ErrorManager.getDefault().annotate(ex, nsfe);
//            throw ex;
//        }
    }
    
    public static void removeCommand(CommandNode node) throws IOException, BadLocationException {
        SourcePosition start = node.getStartingPosition();
        SourcePosition end   = node.getEndingPosition();
        
        if (!Utilities.getDefault().compareFiles(start.getFile(), end.getFile()))
            throw new IllegalArgumentException("...");
        
        Document doc = Utilities.getDefault().openDocument(start.getFile());
        
        int commandRemoveOffsetStart = start.getOffsetValue();
        int commandRemoveOffsetEnd   = node.getArgument(0).getStartingPosition().getOffsetValue();
        Position endOffset           = end.getOffset();
        
        doc.remove(commandRemoveOffsetStart, commandRemoveOffsetEnd - commandRemoveOffsetStart);
        doc.remove(endOffset.getOffset(), 1);
    }
    
    public static void insertCommand(String comandName, Position start, Position end) {
    }
    
    public static Icon loadIcon(String resource) {
        URL imgURL = UIUtilities.class.getResource(resource);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
//            System.err.println("Couldn't find file: " + name);
            return null;
        }
    }
}
