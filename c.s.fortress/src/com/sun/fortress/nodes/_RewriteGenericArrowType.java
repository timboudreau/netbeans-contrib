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
 * Class _RewriteGenericArrowType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class _RewriteGenericArrowType extends AbstractArrowType {
    private final List<StaticParam> _staticParams;
    private final WhereClause _where;

    /**
     * Constructs a _RewriteGenericArrowType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        super(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, in_io);
        if (in_staticParams == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticParams' to the _RewriteGenericArrowType constructor was null");
        }
        _staticParams = in_staticParams;
        if (in_where == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'where' to the _RewriteGenericArrowType constructor was null");
        }
        _where = in_where;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, WhereClause in_where) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, WhereClause in_where) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io, WhereClause in_where) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, List<StaticParam> in_staticParams) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, WhereClause in_where) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, in_throwsClause, in_io, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams) {
        this(in_span, false, in_domain, in_range, in_throwsClause, in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(in_span, false, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, in_throwsClause, false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams) {
        this(in_span, false, in_domain, in_range, in_throwsClause, false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(in_span, false, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, boolean in_io, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, boolean in_io) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, List<StaticParam> in_staticParams) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range, WhereClause in_where) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Span in_span, Type in_domain, Type in_range) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, in_io, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, List<StaticParam> in_staticParams) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range, WhereClause in_where) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(boolean in_parenthesized, Type in_domain, Type in_range) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, in_io, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, List<StaticParam> in_staticParams) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, List<StaticParam> in_staticParams) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, boolean in_io, List<StaticParam> in_staticParams) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), in_io, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, boolean in_io, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, boolean in_io) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), in_io, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, List<StaticParam> in_staticParams, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, List<StaticParam> in_staticParams) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), false, in_staticParams, FortressUtil.emptyWhereClause());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range, WhereClause in_where) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), in_where);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteGenericArrowType(Type in_domain, Type in_range) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), false, Collections.<StaticParam>emptyList(), FortressUtil.emptyWhereClause());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected _RewriteGenericArrowType() {
        _staticParams = null;
        _where = null;
    }

    final public List<StaticParam> getStaticParams() { return _staticParams; }
    final public WhereClause getWhere() { return _where; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.for_RewriteGenericArrowType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.for_RewriteGenericArrowType(this); }

    /** Generate a human-readable representation that can be deserialized. */
    public java.lang.String serialize() {
        java.io.StringWriter w = new java.io.StringWriter();
        serialize(w);
        return w.toString();
    }
    /** Generate a human-readable representation that can be deserialized. */
    public void serialize(java.io.Writer writer) {
        outputHelp(new TabPrintWriter(writer, 2), true);
    }

    public void outputHelp(TabPrintWriter writer, boolean lossless) {
        writer.print("_RewriteGenericArrowType:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        boolean temp_parenthesized = isParenthesized();
        writer.startLine();
        writer.print("parenthesized = ");
        writer.print(temp_parenthesized);

        Type temp_domain = getDomain();
        writer.startLine();
        writer.print("domain = ");
        temp_domain.outputHelp(writer, lossless);

        Type temp_range = getRange();
        writer.startLine();
        writer.print("range = ");
        temp_range.outputHelp(writer, lossless);

        Option<List<Type>> temp_throwsClause = getThrowsClause();
        writer.startLine();
        writer.print("throwsClause = ");
        if (temp_throwsClause.isSome()) {
            writer.print("(");
            List<Type> elt_temp_throwsClause = edu.rice.cs.plt.tuple.Option.unwrap(temp_throwsClause);
            if (elt_temp_throwsClause == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_throwsClause = true;
                for (Type elt_elt_temp_throwsClause : elt_temp_throwsClause) {
                    isempty_elt_temp_throwsClause = false;
                    writer.startLine("* ");
                    if (elt_elt_temp_throwsClause == null) {
                        writer.print("null");
                    } else {
                        elt_elt_temp_throwsClause.outputHelp(writer, lossless);
                    }
                }
                writer.unindent();
                if (isempty_elt_temp_throwsClause) writer.print(" }");
                else writer.startLine("}");
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        boolean temp_io = isIo();
        writer.startLine();
        writer.print("io = ");
        writer.print(temp_io);

        List<StaticParam> temp_staticParams = getStaticParams();
        writer.startLine();
        writer.print("staticParams = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_staticParams = true;
        for (StaticParam elt_temp_staticParams : temp_staticParams) {
            isempty_temp_staticParams = false;
            writer.startLine("* ");
            if (elt_temp_staticParams == null) {
                writer.print("null");
            } else {
                elt_temp_staticParams.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_staticParams) writer.print(" }");
        else writer.startLine("}");

        WhereClause temp_where = getWhere();
        writer.startLine();
        writer.print("where = ");
        temp_where.outputHelp(writer, lossless);
        writer.unindent();
    }

    /**
     * Implementation of equals that is based on the values of the fields of the
     * object. Thus, two objects created with identical parameters will be equal.
     */
    public boolean equals(java.lang.Object obj) {
        if (obj == null) return false;
        if ((obj.getClass() != this.getClass()) || (obj.hashCode() != this.hashCode())) {
            return false;
        } else {
            _RewriteGenericArrowType casted = (_RewriteGenericArrowType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Type temp_domain = getDomain();
            Type casted_domain = casted.getDomain();
            if (!(temp_domain == casted_domain || temp_domain.equals(casted_domain))) return false;
            Type temp_range = getRange();
            Type casted_range = casted.getRange();
            if (!(temp_range == casted_range || temp_range.equals(casted_range))) return false;
            Option<List<Type>> temp_throwsClause = getThrowsClause();
            Option<List<Type>> casted_throwsClause = casted.getThrowsClause();
            if (!(temp_throwsClause == casted_throwsClause || temp_throwsClause.equals(casted_throwsClause))) return false;
            boolean temp_io = isIo();
            boolean casted_io = casted.isIo();
            if (!(temp_io == casted_io)) return false;
            List<StaticParam> temp_staticParams = getStaticParams();
            List<StaticParam> casted_staticParams = casted.getStaticParams();
            if (!(temp_staticParams == casted_staticParams || temp_staticParams.equals(casted_staticParams))) return false;
            WhereClause temp_where = getWhere();
            WhereClause casted_where = casted.getWhere();
            if (!(temp_where == casted_where || temp_where.equals(casted_where))) return false;
            return true;
        }
    }

    /**
     * Implementation of hashCode that is consistent with equals.  The value of
     * the hashCode is formed by XORing the hashcode of the class object with
     * the hashcodes of all the fields of the object.
     */
    public int generateHashCode() {
        int code = getClass().hashCode();
        boolean temp_parenthesized = isParenthesized();
        code ^= temp_parenthesized ? 1231 : 1237;
        Type temp_domain = getDomain();
        code ^= temp_domain.hashCode();
        Type temp_range = getRange();
        code ^= temp_range.hashCode();
        Option<List<Type>> temp_throwsClause = getThrowsClause();
        code ^= temp_throwsClause.hashCode();
        boolean temp_io = isIo();
        code ^= temp_io ? 1231 : 1237;
        List<StaticParam> temp_staticParams = getStaticParams();
        code ^= temp_staticParams.hashCode();
        WhereClause temp_where = getWhere();
        code ^= temp_where.hashCode();
        return code;
    }
}
