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
 * Class AbstractNode, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class AbstractNode extends UIDObject implements Node {
    private final Span _span;
    private int _hashCode;
    private boolean _hasHashCode = false;

    /**
     * Constructs a AbstractNode.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbstractNode(Span in_span) {
        super();
        if (in_span == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'span' to the AbstractNode constructor was null");
        }
        _span = in_span;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbstractNode() {
        this(new Span());
    }

    public Span getSpan() { return _span; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
    public final int hashCode() {
        if (! _hasHashCode) { _hashCode = generateHashCode(); _hasHashCode = true; }
        return _hashCode;
    }
}
