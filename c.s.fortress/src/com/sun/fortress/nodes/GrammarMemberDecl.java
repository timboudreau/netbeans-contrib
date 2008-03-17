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
 * Class GrammarMemberDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class GrammarMemberDecl extends AbstractNode implements AbsDecl {
    private final QualifiedIdName _name;
    private final Option<TraitType> _type;
    private final Option<? extends Modifier> _modifier;

    /**
     * Constructs a GrammarMemberDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public GrammarMemberDecl(Span in_span, QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the GrammarMemberDecl constructor was null");
        }
        _name = in_name;
        if (in_type == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'type' to the GrammarMemberDecl constructor was null");
        }
        _type = in_type;
        if (in_modifier == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'modifier' to the GrammarMemberDecl constructor was null");
        }
        _modifier = in_modifier;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public GrammarMemberDecl(QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier) {
        this(new Span(), in_name, in_type, in_modifier);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected GrammarMemberDecl() {
        _name = null;
        _type = null;
        _modifier = null;
    }

    public QualifiedIdName getName() { return _name; }
    public Option<TraitType> getType() { return _type; }
    public Option<? extends Modifier> getModifier() { return _modifier; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
