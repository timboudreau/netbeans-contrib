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
 * Class FunctionalRef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class FunctionalRef extends Primary {
    private final List<StaticArg> _staticArgs;

    /**
     * Constructs a FunctionalRef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FunctionalRef(Span in_span, boolean in_parenthesized, List<StaticArg> in_staticArgs) {
        super(in_span, in_parenthesized);
        if (in_staticArgs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticArgs' to the FunctionalRef constructor was null");
        }
        _staticArgs = in_staticArgs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FunctionalRef(Span in_span, boolean in_parenthesized) {
        this(in_span, in_parenthesized, Collections.<StaticArg>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FunctionalRef(Span in_span, List<StaticArg> in_staticArgs) {
        this(in_span, false, in_staticArgs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FunctionalRef(Span in_span) {
        this(in_span, false, Collections.<StaticArg>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FunctionalRef(boolean in_parenthesized, List<StaticArg> in_staticArgs) {
        this(new Span(), in_parenthesized, in_staticArgs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FunctionalRef(boolean in_parenthesized) {
        this(new Span(), in_parenthesized, Collections.<StaticArg>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FunctionalRef(List<StaticArg> in_staticArgs) {
        this(new Span(), false, in_staticArgs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FunctionalRef() {
        this(new Span(), false, Collections.<StaticArg>emptyList());
    }

    public List<StaticArg> getStaticArgs() { return _staticArgs; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
