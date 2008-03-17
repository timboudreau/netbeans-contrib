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
 * Class Exit, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Exit extends FlowExpr {
    private final Option<Id> _target;
    private final Option<Expr> _returnExpr;

    /**
     * Constructs a Exit.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Exit(Span in_span, boolean in_parenthesized, Option<Id> in_target, Option<Expr> in_returnExpr) {
        super(in_span, in_parenthesized);
        if (in_target == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'target' to the Exit constructor was null");
        }
        _target = in_target;
        if (in_returnExpr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'returnExpr' to the Exit constructor was null");
        }
        _returnExpr = in_returnExpr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(Span in_span, boolean in_parenthesized, Option<Id> in_target) {
        this(in_span, in_parenthesized, in_target, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(Span in_span, boolean in_parenthesized) {
        this(in_span, in_parenthesized, Option.<Id>none(), Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(Span in_span, Option<Id> in_target, Option<Expr> in_returnExpr) {
        this(in_span, false, in_target, in_returnExpr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(Span in_span, Option<Id> in_target) {
        this(in_span, false, in_target, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(Span in_span) {
        this(in_span, false, Option.<Id>none(), Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(boolean in_parenthesized, Option<Id> in_target, Option<Expr> in_returnExpr) {
        this(new Span(), in_parenthesized, in_target, in_returnExpr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(boolean in_parenthesized, Option<Id> in_target) {
        this(new Span(), in_parenthesized, in_target, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(boolean in_parenthesized) {
        this(new Span(), in_parenthesized, Option.<Id>none(), Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(Option<Id> in_target, Option<Expr> in_returnExpr) {
        this(new Span(), false, in_target, in_returnExpr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit(Option<Id> in_target) {
        this(new Span(), false, in_target, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Exit() {
        this(new Span(), false, Option.<Id>none(), Option.<Expr>none());
    }

    final public Option<Id> getTarget() { return _target; }
    final public Option<Expr> getReturnExpr() { return _returnExpr; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forExit(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forExit(this); }

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
        writer.print("Exit:");
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

        Option<Id> temp_target = getTarget();
        writer.startLine();
        writer.print("target = ");
        if (temp_target.isSome()) {
            writer.print("(");
            Id elt_temp_target = edu.rice.cs.plt.tuple.Option.unwrap(temp_target);
            if (elt_temp_target == null) {
                writer.print("null");
            } else {
                elt_temp_target.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<Expr> temp_returnExpr = getReturnExpr();
        writer.startLine();
        writer.print("returnExpr = ");
        if (temp_returnExpr.isSome()) {
            writer.print("(");
            Expr elt_temp_returnExpr = edu.rice.cs.plt.tuple.Option.unwrap(temp_returnExpr);
            if (elt_temp_returnExpr == null) {
                writer.print("null");
            } else {
                elt_temp_returnExpr.outputHelp(writer, lossless);
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
            Exit casted = (Exit) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Option<Id> temp_target = getTarget();
            Option<Id> casted_target = casted.getTarget();
            if (!(temp_target == casted_target || temp_target.equals(casted_target))) return false;
            Option<Expr> temp_returnExpr = getReturnExpr();
            Option<Expr> casted_returnExpr = casted.getReturnExpr();
            if (!(temp_returnExpr == casted_returnExpr || temp_returnExpr.equals(casted_returnExpr))) return false;
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
        Option<Id> temp_target = getTarget();
        code ^= temp_target.hashCode();
        Option<Expr> temp_returnExpr = getReturnExpr();
        code ^= temp_returnExpr.hashCode();
        return code;
    }
}
