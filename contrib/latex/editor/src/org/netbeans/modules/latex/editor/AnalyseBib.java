/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Document;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.lexer.editorbridge.TokenRootElement;

/**
 *
 * @author  Jan Lahoda
 */
public class AnalyseBib {
    
    private static final boolean debug = Boolean.getBoolean("latex.debug.completion.AnalyseBib");
    
    /** Creates a new instance of Test */
    private AnalyseBib() {
    }
    
    private static AnalyseBib instance = null;
    
    public static synchronized AnalyseBib getDefault() {
        if (instance == null) {
            instance = new AnalyseBib();
        }
        
        return instance;
    }
    
    public static final class BibRecord  {
        private String ref;
        private String title;
        
        public BibRecord(String ref, String title) {
            this.ref = ref;
            this.title = title;
        }
        
        public String getRef() {
            return ref;
        }
        
        public String getTitle() {
            return title;
        }
    }
    
    private List getReferences(Object file, String  bibFileName) throws IOException {
        //SA part:
        if (file instanceof File) {
            File source = (File) file;
        
            File bibFile = new File(source.getParentFile(), bibFileName + ".bib");
            InputStream bibStream = new BufferedInputStream(new FileInputStream(bibFile));
            
            return getReferences(bibStream);
        }
        
        //NB part:
        try {
            InputStream bibStream = getBibInputStreamNB(file, bibFileName);
            
            return getReferences(bibStream);
        } catch (ClassCastException e) {
            //Ignore....
        }
        
        throw new IllegalArgumentException("Unsupported file type. Content class: " + file.getClass());
    }
    
    private InputStream getBibInputStreamNB(Object sourceFO, String bibFileName) throws IOException {
        DataObject source = DataObject.find((FileObject) sourceFO);
        DataFolder parent = source.getFolder();
        
        if (parent == null)
            throw new IllegalArgumentException("The datasource does not have parent!");
        
        Enumeration children = parent.children();
        
        while (children.hasMoreElements()) {
            DataObject child = (DataObject) children.nextElement();
            FileObject primary = child.getPrimaryFile();
            
            if (bibFileName.equals(primary.getName()) && "bib".equals(primary.getExt())) {
                return primary.getInputStream();
            }
        }
                
        IllegalArgumentException toThrow = new IllegalArgumentException("The BiB file " + bibFileName + " cannot be found.");
        
        org.openide.ErrorManager.getDefault().annotate(toThrow, org.openide.ErrorManager.WARNING, toThrow.getMessage(), toThrow.getLocalizedMessage(), null, null);
        
        throw toThrow;
    }
    
    private List getReferences(InputStream ins) throws IOException {
        return parseBib(ins);
    }
    
    private static String readFile(InputStream ins) throws IOException {
        int read;
        StringBuffer result = new StringBuffer();
        
        while ((read = ins.read()) != (-1)) {
            result.append((char) read);
        }
        
        ins.close();
        return result.toString();
    }
    
    /**
     * @param args the command line arguments
     */
    private static List parseBib(InputStream ins) throws IOException {
        if (debug)
            System.err.println("parsing bibTeX file");
        
//        Pattern pattern = Pattern.compile("[^@]*@(\\p{Alpha}+)[{(][^)}]*[)}]");
        Pattern pattern = Pattern.compile("[^@]*@(\\p{Alpha}+)\\s*[{(]\\s*(\\w*)(([^\")}]*(\"[^\"]*\")?)*)[)}]");
        Matcher matcher = pattern.matcher(readFile(ins));
        ArrayList result = new ArrayList();
        
        while (matcher.find()) {
            String kind = matcher.group(1);
            
            if (!"string".equalsIgnoreCase(kind) && !"preamble".equalsIgnoreCase(kind)) {
                String code = matcher.group(2);
                String name = "NOT-FOUND";
                Matcher inner = Pattern.compile("\\s*title\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(matcher.group(3));
                
                if (inner.find()) {
                    name = inner.group(1);
                }
                
                result.add(new BibRecord(code, name));
            }
        }
        
        return result;
    }

    public final List getAllBibReferences(final LaTeXSource source) {
              LaTeXSource.Lock lock   = null;
        final List             result = new ArrayList();
        
        try {
            lock = source.lock();
            
            DocumentNode node = source.getDocument();
            
            node.traverse(new DefaultTraverseHandler() {
                public boolean commandStart(CommandNode node) {
                    if ("\\bibliography".equals(node.getCommand().getCommand())) {
                        String bibFileName = node.getArgument(0).getText().toString();
                        Object file        = source.getMainFile();
                        
                        try {
                            result.addAll(getDefault().getReferences(file, bibFileName));
                        } catch (IOException e) {
                            IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
                            
                            org.openide.ErrorManager.getDefault().annotate(e, iae);
                            
                            throw iae;
                        }
                    }
                    
                    return false;
                }
            });
        } finally {
            if (lock != null)
                source.unlock(lock);
        }

        return result;
    }

    public static final void main(String[] args) throws Exception {
        List list = AnalyseBib.getDefault().getReferences(new FileInputStream("/tmp/bibdatabase.bib"));
        Iterator iter = list.iterator();
        
        while (iter.hasNext()) {
            BibRecord record = (BibRecord) iter.next();
            
            System.err.println(record.getRef() + "=" + record.getTitle());
        }
        
    }
}
