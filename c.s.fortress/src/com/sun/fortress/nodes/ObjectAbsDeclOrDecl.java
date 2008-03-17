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
 * Class ObjectAbsDeclOrDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class ObjectAbsDeclOrDecl extends TraitObjectAbsDeclOrDecl implements GenericAbsDeclOrDeclWithParams {
    private final Option<List<Param>> _params;
    private final Option<List<TraitType>> _throwsClause;
    private final Contract _contract;

    /**
     * Constructs a ObjectAbsDeclOrDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        super(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_decls);
        if (in_params == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'params' to the ObjectAbsDeclOrDecl constructor was null");
        }
        _params = in_params;
        if (in_throwsClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'throwsClause' to the ObjectAbsDeclOrDecl constructor was null");
        }
        _throwsClause = in_throwsClause;
        if (in_contract == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'contract' to the ObjectAbsDeclOrDecl constructor was null");
        }
        _contract = in_contract;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Span in_span, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, WhereClause in_where, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, WhereClause in_where, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, Option<List<Param>> in_params, Option<List<TraitType>> in_throwsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, in_throwsClause, new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, Option<List<Param>> in_params, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, Option<List<Param>> in_params, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_params, Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, Contract in_contract, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), in_contract, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectAbsDeclOrDecl(Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), Option.<List<Param>>none(), Option.<List<TraitType>>none(), new Contract(), in_decls);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ObjectAbsDeclOrDecl() {
        _params = null;
        _throwsClause = null;
        _contract = null;
    }

    public Option<List<Param>> getParams() { return _params; }
    public Option<List<TraitType>> getThrowsClause() { return _throwsClause; }
    public Contract getContract() { return _contract; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
