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
 * Class WhereType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class WhereType extends WhereBinding {
    private final List<TraitType> _supers;

    /**
     * Constructs a WhereType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public WhereType(Span in_span, Id in_name, List<TraitType> in_supers) {
        super(in_span, in_name);
        if (in_supers == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'supers' to the WhereType constructor was null");
        }
        _supers = in_supers;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public WhereType(Id in_name, List<TraitType> in_supers) {
        this(new Span(), in_name, in_supers);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected WhereType() {
        _supers = null;
    }

    final public List<TraitType> getSupers() { return _supers; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forWhereType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forWhereType(this); }

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
        writer.print("WhereType:");
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

        List<TraitType> temp_supers = getSupers();
        writer.startLine();
        writer.print("supers = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_supers = true;
        for (TraitType elt_temp_supers : temp_supers) {
            isempty_temp_supers = false;
            writer.startLine("* ");
            if (elt_temp_supers == null) {
                writer.print("null");
            } else {
                elt_temp_supers.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_supers) writer.print(" }");
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
            WhereType casted = (WhereType) obj;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<TraitType> temp_supers = getSupers();
            List<TraitType> casted_supers = casted.getSupers();
            if (!(temp_supers == casted_supers || temp_supers.equals(casted_supers))) return false;
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
        List<TraitType> temp_supers = getSupers();
        code ^= temp_supers.hashCode();
        return code;
    }
}
