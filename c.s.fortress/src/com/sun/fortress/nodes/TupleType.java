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
 * Class TupleType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class TupleType extends AbstractTupleType {

    /**
     * Constructs a TupleType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TupleType(Span in_span, boolean in_parenthesized, List<Type> in_elements) {
        super(in_span, in_parenthesized, in_elements);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TupleType(Span in_span, List<Type> in_elements) {
        this(in_span, false, in_elements);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TupleType(boolean in_parenthesized, List<Type> in_elements) {
        this(new Span(), in_parenthesized, in_elements);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TupleType(List<Type> in_elements) {
        this(new Span(), false, in_elements);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TupleType() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forTupleType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forTupleType(this); }

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
        writer.print("TupleType:");
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

        List<Type> temp_elements = getElements();
        writer.startLine();
        writer.print("elements = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_elements = true;
        for (Type elt_temp_elements : temp_elements) {
            isempty_temp_elements = false;
            writer.startLine("* ");
            if (elt_temp_elements == null) {
                writer.print("null");
            } else {
                elt_temp_elements.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_elements) writer.print(" }");
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
            TupleType casted = (TupleType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<Type> temp_elements = getElements();
            List<Type> casted_elements = casted.getElements();
            if (!(temp_elements == casted_elements || temp_elements.equals(casted_elements))) return false;
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
        List<Type> temp_elements = getElements();
        code ^= temp_elements.hashCode();
        return code;
    }
}
