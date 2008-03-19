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
 * Class FnAbsDeclOrDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class FnAbsDeclOrDecl extends AbstractNode implements Applicable, GenericDecl {
    private final List<Modifier> _mods;
    private final SimpleName _name;
    private final List<StaticParam> _staticParams;
    private final List<Param> _params;
    private final Option<Type> _returnType;
    private final Option<List<TraitType>> _throwsClause;
    private final WhereClause _where;
    private final Contract _contract;
    private final String _selfName;

    /**
     * Constructs a FnAbsDeclOrDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        super(in_span);
        if (in_mods == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'mods' to the FnAbsDeclOrDecl constructor was null");
        }
        _mods = in_mods;
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the FnAbsDeclOrDecl constructor was null");
        }
        _name = in_name;
        if (in_staticParams == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticParams' to the FnAbsDeclOrDecl constructor was null");
        }
        _staticParams = in_staticParams;
        if (in_params == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'params' to the FnAbsDeclOrDecl constructor was null");
        }
        _params = in_params;
        if (in_returnType == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'returnType' to the FnAbsDeclOrDecl constructor was null");
        }
        _returnType = in_returnType;
        if (in_throwsClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'throwsClause' to the FnAbsDeclOrDecl constructor was null");
        }
        _throwsClause = in_throwsClause;
        if (in_where == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'where' to the FnAbsDeclOrDecl constructor was null");
        }
        _where = in_where;
        if (in_contract == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'contract' to the FnAbsDeclOrDecl constructor was null");
        }
        _contract = in_contract;
        if (in_selfName == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'selfName' to the FnAbsDeclOrDecl constructor was null");
        }
        _selfName = in_selfName.intern();
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(in_span, in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, SimpleName in_name, List<Param> in_params) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(Span in_span, SimpleName in_name, List<Param> in_params) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(new Span(), in_mods, in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(List<Modifier> in_mods, SimpleName in_name, List<Param> in_params) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_throwsClause, FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Option<Type> in_returnType) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, WhereClause in_where) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), in_where, new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Contract in_contract, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, Contract in_contract) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), in_contract, NodeUtil.defaultSelfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params, String in_selfName) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), in_selfName);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnAbsDeclOrDecl(SimpleName in_name, List<Param> in_params) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), Option.<List<TraitType>>none(), FortressUtil.emptyWhereClause(), new Contract(), NodeUtil.defaultSelfName);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected FnAbsDeclOrDecl() {
        _mods = null;
        _name = null;
        _staticParams = null;
        _params = null;
        _returnType = null;
        _throwsClause = null;
        _where = null;
        _contract = null;
        _selfName = null;
    }

    public List<Modifier> getMods() { return _mods; }
    public SimpleName getName() { return _name; }
    public List<StaticParam> getStaticParams() { return _staticParams; }
    public List<Param> getParams() { return _params; }
    public Option<Type> getReturnType() { return _returnType; }
    public Option<List<TraitType>> getThrowsClause() { return _throwsClause; }
    public WhereClause getWhere() { return _where; }
    public Contract getContract() { return _contract; }
    public String getSelfName() { return _selfName; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
