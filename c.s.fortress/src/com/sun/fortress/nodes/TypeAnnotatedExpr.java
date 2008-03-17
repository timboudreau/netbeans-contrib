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
 * Class TypeAnnotatedExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class TypeAnnotatedExpr extends Expr {
    private final Expr _expr;
    private final Type _type;

    /**
     * Constructs a TypeAnnotatedExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TypeAnnotatedExpr(Span in_span, boolean in_parenthesized, Expr in_expr, Type in_type) {
        super(in_span, in_parenthesized);
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the TypeAnnotatedExpr constructor was null");
        }
        _expr = in_expr;
        if (in_type == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'type' to the TypeAnnotatedExpr constructor was null");
        }
        _type = in_type;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TypeAnnotatedExpr(Span in_span, Expr in_expr, Type in_type) {
        this(in_span, false, in_expr, in_type);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TypeAnnotatedExpr(boolean in_parenthesized, Expr in_expr, Type in_type) {
        this(new Span(), in_parenthesized, in_expr, in_type);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TypeAnnotatedExpr(Expr in_expr, Type in_type) {
        this(new Span(), false, in_expr, in_type);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TypeAnnotatedExpr() {
        _expr = null;
        _type = null;
    }

    public Expr getExpr() { return _expr; }
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
