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
package org.netbeans.modules.latex.ui.actions;

import org.openide.util.actions.CookieAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.nodes.Node;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.DialogDisplayer;
import org.openide.DialogDescriptor;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.awt.HtmlBrowser;
import org.netbeans.editor.*;

import javax.swing.text.StyledDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import org.netbeans.modules.latex.editor.TexKit;
import org.netbeans.modules.latex.ui.ExportToLaTeX;
import org.netbeans.modules.latex.ui.LaTeXPrintContainer;

public class ExportLaTeXAction extends CookieAction {

    private static final String LATEX_EXT = ".tex";  //NOI18N
    private static final String FILE_PROTOCOL = "file://"; //NOI18N

    private Dialog dlg;

    public ExportLaTeXAction () {
    }

    protected final int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }



    protected final Class[] cookieClasses() {
        return new Class[] {EditorCookie.class, DataObject.class};
    }

    protected final void performAction(Node[] activatedNodes) {
        EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie (EditorCookie.class);
        StyledDocument doc = ec.getDocument();
        if (doc instanceof BaseDocument) {
            BaseDocument bdoc = (BaseDocument) doc;
            JTextComponent jtc = Utilities.getLastActiveComponent();
            ExportToLaTeX p = new ExportToLaTeX();
            p.setFileName (System.getProperty("user.home")+File.separatorChar+
                    ((DataObject)bdoc.getProperty (Document.StreamDescriptionProperty)).getPrimaryFile().getName()+LATEX_EXT);
            p.setShowLineNumbers(((Boolean)SettingsUtil.getValue (bdoc.getKitClass(),SettingsNames.LINE_NUMBER_VISIBLE,
                    Boolean.FALSE)).booleanValue());
            p.setPrintSelectionOnly((jtc != null && jtc.getSelectionStart()!=jtc.getSelectionEnd()));
            DialogDescriptor dd = new DialogDescriptor (p, NbBundle.getMessage(ExportLaTeXAction.class, "CTL_ExportHtml"));
            dlg = DialogDisplayer.getDefault().createDialog (dd);
            dlg.setVisible (true);
            dlg.dispose();
            dlg = null;
            if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                String file = p.getFileName();
                boolean lineNumbers = p.isShowLineNumbers();
                boolean selection = p.isPrintSelectionOnly();
                boolean toClipboard = p.isPrintToClipboard();
                int selectionStart;
                int selectionEnd;
                if (selection) {
                    selectionStart = jtc.getSelectionStart();
                    selectionEnd = jtc.getSelectionEnd();
                }
                else {
                    selectionStart = 0;
                    selectionEnd = bdoc.getLength();
                }
                try {
                    String text = export (bdoc, lineNumbers, selectionStart, selectionEnd, toClipboard);
                    
                    if (toClipboard) {
                        printToClipboard(text);
                    } else {
                        printToFile(file, text);
                    }
                } catch (IOException ioe) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message (
                            MessageFormat.format (NbBundle.getMessage(ExportLaTeXAction.class,"ERR_IOError"),
                                new Object[]{((DataObject)bdoc.getProperty(Document.StreamDescriptionProperty)).getPrimaryFile().getNameExt()
                                +LATEX_EXT,file}),    //NOI18N
                            NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify (nd);
                    return;
                }
            }
        }
    }

    public final String getName() {
        return NbBundle.getMessage (ExportLaTeXAction.class, "CTL_ExportLaTeXAction");
//        return NbBundle.getBundle("org/netbeans/modules/latex/ui/actions/Bundle").getString("CTL_ExportLaTeXAction");
    }

    public final HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected final boolean asynchronous() {
        return false;
    }

    private String export(final BaseDocument bdoc,  boolean lineNumbers, int selectionStart, int selectionEnd, boolean toClipboard) throws IOException {
        Coloring coloring =  SettingsUtil.getColoring(bdoc.getKitClass(), SettingsNames.DEFAULT_COLORING, false);
        Color bgColor = coloring.getBackColor();
        Color fgColor = coloring.getForeColor();
        Font font = coloring.getFont();
        FileObject fo = ((DataObject)bdoc.getProperty(Document.StreamDescriptionProperty)).getPrimaryFile();
        LaTeXPrintContainer latexPrintContainer = new LaTeXPrintContainer();
        latexPrintContainer.begin(toClipboard);//fo, font, fgColor, bgColor);
        bdoc.print(latexPrintContainer,false, lineNumbers, selectionStart, selectionEnd);
        return latexPrintContainer.end();
    }
    
    private void printToFile(String fileName, String text) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter (new FileWriter (fileName));
            out.print (text);
        } finally {
            if (out != null)
                out.close();
        }
    }
    
    private void printToClipboard(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JEditorPane pane = new JEditorPane();
                
                pane.setContentType(TexKit.TEX_MIME_TYPE);
                pane.setText(text);
                pane.setSelectionStart(0);
                pane.setSelectionEnd(text.length());
                pane.copy();
            }
        });
    }

}
