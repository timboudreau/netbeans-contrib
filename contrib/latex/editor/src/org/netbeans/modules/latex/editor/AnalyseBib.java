/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.Entry;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;


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
        private PublicationEntry entry;
        
        public BibRecord(PublicationEntry entry) {
            this.entry = entry;
        }
        
        public String getRef() {
            return entry.getTag();
        }
        
        public String getTitle() {
            return entry.getTitle();
        }
        
        public PublicationEntry getEntry() {
            return entry;
        }
    }
    
    private List getReferences(Object file, String  bibFileName) throws IOException {
        Object      bibFile = org.netbeans.modules.latex.model.Utilities.getDefault().getRelativeFileName(file, bibFileName);
        
        if (bibFile == null) {
            bibFile = org.netbeans.modules.latex.model.Utilities.getDefault().getRelativeFileName(file, bibFileName + ".bib");
        }
        
        if (bibFile == null)
            throw new IllegalArgumentException("BiBTeX file " + bibFileName + " for main source file " + file + " not found.");
        
        BiBTeXModel model   = BiBTeXModel.getModel(bibFile);
        List        result  = new ArrayList();
        
        for (Iterator i = model.getEntries().iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            
            if (e instanceof PublicationEntry) {
                PublicationEntry pEntry = (PublicationEntry) e;
                
                result.add(new BibRecord(pEntry));
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
                        String          bibFileNames = node.getArgument(0).getText().toString();
                        StringTokenizer divider      = new StringTokenizer(bibFileNames, ",");
                        
                        while (divider.hasMoreTokens()) {
                            String bibFileName  = divider.nextToken();
                            Object file         = source.getMainFile();
                            
                            try {
                                result.addAll(getDefault().getReferences(file, bibFileName));
                            } catch (IOException e) {
                                ErrorManager.getDefault().notifyInformational(e);
                            } catch (IllegalArgumentException e) {
                                ErrorManager.getDefault().notifyInformational(e);
                            }
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

}
