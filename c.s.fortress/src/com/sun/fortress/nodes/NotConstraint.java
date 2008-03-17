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
 * Class NotConstraint, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class NotConstraint extends BoolConstraint {
    private final BoolExpr _bool;

    /**
     * Constructs a NotConstraint.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public NotConstraint(Span in_span, boolean in_parenthesized, BoolExpr in_bool) {
        super(in_span, in_parenthesized);
        if (in_bool == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'bool' to the NotConstraint constructor was null");
        }
        _bool = in_bool;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NotConstraint(Span in_span, BoolExpr in_bool) {
        this(in_span, false, in_bool);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NotConstraint(boolean in_parenthesized, BoolExpr in_bool) {
        this(new Span(), in_parenthesized, in_bool);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NotConstraint(BoolExpr in_bool) {
        this(new Span(), false, in_bool);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected NotConstraint() {
        _bool = null;
    }

    final public BoolExpr getBool() { return _bool; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forNotConstraint(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forNotConstraint(this); }

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
        writer.print("NotConstraint:");
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

        BoolExpr temp_bool = getBool();
        writer.startLine();
        writer.print("bool = ");
        temp_bool.outputHelp(writer, lossless);
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
            NotConstraint casted = (NotConstraint) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            BoolExpr temp_bool = getBool();
            BoolExpr casted_bool = casted.getBool();
            if (!(temp_bool == casted_bool || temp_bool.equals(casted_bool))) return false;
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
        BoolExpr temp_bool = getBool();
        code ^= temp_bool.hashCode();
        return code;
    }
}
