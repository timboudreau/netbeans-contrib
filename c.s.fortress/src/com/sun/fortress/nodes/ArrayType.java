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
 * Class ArrayType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ArrayType extends AbbreviatedType {
    private final Indices _indices;

    /**
     * Constructs a ArrayType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ArrayType(Span in_span, boolean in_parenthesized, Type in_element, Indices in_indices) {
        super(in_span, in_parenthesized, in_element);
        if (in_indices == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'indices' to the ArrayType constructor was null");
        }
        _indices = in_indices;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayType(Span in_span, Type in_element, Indices in_indices) {
        this(in_span, false, in_element, in_indices);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayType(boolean in_parenthesized, Type in_element, Indices in_indices) {
        this(new Span(), in_parenthesized, in_element, in_indices);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayType(Type in_element, Indices in_indices) {
        this(new Span(), false, in_element, in_indices);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ArrayType() {
        _indices = null;
    }

    final public Indices getIndices() { return _indices; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forArrayType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forArrayType(this); }

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
        writer.print("ArrayType:");
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

        Type temp_element = getElement();
        writer.startLine();
        writer.print("element = ");
        temp_element.outputHelp(writer, lossless);

        Indices temp_indices = getIndices();
        writer.startLine();
        writer.print("indices = ");
        temp_indices.outputHelp(writer, lossless);
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
            ArrayType casted = (ArrayType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Type temp_element = getElement();
            Type casted_element = casted.getElement();
            if (!(temp_element == casted_element || temp_element.equals(casted_element))) return false;
            Indices temp_indices = getIndices();
            Indices casted_indices = casted.getIndices();
            if (!(temp_indices == casted_indices || temp_indices.equals(casted_indices))) return false;
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
        Type temp_element = getElement();
        code ^= temp_element.hashCode();
        Indices temp_indices = getIndices();
        code ^= temp_indices.hashCode();
        return code;
    }
}
