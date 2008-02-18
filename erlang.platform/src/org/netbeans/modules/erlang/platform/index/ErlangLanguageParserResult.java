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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.erlang.platform.index;


import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.gsf.ParserFile;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.modules.erlang.editing.semantic.ErlContext;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlangLanguageParserResult extends ParserResult {
    private AstTreeNode ast;
    private AstRootElement rootElement;
    private OffsetRange sanitizedRange = OffsetRange.NONE;
 
    private ASTNode rootNode;
    private ErlContext rootContext;
    /** Result used for failed compilation
     * @todo Provide errors too?
     */
    public ErlangLanguageParserResult(ParserFile file) {
        super(file);
    }

    /**
     * Result used for successful compilation
     */
    public ErlangLanguageParserResult(
            ParserFile file, 
            AstRootElement rootElement, 
            ASTNode rootNode,
	    ErlContext rootCtx) {
        super(file);
        this.rootElement = rootElement;
        this.rootNode = rootNode;
        this.rootContext = rootCtx;
    }

    public ParserResult.AstTreeNode getAst() {
        return null;
    }

    @Override
    public Element getRoot() {
        return rootElement;
    }

    /** 
     * The root node of the AST produced by the parser.
     * Later, rip out the getAst part etc.
     */
    public ASTNode getRootNode() {
        return rootNode;
    }

    public void setRootContext(ErlContext rootCtx) {
        this.rootContext = rootCtx;
    }

    public ErlContext getRootContext() {
	return rootContext;
    }

    /**
     * Return whether the source code for the parse result was "cleaned"
     * or "sanitized" (modified to reduce chance of parser errors) or not.
     * This method returns OffsetRange.NONE if the source was not sanitized,
     * otherwise returns the actual sanitized range.
     */
    public OffsetRange getSanitizedRange() {
        return sanitizedRange;
    }

    /**
     * Set the range of source that was sanitized, if any.
     */
    public void setSanitizedRange(OffsetRange sanitizedRange) {
        this.sanitizedRange = sanitizedRange;
    }


}

