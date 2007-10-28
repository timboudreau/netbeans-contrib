/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
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
