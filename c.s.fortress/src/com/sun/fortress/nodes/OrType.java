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
 * Class OrType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class OrType extends NonArrowType {
    private final Type _first;
    private final Type _second;

    /**
     * Constructs a OrType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public OrType(Span in_span, boolean in_parenthesized, Type in_first, Type in_second) {
        super(in_span, in_parenthesized);
        if (in_first == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'first' to the OrType constructor was null");
        }
        _first = in_first;
        if (in_second == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'second' to the OrType constructor was null");
        }
        _second = in_second;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OrType(Span in_span, Type in_first, Type in_second) {
        this(in_span, false, in_first, in_second);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OrType(boolean in_parenthesized, Type in_first, Type in_second) {
        this(new Span(), in_parenthesized, in_first, in_second);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OrType(Type in_first, Type in_second) {
        this(new Span(), false, in_first, in_second);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected OrType() {
        _first = null;
        _second = null;
    }

    final public Type getFirst() { return _first; }
    final public Type getSecond() { return _second; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forOrType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forOrType(this); }

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
        writer.print("OrType:");
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

        Type temp_first = getFirst();
        writer.startLine();
        writer.print("first = ");
        temp_first.outputHelp(writer, lossless);

        Type temp_second = getSecond();
        writer.startLine();
        writer.print("second = ");
        temp_second.outputHelp(writer, lossless);
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
            OrType casted = (OrType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Type temp_first = getFirst();
            Type casted_first = casted.getFirst();
            if (!(temp_first == casted_first || temp_first.equals(casted_first))) return false;
            Type temp_second = getSecond();
            Type casted_second = casted.getSecond();
            if (!(temp_second == casted_second || temp_second.equals(casted_second))) return false;
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
        Type temp_first = getFirst();
        code ^= temp_first.hashCode();
        Type temp_second = getSecond();
        code ^= temp_second.hashCode();
        return code;
    }
}
