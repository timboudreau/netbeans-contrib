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
 * Class ExponentiationMI, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ExponentiationMI extends NonExprMI {
    private final Op _op;
    private final Option<Expr> _expr;

    /**
     * Constructs a ExponentiationMI.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ExponentiationMI(Span in_span, Op in_op, Option<Expr> in_expr) {
        super(in_span);
        if (in_op == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'op' to the ExponentiationMI constructor was null");
        }
        _op = in_op;
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the ExponentiationMI constructor was null");
        }
        _expr = in_expr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExponentiationMI(Op in_op, Option<Expr> in_expr) {
        this(new Span(), in_op, in_expr);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ExponentiationMI() {
        _op = null;
        _expr = null;
    }

    final public Op getOp() { return _op; }
    final public Option<Expr> getExpr() { return _expr; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forExponentiationMI(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forExponentiationMI(this); }

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
        writer.print("ExponentiationMI:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Op temp_op = getOp();
        writer.startLine();
        writer.print("op = ");
        temp_op.outputHelp(writer, lossless);

        Option<Expr> temp_expr = getExpr();
        writer.startLine();
        writer.print("expr = ");
        if (temp_expr.isSome()) {
            writer.print("(");
            Expr elt_temp_expr = edu.rice.cs.plt.tuple.Option.unwrap(temp_expr);
            if (elt_temp_expr == null) {
                writer.print("null");
            } else {
                elt_temp_expr.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }
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
            ExponentiationMI casted = (ExponentiationMI) obj;
            Op temp_op = getOp();
            Op casted_op = casted.getOp();
            if (!(temp_op == casted_op || temp_op.equals(casted_op))) return false;
            Option<Expr> temp_expr = getExpr();
            Option<Expr> casted_expr = casted.getExpr();
            if (!(temp_expr == casted_expr || temp_expr.equals(casted_expr))) return false;
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
        Op temp_op = getOp();
        code ^= temp_op.hashCode();
        Option<Expr> temp_expr = getExpr();
        code ^= temp_expr.hashCode();
        return code;
    }
}
