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
 * Class VarAbsDeclOrDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class VarAbsDeclOrDecl extends AbstractNode implements AbsDeclOrDecl {
    private final List<LValueBind> _lhs;

    /**
     * Constructs a VarAbsDeclOrDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public VarAbsDeclOrDecl(Span in_span, List<LValueBind> in_lhs) {
        super(in_span);
        if (in_lhs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'lhs' to the VarAbsDeclOrDecl constructor was null");
        }
        _lhs = in_lhs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarAbsDeclOrDecl(List<LValueBind> in_lhs) {
        this(new Span(), in_lhs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected VarAbsDeclOrDecl() {
        _lhs = null;
    }

    public List<LValueBind> getLhs() { return _lhs; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
