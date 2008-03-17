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
 * Class AbstractArrowType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class AbstractArrowType extends Type {
    private final Type _domain;
    private final Type _range;
    private final Option<List<Type>> _throwsClause;
    private final boolean _io;

    /**
     * Constructs a AbstractArrowType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbstractArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        super(in_span, in_parenthesized);
        if (in_domain == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'domain' to the AbstractArrowType constructor was null");
        }
        _domain = in_domain;
        if (in_range == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'range' to the AbstractArrowType constructor was null");
        }
        _range = in_range;
        if (in_throwsClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'throwsClause' to the AbstractArrowType constructor was null");
        }
        _throwsClause = in_throwsClause;
        _io = in_io;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(in_span, false, in_domain, in_range, in_throwsClause, in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(in_span, false, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Span in_span, Type in_domain, Type in_range, boolean in_io) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Span in_span, Type in_domain, Type in_range) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(boolean in_parenthesized, Type in_domain, Type in_range) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Type in_domain, Type in_range, boolean in_io) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractArrowType(Type in_domain, Type in_range) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbstractArrowType() {
        _domain = null;
        _range = null;
        _throwsClause = null;
        _io = false;
    }

    public Type getDomain() { return _domain; }
    public Type getRange() { return _range; }
    public Option<List<Type>> getThrowsClause() { return _throwsClause; }
    public boolean isIo() { return _io; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
