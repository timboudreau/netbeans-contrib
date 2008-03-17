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
 * Class LocalVarDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class LocalVarDecl extends LetExpr {
    private final List<LValue> _lhs;
    private final Option<Expr> _rhs;

    /**
     * Constructs a LocalVarDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public LocalVarDecl(Span in_span, boolean in_parenthesized, List<Expr> in_body, List<LValue> in_lhs, Option<Expr> in_rhs) {
        super(in_span, in_parenthesized, in_body);
        if (in_lhs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'lhs' to the LocalVarDecl constructor was null");
        }
        _lhs = in_lhs;
        if (in_rhs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'rhs' to the LocalVarDecl constructor was null");
        }
        _rhs = in_rhs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LocalVarDecl(Span in_span, boolean in_parenthesized, List<Expr> in_body, List<LValue> in_lhs) {
        this(in_span, in_parenthesized, in_body, in_lhs, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LocalVarDecl(Span in_span, List<Expr> in_body, List<LValue> in_lhs, Option<Expr> in_rhs) {
        this(in_span, false, in_body, in_lhs, in_rhs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LocalVarDecl(Span in_span, List<Expr> in_body, List<LValue> in_lhs) {
        this(in_span, false, in_body, in_lhs, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LocalVarDecl(boolean in_parenthesized, List<Expr> in_body, List<LValue> in_lhs, Option<Expr> in_rhs) {
        this(new Span(), in_parenthesized, in_body, in_lhs, in_rhs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LocalVarDecl(boolean in_parenthesized, List<Expr> in_body, List<LValue> in_lhs) {
        this(new Span(), in_parenthesized, in_body, in_lhs, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LocalVarDecl(List<Expr> in_body, List<LValue> in_lhs, Option<Expr> in_rhs) {
        this(new Span(), false, in_body, in_lhs, in_rhs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LocalVarDecl(List<Expr> in_body, List<LValue> in_lhs) {
        this(new Span(), false, in_body, in_lhs, Option.<Expr>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected LocalVarDecl() {
        _lhs = null;
        _rhs = null;
    }

    final public List<LValue> getLhs() { return _lhs; }
    final public Option<Expr> getRhs() { return _rhs; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forLocalVarDecl(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forLocalVarDecl(this); }

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
        writer.print("LocalVarDecl:");
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

        List<Expr> temp_body = getBody();
        writer.startLine();
        writer.print("body = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_body = true;
        for (Expr elt_temp_body : temp_body) {
            isempty_temp_body = false;
            writer.startLine("* ");
            if (elt_temp_body == null) {
                writer.print("null");
            } else {
                elt_temp_body.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_body) writer.print(" }");
        else writer.startLine("}");

        List<LValue> temp_lhs = getLhs();
        writer.startLine();
        writer.print("lhs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_lhs = true;
        for (LValue elt_temp_lhs : temp_lhs) {
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

        Option<Expr> temp_rhs = getRhs();
        writer.startLine();
        writer.print("rhs = ");
        if (temp_rhs.isSome()) {
            writer.print("(");
            Expr elt_temp_rhs = edu.rice.cs.plt.tuple.Option.unwrap(temp_rhs);
            if (elt_temp_rhs == null) {
                writer.print("null");
            } else {
                elt_temp_rhs.outputHelp(writer, lossless);
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
            LocalVarDecl casted = (LocalVarDecl) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<Expr> temp_body = getBody();
            List<Expr> casted_body = casted.getBody();
            if (!(temp_body == casted_body || temp_body.equals(casted_body))) return false;
            List<LValue> temp_lhs = getLhs();
            List<LValue> casted_lhs = casted.getLhs();
            if (!(temp_lhs == casted_lhs || temp_lhs.equals(casted_lhs))) return false;
            Option<Expr> temp_rhs = getRhs();
            Option<Expr> casted_rhs = casted.getRhs();
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
        List<Expr> temp_body = getBody();
        code ^= temp_body.hashCode();
        List<LValue> temp_lhs = getLhs();
        code ^= temp_lhs.hashCode();
        Option<Expr> temp_rhs = getRhs();
        code ^= temp_rhs.hashCode();
        return code;
    }
}
