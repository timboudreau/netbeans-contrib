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
 * Class DimDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class DimDecl extends DimUnitDecl {
    private final Id _dim;
    private final Option<Type> _derived;
    private final Option<Id> _default;

    /**
     * Constructs a DimDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public DimDecl(Span in_span, Id in_dim, Option<Type> in_derived, Option<Id> in_default) {
        super(in_span);
        if (in_dim == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'dim' to the DimDecl constructor was null");
        }
        _dim = in_dim;
        if (in_derived == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'derived' to the DimDecl constructor was null");
        }
        _derived = in_derived;
        if (in_default == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'default' to the DimDecl constructor was null");
        }
        _default = in_default;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimDecl(Span in_span, Id in_dim, Option<Type> in_derived) {
        this(in_span, in_dim, in_derived, Option.<Id>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimDecl(Span in_span, Id in_dim) {
        this(in_span, in_dim, Option.<Type>none(), Option.<Id>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimDecl(Id in_dim, Option<Type> in_derived, Option<Id> in_default) {
        this(new Span(), in_dim, in_derived, in_default);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimDecl(Id in_dim, Option<Type> in_derived) {
        this(new Span(), in_dim, in_derived, Option.<Id>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimDecl(Id in_dim) {
        this(new Span(), in_dim, Option.<Type>none(), Option.<Id>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected DimDecl() {
        _dim = null;
        _derived = null;
        _default = null;
    }

    final public Id getDim() { return _dim; }
    final public Option<Type> getDerived() { return _derived; }
    final public Option<Id> getDefault() { return _default; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forDimDecl(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forDimDecl(this); }

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
        writer.print("DimDecl:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Id temp_dim = getDim();
        writer.startLine();
        writer.print("dim = ");
        temp_dim.outputHelp(writer, lossless);

        Option<Type> temp_derived = getDerived();
        writer.startLine();
        writer.print("derived = ");
        if (temp_derived.isSome()) {
            writer.print("(");
            Type elt_temp_derived = edu.rice.cs.plt.tuple.Option.unwrap(temp_derived);
            if (elt_temp_derived == null) {
                writer.print("null");
            } else {
                elt_temp_derived.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<Id> temp_default = getDefault();
        writer.startLine();
        writer.print("default = ");
        if (temp_default.isSome()) {
            writer.print("(");
            Id elt_temp_default = edu.rice.cs.plt.tuple.Option.unwrap(temp_default);
            if (elt_temp_default == null) {
                writer.print("null");
            } else {
                elt_temp_default.outputHelp(writer, lossless);
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
            DimDecl casted = (DimDecl) obj;
            Id temp_dim = getDim();
            Id casted_dim = casted.getDim();
            if (!(temp_dim == casted_dim || temp_dim.equals(casted_dim))) return false;
            Option<Type> temp_derived = getDerived();
            Option<Type> casted_derived = casted.getDerived();
            if (!(temp_derived == casted_derived || temp_derived.equals(casted_derived))) return false;
            Option<Id> temp_default = getDefault();
            Option<Id> casted_default = casted.getDefault();
            if (!(temp_default == casted_default || temp_default.equals(casted_default))) return false;
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
        Id temp_dim = getDim();
        code ^= temp_dim.hashCode();
        Option<Type> temp_derived = getDerived();
        code ^= temp_derived.hashCode();
        Option<Id> temp_default = getDefault();
        code ^= temp_default.hashCode();
        return code;
    }
}
