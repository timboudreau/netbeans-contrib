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
 * Class AbsObjectDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class AbsObjectDecl extends ObjectAbsDeclOrDecl implements AbsDecl {

    /**
     * Constructs a AbsObjectDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        super(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<AbsDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, WhereClause in_where, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, Contract in_contract, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Span in_span, Id in_name, List<AbsDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(List<Modifier> in_mods, Id in_name, List<AbsDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<StaticParam> in_staticParams, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, WhereClause in_where, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, WhereClause in_where, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, Option<List<Param>> in_params, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, Option<List<Param>> in_params, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, Contract in_contract, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsObjectDecl(Id in_name, List<AbsDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbsObjectDecl() {
    }

    @SuppressWarnings("unchecked") final public List<AbsDecl> getDecls() { return (List<AbsDecl>) super.getDecls(); }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAbsObjectDecl(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAbsObjectDecl(this); }

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
        writer.print("AbsObjectDecl:");
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

        Id temp_name = getName();
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

        List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
        writer.startLine();
        writer.print("extendsClause = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_extendsClause = true;
        for (TraitTypeWhere elt_temp_extendsClause : temp_extendsClause) {
            isempty_temp_extendsClause = false;
            writer.startLine("* ");
            if (elt_temp_extendsClause == null) {
                writer.print("null");
            } else {
                elt_temp_extendsClause.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_extendsClause) writer.print(" }");
        else writer.startLine("}");

        WhereClause temp_where = getWhere();
        writer.startLine();
        writer.print("where = ");
        temp_where.outputHelp(writer, lossless);

        Option<List<Param>> temp_params = getParams();
        writer.startLine();
        writer.print("params = ");
        if (temp_params.isSome()) {
            writer.print("(");
            List<Param> elt_temp_params = edu.rice.cs.plt.tuple.Option.unwrap(temp_params);
            if (elt_temp_params == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_params = true;
                for (Param elt_elt_temp_params : elt_temp_params) {
                    isempty_elt_temp_params = false;
                    writer.startLine("* ");
                    if (elt_elt_temp_params == null) {
                        writer.print("null");
                    } else {
                        elt_elt_temp_params.outputHelp(writer, lossless);
                    }
                }
                writer.unindent();
                if (isempty_elt_temp_params) writer.print(" }");
                else writer.startLine("}");
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

        Contract temp_contract = getContract();
        writer.startLine();
        writer.print("contract = ");
        temp_contract.outputHelp(writer, lossless);

        List<AbsDecl> temp_decls = getDecls();
        writer.startLine();
        writer.print("decls = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_decls = true;
        for (AbsDecl elt_temp_decls : temp_decls) {
            isempty_temp_decls = false;
            writer.startLine("* ");
            if (elt_temp_decls == null) {
                writer.print("null");
            } else {
                elt_temp_decls.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_decls) writer.print(" }");
        else writer.startLine("}");
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
            AbsObjectDecl casted = (AbsObjectDecl) obj;
            List<Modifier> temp_mods = getMods();
            List<Modifier> casted_mods = casted.getMods();
            if (!(temp_mods == casted_mods || temp_mods.equals(casted_mods))) return false;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<StaticParam> temp_staticParams = getStaticParams();
            List<StaticParam> casted_staticParams = casted.getStaticParams();
            if (!(temp_staticParams == casted_staticParams || temp_staticParams.equals(casted_staticParams))) return false;
            List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
            List<TraitTypeWhere> casted_extendsClause = casted.getExtendsClause();
            if (!(temp_extendsClause == casted_extendsClause || temp_extendsClause.equals(casted_extendsClause))) return false;
            WhereClause temp_where = getWhere();
            WhereClause casted_where = casted.getWhere();
            if (!(temp_where == casted_where || temp_where.equals(casted_where))) return false;
            Option<List<Param>> temp_params = getParams();
            Option<List<Param>> casted_params = casted.getParams();
            if (!(temp_params == casted_params || temp_params.equals(casted_params))) return false;
            Option<List<TraitType>> temp_throwsClause = getThrowsClause();
            Option<List<TraitType>> casted_throwsClause = casted.getThrowsClause();
            if (!(temp_throwsClause == casted_throwsClause || temp_throwsClause.equals(casted_throwsClause))) return false;
            Contract temp_contract = getContract();
            Contract casted_contract = casted.getContract();
            if (!(temp_contract == casted_contract || temp_contract.equals(casted_contract))) return false;
            List<AbsDecl> temp_decls = getDecls();
            List<AbsDecl> casted_decls = casted.getDecls();
            if (!(temp_decls == casted_decls || temp_decls.equals(casted_decls))) return false;
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
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        List<StaticParam> temp_staticParams = getStaticParams();
        code ^= temp_staticParams.hashCode();
        List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
        code ^= temp_extendsClause.hashCode();
        WhereClause temp_where = getWhere();
        code ^= temp_where.hashCode();
        Option<List<Param>> temp_params = getParams();
        code ^= temp_params.hashCode();
        Option<List<TraitType>> temp_throwsClause = getThrowsClause();
        code ^= temp_throwsClause.hashCode();
        Contract temp_contract = getContract();
        code ^= temp_contract.hashCode();
        List<AbsDecl> temp_decls = getDecls();
        code ^= temp_decls.hashCode();
        return code;
    }
}
