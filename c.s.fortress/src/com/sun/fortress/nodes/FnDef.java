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
 * Class FnDef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class FnDef extends FnDecl {
    private final Expr _body;

    /**
     * Constructs a FnDef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        super(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
        if (in_body == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'body' to the FnDef constructor was null");
        }
        _body = in_body;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, String in_selfName, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Expr in_body) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, String in_selfName, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(Span in_span, SimpleName in_name, List<Param> in_params, Expr in_body) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, String in_selfName, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Expr in_body) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Contract in_contract, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, String in_selfName, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDef(SimpleName in_name, List<Param> in_params, Expr in_body) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName, in_body);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected FnDef() {
        _body = null;
    }

    final public Expr getBody() { return _body; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forFnDef(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forFnDef(this); }

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
        writer.print("FnDef:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<Modifier> temp_mods = getMods();
        writer.startLine();
        writer.print("mods = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_mods = true;
        for (Modifier elt_temp_mods : temp_mods) {
            isempty_temp_mods = false;
            writer.startLine("* ");
            if (elt_temp_mods == null) {
                writer.print("null");
            } else {
                elt_temp_mods.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_mods) writer.print(" }");
        else writer.startLine("}");

        SimpleName temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

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

        List<Param> temp_params = getParams();
        writer.startLine();
        writer.print("params = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_params = true;
        for (Param elt_temp_params : temp_params) {
            isempty_temp_params = false;
            writer.startLine("* ");
            if (elt_temp_params == null) {
                writer.print("null");
            } else {
                elt_temp_params.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_params) writer.print(" }");
        else writer.startLine("}");

        Option<Type> temp_returnType = getReturnType();
        writer.startLine();
        writer.print("returnType = ");
        if (temp_returnType.isSome()) {
            writer.print("(");
            Type elt_temp_returnType = edu.rice.cs.plt.tuple.Option.unwrap(temp_returnType);
            if (elt_temp_returnType == null) {
                writer.print("null");
            } else {
                elt_temp_returnType.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<List<TraitType>> temp_throwsClause = getThrowsClause();
        writer.startLine();
        writer.print("throwsClause = ");
        if (temp_throwsClause.isSome()) {
            writer.print("(");
            List<TraitType> elt_temp_throwsClause = edu.rice.cs.plt.tuple.Option.unwrap(temp_throwsClause);
            if (elt_temp_throwsClause == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_throwsClause = true;
                for (TraitType elt_elt_temp_throwsClause : elt_temp_throwsClause) {
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

        WhereClause temp_where = getWhere();
        writer.startLine();
        writer.print("where = ");
        temp_where.outputHelp(writer, lossless);

        Contract temp_contract = getContract();
        writer.startLine();
        writer.print("contract = ");
        temp_contract.outputHelp(writer, lossless);

        String temp_selfName = getSelfName();
        writer.startLine();
        writer.print("selfName = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_selfName);
            writer.print("\"");
        } else { writer.print(temp_selfName); }

        Expr temp_body = getBody();
        writer.startLine();
        writer.print("body = ");
        temp_body.outputHelp(writer, lossless);
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
            FnDef casted = (FnDef) obj;
            List<Modifier> temp_mods = getMods();
            List<Modifier> casted_mods = casted.getMods();
            if (!(temp_mods == casted_mods || temp_mods.equals(casted_mods))) return false;
            SimpleName temp_name = getName();
            SimpleName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<StaticParam> temp_staticParams = getStaticParams();
            List<StaticParam> casted_staticParams = casted.getStaticParams();
            if (!(temp_staticParams == casted_staticParams || temp_staticParams.equals(casted_staticParams))) return false;
            List<Param> temp_params = getParams();
            List<Param> casted_params = casted.getParams();
            if (!(temp_params == casted_params || temp_params.equals(casted_params))) return false;
            Option<Type> temp_returnType = getReturnType();
            Option<Type> casted_returnType = casted.getReturnType();
            if (!(temp_returnType == casted_returnType || temp_returnType.equals(casted_returnType))) return false;
            Option<List<TraitType>> temp_throwsClause = getThrowsClause();
            Option<List<TraitType>> casted_throwsClause = casted.getThrowsClause();
            if (!(temp_throwsClause == casted_throwsClause || temp_throwsClause.equals(casted_throwsClause))) return false;
            WhereClause temp_where = getWhere();
            WhereClause casted_where = casted.getWhere();
            if (!(temp_where == casted_where || temp_where.equals(casted_where))) return false;
            Contract temp_contract = getContract();
            Contract casted_contract = casted.getContract();
            if (!(temp_contract == casted_contract || temp_contract.equals(casted_contract))) return false;
            String temp_selfName = getSelfName();
            String casted_selfName = casted.getSelfName();
            if (!(temp_selfName == casted_selfName)) return false;
            Expr temp_body = getBody();
            Expr casted_body = casted.getBody();
            if (!(temp_body == casted_body || temp_body.equals(casted_body))) return false;
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
        List<Modifier> temp_mods = getMods();
        code ^= temp_mods.hashCode();
        SimpleName temp_name = getName();
        code ^= temp_name.hashCode();
        List<StaticParam> temp_staticParams = getStaticParams();
        code ^= temp_staticParams.hashCode();
        List<Param> temp_params = getParams();
        code ^= temp_params.hashCode();
        Option<Type> temp_returnType = getReturnType();
        code ^= temp_returnType.hashCode();
        Option<List<TraitType>> temp_throwsClause = getThrowsClause();
        code ^= temp_throwsClause.hashCode();
        WhereClause temp_where = getWhere();
        code ^= temp_where.hashCode();
        Contract temp_contract = getContract();
        code ^= temp_contract.hashCode();
        String temp_selfName = getSelfName();
        code ^= temp_selfName.hashCode();
        Expr temp_body = getBody();
        code ^= temp_body.hashCode();
        return code;
    }
}
