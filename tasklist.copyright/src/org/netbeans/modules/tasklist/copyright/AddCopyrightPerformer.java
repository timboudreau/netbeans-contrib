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
                env.getDocument().insertString(0, comment, null);
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
    
    private String getComment(boolean makeHtml) {
        String copyright = getCopyright();
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
        return "YOUR LICENSE HERE. For example, here's the Sun Public License\n" +
            "used by the tasklist modules:\n\n" +
            "                 Sun Public License Notice\n\n" +
            "The contents of this file are subject to the Sun Public License\n" +
            "Version 1.0 (the \"License\"). You may not use this file except in\n" +
            "compliance with the License. A copy of the License is available at\n" +
            "http://www.sun.com/\n\n" +
            "The Original Code is NetBeans. The Initial Developer of the Original\n" +
            "Code is Sun Microsystems, Inc. Portions Copyright 1997-" +
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