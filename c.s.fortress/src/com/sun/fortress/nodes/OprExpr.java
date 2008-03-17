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
 * Class OprExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class OprExpr extends AppExpr {
    private final OpRef _op;
    private final List<Expr> _args;

    /**
     * Constructs a OprExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public OprExpr(Span in_span, boolean in_parenthesized, OpRef in_op, List<Expr> in_args) {
        super(in_span, in_parenthesized);
        if (in_op == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'op' to the OprExpr constructor was null");
        }
        _op = in_op;
        if (in_args == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'args' to the OprExpr constructor was null");
        }
        _args = in_args;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OprExpr(Span in_span, boolean in_parenthesized, OpRef in_op) {
        this(in_span, in_parenthesized, in_op, Collections.<Expr>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OprExpr(Span in_span, OpRef in_op, List<Expr> in_args) {
        this(in_span, false, in_op, in_args);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OprExpr(Span in_span, OpRef in_op) {
        this(in_span, false, in_op, Collections.<Expr>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OprExpr(boolean in_parenthesized, OpRef in_op, List<Expr> in_args) {
        this(new Span(), in_parenthesized, in_op, in_args);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OprExpr(boolean in_parenthesized, OpRef in_op) {
        this(new Span(), in_parenthesized, in_op, Collections.<Expr>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OprExpr(OpRef in_op, List<Expr> in_args) {
        this(new Span(), false, in_op, in_args);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OprExpr(OpRef in_op) {
        this(new Span(), false, in_op, Collections.<Expr>emptyList());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected OprExpr() {
        _op = null;
        _args = null;
    }

    final public OpRef getOp() { return _op; }
    final public List<Expr> getArgs() { return _args; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forOprExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forOprExpr(this); }

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
        writer.print("OprExpr:");
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

        OpRef temp_op = getOp();
        writer.startLine();
        writer.print("op = ");
        temp_op.outputHelp(writer, lossless);

        List<Expr> temp_args = getArgs();
        writer.startLine();
        writer.print("args = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_args = true;
        for (Expr elt_temp_args : temp_args) {
            isempty_temp_args = false;
            writer.startLine("* ");
            if (elt_temp_args == null) {
                writer.print("null");
            } else {
                elt_temp_args.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_args) writer.print(" }");
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
            OprExpr casted = (OprExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            OpRef temp_op = getOp();
            OpRef casted_op = casted.getOp();
            if (!(temp_op == casted_op || temp_op.equals(casted_op))) return false;
            List<Expr> temp_args = getArgs();
            List<Expr> casted_args = casted.getArgs();
            if (!(temp_args == casted_args || temp_args.equals(casted_args))) return false;
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
        OpRef temp_op = getOp();
        code ^= temp_op.hashCode();
        List<Expr> temp_args = getArgs();
        code ^= temp_args.hashCode();
        return code;
    }
}
