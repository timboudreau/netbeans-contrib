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
 * Class ChainExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ChainExpr extends AppExpr {
    private final Expr _first;
    private final List<Pair<Op, Expr>> _links;

    /**
     * Constructs a ChainExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ChainExpr(Span in_span, boolean in_parenthesized, Expr in_first, List<Pair<Op, Expr>> in_links) {
        super(in_span, in_parenthesized);
        if (in_first == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'first' to the ChainExpr constructor was null");
        }
        _first = in_first;
        if (in_links == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'links' to the ChainExpr constructor was null");
        }
        _links = in_links;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ChainExpr(Span in_span, Expr in_first, List<Pair<Op, Expr>> in_links) {
        this(in_span, false, in_first, in_links);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ChainExpr(boolean in_parenthesized, Expr in_first, List<Pair<Op, Expr>> in_links) {
        this(new Span(), in_parenthesized, in_first, in_links);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ChainExpr(Expr in_first, List<Pair<Op, Expr>> in_links) {
        this(new Span(), false, in_first, in_links);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ChainExpr() {
        _first = null;
        _links = null;
    }

    final public Expr getFirst() { return _first; }
    final public List<Pair<Op, Expr>> getLinks() { return _links; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forChainExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forChainExpr(this); }

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
        writer.print("ChainExpr:");
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

        Expr temp_first = getFirst();
        writer.startLine();
        writer.print("first = ");
        temp_first.outputHelp(writer, lossless);

        List<Pair<Op, Expr>> temp_links = getLinks();
        writer.startLine();
        writer.print("links = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_links = true;
        for (Pair<Op, Expr> elt_temp_links : temp_links) {
            isempty_temp_links = false;
            writer.startLine("* ");
            if (elt_temp_links == null) {
                writer.print("null");
            } else {
                if (lossless) {
                    writer.printSerialized(elt_temp_links);
                    writer.print(" ");
                    writer.printEscaped(elt_temp_links);
                } else { writer.print(elt_temp_links); }
            }
        }
        writer.unindent();
        if (isempty_temp_links) writer.print(" }");
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
            ChainExpr casted = (ChainExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_first = getFirst();
            Expr casted_first = casted.getFirst();
            if (!(temp_first == casted_first || temp_first.equals(casted_first))) return false;
            List<Pair<Op, Expr>> temp_links = getLinks();
            List<Pair<Op, Expr>> casted_links = casted.getLinks();
            if (!(temp_links == casted_links || temp_links.equals(casted_links))) return false;
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
        Expr temp_first = getFirst();
        code ^= temp_first.hashCode();
        List<Pair<Op, Expr>> temp_links = getLinks();
        code ^= temp_links.hashCode();
        return code;
    }
}
