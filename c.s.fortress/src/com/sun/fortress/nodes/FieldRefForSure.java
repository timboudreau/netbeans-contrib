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
 * Class FieldRefForSure, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class FieldRefForSure extends AbstractFieldRef {
    private final Id _field;

    /**
     * Constructs a FieldRefForSure.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FieldRefForSure(Span in_span, boolean in_parenthesized, Expr in_obj, Id in_field) {
        super(in_span, in_parenthesized, in_obj);
        if (in_field == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'field' to the FieldRefForSure constructor was null");
        }
        _field = in_field;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FieldRefForSure(Span in_span, Expr in_obj, Id in_field) {
        this(in_span, false, in_obj, in_field);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FieldRefForSure(boolean in_parenthesized, Expr in_obj, Id in_field) {
        this(new Span(), in_parenthesized, in_obj, in_field);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FieldRefForSure(Expr in_obj, Id in_field) {
        this(new Span(), false, in_obj, in_field);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected FieldRefForSure() {
        _field = null;
    }

    final public Id getField() { return _field; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forFieldRefForSure(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forFieldRefForSure(this); }

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
        writer.print("FieldRefForSure:");
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

        Expr temp_obj = getObj();
        writer.startLine();
        writer.print("obj = ");
        temp_obj.outputHelp(writer, lossless);

        Id temp_field = getField();
        writer.startLine();
        writer.print("field = ");
        temp_field.outputHelp(writer, lossless);
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
            FieldRefForSure casted = (FieldRefForSure) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_obj = getObj();
            Expr casted_obj = casted.getObj();
            if (!(temp_obj == casted_obj || temp_obj.equals(casted_obj))) return false;
            Id temp_field = getField();
            Id casted_field = casted.getField();
            if (!(temp_field == casted_field || temp_field.equals(casted_field))) return false;
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
        Expr temp_obj = getObj();
        code ^= temp_obj.hashCode();
        Id temp_field = getField();
        code ^= temp_field.hashCode();
        return code;
    }
}
