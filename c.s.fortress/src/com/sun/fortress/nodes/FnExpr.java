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
 * Class FnExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class FnExpr extends Expr implements Applicable {
    private final SimpleName _name;
    private final List<StaticParam> _staticParams;
    private final List<Param> _params;
    private final Option<Type> _returnType;
    private final WhereClause _where;
    private final Option<List<TraitType>> _throwsClause;
    private final Expr _body;

    /**
     * Constructs a FnExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        super(in_span, in_parenthesized);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the FnExpr constructor was null");
        }
        _name = in_name;
        if (in_staticParams == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticParams' to the FnExpr constructor was null");
        }
        _staticParams = in_staticParams;
        if (in_params == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'params' to the FnExpr constructor was null");
        }
        _params = in_params;
        if (in_returnType == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'returnType' to the FnExpr constructor was null");
        }
        _returnType = in_returnType;
        if (in_where == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'where' to the FnExpr constructor was null");
        }
        _where = in_where;
        if (in_throwsClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'throwsClause' to the FnExpr constructor was null");
        }
        _throwsClause = in_throwsClause;
        if (in_body == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'body' to the FnExpr constructor was null");
        }
        _body = in_body;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, in_name, in_staticParams, in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, in_name, in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, in_parenthesized, in_name, in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, in_name, in_staticParams, in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, in_name, in_staticParams, in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(in_span, in_parenthesized, in_name, in_staticParams, in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, in_parenthesized, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, SimpleName in_name, List<Param> in_params, Expr in_body) {
        this(in_span, in_parenthesized, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), in_staticParams, in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), in_staticParams, in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), in_staticParams, in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, boolean in_parenthesized, List<Param> in_params, Expr in_body) {
        this(in_span, in_parenthesized, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, in_name, in_staticParams, in_params, in_returnType, in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, false, in_name, in_staticParams, in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, in_name, in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, false, in_name, in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, in_name, in_staticParams, in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, false, in_name, in_staticParams, in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(in_span, false, in_name, in_staticParams, in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, false, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, false, in_name, Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, false, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, SimpleName in_name, List<Param> in_params, Expr in_body) {
        this(in_span, false, in_name, Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<StaticParam> in_staticParams, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), in_staticParams, in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), in_staticParams, in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<StaticParam> in_staticParams, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), in_staticParams, in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<StaticParam> in_staticParams, List<Param> in_params, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), in_staticParams, in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<Param> in_params, Option<Type> in_returnType, WhereClause in_where, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<Param> in_params, Option<Type> in_returnType, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<Param> in_params, Option<Type> in_returnType, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, in_returnType, FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<Param> in_params, WhereClause in_where, Option<List<TraitType>> in_throwsClause, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, in_throwsClause, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<Param> in_params, WhereClause in_where, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), in_where, Option.<List<TraitType>>none(), in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FnExpr(Span in_span, List<Param> in_params, Expr in_body) {
        this(in_span, false, new AnonymousFnName(in_span), Collections.<StaticParam>emptyList(), in_params, Option.<Type>none(), FortressUtil.emptyWhereClause(), Option.<List<TraitType>>none(), in_body);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected FnExpr() {
        _name = null;
        _staticParams = null;
        _params = null;
        _returnType = null;
        _where = null;
        _throwsClause = null;
        _body = null;
    }

    final public SimpleName getName() { return _name; }
    final public List<StaticParam> getStaticParams() { return _staticParams; }
    final public List<Param> getParams() { return _params; }
    final public Option<Type> getReturnType() { return _returnType; }
    final public WhereClause getWhere() { return _where; }
    final public Option<List<TraitType>> getThrowsClause() { return _throwsClause; }
    final public Expr getBody() { return _body; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forFnExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forFnExpr(this); }

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
        writer.print("FnExpr:");
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

        WhereClause temp_where = getWhere();
        writer.startLine();
        writer.print("where = ");
        temp_where.outputHelp(writer, lossless);

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
            FnExpr casted = (FnExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
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
            WhereClause temp_where = getWhere();
            WhereClause casted_where = casted.getWhere();
            if (!(temp_where == casted_where || temp_where.equals(casted_where))) return false;
            Option<List<TraitType>> temp_throwsClause = getThrowsClause();
            Option<List<TraitType>> casted_throwsClause = casted.getThrowsClause();
            if (!(temp_throwsClause == casted_throwsClause || temp_throwsClause.equals(casted_throwsClause))) return false;
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
        boolean temp_parenthesized = isParenthesized();
        code ^= temp_parenthesized ? 1231 : 1237;
        SimpleName temp_name = getName();
        code ^= temp_name.hashCode();
        List<StaticParam> temp_staticParams = getStaticParams();
        code ^= temp_staticParams.hashCode();
        List<Param> temp_params = getParams();
        code ^= temp_params.hashCode();
        Option<Type> temp_returnType = getReturnType();
        code ^= temp_returnType.hashCode();
        WhereClause temp_where = getWhere();
        code ^= temp_where.hashCode();
        Option<List<TraitType>> temp_throwsClause = getThrowsClause();
        code ^= temp_throwsClause.hashCode();
        Expr temp_body = getBody();
        code ^= temp_body.hashCode();
        return code;
    }
}
