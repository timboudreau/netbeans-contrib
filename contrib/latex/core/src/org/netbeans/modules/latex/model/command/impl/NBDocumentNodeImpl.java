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
package org.netbeans.modules.latex.model.command.impl;

import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import org.netbeans.modules.latex.model.command.Command;

import org.openide.filesystems.*;

import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.test.TestCertificate;


/**
 *
 * @author Jan Lahoda
 */
public class NBDocumentNodeImpl extends TextNodeImpl implements DocumentNode/*, FileChangeListener*/ {
    
    private List/*<FileObject>*/ files;
    private boolean      uptoDate;
    
    private LaTeXSource source;
    
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
    
//    public void setFiles(Object[] files) {
//        Object[] oldFiles = this.files;
//        
//        Set newF = new HashSet(Arrays.asList(files));
//        Set old  = oldFiles == null ? new HashSet() : new HashSet(Arrays.asList(oldFiles));
//        
//        Set toRemove = new HashSet(old);
//        
//        toRemove.removeAll(newF);
//        
//        Set toAdd    = new HashSet(newF);
//        
//        toAdd.removeAll(old);
//        
//        Iterator toRemoveIterator = toRemove.iterator();
//        
//        //This does not make much sense, as below a weak listener is added, and here "hard" listener is
//        //being removed.
//        while (toRemoveIterator.hasNext()) {
//            ((FileObject) toRemoveIterator.next()).removeFileChangeListener(this);
//        }
//        
//        Iterator toAddIterator    = toAdd.iterator();
//        
//        while (toAddIterator.hasNext()) {
//            FileObject fo = (FileObject) toAddIterator.next();
//            
//            fo.addFileChangeListener(WeakListener.fileChange(this, fo));
//        }
//        
//        this.files = (FileObject[] ) newF.toArray(new FileObject[newF.size()]);
//    }
//    
    public DocumentNode getDocumentNode() {
        return this;
    }
    
    public boolean isUpToDate() {
        return uptoDate;
    }
    
//    public void performUpToDate(Runnable r) {
//        //.....
//    }
    
//    public CommandCollection getCommands() {
//        return commands;
//    }
//    
    public CharSequence getText() {
        return "article"; //TODO: this is only test impl.
    }
    
//    public void fileAttributeChanged(FileAttributeEvent fe) {
//    }
//    
//    public void fileChanged(FileEvent fe) {
//    }
//    
//    public void fileDataCreated(FileEvent fe) {
//    }
//    
//    public void fileDeleted(FileEvent fe) {
//    }
//    
//    public void fileFolderCreated(FileEvent fe) {
//    }
//    
//    public void fileRenamed(FileRenameEvent fe) {
//    }
    
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
