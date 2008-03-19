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
 * Class ArgType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ArgType extends AbstractTupleType {
    private final Option<VarargsType> _varargs;
    private final List<KeywordType> _keywords;
    private final boolean _inArrow;

    /**
     * Constructs a ArgType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords, boolean in_inArrow) {
        super(in_span, in_parenthesized, in_elements);
        if (in_varargs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'varargs' to the ArgType constructor was null");
        }
        _varargs = in_varargs;
        if (in_keywords == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'keywords' to the ArgType constructor was null");
        }
        _keywords = in_keywords;
        _inArrow = in_inArrow;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords) {
        this(in_span, in_parenthesized, in_elements, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs, boolean in_inArrow) {
        this(in_span, in_parenthesized, in_elements, in_varargs, Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs) {
        this(in_span, in_parenthesized, in_elements, in_varargs, Collections.<KeywordType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements, List<KeywordType> in_keywords, boolean in_inArrow) {
        this(in_span, in_parenthesized, in_elements, Option.<VarargsType>none(), in_keywords, in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements, List<KeywordType> in_keywords) {
        this(in_span, in_parenthesized, in_elements, Option.<VarargsType>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements, boolean in_inArrow) {
        this(in_span, in_parenthesized, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, boolean in_parenthesized, List<Type> in_elements) {
        this(in_span, in_parenthesized, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords, boolean in_inArrow) {
        this(in_span, false, in_elements, in_varargs, in_keywords, in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords) {
        this(in_span, false, in_elements, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements, Option<VarargsType> in_varargs, boolean in_inArrow) {
        this(in_span, false, in_elements, in_varargs, Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements, Option<VarargsType> in_varargs) {
        this(in_span, false, in_elements, in_varargs, Collections.<KeywordType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements, List<KeywordType> in_keywords, boolean in_inArrow) {
        this(in_span, false, in_elements, Option.<VarargsType>none(), in_keywords, in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements, List<KeywordType> in_keywords) {
        this(in_span, false, in_elements, Option.<VarargsType>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements, boolean in_inArrow) {
        this(in_span, false, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(Span in_span, List<Type> in_elements) {
        this(in_span, false, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords, boolean in_inArrow) {
        this(new Span(), in_parenthesized, in_elements, in_varargs, in_keywords, in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords) {
        this(new Span(), in_parenthesized, in_elements, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs, boolean in_inArrow) {
        this(new Span(), in_parenthesized, in_elements, in_varargs, Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements, Option<VarargsType> in_varargs) {
        this(new Span(), in_parenthesized, in_elements, in_varargs, Collections.<KeywordType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements, List<KeywordType> in_keywords, boolean in_inArrow) {
        this(new Span(), in_parenthesized, in_elements, Option.<VarargsType>none(), in_keywords, in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements, List<KeywordType> in_keywords) {
        this(new Span(), in_parenthesized, in_elements, Option.<VarargsType>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements, boolean in_inArrow) {
        this(new Span(), in_parenthesized, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(boolean in_parenthesized, List<Type> in_elements) {
        this(new Span(), in_parenthesized, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords, boolean in_inArrow) {
        this(new Span(), false, in_elements, in_varargs, in_keywords, in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements, Option<VarargsType> in_varargs, List<KeywordType> in_keywords) {
        this(new Span(), false, in_elements, in_varargs, in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements, Option<VarargsType> in_varargs, boolean in_inArrow) {
        this(new Span(), false, in_elements, in_varargs, Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements, Option<VarargsType> in_varargs) {
        this(new Span(), false, in_elements, in_varargs, Collections.<KeywordType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements, List<KeywordType> in_keywords, boolean in_inArrow) {
        this(new Span(), false, in_elements, Option.<VarargsType>none(), in_keywords, in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements, List<KeywordType> in_keywords) {
        this(new Span(), false, in_elements, Option.<VarargsType>none(), in_keywords, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements, boolean in_inArrow) {
        this(new Span(), false, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), in_inArrow);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArgType(List<Type> in_elements) {
        this(new Span(), false, in_elements, Option.<VarargsType>none(), Collections.<KeywordType>emptyList(), false);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ArgType() {
        _varargs = null;
        _keywords = null;
        _inArrow = false;
    }

    final public Option<VarargsType> getVarargs() { return _varargs; }
    final public List<KeywordType> getKeywords() { return _keywords; }
    final public boolean isInArrow() { return _inArrow; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forArgType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forArgType(this); }

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
        writer.print("ArgType:");
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

        List<Type> temp_elements = getElements();
        writer.startLine();
        writer.print("elements = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_elements = true;
        for (Type elt_temp_elements : temp_elements) {
            isempty_temp_elements = false;
            writer.startLine("* ");
            if (elt_temp_elements == null) {
                writer.print("null");
            } else {
                elt_temp_elements.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_elements) writer.print(" }");
        else writer.startLine("}");

        Option<VarargsType> temp_varargs = getVarargs();
        writer.startLine();
        writer.print("varargs = ");
        if (temp_varargs.isSome()) {
            writer.print("(");
            VarargsType elt_temp_varargs = edu.rice.cs.plt.tuple.Option.unwrap(temp_varargs);
            if (elt_temp_varargs == null) {
                writer.print("null");
            } else {
                elt_temp_varargs.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        List<KeywordType> temp_keywords = getKeywords();
        writer.startLine();
        writer.print("keywords = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_keywords = true;
        for (KeywordType elt_temp_keywords : temp_keywords) {
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

        boolean temp_inArrow = isInArrow();
        writer.startLine();
        writer.print("inArrow = ");
        writer.print(temp_inArrow);
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
            ArgType casted = (ArgType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<Type> temp_elements = getElements();
            List<Type> casted_elements = casted.getElements();
            if (!(temp_elements == casted_elements || temp_elements.equals(casted_elements))) return false;
            Option<VarargsType> temp_varargs = getVarargs();
            Option<VarargsType> casted_varargs = casted.getVarargs();
            if (!(temp_varargs == casted_varargs || temp_varargs.equals(casted_varargs))) return false;
            List<KeywordType> temp_keywords = getKeywords();
            List<KeywordType> casted_keywords = casted.getKeywords();
            if (!(temp_keywords == casted_keywords || temp_keywords.equals(casted_keywords))) return false;
            boolean temp_inArrow = isInArrow();
            boolean casted_inArrow = casted.isInArrow();
            if (!(temp_inArrow == casted_inArrow)) return false;
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
        List<Type> temp_elements = getElements();
        code ^= temp_elements.hashCode();
        Option<VarargsType> temp_varargs = getVarargs();
        code ^= temp_varargs.hashCode();
        List<KeywordType> temp_keywords = getKeywords();
        code ^= temp_keywords.hashCode();
        boolean temp_inArrow = isInArrow();
        code ^= temp_inArrow ? 1231 : 1237;
        return code;
    }
}
