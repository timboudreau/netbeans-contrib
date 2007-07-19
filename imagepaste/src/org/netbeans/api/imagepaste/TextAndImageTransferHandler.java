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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.imagepaste;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 */
class TextAndImageTransferHandler extends ImageTransferHandler {
    private DataFlavor stringFlavor = DataFlavor.stringFlavor;
    private JTextComponent source;
    private boolean shouldRemove;
    private Position p0 = null, p1 = null;
    public TextAndImageTransferHandler (JEditorPane ed, PasteInfoProvider provider) {
        super (ed, provider);
    }

    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        super.exportAsDrag(comp, e, action);
    }

    public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
        super.exportToClipboard(comp, clip, action);
    }

   public boolean importData(JComponent c, Transferable t) {
        JTextComponent tc = (JTextComponent)c;

        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }

        if (tc.equals(source) && (tc.getCaretPosition() >= p0.getOffset()) &&
                                 (tc.getCaretPosition() <= p1.getOffset())) {
            shouldRemove = false;
            return true;
        }

        if (hasStringFlavor(t.getTransferDataFlavors())) {
            try {
                String str = (String)t.getTransferData(stringFlavor);
                tc.replaceSelection(str);
                return true;
            } catch (UnsupportedFlavorException e) {
                Exceptions.printStackTrace(e);
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }
        //The ColorTransferHandler superclass handles color.
        return super.importData(c, t);
    }

   public boolean canImport(JComponent c, DataFlavor[] flavors) {
        if (hasStringFlavor(flavors)) {
            return true;
        }
        return super.canImport(c, flavors);
    }

    protected Transferable createTransferable(JComponent c) {
        source = (JTextComponent)c;
        int start = source.getSelectionStart();
        int end = source.getSelectionEnd();
        Document doc = source.getDocument();
        if (start == end) {
            return null;
        }
        try {
            p0 = doc.createPosition(start);
            p1 = doc.createPosition(end);
        } catch (BadLocationException e) {
            Logger.getLogger("global").log (Level.INFO, e.getMessage(), 
                    e);
            return null;
        }
        shouldRemove = true;
        String data = source.getSelectedText();
        return new StringSelection(data);
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    //Remove the old text if the action is a MOVE.
    //However, we do not allow dropping on top of the selected text,
    //so in that case do nothing.
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (shouldRemove && (action == MOVE)) {
            if ((p0 != null) && (p1 != null) &&
                (p0.getOffset() != p1.getOffset())) {
                try {
                    JTextComponent tc = (JTextComponent)c;
                    tc.getDocument().remove(
                       p0.getOffset(), p1.getOffset() - p0.getOffset());
                } catch (BadLocationException e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }
        source = null;
    }

    protected boolean hasStringFlavor(DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (stringFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
}
