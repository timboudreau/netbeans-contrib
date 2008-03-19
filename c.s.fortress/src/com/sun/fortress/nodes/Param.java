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
 * Class Param, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class Param extends AbstractNode {
    private final List<Modifier> _mods;
    private final Id _name;

    /**
     * Constructs a Param.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Param(Span in_span, List<Modifier> in_mods, Id in_name) {
        super(in_span);
        if (in_mods == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'mods' to the Param constructor was null");
        }
        _mods = in_mods;
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the Param constructor was null");
        }
        _name = in_name;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Param(Span in_span, Id in_name) {
        this(in_span, Collections.<Modifier>emptyList(), in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Param(List<Modifier> in_mods, Id in_name) {
        this(new Span(), in_mods, in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Param(Id in_name) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Param() {
        _mods = null;
        _name = null;
    }

    public List<Modifier> getMods() { return _mods; }
    public Id getName() { return _name; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
