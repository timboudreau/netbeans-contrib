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
 * Class NumberLiteralExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class NumberLiteralExpr extends LiteralExpr {

    /**
     * Constructs a NumberLiteralExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public NumberLiteralExpr(Span in_span, boolean in_parenthesized, String in_text) {
        super(in_span, in_parenthesized, in_text);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NumberLiteralExpr(Span in_span, String in_text) {
        this(in_span, false, in_text);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NumberLiteralExpr(boolean in_parenthesized, String in_text) {
        this(new Span(), in_parenthesized, in_text);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NumberLiteralExpr(String in_text) {
        this(new Span(), false, in_text);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected NumberLiteralExpr() {
    }


    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
