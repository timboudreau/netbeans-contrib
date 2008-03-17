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
 * Class Assignment, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Assignment extends Expr {
    private final List<LHS> _lhs;
    private final Option<Op> _opr;
    private final Expr _rhs;

    /**
     * Constructs a Assignment.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Assignment(Span in_span, boolean in_parenthesized, List<LHS> in_lhs, Option<Op> in_opr, Expr in_rhs) {
        super(in_span, in_parenthesized);
        if (in_lhs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'lhs' to the Assignment constructor was null");
        }
        _lhs = in_lhs;
        if (in_opr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'opr' to the Assignment constructor was null");
        }
        _opr = in_opr;
        if (in_rhs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'rhs' to the Assignment constructor was null");
        }
        _rhs = in_rhs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Assignment(Span in_span, List<LHS> in_lhs, Option<Op> in_opr, Expr in_rhs) {
        this(in_span, false, in_lhs, in_opr, in_rhs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Assignment(boolean in_parenthesized, List<LHS> in_lhs, Option<Op> in_opr, Expr in_rhs) {
        this(new Span(), in_parenthesized, in_lhs, in_opr, in_rhs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Assignment(List<LHS> in_lhs, Option<Op> in_opr, Expr in_rhs) {
        this(new Span(), false, in_lhs, in_opr, in_rhs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Assignment() {
        _lhs = null;
        _opr = null;
        _rhs = null;
    }

    final public List<LHS> getLhs() { return _lhs; }
    final public Option<Op> getOpr() { return _opr; }
    final public Expr getRhs() { return _rhs; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAssignment(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAssignment(this); }

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
        writer.print("Assignment:");
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

        List<LHS> temp_lhs = getLhs();
        writer.startLine();
        writer.print("lhs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_lhs = true;
        for (LHS elt_temp_lhs : temp_lhs) {
            isempty_temp_lhs = false;
            writer.startLine("* ");
            if (elt_temp_lhs == null) {
                writer.print("null");
            } else {
                elt_temp_lhs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_lhs) writer.print(" }");
        else writer.startLine("}");

        Option<Op> temp_opr = getOpr();
        writer.startLine();
        writer.print("opr = ");
        if (temp_opr.isSome()) {
            writer.print("(");
            Op elt_temp_opr = edu.rice.cs.plt.tuple.Option.unwrap(temp_opr);
            if (elt_temp_opr == null) {
                writer.print("null");
            } else {
                elt_temp_opr.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Expr temp_rhs = getRhs();
        writer.startLine();
        writer.print("rhs = ");
        temp_rhs.outputHelp(writer, lossless);
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
            Assignment casted = (Assignment) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<LHS> temp_lhs = getLhs();
            List<LHS> casted_lhs = casted.getLhs();
            if (!(temp_lhs == casted_lhs || temp_lhs.equals(casted_lhs))) return false;
            Option<Op> temp_opr = getOpr();
            Option<Op> casted_opr = casted.getOpr();
            if (!(temp_opr == casted_opr || temp_opr.equals(casted_opr))) return false;
            Expr temp_rhs = getRhs();
            Expr casted_rhs = casted.getRhs();
            if (!(temp_rhs == casted_rhs || temp_rhs.equals(casted_rhs))) return false;
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
        List<LHS> temp_lhs = getLhs();
        code ^= temp_lhs.hashCode();
        Option<Op> temp_opr = getOpr();
        code ^= temp_opr.hashCode();
        Expr temp_rhs = getRhs();
        code ^= temp_rhs.hashCode();
        return code;
    }
}
