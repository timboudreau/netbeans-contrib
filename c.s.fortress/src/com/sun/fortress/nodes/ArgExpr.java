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
 * Class ArgExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ArgExpr extends AbstractTupleExpr {
    private final Option<VarargsExpr> _varargs;
    private final List<KeywordExpr> _keywords;
    private final boolean _inApp;

    /**
     * Constructs a ArgExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        super(in_span, in_parenthesized, in_exprs);
        if (in_varargs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'varargs' to the ArgExpr constructor was null");
        }
        _varargs = in_varargs;
        if (in_keywords == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'keywords' to the ArgExpr constructor was null");
        }
        _keywords = in_keywords;
        _inApp = in_inApp;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords) {
        this(in_span, in_parenthesized, in_exprs, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, boolean in_inApp) {
        this(in_span, in_parenthesized, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs) {
        this(in_span, in_parenthesized, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        this(in_span, in_parenthesized, in_exprs, Option.<VarargsExpr>none(), in_keywords, in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs, List<KeywordExpr> in_keywords) {
        this(in_span, in_parenthesized, in_exprs, Option.<VarargsExpr>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs, boolean in_inApp) {
        this(in_span, in_parenthesized, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, boolean in_parenthesized, List<Expr> in_exprs) {
        this(in_span, in_parenthesized, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        this(in_span, false, in_exprs, in_varargs, in_keywords, in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords) {
        this(in_span, false, in_exprs, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, boolean in_inApp) {
        this(in_span, false, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs, Option<VarargsExpr> in_varargs) {
        this(in_span, false, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        this(in_span, false, in_exprs, Option.<VarargsExpr>none(), in_keywords, in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs, List<KeywordExpr> in_keywords) {
        this(in_span, false, in_exprs, Option.<VarargsExpr>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs, boolean in_inApp) {
        this(in_span, false, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(Span in_span, List<Expr> in_exprs) {
        this(in_span, false, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        this(new Span(), in_parenthesized, in_exprs, in_varargs, in_keywords, in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords) {
        this(new Span(), in_parenthesized, in_exprs, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs, boolean in_inApp) {
        this(new Span(), in_parenthesized, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs, Option<VarargsExpr> in_varargs) {
        this(new Span(), in_parenthesized, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        this(new Span(), in_parenthesized, in_exprs, Option.<VarargsExpr>none(), in_keywords, in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs, List<KeywordExpr> in_keywords) {
        this(new Span(), in_parenthesized, in_exprs, Option.<VarargsExpr>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs, boolean in_inApp) {
        this(new Span(), in_parenthesized, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(boolean in_parenthesized, List<Expr> in_exprs) {
        this(new Span(), in_parenthesized, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        this(new Span(), false, in_exprs, in_varargs, in_keywords, in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs, Option<VarargsExpr> in_varargs, List<KeywordExpr> in_keywords) {
        this(new Span(), false, in_exprs, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs, Option<VarargsExpr> in_varargs, boolean in_inApp) {
        this(new Span(), false, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs, Option<VarargsExpr> in_varargs) {
        this(new Span(), false, in_exprs, in_varargs, Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs, List<KeywordExpr> in_keywords, boolean in_inApp) {
        this(new Span(), false, in_exprs, Option.<VarargsExpr>none(), in_keywords, in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs, List<KeywordExpr> in_keywords) {
        this(new Span(), false, in_exprs, Option.<VarargsExpr>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs, boolean in_inApp) {
        this(new Span(), false, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), in_inApp);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgExpr(List<Expr> in_exprs) {
        this(new Span(), false, in_exprs, Option.<VarargsExpr>none(), Collections.<KeywordExpr>emptyList(), false);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ArgExpr() {
        _varargs = null;
        _keywords = null;
        _inApp = false;
    }

    final public Option<VarargsExpr> getVarargs() { return _varargs; }
    final public List<KeywordExpr> getKeywords() { return _keywords; }
    final public boolean isInApp() { return _inApp; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forArgExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forArgExpr(this); }

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
        writer.print("ArgExpr:");
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

        Option<VarargsExpr> temp_varargs = getVarargs();
        writer.startLine();
        writer.print("varargs = ");
        if (temp_varargs.isSome()) {
            writer.print("(");
            VarargsExpr elt_temp_varargs = edu.rice.cs.plt.tuple.Option.unwrap(temp_varargs);
            if (elt_temp_varargs == null) {
                writer.print("null");
            } else {
                elt_temp_varargs.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        List<KeywordExpr> temp_keywords = getKeywords();
        writer.startLine();
        writer.print("keywords = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_keywords = true;
        for (KeywordExpr elt_temp_keywords : temp_keywords) {
            isempty_temp_keywords = false;
            writer.startLine("* ");
            if (elt_temp_keywords == null) {
                writer.print("null");
            } else {
                elt_temp_keywords.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_keywords) writer.print(" }");
        else writer.startLine("}");

        boolean temp_inApp = isInApp();
        writer.startLine();
        writer.print("inApp = ");
        writer.print(temp_inApp);
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
            ArgExpr casted = (ArgExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<Expr> temp_exprs = getExprs();
            List<Expr> casted_exprs = casted.getExprs();
            if (!(temp_exprs == casted_exprs || temp_exprs.equals(casted_exprs))) return false;
            Option<VarargsExpr> temp_varargs = getVarargs();
            Option<VarargsExpr> casted_varargs = casted.getVarargs();
            if (!(temp_varargs == casted_varargs || temp_varargs.equals(casted_varargs))) return false;
            List<KeywordExpr> temp_keywords = getKeywords();
            List<KeywordExpr> casted_keywords = casted.getKeywords();
            if (!(temp_keywords == casted_keywords || temp_keywords.equals(casted_keywords))) return false;
            boolean temp_inApp = isInApp();
            boolean casted_inApp = casted.isInApp();
            if (!(temp_inApp == casted_inApp)) return false;
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
        Option<VarargsExpr> temp_varargs = getVarargs();
        code ^= temp_varargs.hashCode();
        List<KeywordExpr> temp_keywords = getKeywords();
        code ^= temp_keywords.hashCode();
        boolean temp_inApp = isInApp();
        code ^= temp_inApp ? 1231 : 1237;
        return code;
    }
}
