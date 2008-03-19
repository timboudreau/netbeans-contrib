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
 * Class AbstractTupleType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class AbstractTupleType extends NonArrowType {
    private final List<Type> _elements;

    /**
     * Constructs a AbstractTupleType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbstractTupleType(Span in_span, boolean in_parenthesized, List<Type> in_elements) {
        super(in_span, in_parenthesized);
        if (in_elements == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'elements' to the AbstractTupleType constructor was null");
        }
        _elements = in_elements;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractTupleType(Span in_span, List<Type> in_elements) {
        this(in_span, false, in_elements);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractTupleType(boolean in_parenthesized, List<Type> in_elements) {
        this(new Span(), in_parenthesized, in_elements);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractTupleType(List<Type> in_elements) {
        this(new Span(), false, in_elements);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbstractTupleType() {
        _elements = null;
    }

    public List<Type> getElements() { return _elements; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
