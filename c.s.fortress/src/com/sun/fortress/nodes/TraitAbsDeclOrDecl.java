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
 * Class TraitAbsDeclOrDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class TraitAbsDeclOrDecl extends TraitObjectAbsDeclOrDecl {
    private final List<TraitType> _excludes;
    private final Option<List<TraitType>> _comprises;

    /**
     * Constructs a TraitAbsDeclOrDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        super(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_decls);
        if (in_excludes == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'excludes' to the TraitAbsDeclOrDecl constructor was null");
        }
        _excludes = in_excludes;
        if (in_comprises == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'comprises' to the TraitAbsDeclOrDecl constructor was null");
        }
        _comprises = in_comprises;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Span in_span, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, WhereClause in_where, List<TraitType> in_excludes, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, WhereClause in_where, List<TraitType> in_excludes, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_excludes, Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, WhereClause in_where, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, Option<List<TraitType>> in_comprises, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), in_comprises, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitAbsDeclOrDecl(Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Collections.<TraitType>emptyList(), Option.<List<TraitType>>none(), in_decls);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TraitAbsDeclOrDecl() {
        _excludes = null;
        _comprises = null;
    }

    public List<TraitType> getExcludes() { return _excludes; }
    public Option<List<TraitType>> getComprises() { return _comprises; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
