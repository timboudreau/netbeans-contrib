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
 * Class BoolConstraintExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class BoolConstraintExpr extends WhereConstraint {
    private final BoolConstraint _constraint;

    /**
     * Constructs a BoolConstraintExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public BoolConstraintExpr(Span in_span, BoolConstraint in_constraint) {
        super(in_span);
        if (in_constraint == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'constraint' to the BoolConstraintExpr constructor was null");
        }
        _constraint = in_constraint;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public BoolConstraintExpr(BoolConstraint in_constraint) {
        this(new Span(), in_constraint);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected BoolConstraintExpr() {
        _constraint = null;
    }

    final public BoolConstraint getConstraint() { return _constraint; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forBoolConstraintExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forBoolConstraintExpr(this); }

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
        writer.print("BoolConstraintExpr:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        BoolConstraint temp_constraint = getConstraint();
        writer.startLine();
        writer.print("constraint = ");
        temp_constraint.outputHelp(writer, lossless);
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
            BoolConstraintExpr casted = (BoolConstraintExpr) obj;
            BoolConstraint temp_constraint = getConstraint();
            BoolConstraint casted_constraint = casted.getConstraint();
            if (!(temp_constraint == casted_constraint || temp_constraint.equals(casted_constraint))) return false;
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
        BoolConstraint temp_constraint = getConstraint();
        code ^= temp_constraint.hashCode();
        return code;
    }
}
