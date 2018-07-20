package org.netbeans.modules.tasklist.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.jmi.javamodel.StringLiteral;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionAgent;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.util.TextPositionsMapper;
import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * Checks // NOI18N comments in one file
 *
 * @author tl
 */
public class TranslateFileChecker {
    /**
     * Found error
     */
    public static class Error {
        /** line number (0 based) */
        public int line;
        
        /** column number (0 based) */
        public int column;
        
        /** content of the string constant */
        public String constant;
        
        /**
         * Constructor
         *
         * @param line line number (0 based)
         * @param column column number (0 based)
         * @param constant value of the constant
         */
        private Error(int line, int column, String constant) {
            this.line = line;
            this.column = column;
            this.constant = constant;
        }
    }
    
    private FileObject fo;
    
    /* List<Error> */
    private List problems = new ArrayList();
    
    private TextPositionsMapper m;
    
    public TranslateFileChecker(FileObject fo) {
        this.fo = fo;
    }
    
    /**
     * Checks comment. This method cannot be called twice.
     *
     * @return found errors
     */
    public Error[] run() {
        String suffix = fo.getExt();
        if (!suffix.equalsIgnoreCase("java")) // NOI18N
            return new Error[0];
        
        // try to get an implementation of Element from the lookup
        // get repository
        MDRepository rep = JavaModel.getJavaRepository();
        // start a read transaction
        rep.beginTrans(false);
        try {
            // set the active classpath to the default project
            // classpath for a given fileobject
            JavaModel.setClassPath(fo);
            
            // get a resource for a given FileObject
            Resource res = JavaModel.getResource(fo);
            m = new TextPositionsMapper(res.getSourceText());
            
            findErrors(res);
        } finally {
            // end transaction in finally block to make
            // sure that the lock is released under any circumstances
            rep.endTrans();
        }
        
        return (Error[]) problems.toArray(new Error[problems.size()]);
    }

    /**
     * Searches for string constants and checks // NOI18N
     *
     * @param el MDR element
     */
    private void findErrors(Element el) {
        List ch = el.getChildren();
        for (int i = 0; i < ch.size(); i++) {
            Element e = (Element) ch.get(i);
            if (e instanceof StringLiteral) {
                int[] pos = new int[2];
                m.findPosition(e.getStartOffset(), pos);
                String line = m.getLine(pos[0]);
                
                // this could be written better as it although 
                // finds lines for example with NOI18N as Java identifier
                if (line.indexOf("NOI18N") == -1) {
                    problems.add(new Error(pos[0], pos[1], 
                        ((StringLiteral) e).getValue()));
                }
            } else {
                findErrors(e);
            }
        }
    }
}
