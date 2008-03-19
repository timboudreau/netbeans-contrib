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
 * Class NamedType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class NamedType extends TraitType {
    private final QualifiedIdName _name;

    /**
     * Constructs a NamedType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public NamedType(Span in_span, boolean in_parenthesized, QualifiedIdName in_name) {
        super(in_span, in_parenthesized);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the NamedType constructor was null");
        }
        _name = in_name;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NamedType(Span in_span, QualifiedIdName in_name) {
        this(in_span, false, in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NamedType(boolean in_parenthesized, QualifiedIdName in_name) {
        this(new Span(), in_parenthesized, in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NamedType(QualifiedIdName in_name) {
        this(new Span(), false, in_name);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected NamedType() {
        _name = null;
    }

    public QualifiedIdName getName() { return _name; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
