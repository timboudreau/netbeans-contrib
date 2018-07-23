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
package org.netbeans.modules.tasklist.copyright;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.core.ConfPanel;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * SuggestionPerformer for adding copyright
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class AddCopyrightPerformer implements SuggestionPerformer {
    private SuggestionContext env;

    /** For some file formats comment must be inseretd into middle of file. */
    private int prologEnd = 0;

    /**
     * todo
     */
    public AddCopyrightPerformer(SuggestionContext env) {
        this.env = env;
    }
    
    public void perform(Suggestion s) {
        String comment = getComment(false);
        if ((comment != null) && (comment.length() > 0)) {
            try {
                env.getDocument().insertString(prologEnd, comment, null);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(
                ErrorManager.WARNING, e);
            }
            // TODO Should I put a message in the status
            // window which tells you how to change the
            // copyright that is used?
        } else {
            JTextArea labelArea = new JTextArea();
            labelArea.setWrapStyleWord(true);
            labelArea.setLineWrap(true);
            labelArea.setEditable(false);
            labelArea.setText(
            NbBundle.getMessage(CopyrightChecker.class,
                "NoChosenCopyright")); // NOI18N
            labelArea.setBackground((Color) UIManager.getDefaults().get(
                "Label.background")); // NOI18N
            JTextArea textArea = new JTextArea();
            textArea.setRows(8);
            String sample = getSampleLicense();
            textArea.setText(sample);
            textArea.select(0, sample.length());
            JScrollPane pane = new JScrollPane(textArea);
            // TODO: add text area to panel!
            
            JPanel body = new JPanel();
            body.setLayout(new BorderLayout());
            body.add(labelArea, BorderLayout.NORTH);
            body.add(pane, BorderLayout.CENTER);
            body.setPreferredSize(new Dimension(400, 300));
            NotifyDescriptor nd =
            new NotifyDescriptor.Confirmation(
                body,
                NotifyDescriptor.OK_CANCEL_OPTION
            );
            Object result =
            DialogDisplayer.getDefault().notify(nd);
            if (NotifyDescriptor.OK_OPTION == result) {
                String copyright = textArea.getText().trim();
                if (copyright.length() > 0) {
                    CopyrightSettings settings =
                    (CopyrightSettings) CopyrightSettings.findObject(
                    CopyrightSettings.class, true);
                    settings.setScanCopyright(copyright);
                    // recurse!
                    perform(s);
                }
            }
        }
    }
    
    private String getCopyright() {
        CopyrightSettings settings =
            (CopyrightSettings) CopyrightSettings.findObject(CopyrightSettings.class, true);
        String copyright = settings.getScanCopyright();
        return copyright;
    }

    /** Side effect sets prologEnd */
    private String getComment(boolean makeHtml) {
        String copyright = getCopyright();
        prologEnd = 0;
        if ((copyright == null) || (copyright.length() == 0)) {
            return null;
        }
        String prefix = "";
        String suffix = "";
        String linefix = null;
        String ext = env.getFileObject().getExt();
        if (ext.equalsIgnoreCase("java") || // NOI18N
        ext.equalsIgnoreCase("cc") || // NOI18N
        ext.equalsIgnoreCase("cpp")) {  // NOI18N
            linefix = "//"; // NOI18N
            prefix = "/*"; // NOI18N
            suffix = "*/"; // NOI18N
        } else if (ext.equalsIgnoreCase("html") || // NOI18N
            ext.equalsIgnoreCase("htm") || // NOI18N
            ext.equalsIgnoreCase("xml")) {  // NOI18N
            prefix = "<!--"; // NOI18N
            suffix = "-->"; // NOI18N

            // #45151 for XML <?xml version=".." encoding="..."?> prolog must be the first
            // XXX works well only for ASCI based encodings, EBDIC ignored
            Document doc = env.getDocument();
            int prologLength = Math.max(doc.getLength(), 80);
            try {
                String prolog = doc.getText(0, prologLength);
                if (prolog.startsWith("<?xml")) {
                    int end = prolog.indexOf("?>");
                    if (end != -1) {
                        prologEnd =  end + 2;
                        prefix = "\n<!--";
                    }
                }
            } catch (BadLocationException e) {
                assert false;
            }
        } else if (ext.equalsIgnoreCase("jsp")) {  // NOI18N
            prefix = "<%--"; // NOI18N
            suffix = "--%>"; // NOI18N
        } else if (ext.equalsIgnoreCase("c")) { // NOI18N
            prefix = "/*"; // NOI18N
            suffix = "*/"; // NOI18N
        } else if (ext.equalsIgnoreCase("properties") || // NOI18N
        ext.equalsIgnoreCase("sh")) { // NOI18N
            linefix = "#"; // NOI18N
        }
        int n = copyright.length();
        if (linefix != null) {
            // Insert a comment string at the beginning of
            // every line
            StringBuffer sb = new StringBuffer(2 * n);
            if (makeHtml) {
                sb.append("<html><body>"); // NOI18N
            }
            boolean commentOut = true;
            if (prefix != "") {
                commentOut = !startsWithComment(copyright, 0, n,
                prefix);
            }
            boolean newline = true;
            for (int i = 0; i < n; i++) {
                if (newline && commentOut &&
                !startsWithComment(copyright, i, n, linefix)) {
                    if (makeHtml) {
                        if (i != 0) {
                            sb.append("</i>"); // NOI18N
                        }
                        sb.append("<b>"); // NOI18N
                        TLUtils.appendHTMLString(sb, linefix);
                        sb.append("</b>&nbsp;<i>"); // NOI18N
                    } else {
                        sb.append(linefix);
                        sb.append(' ');
                    }
                }
                newline = false;
                char c = copyright.charAt(i);
                if (c == '\n') {
                    newline = true;
                }
                if (makeHtml) {
                    TLUtils.appendHTMLChar(sb, c);
                } else {
                    sb.append(c);
                }
            }
            if (makeHtml) {
                sb.append("</i><br></body></html>"); // NOI18N
            } else {
                sb.append('\n');
            }
            return sb.toString();
        } else {
            // TODO - check to see if license already contains
            // a comment prefix.
            StringBuffer sb =
            new StringBuffer(n + 20);
            if (makeHtml) {
                sb.append("<html><body>"); // NOI18N
                // HACK: When the text begins with
                // "/* Hello" it does NOT get rendered
                // by Swing! (On this Apple JDK that
                // I'm developing it on anyway). So
                // hack around it by putting some
                // useless attributes in there.
                sb.append("<b></b><i>"); // NOI18N
            }
            boolean commentOut = true;
            if (startsWithComment(copyright, 0, n, prefix) ||
            ((linefix != null) &&
            (startsWithComment(copyright, 0, n, linefix)))) {
                commentOut = false;
            }
            if (commentOut) {
                if (makeHtml) {
                    sb.append("<b>");
                    TLUtils.appendHTMLString(sb, prefix);
                    sb.append("</b>");
                } else {
                    sb.append(prefix);
                }
                sb.append('\n');
            }
            if (makeHtml) {
                TLUtils.appendHTMLString(sb, copyright);
            } else {
                sb.append(copyright);
            }
            if (commentOut) {
                sb.append('\n');
                if (makeHtml) {
                    sb.append("<b>");
                    TLUtils.appendHTMLString(sb, suffix);
                    sb.append("</b>");
                } else {
                    sb.append(suffix);
                }
            }
            if (makeHtml) {
                sb.append("</i><br></body></html>"); // NOI18N
            } else {
                sb.append('\n');
            }
            return sb.toString();
        }
    }
    
    public boolean hasConfirmation() {
        String copyright = getCopyright();
        return ((copyright != null) && (copyright.length() > 0));
    }
    
    public Object getConfirmation(Suggestion s) {
        String comment = getComment(true);
        String filename = env.getFileObject().getNameExt();
        return new ConfPanel(
            NbBundle.getMessage(CopyrightChecker.class,
            "InsertCopyright"), // NOI18N
            comment,
            NbBundle.getMessage(CopyrightChecker.class,
            "ChangeCopyright"), // NOI18N
            null,
            filename, 1, null);
    }

    private String getSampleLicense() {
        return "YOUR LICENSE HERE. For example, here's the CDDL\n" +
          "\n" +
          "The contents of this file are subject to the terms of the Common Development\n" +
          "and Distribution License (the License). You may not use this file except in\n" +
          "compliance with the License.\n" +
          "\n" +
          "You can obtain a copy of the License at http://www.netbeans.org/cddl.html\n" +
          "or http://www.netbeans.org/cddl.txt.\n" +
          "\n" +
          "When distributing Covered Code, include this CDDL Header Notice in each file\n" +
          "and include the License file at http://www.netbeans.org/cddl.txt.\n" +
          "If applicable, add the following below the CDDL Header, with the fields\n" +
          "enclosed by brackets [] replaced by your own identifying information:\n" +
          "\"Portions Copyrighted [year] [name of copyright owner]\"\n" +
          "\n" +
          "The Original Software is NetBeans. The Initial Developer of the Original\n" +
          "Software is Sun Microsystems, Inc. Portions Copyright 1997-" +
            new SimpleDateFormat("yyyy").format(new Date()) +
            " Sun\n" +
            "Microsystems, Inc. All Rights Reserved.\n\n\n";
    }    
    /** Returns true if the given line (starting at str+index) begins
     * with the given comment prefix (after an optional range of whitespace) */
    private static boolean startsWithComment(String str, int index, int len,
    String commentPrefix) {
        while ((index < len) &&
        (Character.isSpaceChar(str.charAt(index)))) {
            index++;
        }
        return str.startsWith(commentPrefix, index);
    }
};
