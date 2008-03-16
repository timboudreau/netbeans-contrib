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
 * Class FloatLiteralExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class FloatLiteralExpr extends NumberLiteralExpr {
    private final BigInteger _intPart;
    private final BigInteger _numerator;
    private final int _denomBase;
    private final int _denomPower;

    /**
     * Constructs a FloatLiteralExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FloatLiteralExpr(Span in_span, boolean in_parenthesized, String in_text, BigInteger in_intPart, BigInteger in_numerator, int in_denomBase, int in_denomPower) {
        super(in_span, in_parenthesized, in_text);
        if (in_intPart == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'intPart' to the FloatLiteralExpr constructor was null");
        }
        _intPart = in_intPart;
        if (in_numerator == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'numerator' to the FloatLiteralExpr constructor was null");
        }
        _numerator = in_numerator;
        _denomBase = in_denomBase;
        _denomPower = in_denomPower;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FloatLiteralExpr(Span in_span, String in_text, BigInteger in_intPart, BigInteger in_numerator, int in_denomBase, int in_denomPower) {
        this(in_span, false, in_text, in_intPart, in_numerator, in_denomBase, in_denomPower);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FloatLiteralExpr(boolean in_parenthesized, String in_text, BigInteger in_intPart, BigInteger in_numerator, int in_denomBase, int in_denomPower) {
        this(new Span(), in_parenthesized, in_text, in_intPart, in_numerator, in_denomBase, in_denomPower);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FloatLiteralExpr(String in_text, BigInteger in_intPart, BigInteger in_numerator, int in_denomBase, int in_denomPower) {
        this(new Span(), false, in_text, in_intPart, in_numerator, in_denomBase, in_denomPower);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected FloatLiteralExpr() {
        _intPart = null;
        _numerator = null;
        _denomBase = 0;
        _denomPower = 0;
    }

    final public BigInteger getIntPart() { return _intPart; }
    final public BigInteger getNumerator() { return _numerator; }
    final public int getDenomBase() { return _denomBase; }
    final public int getDenomPower() { return _denomPower; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forFloatLiteralExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forFloatLiteralExpr(this); }

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
        writer.print("FloatLiteralExpr:");
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

        String temp_text = getText();
        writer.startLine();
        writer.print("text = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_text);
            writer.print("\"");
        } else { writer.print(temp_text); }

        BigInteger temp_intPart = getIntPart();
        writer.startLine();
        writer.print("intPart = ");
        if (lossless) {
            writer.printSerialized(temp_intPart);
            writer.print(" ");
            writer.printEscaped(temp_intPart);
        } else { writer.print(temp_intPart); }

        BigInteger temp_numerator = getNumerator();
        writer.startLine();
        writer.print("numerator = ");
        if (lossless) {
            writer.printSerialized(temp_numerator);
            writer.print(" ");
            writer.printEscaped(temp_numerator);
        } else { writer.print(temp_numerator); }

        int temp_denomBase = getDenomBase();
        writer.startLine();
        writer.print("denomBase = ");
        writer.print(temp_denomBase);

        int temp_denomPower = getDenomPower();
        writer.startLine();
        writer.print("denomPower = ");
        writer.print(temp_denomPower);
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
            FloatLiteralExpr casted = (FloatLiteralExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            BigInteger temp_intPart = getIntPart();
            BigInteger casted_intPart = casted.getIntPart();
            if (!(temp_intPart == casted_intPart || temp_intPart.equals(casted_intPart))) return false;
            BigInteger temp_numerator = getNumerator();
            BigInteger casted_numerator = casted.getNumerator();
            if (!(temp_numerator == casted_numerator || temp_numerator.equals(casted_numerator))) return false;
            int temp_denomBase = getDenomBase();
            int casted_denomBase = casted.getDenomBase();
            if (!(temp_denomBase == casted_denomBase)) return false;
            int temp_denomPower = getDenomPower();
            int casted_denomPower = casted.getDenomPower();
            if (!(temp_denomPower == casted_denomPower)) return false;
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
        BigInteger temp_intPart = getIntPart();
        code ^= temp_intPart.hashCode();
        BigInteger temp_numerator = getNumerator();
        code ^= temp_numerator.hashCode();
        int temp_denomBase = getDenomBase();
        code ^= temp_denomBase;
        int temp_denomPower = getDenomPower();
        code ^= temp_denomPower;
        return code;
    }
}
