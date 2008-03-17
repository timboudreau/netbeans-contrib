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
 * Class Indices, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Indices extends AbstractNode {
    private final List<ExtentRange> _extents;

    /**
     * Constructs a Indices.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Indices(Span in_span, List<ExtentRange> in_extents) {
        super(in_span);
        if (in_extents == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'extents' to the Indices constructor was null");
        }
        _extents = in_extents;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Indices(List<ExtentRange> in_extents) {
        this(new Span(), in_extents);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Indices() {
        _extents = null;
    }

    final public List<ExtentRange> getExtents() { return _extents; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forIndices(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forIndices(this); }

    /** Generate a human-readable representation that can be deserialized. */
    public java.lang.String serialize() {
        java.io.StringWriter w = new java.io.StringWriter();
        serialize(w);
        return w.toString();
    }
    /** Generate a human-readable representation that can be deserialized. */
    public void serialize(java.io.Writer writer) {
        outputHelp(new TabPrintWriter(writer, 2), true);
    }

    public void outputHelp(TabPrintWriter writer, boolean lossless) {
        writer.print("Indices:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<ExtentRange> temp_extents = getExtents();
        writer.startLine();
        writer.print("extents = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_extents = true;
        for (ExtentRange elt_temp_extents : temp_extents) {
            isempty_temp_extents = false;
            writer.startLine("* ");
            if (elt_temp_extents == null) {
                writer.print("null");
            } else {
                elt_temp_extents.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_extents) writer.print(" }");
        else writer.startLine("}");
        writer.unindent();
    }

    /**
     * Implementation of equals that is based on the values of the fields of the
     * object. Thus, two objects created with identical parameters will be equal.
     */
    public boolean equals(java.lang.Object obj) {
        if (obj == null) return false;
        if ((obj.getClass() != this.getClass()) || (obj.hashCode() != this.hashCode())) {
            return false;
        } else {
            Indices casted = (Indices) obj;
            List<ExtentRange> temp_extents = getExtents();
            List<ExtentRange> casted_extents = casted.getExtents();
            if (!(temp_extents == casted_extents || temp_extents.equals(casted_extents))) return false;
            return true;
        }
    }

    /**
     * Implementation of hashCode that is consistent with equals.  The value of
     * the hashCode is formed by XORing the hashcode of the class object with
     * the hashcodes of all the fields of the object.
     */
    public int generateHashCode() {
        int code = getClass().hashCode();
        List<ExtentRange> temp_extents = getExtents();
        code ^= temp_extents.hashCode();
        return code;
    }
}
