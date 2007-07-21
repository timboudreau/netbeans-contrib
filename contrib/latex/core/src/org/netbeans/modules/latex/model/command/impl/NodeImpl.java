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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.ElementKind;
import org.netbeans.api.gsf.Modifier;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.modules.latex.test.TestCertificate;

/**
 *
 * @author Jan Lahoda
 */
public abstract class NodeImpl implements Node, Element {
    
    public static final String START    = "start";
    public static final String END      = "end";
    
    private SourcePosition start;
    
    private SourcePosition end;

    private PropertyChangeSupport pcs;
    
    private Node parent;
    
    private DocumentNode document;
    
    private NodeImpl previousCommandDefiningNode;
    private CommandCollection commandCollection;
    
    /** Creates a new instance of NodeImpl */
    protected NodeImpl(Node parent, NodeImpl previousCommandDefiningNode) {
        pcs = new PropertyChangeSupport(this);
//        System.err.println("class = " + getClass() + ", parent = " + parent );
//        new Exception().printStackTrace(System.err);
        this.parent = parent;
        
        if (parent != null)
            document = parent.getDocumentNode();
        
        this.previousCommandDefiningNode = previousCommandDefiningNode;
        this.commandCollection = null;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    protected void firePropertyChange(String name, Object old, Object nue) {
        pcs.firePropertyChange(name, old, nue);
    }
    
    public synchronized SourcePosition getEndingPosition() {
        return end;
    }

    public void setEndingPosition(SourcePosition p) {
        SourcePosition old = this.end;
        
        this.end       = p;
        
        firePropertyChange(END, null, p);
    }
    
    public SourcePosition getStartingPosition() {
        return start;
    }
    
    public void setStartingPosition(SourcePosition p) {
        SourcePosition old = this.start;
        
        this.start       = p;
        
        firePropertyChange(START, null, p);
    }
    
    public void setParent(Node node) {
        this.parent = node;
    }
    
    protected boolean isIn(Object file, Position pos, Node node) {
        assert Utilities.getDefault().compareFiles(node.getStartingPosition().getFile(), node.getEndingPosition().getFile());
        
        if (!Utilities.getDefault().compareFiles(file, node.getStartingPosition().getFile()))
            return false;
        
        return node.getStartingPosition().getOffsetValue() < pos.getOffset() && pos.getOffset() < node.getEndingPosition().getOffsetValue();
    }
    
    protected boolean isInChild(Object file, Position pos) {
        return false;
    }

    public Iterable<? extends Token<TexTokenId>> getNodeTokens() throws IOException {
        try {
            SourcePosition start = getStartingPosition();
            SourcePosition end   = getEndingPosition();
            
//            System.err.println("start = " + start );
//            System.err.println("end = " + end );
            
            if (!Utilities.getDefault().compareFiles(start.getFile(), end.getFile()))
                throw new IllegalStateException("Whole Node should be in one file, but this condition is not fullfilled now. Start file=" + start.getFile() + ", end file=" + end.getFile() + ".");
            
            Document doc = Utilities.getDefault().openDocument(start.getFile());
            
            if (doc == null)
                throw new IllegalStateException();
            
            TokenHierarchy h = TokenHierarchy.get(doc);
            TokenSequence  ts = h.tokenSequence();
            List<Token<TexTokenId>> result = new LinkedList<Token<TexTokenId>>();
            
            ts.move(start.getOffsetValue());

            if (!ts.moveNext())
                return result;
            
            while (ts.offset() < end.getOffsetValue()) {
                if (!isInChild(start.getFile(), doc.createPosition(ts.offset())))
                    result.add((Token) ts.token());
                
                if (!ts.moveNext())
                    break;
            }
            
            return result;
        } catch (BadLocationException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public Iterable<? extends Token<TexTokenId>> getDeepNodeTokens() throws IOException {
        SourcePosition start = getStartingPosition();
        SourcePosition end   = getEndingPosition();
        
        //            System.err.println("start = " + start );
        //            System.err.println("end = " + end );
        
        if (!Utilities.getDefault().compareFiles(start.getFile(), end.getFile()))
            throw new IllegalStateException("Whole Node should be in one file, but this condition is not fullfilled now. Start file=" + start.getFile() + ", end file=" + end.getFile() + ".");
        
        Document doc = Utilities.getDefault().openDocument(start.getFile());
        
        if (doc == null)
            throw new IllegalStateException();
        
        if (start.getOffset().getOffset() == end.getOffset().getOffset()) //the node is empty
            return Collections.emptyList();
        
        TokenHierarchy h = TokenHierarchy.get(doc);
        TokenSequence  ts = h.tokenSequence();
        List<Token<TexTokenId>> result = new LinkedList<Token<TexTokenId>>();
        
        ts.move(start.getOffsetValue());
        
        if (!ts.moveNext())
            return result;
        
        //TODO: this will work only for one file, not sure whether this is problem now.
        while (ts.offset() < end.getOffsetValue()) {
            result.add((Token) ts.token());
            
            if (!ts.moveNext())
                break;
        }
        
        return result;
    }

    public CharSequence getText() {
//        System.err.println("getText() start");
        
        try {
            StringBuffer  result = new StringBuffer();
            
            for (Token<TexTokenId> token : getNodeTokens()) {
                if (token.id() == TexTokenId.WORD || token.id() == TexTokenId.WHITESPACE || token.id() == TexTokenId.UNKNOWN_CHARACTER) {
                    CharSequence s = token.text();
                    
//                    if (s.length() == 1 && s.charAt(0) == '\n')
//                        s = " ";
                        
                    result.append(s.toString());
                }
            }
            
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
    
    public DocumentNode getDocumentNode() {
        return document;
    }
    
    public Node getParent() {
        return parent;
    }
    
    public void traverse(TraverseHandler th) {
        //Nothing to do for Node.
    }
    
    public boolean contains(SourcePosition position) {
        //The polymorphism should be used here, but currently prepared method will be used:
        return     isIn(position.getFile(), position.getOffset(), this)
               && !isInChild(position.getFile(), position.getOffset());
    }
    
    protected final void dumpPositions(TestCertificate tc, PrintWriter pw) {
        pw.println("<positions>");
        pw.print("<position><![CDATA[");
        pw.print(getStartingPosition().dump());
        pw.println("]]></position>");
        pw.print("<position><![CDATA[");
        pw.print(getEndingPosition().dump());
        pw.println("]]></position>");
        pw.println("</positions>");
    }
    
    public abstract void dump(TestCertificate tc, PrintWriter pw);
    
    public CommandCollection getCommandCollection() {
        return commandCollection;
    }
    
    protected void setCommandCollection(CommandCollection commandCollection) {
        this.commandCollection = commandCollection;
    }
    
    public NodeImpl getPreviousCommandDefiningNode() {
        return previousCommandDefiningNode;
    }
    
    /**
     */
    public List getCommands(boolean includingThis) {
        if (includingThis)
            return getCommandsImpl(new ArrayList());
        
        if (getPreviousCommandDefiningNode() != null) {
            return getPreviousCommandDefiningNode().getCommandsImpl(new ArrayList());
        }
        
        return Collections.EMPTY_LIST;
    }
    
    protected List getCommandsImpl(List result) {
        result.addAll(getCommandCollection().getCommands());
        
        if (getPreviousCommandDefiningNode() != null) {
            getPreviousCommandDefiningNode().getCommandsImpl(result);
        }
        
        return result;
    }
    
    public Command getCommand(String name, boolean includingThis) {
        if (includingThis)
            return getCommandImpl(name);
        
        if (getPreviousCommandDefiningNode() != null)
            return getPreviousCommandDefiningNode().getCommandImpl(name);
        
        return null;
    }
    
    protected Command getCommandImpl(String name) {
//        if ("\\bigoh".equals(name))
//            System.err.println("getCommandImpl(" + name + "), this=" + this + ", coll=" + getCommandCollection());
        Command fromMineCollection = getCommandCollection().getCommand(name);
        
        if (fromMineCollection == null && getPreviousCommandDefiningNode() != null) {
            return getPreviousCommandDefiningNode().getCommandImpl(name);
        }
        
        return fromMineCollection;
    }

    /**
     */
    public List getEnvironments(boolean includingThis) {
        if (includingThis)
            return getEnvironmentsImpl(new ArrayList());
        
        if (getPreviousCommandDefiningNode() != null) {
            return getPreviousCommandDefiningNode().getEnvironmentsImpl(new ArrayList());
        }
        
        return Collections.EMPTY_LIST;
    }
    
    protected List getEnvironmentsImpl(List result) {
        result.addAll(getCommandCollection().getEnvironments());
        
        if (getPreviousCommandDefiningNode() != null) {
            getPreviousCommandDefiningNode().getEnvironmentsImpl(result);
        }
        
        return result;
    }
    
    public Environment getEnvironment(String name, boolean includingThis) {
        if (includingThis)
            return getEnvironmentImpl(name);
        
        if (getPreviousCommandDefiningNode() != null)
            return getPreviousCommandDefiningNode().getEnvironmentImpl(name);
        
        return null;
    }
    
    protected Environment getEnvironmentImpl(String name) {
//        if ("\\bigoh".equals(name))
//            System.err.println("getCommandImpl(" + name + "), this=" + this + ", coll=" + getCommandCollection());
        Environment fromMineCollection = getCommandCollection().getEnvironment(name);
        
        if (fromMineCollection == null && getPreviousCommandDefiningNode() != null) {
            return getPreviousCommandDefiningNode().getEnvironmentImpl(name);
        }
        
        return fromMineCollection;
    }

    public CharSequence getFullText() {
        try {
            StringBuffer  result = new StringBuffer();
            
            for (Token<TexTokenId> token : getDeepNodeTokens()) {
                CharSequence s = token.text();
                
                result.append(s);
            }
            
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**No attributes
     */
    public String getAttribute(String name) {
        return null;
    }

    /**No attributes
     */
    public boolean hasAttribute(String name) {
        return false;
    }
    

    //XXX: from element:
    public String getName() {
        return "";
    }
    
    public String getIn() {
        return "";
    }
    
    public ElementKind getKind() {
        return ElementKind.OTHER;
    }
    
    public Set<Modifier> getModifiers() {
        return EnumSet.noneOf(Modifier.class);
    }
}
