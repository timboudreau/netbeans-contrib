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
 * Class ArrowType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ArrowType extends AbstractArrowType {

    /**
     * Constructs a ArrowType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        super(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(in_span, in_parenthesized, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Span in_span, boolean in_parenthesized, Type in_domain, Type in_range) {
        this(in_span, in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(in_span, false, in_domain, in_range, in_throwsClause, in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Span in_span, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(in_span, false, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Span in_span, Type in_domain, Type in_range, boolean in_io) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Span in_span, Type in_domain, Type in_range) {
        this(in_span, false, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(boolean in_parenthesized, Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(new Span(), in_parenthesized, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(boolean in_parenthesized, Type in_domain, Type in_range, boolean in_io) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(boolean in_parenthesized, Type in_domain, Type in_range) {
        this(new Span(), in_parenthesized, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause, boolean in_io) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Type in_domain, Type in_range, Option<List<Type>> in_throwsClause) {
        this(new Span(), false, in_domain, in_range, in_throwsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Type in_domain, Type in_range, boolean in_io) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), in_io);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrowType(Type in_domain, Type in_range) {
        this(new Span(), false, in_domain, in_range, Option.<List<Type>>none(), false);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ArrowType() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forArrowType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forArrowType(this); }

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
        writer.print("ArrowType:");
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

        Type temp_domain = getDomain();
        writer.startLine();
        writer.print("domain = ");
        temp_domain.outputHelp(writer, lossless);

        Type temp_range = getRange();
        writer.startLine();
        writer.print("range = ");
        temp_range.outputHelp(writer, lossless);

        Option<List<Type>> temp_throwsClause = getThrowsClause();
        writer.startLine();
        writer.print("throwsClause = ");
        if (temp_throwsClause.isSome()) {
            writer.print("(");
            List<Type> elt_temp_throwsClause = edu.rice.cs.plt.tuple.Option.unwrap(temp_throwsClause);
            if (elt_temp_throwsClause == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_throwsClause = true;
                for (Type elt_elt_temp_throwsClause : elt_temp_throwsClause) {
                    isempty_elt_temp_throwsClause = false;
                    writer.startLine("* ");
                    if (elt_elt_temp_throwsClause == null) {
                        writer.print("null");
                    } else {
                        elt_elt_temp_throwsClause.outputHelp(writer, lossless);
                    }
                }
                writer.unindent();
                if (isempty_elt_temp_throwsClause) writer.print(" }");
                else writer.startLine("}");
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        boolean temp_io = isIo();
        writer.startLine();
        writer.print("io = ");
        writer.print(temp_io);
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
            ArrowType casted = (ArrowType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Type temp_domain = getDomain();
            Type casted_domain = casted.getDomain();
            if (!(temp_domain == casted_domain || temp_domain.equals(casted_domain))) return false;
            Type temp_range = getRange();
            Type casted_range = casted.getRange();
            if (!(temp_range == casted_range || temp_range.equals(casted_range))) return false;
            Option<List<Type>> temp_throwsClause = getThrowsClause();
            Option<List<Type>> casted_throwsClause = casted.getThrowsClause();
            if (!(temp_throwsClause == casted_throwsClause || temp_throwsClause.equals(casted_throwsClause))) return false;
            boolean temp_io = isIo();
            boolean casted_io = casted.isIo();
            if (!(temp_io == casted_io)) return false;
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
        Type temp_domain = getDomain();
        code ^= temp_domain.hashCode();
        Type temp_range = getRange();
        code ^= temp_range.hashCode();
        Option<List<Type>> temp_throwsClause = getThrowsClause();
        code ^= temp_throwsClause.hashCode();
        boolean temp_io = isIo();
        code ^= temp_io ? 1231 : 1237;
        return code;
    }
}
