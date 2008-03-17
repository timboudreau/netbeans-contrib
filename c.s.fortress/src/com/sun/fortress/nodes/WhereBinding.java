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
 * Class WhereBinding, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class WhereBinding extends AbstractNode {
    private final Id _name;

    /**
     * Constructs a WhereBinding.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public WhereBinding(Span in_span, Id in_name) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the WhereBinding constructor was null");
        }
        _name = in_name;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public WhereBinding(Id in_name) {
        this(new Span(), in_name);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected WhereBinding() {
        _name = null;
    }

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
