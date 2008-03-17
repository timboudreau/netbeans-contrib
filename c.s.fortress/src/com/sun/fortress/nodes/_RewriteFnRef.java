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
 * Class _RewriteFnRef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class _RewriteFnRef extends FunctionalRef {
    private final Expr _fn;

    /**
     * Constructs a _RewriteFnRef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public _RewriteFnRef(Span in_span, boolean in_parenthesized, Expr in_fn, List<StaticArg> in_staticArgs) {
        super(in_span, in_parenthesized, in_staticArgs);
        if (in_fn == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'fn' to the _RewriteFnRef constructor was null");
        }
        _fn = in_fn;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteFnRef(Span in_span, boolean in_parenthesized, Expr in_fn) {
        this(in_span, in_parenthesized, in_fn, Collections.<StaticArg>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteFnRef(Span in_span, Expr in_fn, List<StaticArg> in_staticArgs) {
        this(in_span, false, in_fn, in_staticArgs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteFnRef(Span in_span, Expr in_fn) {
        this(in_span, false, in_fn, Collections.<StaticArg>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteFnRef(boolean in_parenthesized, Expr in_fn, List<StaticArg> in_staticArgs) {
        this(new Span(), in_parenthesized, in_fn, in_staticArgs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteFnRef(boolean in_parenthesized, Expr in_fn) {
        this(new Span(), in_parenthesized, in_fn, Collections.<StaticArg>emptyList());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteFnRef(Expr in_fn, List<StaticArg> in_staticArgs) {
        this(new Span(), false, in_fn, in_staticArgs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteFnRef(Expr in_fn) {
        this(new Span(), false, in_fn, Collections.<StaticArg>emptyList());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected _RewriteFnRef() {
        _fn = null;
    }

    final public Expr getFn() { return _fn; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.for_RewriteFnRef(this); }
    public void accept(NodeVisitor_void visitor) { visitor.for_RewriteFnRef(this); }

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
        writer.print("_RewriteFnRef:");
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

        Expr temp_fn = getFn();
        writer.startLine();
        writer.print("fn = ");
        temp_fn.outputHelp(writer, lossless);

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
            _RewriteFnRef casted = (_RewriteFnRef) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_fn = getFn();
            Expr casted_fn = casted.getFn();
            if (!(temp_fn == casted_fn || temp_fn.equals(casted_fn))) return false;
            List<StaticArg> temp_staticArgs = getStaticArgs();
            List<StaticArg> casted_staticArgs = casted.getStaticArgs();
            if (!(temp_staticArgs == casted_staticArgs || temp_staticArgs.equals(casted_staticArgs))) return false;
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
        Expr temp_fn = getFn();
        code ^= temp_fn.hashCode();
        List<StaticArg> temp_staticArgs = getStaticArgs();
        code ^= temp_staticArgs.hashCode();
        return code;
    }
}
