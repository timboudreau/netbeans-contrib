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

package org.netbeans.modules.fileopenserver;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;


/**
 * Listener of file open rquests.
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class FileOpenRequestListenerImpl implements FileOpenRequestListener {
    /**
     * Open the requested file.
     *
     * @see FileOpenServer.FileOpenRequestListener#fileOpenRequest(FileOpenServer.FileOpenRequestEvent)
     */
    public void fileOpenRequest(final FileOpenRequestEvent event) {        
        final String filepath = event.getFileName();
        
        if (filepath != null) {
            final File file = new File(filepath);
            final int lineNumber = event.getLineNumber();
            final int columnNumber = event.getColumnNumber();
            
            if (FileOpenServer.getFileOpenServerSettings().isLogRequests()) {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL,
                        (event.isExternal() ? "[EXT]" : "[INT]") + filepath + ":" + lineNumber + ":" + columnNumber);
                IOProvider.getDefault().getStdOut().println((event.isExternal() ? "Open in external editor: " : "Open in NetBeans editor: ") + filepath + ":" + lineNumber + ":" + columnNumber);
            }
            
            if (event.isExternal()) {
                String externalEditorCommand = FileOpenServer.getFileOpenServerSettings().getExternalEditorCommand();
                
                if (externalEditorCommand != null && externalEditorCommand.length() > 0) {
                    try {
                        MessageFormat mf = new MessageFormat(
                                externalEditorCommand);
                        NumberFormat nf = NumberFormat.getIntegerInstance();
                        nf.setGroupingUsed(false);
                        mf.setFormatByArgumentIndex(1, nf);
                        mf.setFormatByArgumentIndex(2, nf);
                        String command = mf.format(new Object[] {
                            filepath.toString(), 
                            new Integer(lineNumber + (FileOpenServerSettings.getInstance().isLineNumberStartsWith0() ? 0 : 1)),
                            new Integer(columnNumber + (FileOpenServerSettings.getInstance().isColumnNumberStartsWith0() ? 0 : 1))});
                            String[] commandAndArgsArray = Utilities.parseParameters(command);
                            Process externalEditorProcess = Runtime.getRuntime()
                            .exec(commandAndArgsArray);
                            externalEditorProcess.waitFor();
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
                    } catch (InterruptedException e) {
                        ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
                    }
                }
            } else {
                if (file != null && file.exists()) {
                    try {
                        FileObject fileObject = FileUtil.toFileObject(file);
                        final DataObject dataObject = DataObject.find(fileObject);
                        final EditorCookie editorCookie = (EditorCookie)dataObject.getCookie(EditorCookie.class);
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                
                                
                                if (editorCookie != null) {
                                    editorCookie.open();
                                    if (editorCookie.getOpenedPanes() == null) {
                                        editorCookie.open();
                                    }
                                    StyledDocument styledDocument = editorCookie.getDocument();
                                    JEditorPane editorPane = editorCookie.getOpenedPanes()[0];
                                    editorPane.setCaretPosition(NbDocument.findLineOffset(styledDocument, lineNumber) + columnNumber);
                                } else {
                                    OpenCookie oc = (OpenCookie)dataObject.getCookie(OpenCookie.class);
                                    if (oc != null) {
                                        oc.open();
                                    } else {
                                        Toolkit.getDefaultToolkit().beep();
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
                    }
                }
            }
        }
    }
}
