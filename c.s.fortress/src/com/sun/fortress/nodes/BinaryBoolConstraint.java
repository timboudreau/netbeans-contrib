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
 * Class BinaryBoolConstraint, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class BinaryBoolConstraint extends BoolConstraint {
    private final BoolExpr _left;
    private final BoolExpr _right;

    /**
     * Constructs a BinaryBoolConstraint.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public BinaryBoolConstraint(Span in_span, boolean in_parenthesized, BoolExpr in_left, BoolExpr in_right) {
        super(in_span, in_parenthesized);
        if (in_left == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'left' to the BinaryBoolConstraint constructor was null");
        }
        _left = in_left;
        if (in_right == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'right' to the BinaryBoolConstraint constructor was null");
        }
        _right = in_right;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public BinaryBoolConstraint(Span in_span, BoolExpr in_left, BoolExpr in_right) {
        this(in_span, false, in_left, in_right);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public BinaryBoolConstraint(boolean in_parenthesized, BoolExpr in_left, BoolExpr in_right) {
        this(new Span(), in_parenthesized, in_left, in_right);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public BinaryBoolConstraint(BoolExpr in_left, BoolExpr in_right) {
        this(new Span(), false, in_left, in_right);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected BinaryBoolConstraint() {
        _left = null;
        _right = null;
    }

    public BoolExpr getLeft() { return _left; }
    public BoolExpr getRight() { return _right; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
