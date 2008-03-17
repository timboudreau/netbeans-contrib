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
 * Class SubscriptExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class SubscriptExpr extends OpExpr implements LHS {
    private final Expr _obj;
    private final List<Expr> _subs;
    private final Option<Enclosing> _op;

    /**
     * Constructs a SubscriptExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public SubscriptExpr(Span in_span, boolean in_parenthesized, Expr in_obj, List<Expr> in_subs, Option<Enclosing> in_op) {
        super(in_span, in_parenthesized);
        if (in_obj == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'obj' to the SubscriptExpr constructor was null");
        }
        _obj = in_obj;
        if (in_subs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'subs' to the SubscriptExpr constructor was null");
        }
        _subs = in_subs;
        if (in_op == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'op' to the SubscriptExpr constructor was null");
        }
        _op = in_op;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptExpr(Span in_span, boolean in_parenthesized, Expr in_obj, List<Expr> in_subs) {
        this(in_span, in_parenthesized, in_obj, in_subs, Option.<Enclosing>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptExpr(Span in_span, Expr in_obj, List<Expr> in_subs, Option<Enclosing> in_op) {
        this(in_span, false, in_obj, in_subs, in_op);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptExpr(Span in_span, Expr in_obj, List<Expr> in_subs) {
        this(in_span, false, in_obj, in_subs, Option.<Enclosing>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptExpr(boolean in_parenthesized, Expr in_obj, List<Expr> in_subs, Option<Enclosing> in_op) {
        this(new Span(), in_parenthesized, in_obj, in_subs, in_op);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptExpr(boolean in_parenthesized, Expr in_obj, List<Expr> in_subs) {
        this(new Span(), in_parenthesized, in_obj, in_subs, Option.<Enclosing>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptExpr(Expr in_obj, List<Expr> in_subs, Option<Enclosing> in_op) {
        this(new Span(), false, in_obj, in_subs, in_op);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SubscriptExpr(Expr in_obj, List<Expr> in_subs) {
        this(new Span(), false, in_obj, in_subs, Option.<Enclosing>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected SubscriptExpr() {
        _obj = null;
        _subs = null;
        _op = null;
    }

    final public Expr getObj() { return _obj; }
    final public List<Expr> getSubs() { return _subs; }
    final public Option<Enclosing> getOp() { return _op; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forSubscriptExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forSubscriptExpr(this); }

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
        writer.print("SubscriptExpr:");
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

        Expr temp_obj = getObj();
        writer.startLine();
        writer.print("obj = ");
        temp_obj.outputHelp(writer, lossless);

        List<Expr> temp_subs = getSubs();
        writer.startLine();
        writer.print("subs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_subs = true;
        for (Expr elt_temp_subs : temp_subs) {
            isempty_temp_subs = false;
            writer.startLine("* ");
            if (elt_temp_subs == null) {
                writer.print("null");
            } else {
                elt_temp_subs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_subs) writer.print(" }");
        else writer.startLine("}");

        Option<Enclosing> temp_op = getOp();
        writer.startLine();
        writer.print("op = ");
        if (temp_op.isSome()) {
            writer.print("(");
            Enclosing elt_temp_op = edu.rice.cs.plt.tuple.Option.unwrap(temp_op);
            if (elt_temp_op == null) {
                writer.print("null");
            } else {
                elt_temp_op.outputHelp(writer, lossless);
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
            SubscriptExpr casted = (SubscriptExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_obj = getObj();
            Expr casted_obj = casted.getObj();
            if (!(temp_obj == casted_obj || temp_obj.equals(casted_obj))) return false;
            List<Expr> temp_subs = getSubs();
            List<Expr> casted_subs = casted.getSubs();
            if (!(temp_subs == casted_subs || temp_subs.equals(casted_subs))) return false;
            Option<Enclosing> temp_op = getOp();
            Option<Enclosing> casted_op = casted.getOp();
            if (!(temp_op == casted_op || temp_op.equals(casted_op))) return false;
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
        Expr temp_obj = getObj();
        code ^= temp_obj.hashCode();
        List<Expr> temp_subs = getSubs();
        code ^= temp_subs.hashCode();
        Option<Enclosing> temp_op = getOp();
        code ^= temp_op.hashCode();
        return code;
    }
}
