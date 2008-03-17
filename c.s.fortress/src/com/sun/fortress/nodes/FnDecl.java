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
 * Class FnDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class FnDecl extends FnAbsDeclOrDecl {

    /**
     * Constructs a FnDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        super(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(Span in_span, SimpleName in_name, List<Param> in_params) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnDecl(SimpleName in_name, List<Param> in_params) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected FnDecl() {
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
