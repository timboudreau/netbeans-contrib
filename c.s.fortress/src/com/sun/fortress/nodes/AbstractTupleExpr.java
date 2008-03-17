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
 * Class AbstractTupleExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class AbstractTupleExpr extends DelimitedExpr {
    private final List<Expr> _exprs;

    /**
     * Constructs a AbstractTupleExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbstractTupleExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs) {
        super(in_span, in_parenthesized);
        if (in_exprs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'exprs' to the AbstractTupleExpr constructor was null");
        }
        _exprs = in_exprs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractTupleExpr(Span in_span, List<Expr> in_exprs) {
        this(in_span, false, in_exprs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractTupleExpr(boolean in_parenthesized, List<Expr> in_exprs) {
        this(new Span(), in_parenthesized, in_exprs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractTupleExpr(List<Expr> in_exprs) {
        this(new Span(), false, in_exprs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbstractTupleExpr() {
        _exprs = null;
    }

    public List<Expr> getExprs() { return _exprs; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
