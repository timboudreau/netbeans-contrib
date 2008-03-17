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
 * Class IntLiteralExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class IntLiteralExpr extends NumberLiteralExpr {
    private final BigInteger _val;

    /**
     * Constructs a IntLiteralExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public IntLiteralExpr(Span in_span, boolean in_parenthesized, String in_text, BigInteger in_val) {
        super(in_span, in_parenthesized, in_text);
        if (in_val == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'val' to the IntLiteralExpr constructor was null");
        }
        _val = in_val;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntLiteralExpr(Span in_span, boolean in_parenthesized, BigInteger in_val) {
        this(in_span, in_parenthesized, in_val.toString(), in_val);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntLiteralExpr(Span in_span, String in_text, BigInteger in_val) {
        this(in_span, false, in_text, in_val);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntLiteralExpr(Span in_span, BigInteger in_val) {
        this(in_span, false, in_val.toString(), in_val);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntLiteralExpr(boolean in_parenthesized, String in_text, BigInteger in_val) {
        this(new Span(), in_parenthesized, in_text, in_val);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntLiteralExpr(boolean in_parenthesized, BigInteger in_val) {
        this(new Span(), in_parenthesized, in_val.toString(), in_val);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntLiteralExpr(String in_text, BigInteger in_val) {
        this(new Span(), false, in_text, in_val);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public IntLiteralExpr(BigInteger in_val) {
        this(new Span(), false, in_val.toString(), in_val);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected IntLiteralExpr() {
        _val = null;
    }

    final public BigInteger getVal() { return _val; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forIntLiteralExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forIntLiteralExpr(this); }

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
        writer.print("IntLiteralExpr:");
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

        BigInteger temp_val = getVal();
        writer.startLine();
        writer.print("val = ");
        if (lossless) {
            writer.printSerialized(temp_val);
            writer.print(" ");
            writer.printEscaped(temp_val);
        } else { writer.print(temp_val); }
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
            IntLiteralExpr casted = (IntLiteralExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            String temp_text = getText();
            String casted_text = casted.getText();
            if (!(temp_text == casted_text)) return false;
            BigInteger temp_val = getVal();
            BigInteger casted_val = casted.getVal();
            if (!(temp_val == casted_val || temp_val.equals(casted_val))) return false;
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
        String temp_text = getText();
        code ^= temp_text.hashCode();
        BigInteger temp_val = getVal();
        code ^= temp_val.hashCode();
        return code;
    }
}
