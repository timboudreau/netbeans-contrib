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
 * Class GeneratedExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class GeneratedExpr extends Expr {
    private final Expr _expr;
    private final List<GeneratorClause> _gens;

    /**
     * Constructs a GeneratedExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public GeneratedExpr(Span in_span, boolean in_parenthesized, Expr in_expr, List<GeneratorClause> in_gens) {
        super(in_span, in_parenthesized);
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the GeneratedExpr constructor was null");
        }
        _expr = in_expr;
        if (in_gens == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'gens' to the GeneratedExpr constructor was null");
        }
        _gens = in_gens;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public GeneratedExpr(Span in_span, Expr in_expr, List<GeneratorClause> in_gens) {
        this(in_span, false, in_expr, in_gens);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public GeneratedExpr(boolean in_parenthesized, Expr in_expr, List<GeneratorClause> in_gens) {
        this(new Span(), in_parenthesized, in_expr, in_gens);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public GeneratedExpr(Expr in_expr, List<GeneratorClause> in_gens) {
        this(new Span(), false, in_expr, in_gens);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected GeneratedExpr() {
        _expr = null;
        _gens = null;
    }

    final public Expr getExpr() { return _expr; }
    final public List<GeneratorClause> getGens() { return _gens; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forGeneratedExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forGeneratedExpr(this); }

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
        writer.print("GeneratedExpr:");
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

        Expr temp_expr = getExpr();
        writer.startLine();
        writer.print("expr = ");
        temp_expr.outputHelp(writer, lossless);

        List<GeneratorClause> temp_gens = getGens();
        writer.startLine();
        writer.print("gens = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_gens = true;
        for (GeneratorClause elt_temp_gens : temp_gens) {
            isempty_temp_gens = false;
            writer.startLine("* ");
            if (elt_temp_gens == null) {
                writer.print("null");
            } else {
                elt_temp_gens.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_gens) writer.print(" }");
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
            GeneratedExpr casted = (GeneratedExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_expr = getExpr();
            Expr casted_expr = casted.getExpr();
            if (!(temp_expr == casted_expr || temp_expr.equals(casted_expr))) return false;
            List<GeneratorClause> temp_gens = getGens();
            List<GeneratorClause> casted_gens = casted.getGens();
            if (!(temp_gens == casted_gens || temp_gens.equals(casted_gens))) return false;
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
        Expr temp_expr = getExpr();
        code ^= temp_expr.hashCode();
        List<GeneratorClause> temp_gens = getGens();
        code ^= temp_gens.hashCode();
        return code;
    }
}
