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

package org.netbeans.modules.tasklist.html;

import java.awt.Component;
import java.awt.Dialog;
import org.openide.text.Line;
import org.openide.loaders.DataObject;
import java.io.*;
import org.openide.ErrorManager;

import javax.swing.text.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import org.netbeans.modules.html.*;
import org.netbeans.api.diff.*;

import org.netbeans.modules.tasklist.core.*;
import org.netbeans.api.tasklist.*;

import org.w3c.tidy.*;

/**
 * Rewrite the document
 * <p>
 * @todo Use a single button OK panel, not an OK/Cancel
 *    dialog for the preview dialog
 * @todo Allow the action to operate on unopened files.
 *    Probably as simple as enablin the action even when
 *    getDocument() returns null, and in performAction
 *    call openDocument() instead of getDocument().
 *
 * @author Tor Norbye
 */

public class RewriteAction extends NodeAction 
     implements ErrorReporter  {

    public void reportError(int line, int col, boolean error, String message) {
        //System.err.println("reportError(" + line + ", " + col + ", " + error + ", " + message + ")");
    }

    protected boolean enable(Node[] node) {
        if ((node == null) || (node.length != 1)) {
            return false;
        }

        DataObject dobj = (DataObject)node[0].getCookie(DataObject.class);
        if (dobj == null) {
            return false;
        }
        Document doc = TLUtils.getDocument(dobj);
        if (doc == null) { // Not open
            return false;
        }
        if (TidySuggester.isHTML(dobj)) {
            return true;
        }
        if (TidySuggester.isJSP(dobj)) {
            return true;
        }
        if (TidySuggester.isXML(dobj)) {
            return true;
        }
        return false;
    }

    private Tidy tidy = null;

    protected void performAction(Node[] node) {
        // Figure out which data object the node is associated
        // with.
               // XXX Later I could store this in the Suggestion
               // rather than relying on the Line object (since
               // for example category nodes don't have Line objects)
               // (e.g. the suggestion manager would associate the
               // data object with the node)
        Suggestion item = (Suggestion)TaskNode.getTask(node[0]);
        DataObject dobj;
        if (item != null) {
            Line l = item.getLine();
            dobj = l.getDataObject();
        } else {
            dobj = (DataObject)node[0].getCookie(DataObject.class);
            if (dobj == null) {
                return;
            }
        }
        Document doc = TLUtils.getDocument(dobj);
        if (doc == null) {
            return; // XXX signal error?
        }

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
            tidy = new Tidy();
        }
        tidy.setOnlyErrors(false);
        tidy.setShowWarnings(false);
        tidy.setQuiet(true);
        
        tidy.setXmlTags(isXML);

        RewritePanel panel = new RewritePanel(this, doc, dobj);
        panel.setXHTML(tidy.getXHTML());

        panel.setWrapCol(tidy.getWraplen());
        panel.setOmit(tidy.getHideEndTags());
        panel.setUpper(tidy.getUpperCaseTags());
        panel.setIndent(tidy.getIndentContent());
        panel.setReplace(tidy.getMakeClean());
        panel.setXML(tidy.getXmlTags());

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

        tidy.setXHTML(panel.getXHTML());
        tidy.setMakeClean(panel.getReplace());
        tidy.setIndentContent(panel.getIndent());
        tidy.setSmartIndent(panel.getIndent());
        tidy.setUpperCaseTags(panel.getUpper());
        tidy.setHideEndTags(panel.getOmit());
        tidy.setWraplen(panel.getWrapCol());

        String rewritten = rewrite(doc);

        try {
            // JDK14
            /* Grrr ... turns out replace() is only available as of JDK 1.4...
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument)doc).replace(0, doc.getLength(), rewritten,
                                                null);
            }
            else {
            */
                doc.remove(0, doc.getLength());
                doc.insertString(0, rewritten, null);
            //}
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
        tidy.parse(input, output);
        return sb.toString();
    }

    void preview(RewritePanel panel, Document doc, DataObject dobj) {
        tidy.setXHTML(panel.getXHTML());
        tidy.setMakeClean(panel.getReplace());
        tidy.setIndentContent(panel.getIndent());
        tidy.setSmartIndent(panel.getIndent());
        tidy.setUpperCaseTags(panel.getUpper());
        tidy.setHideEndTags(panel.getOmit());
        tidy.setWraplen(panel.getWrapCol());

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

        //NotifyDescriptor d =
        // new NotifyDescriptor.Message("Hello...", NotifyDescriptor.INFORMATION_MESSAGE);
        // TopManager.getDefault().notify(d);

        
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

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/html/rewrite.gif"; // NOI18N
    }
    
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
