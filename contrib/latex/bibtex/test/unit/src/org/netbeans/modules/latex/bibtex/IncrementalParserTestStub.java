/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.Utilities;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Jan Lahoda
 */
public class IncrementalParserTestStub {
    
    /** Creates a new instance of IncrementalParserTest */
    public IncrementalParserTestStub() {
    }
    
    private void test(Document doc, Description description) throws BadLocationException, IOException {
        String content = description.content;
        
        doc.remove(0, doc.getLength());
        doc.insertString(0,  content, null);
        
        //validate?
        BiBTeXModel.getModel(Utilities.getDefault().getFile(doc)).getEntries();
        
        for (int cntr = 0; cntr < description.changes.length; cntr++) {
            description.changes[cntr].performChange(doc);
        }
    }
    
    
    public static class Description {
        private String content;
        private Change[] changes;
        
        public Description(String content, Change[] changes) {
            this.content = content;
            this.changes = changes;
        }
    }
    
    public static abstract class Validator {
        
        public abstract void validate(Document doc) throws BadLocationException, IOException ;
        
    }
    
    public static class ProxyValidator extends Validator {
        
        private List/*<Validator>*/ validators;
        
        public ProxyValidator(List/*<Validator>*/ validators) {
            this.validators = validators;
        }
        
        public ProxyValidator(Validator[] validators) {
            this(Arrays.asList(validators));
        }
        
        public void validate(Document doc) throws BadLocationException, IOException {
            for (Iterator i = validators.iterator(); i.hasNext(); ) {
                ((Validator) i.next()).validate(doc);
            }
        }
        
    }
    
    public static class TagValidator extends Validator {
        private String[] tags;
        
        public TagValidator(String[] tags) {
            this.tags = tags;
        }
        
        public void validate(Document doc) throws BadLocationException, IOException {
            BiBTeXModel model = BiBTeXModel.getModel(Utilities.getDefault().getFile(doc));
            List/*<Entry>*/ entries = model.getEntries();
            int index = 0;
            
            for (Iterator i = entries.iterator(); i.hasNext(); ) {
                Entry e = (Entry) i.next();
                
                if (e instanceof PublicationEntry) {
                    PublicationEntry pEntry = (PublicationEntry) e;
                    
                    if (!tags[index].equals(pEntry.getTag())) {
                        IllegalStateException exc = new IllegalStateException("Awaited tag: " + tags[index] + ", but found: " + pEntry.getTag() + ".");
                        
                        throw exc;
                    }
                    
                    index++;
                }
            }
            
            if (index < tags.length) {
                IllegalStateException exc = new IllegalStateException("Awaited " + tags.length + " tags, but found only " + index + ".");
                
                throw exc;
            }
        }
        
    }
    
    public static class DefaultValidator extends Validator {
        
        public void validate(Document doc) throws BadLocationException, IOException {
            BiBTeXModel model = BiBTeXModel.getModel(Utilities.getDefault().getFile(doc));
            List incremental  = model.getEntries();
            List batch        = new ArrayList(model.doParse());
            
            if (!incremental.equals(batch)) {
                String annotation = "";
                int    isize      = incremental.size();
                int    bsize      = batch.size();
                
                if (isize != bsize)
                    annotation = "The sizes of entries arrays are not the same. ";
                
                int len = isize < bsize ? isize : bsize;
                
                for (int cntr = 0; cntr < len; cntr++) {
                    Entry e1 = (Entry) incremental.get(cntr);
                    Entry e2 = (Entry) batch.get(cntr);
                    
                    if (!e1.equals(e2)) {
                        annotation += "The following entries are not equal: " +
                             e1.toString() + ", " + e2.toString() + ". ";
                    }
                }
                IllegalStateException exc = new IllegalStateException(annotation + "The incremental and batch parser do not provide the same result!\n" + "incremental=" + incremental + ",\nbatch=" + batch + "\n");
                
                throw exc;
            }
            
//            System.err.println("incremental=" + incremental);
//            System.err.println("batch = " + batch );
//            try {
//                String s = doc.getText(0, doc.getLength());
//                System.err.println("s=" + s);
//            } catch (BadLocationException e) {
//                e.printStackTrace();
//            }
        }
        
    }
    
    public static final Validator DEFAULT_VALIDATOR = new DefaultValidator();
    
    public static abstract class Change {
        
        private Validator validator;
        
        protected Change(Validator validator) {
            this.validator = validator;
        }
        
        protected Validator getValidator() {
            return validator;
        }
        
        protected int find(Document doc, String toFind) throws BadLocationException {
            return doc.getText(0, doc.getLength()).indexOf(toFind);
        }
        
        public abstract void performChange(Document doc) throws BadLocationException, IOException;

    }

    public static class AddChange extends Change {
        private String  find;
        private String  toAdd;
        
        public AddChange(Validator validator, String find, String toAdd) {
            super(validator);
            this.find = find;
            this.toAdd = toAdd;
        }

        public void performChange(Document doc) throws BadLocationException, IOException {
            int offset = find(doc, find);
            int length = toAdd.length();
            
            for (int cntr = 0; cntr < length; cntr++) {
                doc.insertString(offset + cntr, String.valueOf(toAdd.charAt(cntr)), null);
                
                getValidator().validate(doc);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class PasteChange extends Change {
        private String  find;
        private String  toPaste;
        
        public PasteChange(Validator validator, String find, String toPaste) {
            super(validator);
            this.find = find;
            this.toPaste = toPaste;
        }

        public void performChange(Document doc) throws BadLocationException, IOException {
            int offset = find(doc, find);
            doc.insertString(offset, toPaste, null);
            getValidator().validate(doc);
        }
    }

    public static class RemoveChange extends Change {
        private String  find;
        private int     removeLen;
        
        public RemoveChange(Validator validator, String find, int removeLen) {
            super(validator);
            this.find = find;
            this.removeLen = removeLen;
        }

        public void performChange(Document doc) throws BadLocationException, IOException {
            int offset = find(doc, find);
            
            for (int cntr = 0; cntr < removeLen; cntr++) {
                doc.remove(offset, 1);
                
                getValidator().validate(doc);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static class DeleteChange extends Change {
        private String  find;
        private int     deleteLen;
        
        public DeleteChange(Validator validator, String find, int deleteLen) {
            super(validator);
            this.find = find;
            this.deleteLen = deleteLen;
        }

        public void performChange(Document doc) throws BadLocationException, IOException {
            int offset = find(doc, find);
            
            doc.remove(offset, deleteLen);
            
            getValidator().validate(doc);
        }
    }
    
    public static class AddEntryChange extends Change {
        
        private Entry e;
        
        public AddEntryChange(Validator validator, Entry e) {
            super(validator);
            this.e = e;
        }
        
        public void performChange(Document doc) throws BadLocationException, IOException {
            BiBTeXModel model = BiBTeXModel.getModel(Utilities.getDefault().getFile(doc));
            
            model.addEntry(e);
            
            getValidator().validate(doc);
        }
        
    }

    public static void performTest(URL testFile, Description description) throws Exception {
        Document doc = Utilities.getDefault().openDocument(testFile);
        
        new IncrementalParserTestStub().test(doc, description);
    }
    
//    public static final void main(String[] args) throws Exception {
//        File testFile = new File("/home/lahvac/netbeans/sampledir/bibdatabase.bib");
//
//        for (int cntr = 0; cntr < descriptions.length; cntr++) {
//            performTest(testFile, descriptions[cntr]);
//        }
//    }
//    
}
