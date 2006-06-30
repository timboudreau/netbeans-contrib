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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
