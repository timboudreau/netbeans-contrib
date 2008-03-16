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
 * Class TaggedUnitType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class TaggedUnitType extends DimType {
    private final Expr _unit;

    /**
     * Constructs a TaggedUnitType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TaggedUnitType(Span in_span, boolean in_parenthesized, Type in_type, Expr in_unit) {
        super(in_span, in_parenthesized, in_type);
        if (in_unit == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'unit' to the TaggedUnitType constructor was null");
        }
        _unit = in_unit;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TaggedUnitType(Span in_span, Type in_type, Expr in_unit) {
        this(in_span, false, in_type, in_unit);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TaggedUnitType(boolean in_parenthesized, Type in_type, Expr in_unit) {
        this(new Span(), in_parenthesized, in_type, in_unit);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TaggedUnitType(Type in_type, Expr in_unit) {
        this(new Span(), false, in_type, in_unit);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TaggedUnitType() {
        _unit = null;
    }

    final public Expr getUnit() { return _unit; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forTaggedUnitType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forTaggedUnitType(this); }

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
        writer.print("TaggedUnitType:");
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

        Type temp_type = getType();
        writer.startLine();
        writer.print("type = ");
        temp_type.outputHelp(writer, lossless);

        Expr temp_unit = getUnit();
        writer.startLine();
        writer.print("unit = ");
        temp_unit.outputHelp(writer, lossless);
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
            TaggedUnitType casted = (TaggedUnitType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Type temp_type = getType();
            Type casted_type = casted.getType();
            if (!(temp_type == casted_type || temp_type.equals(casted_type))) return false;
            Expr temp_unit = getUnit();
            Expr casted_unit = casted.getUnit();
            if (!(temp_unit == casted_unit || temp_unit.equals(casted_unit))) return false;
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
        Type temp_type = getType();
        code ^= temp_type.hashCode();
        Expr temp_unit = getUnit();
        code ^= temp_unit.hashCode();
        return code;
    }
}
