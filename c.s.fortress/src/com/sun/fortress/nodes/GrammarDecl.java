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
 * Class GrammarDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class GrammarDecl extends AbstractNode implements AbsDecl {
    private final QualifiedIdName _name;
    private final List<QualifiedIdName> _extends;

    /**
     * Constructs a GrammarDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public GrammarDecl(Span in_span, QualifiedIdName in_name, List<QualifiedIdName> in_extends) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the GrammarDecl constructor was null");
        }
        _name = in_name;
        if (in_extends == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'extends' to the GrammarDecl constructor was null");
        }
        _extends = in_extends;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public GrammarDecl(QualifiedIdName in_name, List<QualifiedIdName> in_extends) {
        this(new Span(), in_name, in_extends);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected GrammarDecl() {
        _name = null;
        _extends = null;
    }

    public QualifiedIdName getName() { return _name; }
    public List<QualifiedIdName> getExtends() { return _extends; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
