/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.DocumentNode;
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
    
    private Map<FileObject, TokenHierarchy<Void>> file2Text;
    private boolean      uptoDate;
    
    private FileSystem  memoryFS;
    
    private String documentClass = "<unknown>";
    
    /** Creates a new instance of DocumentNodeImpl */
    public NBDocumentNodeImpl() {
        super(null, null);
        
        this.uptoDate = true;
        this.file2Text = new HashMap<FileObject, TokenHierarchy<Void>>();
        
        CommandCollection coll = new CommandCollection();
        
        coll.addCommand(new Command("\\documentclass[#code|#documentclassoptions]{#code|#documentclass}:preamble,hide-whole-node"));
        setCommandCollection(coll);
    }
    
    public Collection getFiles() {
        return Collections.unmodifiableCollection(file2Text.keySet());
    }
    
    public void addUsedFile(FileObject file, TokenHierarchy<Void> th) {
        file2Text.put(file, th);
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
    
    public TokenHierarchy<Void> findTokenHierarchy(FileObject file) {
        return file2Text.get(file);
    }
    
}
