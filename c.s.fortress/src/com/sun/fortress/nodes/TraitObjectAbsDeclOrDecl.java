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
 * Class TraitObjectAbsDeclOrDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class TraitObjectAbsDeclOrDecl extends AbstractNode implements HasWhere, GenericAbsDeclOrDecl {
    private final List<Modifier> _mods;
    private final Id _name;
    private final List<StaticParam> _staticParams;
    private final List<TraitTypeWhere> _extendsClause;
    private final WhereClause _where;
    private final List<? extends AbsDeclOrDecl> _decls;

    /**
     * Constructs a TraitObjectAbsDeclOrDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        super(in_span);
        if (in_mods == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'mods' to the TraitObjectAbsDeclOrDecl constructor was null");
        }
        _mods = in_mods;
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the TraitObjectAbsDeclOrDecl constructor was null");
        }
        _name = in_name;
        if (in_staticParams == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticParams' to the TraitObjectAbsDeclOrDecl constructor was null");
        }
        _staticParams = in_staticParams;
        if (in_extendsClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'extendsClause' to the TraitObjectAbsDeclOrDecl constructor was null");
        }
        _extendsClause = in_extendsClause;
        if (in_where == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'where' to the TraitObjectAbsDeclOrDecl constructor was null");
        }
        _where = in_where;
        if (in_decls == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'decls' to the TraitObjectAbsDeclOrDecl constructor was null");
        }
        _decls = in_decls;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, List<Modifier> in_mods, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Span in_span, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(List<Modifier> in_mods, Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), in_mods, in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<TraitTypeWhere> in_extendsClause, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, in_extendsClause, FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Id in_name, List<StaticParam> in_staticParams, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_staticParams, Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Id in_name, WhereClause in_where, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), in_where, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitObjectAbsDeclOrDecl(Id in_name, List<? extends AbsDeclOrDecl> in_decls) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Collections.<StaticParam>emptyList(), Collections.<TraitTypeWhere>emptyList(), FortressUtil.emptyWhereClause(), in_decls);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TraitObjectAbsDeclOrDecl() {
        _mods = null;
        _name = null;
        _staticParams = null;
        _extendsClause = null;
        _where = null;
        _decls = null;
    }

    public List<Modifier> getMods() { return _mods; }
    public Id getName() { return _name; }
    public List<StaticParam> getStaticParams() { return _staticParams; }
    public List<TraitTypeWhere> getExtendsClause() { return _extendsClause; }
    public WhereClause getWhere() { return _where; }
    public List<? extends AbsDeclOrDecl> getDecls() { return _decls; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
