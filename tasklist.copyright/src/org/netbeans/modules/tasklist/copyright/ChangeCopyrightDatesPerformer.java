package org.netbeans.modules.tasklist.copyright;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.core.ConfPanel;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.openide.ErrorManager;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * todo
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class ChangeCopyrightDatesPerformer implements SuggestionPerformer {
    private SuggestionContext env;
    private int fRangeEnd, fBegin, fListEnd, fDateEnd, lineno;
    private Document doc;
    private String year;
    
    /** Creates a new instance of ChangeCopyrightDatesPerformer todo */
    public ChangeCopyrightDatesPerformer(SuggestionContext env,
        int fRangeEnd, int fBegin, int fListEnd, int fDateEnd, Document doc,
        int lineno, String year) {
        this.env = env;
        this.fRangeEnd = fRangeEnd;
        this.fBegin = fBegin;
        this.fListEnd = fListEnd;
        this.fDateEnd = fDateEnd;
        this.doc = doc;
        this.lineno = lineno;
        this.year = year;
    }
    
    public void perform(Suggestion s) {
        // Replace the end of the range
        substitute(doc, fRangeEnd, fBegin, fListEnd,
            fDateEnd, year);
    }

    public boolean hasConfirmation() {
        return true;
    }

    public Object getConfirmation(Suggestion s) {
        String text = getLineContents(doc, lineno - 1);
        Document newdoc = new PlainDocument();
        String preview = null;
        try {
            newdoc.insertString(0, text, null);
            // XXX Gotta subtract from the indices!
            int rangeEnd = fRangeEnd;
            int listEnd = fListEnd;
            int dateEnd = fDateEnd;
                   /*
                    int rangeEnd = (fRangeEnd != -1) ?
                        fRangeEnd-fBegin : -1;
                    int listEnd = (fListEnd != -1) ?
                        fListEnd-fBegin : -1;
                    int dateEnd = (fDateEnd != -1) ?
                        fDateEnd-fBegin : -1;
                     */
             substitute(newdoc, rangeEnd, 0, listEnd, dateEnd,
                year);
             preview = newdoc.getText(0, newdoc.getLength());
         } catch (BadLocationException ex) {
             ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
             return null;
         }

         //return NbBundle.getMessage(CopyrightChecker.class,
         // "CopyrightConfirmation", oldRange, newRange, text);
         String filename = env.getFileObject().getNameExt();
         String beforeDesc = NbBundle.getMessage(
            ChangeCopyrightDatesPerformer.class,
            "CopyrightConfirmation"); // NOI18N
         //String beforeContents = "<html><b>" + text.trim() + "</b></html>";
         String afterDesc = NbBundle.getMessage(
            ChangeCopyrightDatesPerformer.class,
         "CopyrightConfirmationAfter"); // NOI18N
         //String afterContents = "<html><b>" + preview.trim() + "</b></html>";


         int fd = TLUtils.firstDiff(text, preview);
         int ld = TLUtils.lastDiff(text, preview);

         Line l = s.getLine();
         StringBuffer sb = new StringBuffer(200);
         sb.append("<html>"); // NOI18N
         // HACK: When the text begins with
         // "// Hello" it does NOT get rendered by
         // Swing! (On this Apple JDK that I'm developing it
         // on anyway). So hack around it by putting some
         // useless attributes in there.
         sb.append("<b></b>");
         // XXX Make sure it can begin with // copyright 2000 !
         TLUtils.appendSurroundingLine(sb, l, -1);
         //sb.append("<b>");
         TLUtils.appendAttributed(sb, text, fd,
         text.length() - ld,
         true, true);
         //sb.append("</b>");
         TLUtils.appendSurroundingLine(sb, l, +1);
         sb.append("</html>"); // NOI18N
         String beforeContents = sb.toString();


         sb.setLength(0);
         sb.append("<html>");
         // HACK: I also noticed that "/*\n* Copyright"
         // wouldn't correctly draw the first line, so
         // hack around it by putting some useless
         // attributes in there.
         sb.append("<b></b>");

         TLUtils.appendSurroundingLine(sb, l, -1);
         //sb.append("<b>");
         TLUtils.appendAttributed(sb, preview, fd,
         preview.length() - ld,
         true, true);
         //sb.append("</b>");

         TLUtils.appendSurroundingLine(sb, l, +1);
         sb.append("</html>"); // NOI18N
         String afterContents = sb.toString();

         return new ConfPanel(beforeDesc,
            beforeContents, afterDesc,
            afterContents,
            filename, lineno, null);
     }

    private String getLineContents(Document doc, int linenumber) {
        Element elm = getElement(doc, linenumber);
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return null;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();
        
        try {
            String text = doc.getText(offset, endOffset - offset);
            return text;
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        return null;
    }

    /**
     * @param begin Position within the document to start
     */
    private static void substitute(Document doc, int rangeEnd, int begin, int listEnd,
    int dateEnd, String year) {
        if (rangeEnd != -1) {
            // Replace the end of the range
            final int pos = rangeEnd + begin;
            try {
                doc.remove(pos, 4);
                doc.insertString(pos, year, null);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        } else if (listEnd != -1) {
            // Add one more item to the list
            final int pos = listEnd + 4 + begin;
            try {
                doc.insertString(pos, ", " + year, null);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        } else { // assert dateEnd != -1
            final int pos = dateEnd + 4 + begin;
            // Create a date range
            try {
                doc.insertString(pos, "-" + year, null);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        }
    }
    
    private static Element getElement(Document d, int linenumber) {
        if (d == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "d was null");
            return null;
        }
        
        if (!(d instanceof StyledDocument)) {
            ErrorManager.getDefault().log(ErrorManager.USER, "Not a styleddocument");
            return null;
        }
        
        StyledDocument doc = (StyledDocument) d;
        Element e = doc.getParagraphElement(0).getParentElement();
        if (e == null) {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement();
        }
        Element elm = e.getElement(linenumber);
        return elm;
    }
};
