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
 * Class ImportedNames, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class ImportedNames extends Import {
    private final APIName _api;

    /**
     * Constructs a ImportedNames.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ImportedNames(Span in_span, APIName in_api) {
        super(in_span);
        if (in_api == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'api' to the ImportedNames constructor was null");
        }
        _api = in_api;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ImportedNames(APIName in_api) {
        this(new Span(), in_api);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ImportedNames() {
        _api = null;
    }

    public APIName getApi() { return _api; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
