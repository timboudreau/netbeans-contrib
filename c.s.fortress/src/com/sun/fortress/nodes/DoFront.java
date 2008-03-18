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
 * Class DoFront, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class DoFront extends AbstractNode {
    private final Option<Expr> _loc;
    private final boolean _atomic;
    private final Block _expr;

    /**
     * Constructs a DoFront.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public DoFront(Span in_span, Option<Expr> in_loc, boolean in_atomic, Block in_expr) {
        super(in_span);
        if (in_loc == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'loc' to the DoFront constructor was null");
        }
        _loc = in_loc;
        _atomic = in_atomic;
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the DoFront constructor was null");
        }
        _expr = in_expr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DoFront(Span in_span, Option<Expr> in_loc, Block in_expr) {
        this(in_span, in_loc, false, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DoFront(Span in_span, boolean in_atomic, Block in_expr) {
        this(in_span, Option.<Expr>none(), in_atomic, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DoFront(Span in_span, Block in_expr) {
        this(in_span, Option.<Expr>none(), false, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DoFront(Option<Expr> in_loc, boolean in_atomic, Block in_expr) {
        this(new Span(), in_loc, in_atomic, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DoFront(Option<Expr> in_loc, Block in_expr) {
        this(new Span(), in_loc, false, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DoFront(boolean in_atomic, Block in_expr) {
        this(new Span(), Option.<Expr>none(), in_atomic, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DoFront(Block in_expr) {
        this(new Span(), Option.<Expr>none(), false, in_expr);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected DoFront() {
        _loc = null;
        _atomic = false;
        _expr = null;
    }

    final public Option<Expr> getLoc() { return _loc; }
    final public boolean isAtomic() { return _atomic; }
    final public Block getExpr() { return _expr; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forDoFront(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forDoFront(this); }

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
        writer.print("DoFront:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Option<Expr> temp_loc = getLoc();
        writer.startLine();
        writer.print("loc = ");
        if (temp_loc.isSome()) {
            writer.print("(");
            Expr elt_temp_loc = edu.rice.cs.plt.tuple.Option.unwrap(temp_loc);
            if (elt_temp_loc == null) {
                writer.print("null");
            } else {
                elt_temp_loc.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        boolean temp_atomic = isAtomic();
        writer.startLine();
        writer.print("atomic = ");
        writer.print(temp_atomic);

        Block temp_expr = getExpr();
        writer.startLine();
        writer.print("expr = ");
        temp_expr.outputHelp(writer, lossless);
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
            DoFront casted = (DoFront) obj;
            Option<Expr> temp_loc = getLoc();
            Option<Expr> casted_loc = casted.getLoc();
            if (!(temp_loc == casted_loc || temp_loc.equals(casted_loc))) return false;
            boolean temp_atomic = isAtomic();
            boolean casted_atomic = casted.isAtomic();
            if (!(temp_atomic == casted_atomic)) return false;
            Block temp_expr = getExpr();
            Block casted_expr = casted.getExpr();
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
        Option<Expr> temp_loc = getLoc();
        code ^= temp_loc.hashCode();
        boolean temp_atomic = isAtomic();
        code ^= temp_atomic ? 1231 : 1237;
        Block temp_expr = getExpr();
        code ^= temp_expr.hashCode();
        return code;
    }
}
