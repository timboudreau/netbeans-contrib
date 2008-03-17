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
 * Class CaseExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class CaseExpr extends DelimitedExpr {
    private final Option<Expr> _param;
    private final Option<Op> _compare;
    private final List<CaseClause> _clauses;
    private final Option<Block> _elseClause;

    /**
     * Constructs a CaseExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public CaseExpr(Span in_span, boolean in_parenthesized, Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        super(in_span, in_parenthesized);
        if (in_param == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'param' to the CaseExpr constructor was null");
        }
        _param = in_param;
        if (in_compare == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'compare' to the CaseExpr constructor was null");
        }
        _compare = in_compare;
        if (in_clauses == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'clauses' to the CaseExpr constructor was null");
        }
        _clauses = in_clauses;
        if (in_elseClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'elseClause' to the CaseExpr constructor was null");
        }
        _elseClause = in_elseClause;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Span in_span, boolean in_parenthesized, Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses) {
        this(in_span, in_parenthesized, in_param, in_compare, in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Span in_span, boolean in_parenthesized, Option<Expr> in_param, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        this(in_span, in_parenthesized, in_param, Option.<Op>none(), in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Span in_span, boolean in_parenthesized, Option<Expr> in_param, List<CaseClause> in_clauses) {
        this(in_span, in_parenthesized, in_param, Option.<Op>none(), in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Span in_span, Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        this(in_span, false, in_param, in_compare, in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Span in_span, Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses) {
        this(in_span, false, in_param, in_compare, in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Span in_span, Option<Expr> in_param, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        this(in_span, false, in_param, Option.<Op>none(), in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Span in_span, Option<Expr> in_param, List<CaseClause> in_clauses) {
        this(in_span, false, in_param, Option.<Op>none(), in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(boolean in_parenthesized, Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        this(new Span(), in_parenthesized, in_param, in_compare, in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(boolean in_parenthesized, Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses) {
        this(new Span(), in_parenthesized, in_param, in_compare, in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(boolean in_parenthesized, Option<Expr> in_param, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        this(new Span(), in_parenthesized, in_param, Option.<Op>none(), in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(boolean in_parenthesized, Option<Expr> in_param, List<CaseClause> in_clauses) {
        this(new Span(), in_parenthesized, in_param, Option.<Op>none(), in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        this(new Span(), false, in_param, in_compare, in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Option<Expr> in_param, Option<Op> in_compare, List<CaseClause> in_clauses) {
        this(new Span(), false, in_param, in_compare, in_clauses, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Option<Expr> in_param, List<CaseClause> in_clauses, Option<Block> in_elseClause) {
        this(new Span(), false, in_param, Option.<Op>none(), in_clauses, in_elseClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CaseExpr(Option<Expr> in_param, List<CaseClause> in_clauses) {
        this(new Span(), false, in_param, Option.<Op>none(), in_clauses, Option.<Block>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected CaseExpr() {
        _param = null;
        _compare = null;
        _clauses = null;
        _elseClause = null;
    }

    final public Option<Expr> getParam() { return _param; }
    final public Option<Op> getCompare() { return _compare; }
    final public List<CaseClause> getClauses() { return _clauses; }
    final public Option<Block> getElseClause() { return _elseClause; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forCaseExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forCaseExpr(this); }

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
        writer.print("CaseExpr:");
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

        Option<Expr> temp_param = getParam();
        writer.startLine();
        writer.print("param = ");
        if (temp_param.isSome()) {
            writer.print("(");
            Expr elt_temp_param = edu.rice.cs.plt.tuple.Option.unwrap(temp_param);
            if (elt_temp_param == null) {
                writer.print("null");
            } else {
                elt_temp_param.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<Op> temp_compare = getCompare();
        writer.startLine();
        writer.print("compare = ");
        if (temp_compare.isSome()) {
            writer.print("(");
            Op elt_temp_compare = edu.rice.cs.plt.tuple.Option.unwrap(temp_compare);
            if (elt_temp_compare == null) {
                writer.print("null");
            } else {
                elt_temp_compare.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        List<CaseClause> temp_clauses = getClauses();
        writer.startLine();
        writer.print("clauses = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_clauses = true;
        for (CaseClause elt_temp_clauses : temp_clauses) {
            isempty_temp_clauses = false;
            writer.startLine("* ");
            if (elt_temp_clauses == null) {
                writer.print("null");
            } else {
                elt_temp_clauses.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_clauses) writer.print(" }");
        else writer.startLine("}");

        Option<Block> temp_elseClause = getElseClause();
        writer.startLine();
        writer.print("elseClause = ");
        if (temp_elseClause.isSome()) {
            writer.print("(");
            Block elt_temp_elseClause = edu.rice.cs.plt.tuple.Option.unwrap(temp_elseClause);
            if (elt_temp_elseClause == null) {
                writer.print("null");
            } else {
                elt_temp_elseClause.outputHelp(writer, lossless);
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
            CaseExpr casted = (CaseExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Option<Expr> temp_param = getParam();
            Option<Expr> casted_param = casted.getParam();
            if (!(temp_param == casted_param || temp_param.equals(casted_param))) return false;
            Option<Op> temp_compare = getCompare();
            Option<Op> casted_compare = casted.getCompare();
            if (!(temp_compare == casted_compare || temp_compare.equals(casted_compare))) return false;
            List<CaseClause> temp_clauses = getClauses();
            List<CaseClause> casted_clauses = casted.getClauses();
            if (!(temp_clauses == casted_clauses || temp_clauses.equals(casted_clauses))) return false;
            Option<Block> temp_elseClause = getElseClause();
            Option<Block> casted_elseClause = casted.getElseClause();
            if (!(temp_elseClause == casted_elseClause || temp_elseClause.equals(casted_elseClause))) return false;
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
        Option<Expr> temp_param = getParam();
        code ^= temp_param.hashCode();
        Option<Op> temp_compare = getCompare();
        code ^= temp_compare.hashCode();
        List<CaseClause> temp_clauses = getClauses();
        code ^= temp_clauses.hashCode();
        Option<Block> temp_elseClause = getElseClause();
        code ^= temp_elseClause.hashCode();
        return code;
    }
}
