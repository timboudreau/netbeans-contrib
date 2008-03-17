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
 * Class NormalParam, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class NormalParam extends Param {
    private final Option<Type> _type;
    private final Option<Expr> _defaultExpr;

    /**
     * Constructs a NormalParam.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public NormalParam(Span in_span, List<Modifier> in_mods, Id in_name, Option<Type> in_type, Option<Expr> in_defaultExpr) {
        super(in_span, in_mods, in_name);
        if (in_type == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'type' to the NormalParam constructor was null");
        }
        _type = in_type;
        if (in_defaultExpr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'defaultExpr' to the NormalParam constructor was null");
        }
        _defaultExpr = in_defaultExpr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Span in_span, List<Modifier> in_mods, Id in_name, Option<Type> in_type) {
        this(in_span, in_mods, in_name, in_type, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Span in_span, List<Modifier> in_mods, Id in_name) {
        this(in_span, in_mods, in_name, Option.<Type>none(), Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Span in_span, Id in_name, Option<Type> in_type, Option<Expr> in_defaultExpr) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_type, in_defaultExpr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Span in_span, Id in_name, Option<Type> in_type) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_type, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Span in_span, Id in_name) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, Option.<Type>none(), Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(List<Modifier> in_mods, Id in_name, Option<Type> in_type, Option<Expr> in_defaultExpr) {
        this(new Span(), in_mods, in_name, in_type, in_defaultExpr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(List<Modifier> in_mods, Id in_name, Option<Type> in_type) {
        this(new Span(), in_mods, in_name, in_type, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(List<Modifier> in_mods, Id in_name) {
        this(new Span(), in_mods, in_name, Option.<Type>none(), Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Id in_name, Option<Type> in_type, Option<Expr> in_defaultExpr) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_type, in_defaultExpr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Id in_name, Option<Type> in_type) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_type, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NormalParam(Id in_name) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, Option.<Type>none(), Option.<Expr>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected NormalParam() {
        _type = null;
        _defaultExpr = null;
    }

    final public Option<Type> getType() { return _type; }
    final public Option<Expr> getDefaultExpr() { return _defaultExpr; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forNormalParam(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forNormalParam(this); }

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
        writer.print("NormalParam:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<Modifier> temp_mods = getMods();
        writer.startLine();
        writer.print("mods = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_mods = true;
        for (Modifier elt_temp_mods : temp_mods) {
            isempty_temp_mods = false;
            writer.startLine("* ");
            if (elt_temp_mods == null) {
                writer.print("null");
            } else {
                elt_temp_mods.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_mods) writer.print(" }");
        else writer.startLine("}");

        Id temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        Option<Type> temp_type = getType();
        writer.startLine();
        writer.print("type = ");
        if (temp_type.isSome()) {
            writer.print("(");
            Type elt_temp_type = edu.rice.cs.plt.tuple.Option.unwrap(temp_type);
            if (elt_temp_type == null) {
                writer.print("null");
            } else {
                elt_temp_type.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<Expr> temp_defaultExpr = getDefaultExpr();
        writer.startLine();
        writer.print("defaultExpr = ");
        if (temp_defaultExpr.isSome()) {
            writer.print("(");
            Expr elt_temp_defaultExpr = edu.rice.cs.plt.tuple.Option.unwrap(temp_defaultExpr);
            if (elt_temp_defaultExpr == null) {
                writer.print("null");
            } else {
                elt_temp_defaultExpr.outputHelp(writer, lossless);
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
            NormalParam casted = (NormalParam) obj;
            List<Modifier> temp_mods = getMods();
            List<Modifier> casted_mods = casted.getMods();
            if (!(temp_mods == casted_mods || temp_mods.equals(casted_mods))) return false;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Option<Type> temp_type = getType();
            Option<Type> casted_type = casted.getType();
            if (!(temp_type == casted_type || temp_type.equals(casted_type))) return false;
            Option<Expr> temp_defaultExpr = getDefaultExpr();
            Option<Expr> casted_defaultExpr = casted.getDefaultExpr();
            if (!(temp_defaultExpr == casted_defaultExpr || temp_defaultExpr.equals(casted_defaultExpr))) return false;
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
        List<Modifier> temp_mods = getMods();
        code ^= temp_mods.hashCode();
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        Option<Type> temp_type = getType();
        code ^= temp_type.hashCode();
        Option<Expr> temp_defaultExpr = getDefaultExpr();
        code ^= temp_defaultExpr.hashCode();
        return code;
    }
}
