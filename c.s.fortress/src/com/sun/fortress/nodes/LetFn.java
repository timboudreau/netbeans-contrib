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
 * Class LetFn, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class LetFn extends LetExpr {
    private final List<FnDef> _fns;

    /**
     * Constructs a LetFn.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public LetFn(Span in_span, boolean in_parenthesized, List<Expr> in_body, List<FnDef> in_fns) {
        super(in_span, in_parenthesized, in_body);
        if (in_fns == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'fns' to the LetFn constructor was null");
        }
        _fns = in_fns;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LetFn(Span in_span, List<Expr> in_body, List<FnDef> in_fns) {
        this(in_span, false, in_body, in_fns);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LetFn(boolean in_parenthesized, List<Expr> in_body, List<FnDef> in_fns) {
        this(new Span(), in_parenthesized, in_body, in_fns);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LetFn(List<Expr> in_body, List<FnDef> in_fns) {
        this(new Span(), false, in_body, in_fns);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected LetFn() {
        _fns = null;
    }

    final public List<FnDef> getFns() { return _fns; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forLetFn(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forLetFn(this); }

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
        writer.print("LetFn:");
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

        List<FnDef> temp_fns = getFns();
        writer.startLine();
        writer.print("fns = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_fns = true;
        for (FnDef elt_temp_fns : temp_fns) {
            isempty_temp_fns = false;
            writer.startLine("* ");
            if (elt_temp_fns == null) {
                writer.print("null");
            } else {
                elt_temp_fns.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_fns) writer.print(" }");
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
            LetFn casted = (LetFn) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<Expr> temp_body = getBody();
            List<Expr> casted_body = casted.getBody();
            if (!(temp_body == casted_body || temp_body.equals(casted_body))) return false;
            List<FnDef> temp_fns = getFns();
            List<FnDef> casted_fns = casted.getFns();
            if (!(temp_fns == casted_fns || temp_fns.equals(casted_fns))) return false;
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
        List<FnDef> temp_fns = getFns();
        code ^= temp_fns.hashCode();
        return code;
    }
}
