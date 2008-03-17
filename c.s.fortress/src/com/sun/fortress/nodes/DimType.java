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
 * Class DimType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class DimType extends NonArrowType {
    private final Type _type;

    /**
     * Constructs a DimType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public DimType(Span in_span, boolean in_parenthesized, Type in_type) {
        super(in_span, in_parenthesized);
        if (in_type == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'type' to the DimType constructor was null");
        }
        _type = in_type;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimType(Span in_span, Type in_type) {
        this(in_span, false, in_type);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimType(boolean in_parenthesized, Type in_type) {
        this(new Span(), in_parenthesized, in_type);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimType(Type in_type) {
        this(new Span(), false, in_type);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected DimType() {
        _type = null;
    }

    public Type getType() { return _type; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
