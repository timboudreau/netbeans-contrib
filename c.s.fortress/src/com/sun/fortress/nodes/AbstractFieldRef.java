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
 * Class AbstractFieldRef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class AbstractFieldRef extends Primary implements LHS {
    private final Expr _obj;

    /**
     * Constructs a AbstractFieldRef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbstractFieldRef(Span in_span, boolean in_parenthesized, Expr in_obj) {
        super(in_span, in_parenthesized);
        if (in_obj == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'obj' to the AbstractFieldRef constructor was null");
        }
        _obj = in_obj;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractFieldRef(Span in_span, Expr in_obj) {
        this(in_span, false, in_obj);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractFieldRef(boolean in_parenthesized, Expr in_obj) {
        this(new Span(), in_parenthesized, in_obj);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractFieldRef(Expr in_obj) {
        this(new Span(), false, in_obj);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbstractFieldRef() {
        _obj = null;
    }

    public Expr getObj() { return _obj; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
