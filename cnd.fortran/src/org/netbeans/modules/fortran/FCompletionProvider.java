/*
 * FCompletionProvider.java
 *
 * Created on July 15, 2007, 11:40 PM
 *
 */

package org.netbeans.modules.fortran;
 
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.cnd.completion.cplusplus.ext.CsmSyntaxSupport;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;

/**
 * The class to perform code completion.
 * Completion works only for keywords.
 * @author Andrey Gubichev
 */
public class FCompletionProvider implements CompletionProvider{




    /**
     * perform code completion
     */
public CompletionTask createTask(int type, final JTextComponent jTextComponent) {
     return new AsyncCompletionTask(new AsyncCompletionQuery() {

        protected void query(final CompletionResultSet completionResultSet, Document document, final int caretOffset) {

            final StyledDocument bDoc = (StyledDocument)document;

            int startOffset = -1;

            class Operation extends Thread {
                boolean showCompletion = false;
                String filter = null;
                int startOffset = caretOffset-1;

                public void run() {

                    try {
                        final int lineStartOffset = getRowFirstNonWhite(bDoc, caretOffset);
                        if(lineStartOffset > -1 && caretOffset> lineStartOffset){
                            final char[] line = bDoc.getText(lineStartOffset, caretOffset-lineStartOffset).toCharArray();
                            final int whiteOffset = indexOfWhite(line);
                            filter = new String(line, whiteOffset+1, line.length-whiteOffset-1);
                            if(whiteOffset > 0){
                                startOffset = lineStartOffset + whiteOffset + 1;
                            } else{
                                startOffset = lineStartOffset;
                            }
                        } else {
                            showCompletion = true;
                        }
                    } catch (BadLocationException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }

                }

                public void finishWork() {

                    if (startOffset > -1 && caretOffset > startOffset) {
                              String record = new String();
                              HashSet<String> result = FCompletionSupport.getCompletionResult(jTextComponent.getText());
                              
                            if (filter != null) {
                                for (Iterator i=result.iterator(); i.hasNext(); ){
                                    record = (String)i.next();
                                    if(record.startsWith(filter)){
                                        completionResultSet.addItem(new FCompletionItem(record, startOffset, caretOffset));
                                    }
                                }
                            } else {                          
                                  for (Iterator i=result.iterator(); i.hasNext(); ){
                                    record = (String)i.next();
                                    completionResultSet.addItem(new FCompletionItem(record, startOffset, caretOffset));
                                }
                            }
                    }                 
                    completionResultSet.setAnchorOffset(startOffset);
                    completionResultSet.finish();
                }
            }

            Operation oper = new Operation();
            try {
                NbDocument.runAtomicAsUser(bDoc, oper);
                oper.finishWork();
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }

    },jTextComponent);
}
 
static int getRowFirstNonWhite(StyledDocument doc, int offset)
throws BadLocationException {
    Element lineElement = doc.getParagraphElement(offset);
    int start = lineElement.getStartOffset();
    while (start + 1 < lineElement.getEndOffset()) {
        try {
            if (doc.getText(start, 1).charAt(0) != ' ') {
                break;
            }
        } catch (BadLocationException ex) {
            throw (BadLocationException)new BadLocationException(
                    "calling getText(" + start + ", " + (start + 1) +
                    ") on doc of length: " + doc.getLength(), start
                    ).initCause(ex);
        }
        start++;
    }
    return start;
}
static int indexOfWhite(char[] line){
    int i = line.length;
    while(--i > -1){
        final char c = line[i];
        if(Character.isWhitespace(c)){
            return i;
        }
    }
    return -1;
}


    /**
     * indicates whether the code completion box appears automatically or not
     */
public int getAutoQueryTypes(JTextComponent component, String string) {
       SyntaxSupport s = Utilities.getSyntaxSupport(component).get(FSyntaxSupport.class);
       if (s != null)
           System.out.println("CCC");
        return 0;
    }
}
