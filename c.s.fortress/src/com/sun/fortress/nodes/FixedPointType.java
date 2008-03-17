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
 * Class FixedPointType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class FixedPointType extends NonArrowType {
    private final QualifiedIdName _name;
    private final Type _body;

    /**
     * Constructs a FixedPointType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public FixedPointType(Span in_span, boolean in_parenthesized, QualifiedIdName in_name, Type in_body) {
        super(in_span, in_parenthesized);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the FixedPointType constructor was null");
        }
        _name = in_name;
        if (in_body == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'body' to the FixedPointType constructor was null");
        }
        _body = in_body;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FixedPointType(Span in_span, QualifiedIdName in_name, Type in_body) {
        this(in_span, false, in_name, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FixedPointType(boolean in_parenthesized, QualifiedIdName in_name, Type in_body) {
        this(new Span(), in_parenthesized, in_name, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public FixedPointType(QualifiedIdName in_name, Type in_body) {
        this(new Span(), false, in_name, in_body);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected FixedPointType() {
        _name = null;
        _body = null;
    }

    final public QualifiedIdName getName() { return _name; }
    final public Type getBody() { return _body; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forFixedPointType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forFixedPointType(this); }

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
        writer.print("FixedPointType:");
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

        QualifiedIdName temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        Type temp_body = getBody();
        writer.startLine();
        writer.print("body = ");
        temp_body.outputHelp(writer, lossless);
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
            FixedPointType casted = (FixedPointType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            QualifiedIdName temp_name = getName();
            QualifiedIdName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Type temp_body = getBody();
            Type casted_body = casted.getBody();
            if (!(temp_body == casted_body || temp_body.equals(casted_body))) return false;
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
        QualifiedIdName temp_name = getName();
        code ^= temp_name.hashCode();
        Type temp_body = getBody();
        code ^= temp_body.hashCode();
        return code;
    }
}
