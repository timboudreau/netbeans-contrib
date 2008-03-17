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
 * Class MathPrimary, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class MathPrimary extends Primary {
    private final Expr _front;
    private final List<MathItem> _rest;

    /**
     * Constructs a MathPrimary.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public MathPrimary(Span in_span, boolean in_parenthesized, Expr in_front, List<MathItem> in_rest) {
        super(in_span, in_parenthesized);
        if (in_front == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'front' to the MathPrimary constructor was null");
        }
        _front = in_front;
        if (in_rest == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'rest' to the MathPrimary constructor was null");
        }
        _rest = in_rest;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MathPrimary(Span in_span, Expr in_front, List<MathItem> in_rest) {
        this(in_span, false, in_front, in_rest);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MathPrimary(boolean in_parenthesized, Expr in_front, List<MathItem> in_rest) {
        this(new Span(), in_parenthesized, in_front, in_rest);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MathPrimary(Expr in_front, List<MathItem> in_rest) {
        this(new Span(), false, in_front, in_rest);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected MathPrimary() {
        _front = null;
        _rest = null;
    }

    final public Expr getFront() { return _front; }
    final public List<MathItem> getRest() { return _rest; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forMathPrimary(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forMathPrimary(this); }

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
        writer.print("MathPrimary:");
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

        Expr temp_front = getFront();
        writer.startLine();
        writer.print("front = ");
        temp_front.outputHelp(writer, lossless);

        List<MathItem> temp_rest = getRest();
        writer.startLine();
        writer.print("rest = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_rest = true;
        for (MathItem elt_temp_rest : temp_rest) {
            isempty_temp_rest = false;
            writer.startLine("* ");
            if (elt_temp_rest == null) {
                writer.print("null");
            } else {
                elt_temp_rest.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_rest) writer.print(" }");
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
            MathPrimary casted = (MathPrimary) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_front = getFront();
            Expr casted_front = casted.getFront();
            if (!(temp_front == casted_front || temp_front.equals(casted_front))) return false;
            List<MathItem> temp_rest = getRest();
            List<MathItem> casted_rest = casted.getRest();
            if (!(temp_rest == casted_rest || temp_rest.equals(casted_rest))) return false;
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
        Expr temp_front = getFront();
        code ^= temp_front.hashCode();
        List<MathItem> temp_rest = getRest();
        code ^= temp_rest.hashCode();
        return code;
    }
}
