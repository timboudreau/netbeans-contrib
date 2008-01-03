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

package org.netbeans.modules.scala.util;

import java.io.IOException;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.windows.TopComponent;


/**
 * Utilities related to NetBeans - finding active editor, opening a file location, etc.
 *
 * @author Tor Norbye
 */
public class NbUtilities {

    private static WeakHashMap<Document, JTextComponent> docToEditor = new WeakHashMap<Document, JTextComponent>();

    private NbUtilities() {
    }

    /** @NOTICE: expected in AWT thread only */
    public static JTextComponent getOpenEditorPane(Document doc) {
        JTextComponent pane = docToEditor.get(doc);
        if (pane != null) {
            return pane;
        }
	pane = getOpenPane();
	if (pane != null) {
	    Document docX = pane.getDocument();
            if (docX == doc) {
                docToEditor.put(doc, pane);
                return pane;
            }		
	}
	return null;
    }

    
    public static NbEditorDocument getOpenedDocument(DataObject dataObj) {
        Set<TopComponent> tcs = TopComponent.getRegistry().getOpened();
        for (TopComponent tc : tcs) {
            Node[] nodes = tc.getActivatedNodes();
            for (Node node : nodes) {
                EditorCookie ec = node.getCookie(EditorCookie.class);
                if (ec != null) {
                    JEditorPane[] panes = ec.getOpenedPanes();
                    if (panes != null) {
                        for (JEditorPane pane : panes) {
                            Document doc = pane.getDocument();
                            if (NbEditorUtilities.getDataObject(doc) == dataObj) {
                                return (NbEditorDocument) doc;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /** @NOTICE: expected in AWT thread only, since ec.getOpenedPanes() too */
    public static JEditorPane getOpenPane() {
        Node[] arr = TopComponent.getRegistry().getActivatedNodes();

        if (arr.length > 0) {
            EditorCookie ec = arr[0].getCookie(EditorCookie.class);

            if (ec != null) {
                JEditorPane[] openedPanes = ec.getOpenedPanes();

                if ((openedPanes != null) && (openedPanes.length > 0)) {
                    return openedPanes[0];
                }
            }
        }

        return null;
    }

    public static FileObject findFileObject(JTextComponent target) {
        Document doc = target.getDocument();
        DataObject dobj = (DataObject)doc.getProperty(Document.StreamDescriptionProperty);

        if (dobj == null) {
            return null;
        }

        return dobj.getPrimaryFile();
    }

    // Copied from UiUtils. Shouldn't this be in a common library somewhere?
    public static boolean open(final FileObject fo, final int offset, final String search) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        doOpen(fo, offset, search);
                    }
                });

            return true; // not exactly accurate, but....
        }

        return doOpen(fo, offset, search);
    }

    // Private methods ---------------------------------------------------------
    private static boolean doOpen(FileObject fo, int offset, String search) {
        try {
            DataObject od = DataObject.find(fo);
            EditorCookie ec = (EditorCookie)od.getCookie(EditorCookie.class);
            LineCookie lc = (LineCookie)od.getCookie(LineCookie.class);

            // Simple text search if no known offset (e.g. broken/unparseable source)
            if ((ec != null) && (search != null) && (offset == -1)) {
                StyledDocument doc = ec.openDocument();

                try {
                    String text = doc.getText(0, doc.getLength());
                    offset = text.indexOf(search);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }

            if ((ec != null) && (lc != null) && (offset != -1)) {
                StyledDocument doc = ec.openDocument();

                if (doc != null) {
                    int line = NbDocument.findLineNumber(doc, offset);
                    int lineOffset = NbDocument.findLineOffset(doc, line);
                    int column = offset - lineOffset;

                    if (line != -1) {
                        Line l = lc.getLineSet().getCurrent(line);

                        if (l != null) {
                            l.show(Line.SHOW_GOTO, column);

                            return true;
                        }
                    }
                }
            }

            OpenCookie oc = (OpenCookie)od.getCookie(OpenCookie.class);

            if (oc != null) {
                oc.open();

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
