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
 * Class TerminalDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class TerminalDecl extends GrammarMemberDecl {
    private final SyntaxDef _syntaxDef;

    /**
     * Constructs a TerminalDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TerminalDecl(Span in_span, QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier, SyntaxDef in_syntaxDef) {
        super(in_span, in_name, in_type, in_modifier);
        if (in_syntaxDef == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'syntaxDef' to the TerminalDecl constructor was null");
        }
        _syntaxDef = in_syntaxDef;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TerminalDecl(QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier, SyntaxDef in_syntaxDef) {
        this(new Span(), in_name, in_type, in_modifier, in_syntaxDef);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TerminalDecl() {
        _syntaxDef = null;
    }

    public SyntaxDef getSyntaxDef() { return _syntaxDef; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
