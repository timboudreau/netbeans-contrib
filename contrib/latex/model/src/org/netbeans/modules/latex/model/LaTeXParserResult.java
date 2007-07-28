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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.ParserFile;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.modules.latex.model.command.CommandUtilities;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public final class LaTeXParserResult extends ParserResult {

    private DocumentNode root;
    private StructuralElement structuralRoot;
    private CommandUtilities utils;
    private FileObject mainFile;
    private Collection<ErrorDescription> errors;
            
    public LaTeXParserResult(ParserFile file, FileObject mainFile, DocumentNode root, StructuralElement structuralRoot, CommandUtilities utils, Collection<ErrorDescription> errors) {
        super(file);
        this.root = root;
        this.structuralRoot = structuralRoot;
        this.utils = utils;
        this.mainFile = mainFile;
        this.errors = Collections.unmodifiableCollection(new LinkedList<ErrorDescription>(errors));
    }

    public Element getRoot() {
        return null;
    }

    public AstTreeNode getAst() {
        return null;
    }

    public DocumentNode getDocument() {
        return root;
    }

    public CommandUtilities getCommandUtilities() {
        return utils;
    }

    public FileObject getMainFile() {
        return mainFile;
    }

    public StructuralElement getStructuralRoot() {
        return structuralRoot;
    }

    public Collection<ErrorDescription> getErrors() {
        return errors;
    }

}
