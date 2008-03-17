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
 * Class WhereClause, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class WhereClause extends AbstractNode {
    private final List<WhereBinding> _bindings;
    private final List<WhereConstraint> _constraints;

    /**
     * Constructs a WhereClause.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public WhereClause(Span in_span, List<WhereBinding> in_bindings, List<WhereConstraint> in_constraints) {
        super(in_span);
        if (in_bindings == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'bindings' to the WhereClause constructor was null");
        }
        _bindings = in_bindings;
        if (in_constraints == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'constraints' to the WhereClause constructor was null");
        }
        _constraints = in_constraints;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public WhereClause(Span in_span, List<WhereBinding> in_bindings) {
        this(in_span, in_bindings, Collections.<WhereConstraint>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public WhereClause(Span in_span) {
        this(in_span, Collections.<WhereBinding>emptyList(), Collections.<WhereConstraint>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public WhereClause(List<WhereBinding> in_bindings, List<WhereConstraint> in_constraints) {
        this(new Span(), in_bindings, in_constraints);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public WhereClause(List<WhereBinding> in_bindings) {
        this(new Span(), in_bindings, Collections.<WhereConstraint>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public WhereClause() {
        this(new Span(), Collections.<WhereBinding>emptyList(), Collections.<WhereConstraint>emptyList());
    }

    final public List<WhereBinding> getBindings() { return _bindings; }
    final public List<WhereConstraint> getConstraints() { return _constraints; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forWhereClause(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forWhereClause(this); }

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
        writer.print("WhereClause:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<WhereBinding> temp_bindings = getBindings();
        writer.startLine();
        writer.print("bindings = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_bindings = true;
        for (WhereBinding elt_temp_bindings : temp_bindings) {
            isempty_temp_bindings = false;
            writer.startLine("* ");
            if (elt_temp_bindings == null) {
                writer.print("null");
            } else {
                elt_temp_bindings.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_bindings) writer.print(" }");
        else writer.startLine("}");

        List<WhereConstraint> temp_constraints = getConstraints();
        writer.startLine();
        writer.print("constraints = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_constraints = true;
        for (WhereConstraint elt_temp_constraints : temp_constraints) {
            isempty_temp_constraints = false;
            writer.startLine("* ");
            if (elt_temp_constraints == null) {
                writer.print("null");
            } else {
                elt_temp_constraints.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_constraints) writer.print(" }");
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
            WhereClause casted = (WhereClause) obj;
            List<WhereBinding> temp_bindings = getBindings();
            List<WhereBinding> casted_bindings = casted.getBindings();
            if (!(temp_bindings == casted_bindings || temp_bindings.equals(casted_bindings))) return false;
            List<WhereConstraint> temp_constraints = getConstraints();
            List<WhereConstraint> casted_constraints = casted.getConstraints();
            if (!(temp_constraints == casted_constraints || temp_constraints.equals(casted_constraints))) return false;
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
        List<WhereBinding> temp_bindings = getBindings();
        code ^= temp_bindings.hashCode();
        List<WhereConstraint> temp_constraints = getConstraints();
        code ^= temp_constraints.hashCode();
        return code;
    }
}
