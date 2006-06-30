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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.test.TestCertificate;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class NBDocumentNodeImpl extends TextNodeImpl implements DocumentNode {
    
    private List/*<FileObject>*/ files;
    private boolean      uptoDate;
    
    private LaTeXSource source;
    private FileSystem  memoryFS;
    
    private String documentClass = "<unknown>";
    
    /** Creates a new instance of DocumentNodeImpl */
    public NBDocumentNodeImpl(LaTeXSource source) {
        super(null, null);
        
        this.source   = source;
        this.uptoDate = true;
        this.files    = new ArrayList();
        
        CommandCollection coll = new CommandCollection();
        
        coll.addCommand(new Command("\\documentclass[#code|#documentclassoptions]{#code|#documentclass}:preamble,hide-whole-node"));
        setCommandCollection(coll);
    }
    
    public Collection getFiles() {
        return Collections.unmodifiableCollection(files);
    }
    
    public void addUsedFile(FileObject file) {
        files.add(file);
    }
    
    public synchronized FileSystem getTemporaryFS() {
        if (memoryFS == null) {
            memoryFS = FileUtil.createMemoryFileSystem();
        }
        
        return memoryFS;
    }
    
    public DocumentNode getDocumentNode() {
        return this;
    }
    
    public boolean isUpToDate() {
        return uptoDate;
    }
    
    public CharSequence getText() {
        return "article"; //TODO: this is only test impl.
    }
    
    public void traverse(TraverseHandler th) {
//        if (!th.argumentStart(this))
//            return ;
        
        int count = getChildrenCount();
        
        for (int cntr = 0; cntr < count; cntr++) {
            getChild(cntr).traverse(th);
        }
        
//        th.argumentEnd(this);
    }
    
    public LaTeXSource getSource() {
        return source;
    }
    
    public void dump(TestCertificate tc, PrintWriter pw) {
        pw.println("<NBDocumentNodeImpl>");
        dumpPositions(tc, pw);

        Iterator iter = getChildrenIterator();
        
        while (iter.hasNext()) {
            ((NodeImpl) iter.next()).dump(tc, pw);
        }
        
        pw.println("</NBDocumentNodeImpl>");
    }

    public String getDocumentClass() {
        return documentClass;
    }
    
    public void setDocumentClass(String documentClass) {
        this.documentClass = documentClass;
    }
    
}
