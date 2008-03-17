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
 * Class KeywordType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class KeywordType extends AbstractNode {
    private final Id _name;
    private final Type _type;

    /**
     * Constructs a KeywordType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public KeywordType(Span in_span, Id in_name, Type in_type) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the KeywordType constructor was null");
        }
        _name = in_name;
        if (in_type == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'type' to the KeywordType constructor was null");
        }
        _type = in_type;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public KeywordType(Id in_name, Type in_type) {
        this(new Span(), in_name, in_type);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected KeywordType() {
        _name = null;
        _type = null;
    }

    final public Id getName() { return _name; }
    final public Type getType() { return _type; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forKeywordType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forKeywordType(this); }

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
        writer.print("KeywordType:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Id temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        Type temp_type = getType();
        writer.startLine();
        writer.print("type = ");
        temp_type.outputHelp(writer, lossless);
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
            KeywordType casted = (KeywordType) obj;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Type temp_type = getType();
            Type casted_type = casted.getType();
            if (!(temp_type == casted_type || temp_type.equals(casted_type))) return false;
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
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        Type temp_type = getType();
        code ^= temp_type.hashCode();
        return code;
    }
}
