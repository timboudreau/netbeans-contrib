/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.html;

import java.awt.Component;
import java.awt.Dialog;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import org.openide.text.Line;
import org.openide.loaders.DataObject;
import java.io.*;
import org.openide.ErrorManager;

import javax.swing.text.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.CallableSystemAction;

import org.netbeans.modules.html.*;
import org.netbeans.api.diff.*;

import org.netbeans.modules.tasklist.core.*;
import org.netbeans.api.tasklist.*;

import org.w3c.tidy.*;

/**
 * Rewrite the document
 * <p>
 *
 * @author Tor Norbye
 */

public class RewriteAction extends NodeAction 
     implements Report.ErrorReporter  {

    public void reportError(int line, int col, boolean error, String message) {
        //System.err.println("reportError(" + line + ", " + col + ", " + error + ", " + message + ")");
    }

    protected boolean enable(Node[] node) {
        if ((node == null) || (node.length != 1)) {
            return false;
        }
        Suggestion item = (Suggestion)TaskNode.getTask(node[0]);
        if (item == null) {
            return false;
        }
        Line l = item.getLine();
        if (l == null) {
            return false;
        }        
        return true;
    }

    private TidyRunner tidy = null;

    protected void performAction(Node[] node) {
        // Figure out which data object the node is associated
        // with.
               // XXX Later I could store this in the Suggestion
               // rather than relying on the Line object (since
               // for example category nodes don't have Line objects)
               // (e.g. the suggestion manager would associate the
               // data object with the node)
        SuggestionManager manager =  SuggestionManager.getDefault();
        Suggestion item = (Suggestion)TaskNode.getTask(node[0]);
        Line l = item.getLine();
        Document doc = TLUtils.getDocument(l);
        if (doc == null) {
            return; // XXX signal error?
        }

        DataObject dobj = l.getDataObject();
        boolean isHTML = TidySuggester.isHTML(dobj);
        boolean isJSP = false;
        boolean isXML = false;
        if (!isHTML) {
            isJSP = TidySuggester.isJSP(dobj);
            if (!isJSP) {
                isXML = TidySuggester.isXML(dobj);
            }
        }
        if (!(isHTML || isJSP || isXML)) {
            return;
        }

        // Set configuration settings
        if (tidy == null) {
            tidy = new TidyRunner();
        }
        Configuration config = tidy.getConfiguration();
        config.XmlTags = isXML;

        RewritePanel panel = new RewritePanel(this, doc, dobj);
        panel.setXHTML(config.xHTML);
        panel.setWrapCol(config.wraplen);
        panel.setOmit(config.HideEndTags);
        panel.setUpper(config.UpperCaseTags);
        panel.setIndent(config.IndentContent);
        panel.setReplace(config.MakeClean);
        panel.setXML(config.XmlTags);
        DialogDescriptor d = new DialogDescriptor(panel,
                    NbBundle.getMessage(RewriteAction.class,
                    "TITLE_rewrite")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();
        if (d.getValue() != NotifyDescriptor.OK_OPTION) {
            return;
        }

        config.xHTML = panel.getXHTML();
        config.MakeClean = panel.getReplace();
        config.IndentContent = panel.getIndent();
        config.SmartIndent = panel.getIndent();
        config.UpperCaseTags = panel.getUpper();
        config.HideEndTags = panel.getOmit();
        config.wraplen = panel.getWrapCol();

        String rewritten = rewrite(doc);

        try {
            // TODO Instead of a two-step process, use replaceSelection
            // to perform a single step replacement
            doc.remove(0, doc.getLength());
            doc.insertString(0, rewritten, null);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
        Suggestion s = (Suggestion)node[0].getCookie(Suggestion.class);
        if (s != null) {
            SuggestionProvider provider = s.getProvider();
            if ((provider != null) && (provider instanceof TidySuggester)) {
                ((TidySuggester)provider).rescan();
            }
        }
    }
    
    private String rewrite(Document doc) {
        InputStream input = null;
        try {
            String text = doc.getText(0, doc.getLength());
            input = new StringBufferInputStream(text);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().
                notify(ErrorManager.WARNING, e);
            return "";
        }
        StringBuffer sb = new StringBuffer(doc.getLength()+500);
        OutputStream output = new StringBufferOutputStream(sb);
            
        try {
            tidy.parse(input, output, this, true);
        } catch (FileNotFoundException fnfe) {
            ErrorManager.getDefault().notify(fnfe);
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
        }
        return sb.toString();
    }

    void preview(RewritePanel panel, Document doc, DataObject dobj) {
        Configuration config = tidy.getConfiguration();
        config.xHTML = panel.getXHTML();
        config.MakeClean = panel.getReplace();
        config.IndentContent = panel.getIndent();
        config.SmartIndent = panel.getIndent();
        config.UpperCaseTags = panel.getUpper();
        config.HideEndTags = panel.getOmit();
        config.wraplen = panel.getWrapCol();

        String before;
        try {
            before = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            ErrorManager.getDefault().
                notify(ErrorManager.WARNING, e);
            return;
        }
        String rewritten = rewrite(doc);
        String mime = dobj.getPrimaryFile().getMIMEType();
        diff(before, rewritten, mime);

    }

    void diff(String before, String after, String mime) {
        Diff diff = Diff.getDefault();
        if (diff == null) {
            // TODO Check for this condition and hide the Diff button
            // if this is the case
            return ;
        }

        String beforeDesc = NbBundle.getMessage(RewriteAction.class,
                            "DiffBefore"); // NOI18N
        String afterDesc = NbBundle.getMessage(RewriteAction.class,
                            "DiffAfter"); // NOI18N
        String beforeTitle = beforeDesc; 
        String afterTitle = afterDesc; 

        Component tp = null;
        try {
            tp = diff.createDiff(beforeDesc, beforeTitle,
                                 new StringReader(before),
                                 afterDesc, afterTitle,
                                 new StringReader(after),
                                 mime);
        } catch (IOException ioex) {
            ErrorManager.getDefault().notify(ioex);
            return ;
        }
        if (tp == null) {
            return;
        }

        DialogDescriptor d = new DialogDescriptor(tp,
                    NbBundle.getMessage(RewriteAction.class,
                    "TITLE_diff")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.DEFAULT_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();
    }


    public String getName() {
        return NbBundle.getMessage(RewriteAction.class,
                                   "Rewrite"); // NOI18N
    }

    /*
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/html/rewrite.gif"; // NOI18N
    }
    */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }

    // Grr... tidy uses input/output stream instead of input/output writer
    private class StringBufferOutputStream extends OutputStream {
        private StringBuffer sb;
        StringBufferOutputStream(StringBuffer sb) {
            this.sb = sb;
        }

        public void write(int b) {
            sb.append((char)b);
        }
    }

    
}
