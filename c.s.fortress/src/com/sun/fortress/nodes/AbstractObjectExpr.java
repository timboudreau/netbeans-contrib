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
 * Class AbstractObjectExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class AbstractObjectExpr extends DelimitedExpr {
    private final List<TraitTypeWhere> _extendsClause;
    private final List<Decl> _decls;

    /**
     * Constructs a AbstractObjectExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbstractObjectExpr(Span in_span, boolean in_parenthesized, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        super(in_span, in_parenthesized);
        if (in_extendsClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'extendsClause' to the AbstractObjectExpr constructor was null");
        }
        _extendsClause = in_extendsClause;
        if (in_decls == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'decls' to the AbstractObjectExpr constructor was null");
        }
        _decls = in_decls;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractObjectExpr(Span in_span, boolean in_parenthesized, List<Decl> in_decls) {
        this(in_span, in_parenthesized, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractObjectExpr(Span in_span, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        this(in_span, false, in_extendsClause, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractObjectExpr(Span in_span, List<Decl> in_decls) {
        this(in_span, false, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractObjectExpr(boolean in_parenthesized, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        this(new Span(), in_parenthesized, in_extendsClause, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractObjectExpr(boolean in_parenthesized, List<Decl> in_decls) {
        this(new Span(), in_parenthesized, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractObjectExpr(List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        this(new Span(), false, in_extendsClause, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractObjectExpr(List<Decl> in_decls) {
        this(new Span(), false, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbstractObjectExpr() {
        _extendsClause = null;
        _decls = null;
    }

    public List<TraitTypeWhere> getExtendsClause() { return _extendsClause; }
    public List<Decl> getDecls() { return _decls; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
