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
 * Class InferenceVarType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class InferenceVarType extends NonArrowType {
    private final Object _id;
    private final int _index;

    /**
     * Constructs a InferenceVarType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public InferenceVarType(Span in_span, boolean in_parenthesized, Object in_id, int in_index) {
        super(in_span, in_parenthesized);
        if (in_id == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'id' to the InferenceVarType constructor was null");
        }
        _id = in_id;
        _index = in_index;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InferenceVarType(Span in_span, boolean in_parenthesized, Object in_id) {
        this(in_span, in_parenthesized, in_id, -1);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InferenceVarType(Span in_span, Object in_id, int in_index) {
        this(in_span, false, in_id, in_index);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InferenceVarType(Span in_span, Object in_id) {
        this(in_span, false, in_id, -1);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InferenceVarType(boolean in_parenthesized, Object in_id, int in_index) {
        this(new Span(), in_parenthesized, in_id, in_index);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InferenceVarType(boolean in_parenthesized, Object in_id) {
        this(new Span(), in_parenthesized, in_id, -1);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InferenceVarType(Object in_id, int in_index) {
        this(new Span(), false, in_id, in_index);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InferenceVarType(Object in_id) {
        this(new Span(), false, in_id, -1);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected InferenceVarType() {
        _id = null;
        _index = 0;
    }

    final public Object getId() { return _id; }
    final public int getIndex() { return _index; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forInferenceVarType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forInferenceVarType(this); }

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
        writer.print("InferenceVarType:");
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

        Object temp_id = getId();
        writer.startLine();
        writer.print("id = ");
        if (lossless) {
            writer.printSerialized(temp_id);
            writer.print(" ");
            writer.printEscaped(temp_id);
        } else { writer.print(temp_id); }

        int temp_index = getIndex();
        writer.startLine();
        writer.print("index = ");
        writer.print(temp_index);
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
            InferenceVarType casted = (InferenceVarType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Object temp_id = getId();
            Object casted_id = casted.getId();
            if (!(temp_id == casted_id || temp_id.equals(casted_id))) return false;
            int temp_index = getIndex();
            int casted_index = casted.getIndex();
            if (!(temp_index == casted_index)) return false;
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
        Object temp_id = getId();
        code ^= temp_id.hashCode();
        int temp_index = getIndex();
        code ^= temp_index;
        return code;
    }
}
