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
 * Class ArrayComprehension, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ArrayComprehension extends BigOprApp {
    private final List<ArrayComprehensionClause> _clauses;

    /**
     * Constructs a ArrayComprehension.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ArrayComprehension(Span in_span, boolean in_parenthesized, List<StaticArg> in_staticArgs, List<ArrayComprehensionClause> in_clauses) {
        super(in_span, in_parenthesized, in_staticArgs);
        if (in_clauses == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'clauses' to the ArrayComprehension constructor was null");
        }
        _clauses = in_clauses;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehension(Span in_span, boolean in_parenthesized, List<ArrayComprehensionClause> in_clauses) {
        this(in_span, in_parenthesized, Collections.<StaticArg>emptyList(), in_clauses);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehension(Span in_span, List<StaticArg> in_staticArgs, List<ArrayComprehensionClause> in_clauses) {
        this(in_span, false, in_staticArgs, in_clauses);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehension(Span in_span, List<ArrayComprehensionClause> in_clauses) {
        this(in_span, false, Collections.<StaticArg>emptyList(), in_clauses);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehension(boolean in_parenthesized, List<StaticArg> in_staticArgs, List<ArrayComprehensionClause> in_clauses) {
        this(new Span(), in_parenthesized, in_staticArgs, in_clauses);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehension(boolean in_parenthesized, List<ArrayComprehensionClause> in_clauses) {
        this(new Span(), in_parenthesized, Collections.<StaticArg>emptyList(), in_clauses);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehension(List<StaticArg> in_staticArgs, List<ArrayComprehensionClause> in_clauses) {
        this(new Span(), false, in_staticArgs, in_clauses);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehension(List<ArrayComprehensionClause> in_clauses) {
        this(new Span(), false, Collections.<StaticArg>emptyList(), in_clauses);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ArrayComprehension() {
        _clauses = null;
    }

    final public List<ArrayComprehensionClause> getClauses() { return _clauses; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forArrayComprehension(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forArrayComprehension(this); }

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
        writer.print("ArrayComprehension:");
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

        List<StaticArg> temp_staticArgs = getStaticArgs();
        writer.startLine();
        writer.print("staticArgs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_staticArgs = true;
        for (StaticArg elt_temp_staticArgs : temp_staticArgs) {
            isempty_temp_staticArgs = false;
            writer.startLine("* ");
            if (elt_temp_staticArgs == null) {
                writer.print("null");
            } else {
                elt_temp_staticArgs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_staticArgs) writer.print(" }");
        else writer.startLine("}");

        List<ArrayComprehensionClause> temp_clauses = getClauses();
        writer.startLine();
        writer.print("clauses = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_clauses = true;
        for (ArrayComprehensionClause elt_temp_clauses : temp_clauses) {
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
            ArrayComprehension casted = (ArrayComprehension) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<StaticArg> temp_staticArgs = getStaticArgs();
            List<StaticArg> casted_staticArgs = casted.getStaticArgs();
            if (!(temp_staticArgs == casted_staticArgs || temp_staticArgs.equals(casted_staticArgs))) return false;
            List<ArrayComprehensionClause> temp_clauses = getClauses();
            List<ArrayComprehensionClause> casted_clauses = casted.getClauses();
            if (!(temp_clauses == casted_clauses || temp_clauses.equals(casted_clauses))) return false;
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
        List<StaticArg> temp_staticArgs = getStaticArgs();
        code ^= temp_staticArgs.hashCode();
        List<ArrayComprehensionClause> temp_clauses = getClauses();
        code ^= temp_clauses.hashCode();
        return code;
    }
}
