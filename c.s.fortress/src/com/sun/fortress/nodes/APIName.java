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
 * Class APIName, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class APIName extends Name {
    private final List<Id> _ids;

    /**
     * Constructs a APIName.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public APIName(Span in_span, List<Id> in_ids) {
        super(in_span);
        if (in_ids == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'ids' to the APIName constructor was null");
        }
        _ids = in_ids;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public APIName(List<Id> in_ids) {
        this(new Span(), in_ids);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected APIName() {
        _ids = null;
    }

    final public List<Id> getIds() { return _ids; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAPIName(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAPIName(this); }

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
        writer.print("APIName:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<Id> temp_ids = getIds();
        writer.startLine();
        writer.print("ids = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_ids = true;
        for (Id elt_temp_ids : temp_ids) {
            isempty_temp_ids = false;
            writer.startLine("* ");
            if (elt_temp_ids == null) {
                writer.print("null");
            } else {
                elt_temp_ids.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_ids) writer.print(" }");
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
            APIName casted = (APIName) obj;
            List<Id> temp_ids = getIds();
            List<Id> casted_ids = casted.getIds();
            if (!(temp_ids == casted_ids || temp_ids.equals(casted_ids))) return false;
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
        List<Id> temp_ids = getIds();
        code ^= temp_ids.hashCode();
        return code;
    }
}
