/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.providers;

import org.openide.loaders.DataObject;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;

import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import java.io.IOException;

/**
 * Passes working environment to suggestion provides.
 *
 * @author Petr Kuzel
 * @deprecated Experimental SPI
 * @since 1.3
 *
 */
public final class SuggestionContext {

    private DataObject dataObject;

    private String cachedString;

    // we have soft runtime dependency on java module
    private static boolean linkageError;

    /**
     * For internal framework purposes only!
     */
    SuggestionContext(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * @return read-only snapshot of context representation
     */
    public CharSequence getCharSequence() {

        if (cachedString == null) {

            FileObject fo = getFileObject();
            if (linkageError == false && fo.hasExt("java")) {  // NOI18N
                // use faster direct access to file for java sources
                // I measured 10% speedup
                try {
                    // XXX it does not normalize line separators to \n
                    cachedString = JavaSuggestionContext.getContent(fo);
                    return cachedString;
                } catch (LinkageError link) {
                    // use EditorCookie below
                    link.printStackTrace();
                    linkageError = true;
                }
            }

            EditorCookie edit =
                (EditorCookie) dataObject.getCookie(EditorCookie.class);
            if (edit != null) {
                Document doc;
                try {
                    doc = edit.openDocument(); // DOES block
                    cachedString = extractString(doc);
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
        return cachedString;
    }

    /**
     * @return read/write live in-memory context representation
     */
    public Document getDocument() {
        EditorCookie edit =
            (EditorCookie) dataObject.getCookie(EditorCookie.class);
        if (edit != null) {
            try {
                return edit.openDocument(); // DOES block
            } catch (IOException e) {
                // XXX
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @return filesystem context representation
     */
    public FileObject getFileObject() {
        return dataObject.getPrimaryFile();
    }

    /**
     * Extracts document content as a string
     * @param doc source document (never null)
     * @return extracted text
     */
    private static String extractString(final Document doc) {
        final String text[] = new String[1];
        doc.render(new Runnable () {
            public void run() {
                try {
                    text[0] = doc.getText(0, doc.getLength());
                } catch (BadLocationException ex) {
                    assert false : ex;
                }
            }
        });
        return text[0];
    }

}
