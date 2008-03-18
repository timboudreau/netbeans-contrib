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
 * Class QualifiedName, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class QualifiedName extends Name {
    private final Option<APIName> _api;
    private final SimpleName _name;

    /**
     * Constructs a QualifiedName.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public QualifiedName(Span in_span, Option<APIName> in_api, SimpleName in_name) {
        super(in_span);
        if (in_api == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'api' to the QualifiedName constructor was null");
        }
        _api = in_api;
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the QualifiedName constructor was null");
        }
        _name = in_name;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QualifiedName(Span in_span, SimpleName in_name) {
        this(in_span, Option.<APIName>none(), in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QualifiedName(Option<APIName> in_api, SimpleName in_name) {
        this(new Span(), in_api, in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QualifiedName(SimpleName in_name) {
        this(new Span(), Option.<APIName>none(), in_name);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected QualifiedName() {
        _api = null;
        _name = null;
    }

    public Option<APIName> getApi() { return _api; }
    public SimpleName getName() { return _name; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
