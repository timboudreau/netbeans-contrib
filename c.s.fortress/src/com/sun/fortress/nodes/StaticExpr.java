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
 * Class StaticExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class StaticExpr extends AbstractNode {
    private final boolean _parenthesized;

    /**
     * Constructs a StaticExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public StaticExpr(Span in_span, boolean in_parenthesized) {
        super(in_span);
        _parenthesized = in_parenthesized;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public StaticExpr(Span in_span) {
        this(in_span, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public StaticExpr(boolean in_parenthesized) {
        this(new Span(), in_parenthesized);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public StaticExpr() {
        this(new Span(), false);
    }

    public boolean isParenthesized() { return _parenthesized; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
