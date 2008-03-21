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
 * Class MinusConstraint, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class MinusConstraint extends IntOpExpr {

    /**
     * Constructs a MinusConstraint.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public MinusConstraint(Span in_span, boolean in_parenthesized, IntExpr in_left, IntExpr in_right) {
        super(in_span, in_parenthesized, in_left, in_right);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MinusConstraint(Span in_span, IntExpr in_left, IntExpr in_right) {
        this(in_span, false, in_left, in_right);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MinusConstraint(boolean in_parenthesized, IntExpr in_left, IntExpr in_right) {
        this(new Span(), in_parenthesized, in_left, in_right);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MinusConstraint(IntExpr in_left, IntExpr in_right) {
        this(new Span(), false, in_left, in_right);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected MinusConstraint() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forMinusConstraint(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forMinusConstraint(this); }

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
        writer.print("MinusConstraint:");
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

        IntExpr temp_left = getLeft();
        writer.startLine();
        writer.print("left = ");
        temp_left.outputHelp(writer, lossless);

        IntExpr temp_right = getRight();
        writer.startLine();
        writer.print("right = ");
        temp_right.outputHelp(writer, lossless);
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
            MinusConstraint casted = (MinusConstraint) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            IntExpr temp_left = getLeft();
            IntExpr casted_left = casted.getLeft();
            if (!(temp_left == casted_left || temp_left.equals(casted_left))) return false;
            IntExpr temp_right = getRight();
            IntExpr casted_right = casted.getRight();
            if (!(temp_right == casted_right || temp_right.equals(casted_right))) return false;
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
        IntExpr temp_left = getLeft();
        code ^= temp_left.hashCode();
        IntExpr temp_right = getRight();
        code ^= temp_right.hashCode();
        return code;
    }
}