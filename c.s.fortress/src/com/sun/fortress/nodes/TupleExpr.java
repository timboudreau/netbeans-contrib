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
 * Class TupleExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class TupleExpr extends AbstractTupleExpr {

    /**
     * Constructs a TupleExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TupleExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs) {
        super(in_span, in_parenthesized, in_exprs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TupleExpr(Span in_span, List<Expr> in_exprs) {
        this(in_span, false, in_exprs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TupleExpr(boolean in_parenthesized, List<Expr> in_exprs) {
        this(new Span(), in_parenthesized, in_exprs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TupleExpr(List<Expr> in_exprs) {
        this(new Span(), false, in_exprs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TupleExpr() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forTupleExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forTupleExpr(this); }

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
        writer.print("TupleExpr:");
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
            TupleExpr casted = (TupleExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
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
        boolean temp_parenthesized = isParenthesized();
        code ^= temp_parenthesized ? 1231 : 1237;
        List<Expr> temp_exprs = getExprs();
        code ^= temp_exprs.hashCode();
        return code;
    }
}
