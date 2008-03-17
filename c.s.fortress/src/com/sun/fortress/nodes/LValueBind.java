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
 * Class LValueBind, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class LValueBind extends LValue implements LHS {
    private final Id _name;
    private final Option<Type> _type;
    private final List<Modifier> _mods;
    private final boolean _mutable;

    /**
     * Constructs a LValueBind.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public LValueBind(Span in_span, Id in_name, Option<Type> in_type, List<Modifier> in_mods, boolean in_mutable) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the LValueBind constructor was null");
        }
        _name = in_name;
        if (in_type == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'type' to the LValueBind constructor was null");
        }
        _type = in_type;
        if (in_mods == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'mods' to the LValueBind constructor was null");
        }
        _mods = in_mods;
        _mutable = in_mutable;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LValueBind(Span in_span, Id in_name, Option<Type> in_type, boolean in_mutable) {
        this(in_span, in_name, in_type, Collections.<Modifier>emptyList(), in_mutable);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LValueBind(Span in_span, Id in_name, List<Modifier> in_mods, boolean in_mutable) {
        this(in_span, in_name, Option.<Type>none(), in_mods, in_mutable);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LValueBind(Span in_span, Id in_name, boolean in_mutable) {
        this(in_span, in_name, Option.<Type>none(), Collections.<Modifier>emptyList(), in_mutable);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LValueBind(Id in_name, Option<Type> in_type, List<Modifier> in_mods, boolean in_mutable) {
        this(new Span(), in_name, in_type, in_mods, in_mutable);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LValueBind(Id in_name, Option<Type> in_type, boolean in_mutable) {
        this(new Span(), in_name, in_type, Collections.<Modifier>emptyList(), in_mutable);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LValueBind(Id in_name, List<Modifier> in_mods, boolean in_mutable) {
        this(new Span(), in_name, Option.<Type>none(), in_mods, in_mutable);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public LValueBind(Id in_name, boolean in_mutable) {
        this(new Span(), in_name, Option.<Type>none(), Collections.<Modifier>emptyList(), in_mutable);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected LValueBind() {
        _name = null;
        _type = null;
        _mods = null;
        _mutable = false;
    }

    final public Id getName() { return _name; }
    final public Option<Type> getType() { return _type; }
    final public List<Modifier> getMods() { return _mods; }
    final public boolean isMutable() { return _mutable; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forLValueBind(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forLValueBind(this); }

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
        writer.print("LValueBind:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

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

        boolean temp_mutable = isMutable();
        writer.startLine();
        writer.print("mutable = ");
        writer.print(temp_mutable);
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
            LValueBind casted = (LValueBind) obj;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Option<Type> temp_type = getType();
            Option<Type> casted_type = casted.getType();
            if (!(temp_type == casted_type || temp_type.equals(casted_type))) return false;
            List<Modifier> temp_mods = getMods();
            List<Modifier> casted_mods = casted.getMods();
            if (!(temp_mods == casted_mods || temp_mods.equals(casted_mods))) return false;
            boolean temp_mutable = isMutable();
            boolean casted_mutable = casted.isMutable();
            if (!(temp_mutable == casted_mutable)) return false;
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
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        Option<Type> temp_type = getType();
        code ^= temp_type.hashCode();
        List<Modifier> temp_mods = getMods();
        code ^= temp_mods.hashCode();
        boolean temp_mutable = isMutable();
        code ^= temp_mutable ? 1231 : 1237;
        return code;
    }
}
