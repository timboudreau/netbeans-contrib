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
 * Class ExprMI, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class ExprMI extends MathItem {
    private final Expr _expr;

    /**
     * Constructs a ExprMI.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ExprMI(Span in_span, Expr in_expr) {
        super(in_span);
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the ExprMI constructor was null");
        }
        _expr = in_expr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExprMI(Expr in_expr) {
        this(new Span(), in_expr);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ExprMI() {
        _expr = null;
    }

    public Expr getExpr() { return _expr; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
