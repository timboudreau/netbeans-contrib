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
 * Class IntConstraint, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class IntConstraint extends WhereConstraint {
    private final IntExpr _left;
    private final IntExpr _right;

    /**
     * Constructs a IntConstraint.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public IntConstraint(Span in_span, IntExpr in_left, IntExpr in_right) {
        super(in_span);
        if (in_left == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'left' to the IntConstraint constructor was null");
        }
        _left = in_left;
        if (in_right == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'right' to the IntConstraint constructor was null");
        }
        _right = in_right;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntConstraint(IntExpr in_left, IntExpr in_right) {
        this(new Span(), in_left, in_right);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected IntConstraint() {
        _left = null;
        _right = null;
    }

    public IntExpr getLeft() { return _left; }
    public IntExpr getRight() { return _right; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
