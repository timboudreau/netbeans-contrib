package com.sun.fortress.nodes;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.sun.fortress.nodes_util.*;
import com.sun.fortress.parser_util.*;
import com.sun.fortress.parser_util.precedence_opexpr.*;
import com.sun.fortress.useful.*;
import edu.rice.cs.plt.tuple.Option;

/**
 * Class ExternalSyntaxAbsDeclOrDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class ExternalSyntaxAbsDeclOrDecl extends AbstractNode implements AbsDeclOrDecl {
    private final SimpleName _openExpander;
    private final Id _name;
    private final SimpleName _closeExpander;

    /**
     * Constructs a ExternalSyntaxAbsDeclOrDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ExternalSyntaxAbsDeclOrDecl(Span in_span, SimpleName in_openExpander, Id in_name, SimpleName in_closeExpander) {
        super(in_span);
        if (in_openExpander == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'openExpander' to the ExternalSyntaxAbsDeclOrDecl constructor was null");
        }
        _openExpander = in_openExpander;
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the ExternalSyntaxAbsDeclOrDecl constructor was null");
        }
        _name = in_name;
        if (in_closeExpander == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'closeExpander' to the ExternalSyntaxAbsDeclOrDecl constructor was null");
        }
        _closeExpander = in_closeExpander;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExternalSyntaxAbsDeclOrDecl(SimpleName in_openExpander, Id in_name, SimpleName in_closeExpander) {
        this(new Span(), in_openExpander, in_name, in_closeExpander);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ExternalSyntaxAbsDeclOrDecl() {
        _openExpander = null;
        _name = null;
        _closeExpander = null;
    }

    public SimpleName getOpenExpander() { return _openExpander; }
    public Id getName() { return _name; }
    public SimpleName getCloseExpander() { return _closeExpander; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
