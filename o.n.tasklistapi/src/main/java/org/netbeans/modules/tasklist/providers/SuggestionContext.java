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
 */
public final class SuggestionContext {

    private final DataObject dataObject;

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

            if (fo.hasExt("properties") && dataObject.isModified() == false) { // NOI18N
                cachedString = PropertiesSuggestionContext.getContent(fo);
                return cachedString;
            }

            if ("xml".equalsIgnoreCase(fo.getExt()) && dataObject.isModified() == false) {  // NOI18N
                cachedString = XMLSuggestionContext.getContent(fo);
                if (cachedString != null) return cachedString;
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
