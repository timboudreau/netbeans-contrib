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
 * Class TraitTypeWhere, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class TraitTypeWhere extends AbstractNode {
    private final TraitType _type;
    private final WhereClause _where;

    /**
     * Constructs a TraitTypeWhere.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TraitTypeWhere(Span in_span, TraitType in_type, WhereClause in_where) {
        super(in_span);
        if (in_type == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'type' to the TraitTypeWhere constructor was null");
        }
        _type = in_type;
        if (in_where == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'where' to the TraitTypeWhere constructor was null");
        }
        _where = in_where;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TraitTypeWhere(TraitType in_type, WhereClause in_where) {
        this(new Span(), in_type, in_where);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TraitTypeWhere() {
        _type = null;
        _where = null;
    }

    final public TraitType getType() { return _type; }
    final public WhereClause getWhere() { return _where; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forTraitTypeWhere(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forTraitTypeWhere(this); }

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
        writer.print("TraitTypeWhere:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        TraitType temp_type = getType();
        writer.startLine();
        writer.print("type = ");
        temp_type.outputHelp(writer, lossless);

        WhereClause temp_where = getWhere();
        writer.startLine();
        writer.print("where = ");
        temp_where.outputHelp(writer, lossless);
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
            TraitTypeWhere casted = (TraitTypeWhere) obj;
            TraitType temp_type = getType();
            TraitType casted_type = casted.getType();
            if (!(temp_type == casted_type || temp_type.equals(casted_type))) return false;
            WhereClause temp_where = getWhere();
            WhereClause casted_where = casted.getWhere();
            if (!(temp_where == casted_where || temp_where.equals(casted_where))) return false;
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
        TraitType temp_type = getType();
        code ^= temp_type.hashCode();
        WhereClause temp_where = getWhere();
        code ^= temp_where.hashCode();
        return code;
    }
}
