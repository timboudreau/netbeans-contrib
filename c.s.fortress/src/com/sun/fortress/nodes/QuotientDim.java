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
 * Class QuotientDim, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class QuotientDim extends DimExpr {
    private final DimExpr _numerator;
    private final DimExpr _denominator;

    /**
     * Constructs a QuotientDim.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public QuotientDim(Span in_span, boolean in_parenthesized, DimExpr in_numerator, DimExpr in_denominator) {
        super(in_span, in_parenthesized);
        if (in_numerator == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'numerator' to the QuotientDim constructor was null");
        }
        _numerator = in_numerator;
        if (in_denominator == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'denominator' to the QuotientDim constructor was null");
        }
        _denominator = in_denominator;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QuotientDim(Span in_span, DimExpr in_numerator, DimExpr in_denominator) {
        this(in_span, false, in_numerator, in_denominator);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QuotientDim(boolean in_parenthesized, DimExpr in_numerator, DimExpr in_denominator) {
        this(new Span(), in_parenthesized, in_numerator, in_denominator);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QuotientDim(DimExpr in_numerator, DimExpr in_denominator) {
        this(new Span(), false, in_numerator, in_denominator);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected QuotientDim() {
        _numerator = null;
        _denominator = null;
    }

    final public DimExpr getNumerator() { return _numerator; }
    final public DimExpr getDenominator() { return _denominator; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forQuotientDim(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forQuotientDim(this); }

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
        writer.print("QuotientDim:");
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

        DimExpr temp_numerator = getNumerator();
        writer.startLine();
        writer.print("numerator = ");
        temp_numerator.outputHelp(writer, lossless);

        DimExpr temp_denominator = getDenominator();
        writer.startLine();
        writer.print("denominator = ");
        temp_denominator.outputHelp(writer, lossless);
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
            QuotientDim casted = (QuotientDim) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            DimExpr temp_numerator = getNumerator();
            DimExpr casted_numerator = casted.getNumerator();
            if (!(temp_numerator == casted_numerator || temp_numerator.equals(casted_numerator))) return false;
            DimExpr temp_denominator = getDenominator();
            DimExpr casted_denominator = casted.getDenominator();
            if (!(temp_denominator == casted_denominator || temp_denominator.equals(casted_denominator))) return false;
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
        DimExpr temp_numerator = getNumerator();
        code ^= temp_numerator.hashCode();
        DimExpr temp_denominator = getDenominator();
        code ^= temp_denominator.hashCode();
        return code;
    }
}
