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
 * Class Do, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Do extends DelimitedExpr {
    private final List<DoFront> _fronts;

    /**
     * Constructs a Do.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Do(Span in_span, boolean in_parenthesized, List<DoFront> in_fronts) {
        super(in_span, in_parenthesized);
        if (in_fronts == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'fronts' to the Do constructor was null");
        }
        _fronts = in_fronts;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Do(Span in_span, List<DoFront> in_fronts) {
        this(in_span, false, in_fronts);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Do(boolean in_parenthesized, List<DoFront> in_fronts) {
        this(new Span(), in_parenthesized, in_fronts);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Do(List<DoFront> in_fronts) {
        this(new Span(), false, in_fronts);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Do() {
        _fronts = null;
    }

    final public List<DoFront> getFronts() { return _fronts; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forDo(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forDo(this); }

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
        writer.print("Do:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        boolean temp_parenthesized = isParenthesized();
        writer.startLine();
        writer.print("parenthesized = ");
        writer.print(temp_parenthesized);

        List<DoFront> temp_fronts = getFronts();
        writer.startLine();
        writer.print("fronts = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_fronts = true;
        for (DoFront elt_temp_fronts : temp_fronts) {
            isempty_temp_fronts = false;
            writer.startLine("* ");
            if (elt_temp_fronts == null) {
                writer.print("null");
            } else {
                elt_temp_fronts.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_fronts) writer.print(" }");
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
            Do casted = (Do) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<DoFront> temp_fronts = getFronts();
            List<DoFront> casted_fronts = casted.getFronts();
            if (!(temp_fronts == casted_fronts || temp_fronts.equals(casted_fronts))) return false;
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
        boolean temp_parenthesized = isParenthesized();
        code ^= temp_parenthesized ? 1231 : 1237;
        List<DoFront> temp_fronts = getFronts();
        code ^= temp_fronts.hashCode();
        return code;
    }
}
