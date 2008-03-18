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
 * Class ExponentDim, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ExponentDim extends DimExpr {
    private final DimExpr _base;
    private final IntExpr _power;

    /**
     * Constructs a ExponentDim.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ExponentDim(Span in_span, boolean in_parenthesized, DimExpr in_base, IntExpr in_power) {
        super(in_span, in_parenthesized);
        if (in_base == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'base' to the ExponentDim constructor was null");
        }
        _base = in_base;
        if (in_power == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'power' to the ExponentDim constructor was null");
        }
        _power = in_power;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExponentDim(Span in_span, DimExpr in_base, IntExpr in_power) {
        this(in_span, false, in_base, in_power);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExponentDim(boolean in_parenthesized, DimExpr in_base, IntExpr in_power) {
        this(new Span(), in_parenthesized, in_base, in_power);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExponentDim(DimExpr in_base, IntExpr in_power) {
        this(new Span(), false, in_base, in_power);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ExponentDim() {
        _base = null;
        _power = null;
    }

    final public DimExpr getBase() { return _base; }
    final public IntExpr getPower() { return _power; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forExponentDim(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forExponentDim(this); }

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
        writer.print("ExponentDim:");
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

        DimExpr temp_base = getBase();
        writer.startLine();
        writer.print("base = ");
        temp_base.outputHelp(writer, lossless);

        IntExpr temp_power = getPower();
        writer.startLine();
        writer.print("power = ");
        temp_power.outputHelp(writer, lossless);
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
            ExponentDim casted = (ExponentDim) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            DimExpr temp_base = getBase();
            DimExpr casted_base = casted.getBase();
            if (!(temp_base == casted_base || temp_base.equals(casted_base))) return false;
            IntExpr temp_power = getPower();
            IntExpr casted_power = casted.getPower();
            if (!(temp_power == casted_power || temp_power.equals(casted_power))) return false;
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
        DimExpr temp_base = getBase();
        code ^= temp_base.hashCode();
        IntExpr temp_power = getPower();
        code ^= temp_power.hashCode();
        return code;
    }
}
