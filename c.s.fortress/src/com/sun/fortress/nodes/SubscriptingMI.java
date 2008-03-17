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
 * Class SubscriptingMI, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class SubscriptingMI extends NonExprMI {
    private final Enclosing _op;
    private final List<Expr> _exprs;

    /**
     * Constructs a SubscriptingMI.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public SubscriptingMI(Span in_span, Enclosing in_op, List<Expr> in_exprs) {
        super(in_span);
        if (in_op == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'op' to the SubscriptingMI constructor was null");
        }
        _op = in_op;
        if (in_exprs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'exprs' to the SubscriptingMI constructor was null");
        }
        _exprs = in_exprs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptingMI(Enclosing in_op, List<Expr> in_exprs) {
        this(new Span(), in_op, in_exprs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected SubscriptingMI() {
        _op = null;
        _exprs = null;
    }

    final public Enclosing getOp() { return _op; }
    final public List<Expr> getExprs() { return _exprs; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forSubscriptingMI(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forSubscriptingMI(this); }

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
        writer.print("SubscriptingMI:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Enclosing temp_op = getOp();
        writer.startLine();
        writer.print("op = ");
        temp_op.outputHelp(writer, lossless);

        List<Expr> temp_exprs = getExprs();
        writer.startLine();
        writer.print("exprs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_exprs = true;
        for (Expr elt_temp_exprs : temp_exprs) {
            isempty_temp_exprs = false;
            writer.startLine("* ");
            if (elt_temp_exprs == null) {
                writer.print("null");
            } else {
                elt_temp_exprs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_exprs) writer.print(" }");
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
            SubscriptingMI casted = (SubscriptingMI) obj;
            Enclosing temp_op = getOp();
            Enclosing casted_op = casted.getOp();
            if (!(temp_op == casted_op || temp_op.equals(casted_op))) return false;
            List<Expr> temp_exprs = getExprs();
            List<Expr> casted_exprs = casted.getExprs();
            if (!(temp_exprs == casted_exprs || temp_exprs.equals(casted_exprs))) return false;
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
        Enclosing temp_op = getOp();
        code ^= temp_op.hashCode();
        List<Expr> temp_exprs = getExprs();
        code ^= temp_exprs.hashCode();
        return code;
    }
}
