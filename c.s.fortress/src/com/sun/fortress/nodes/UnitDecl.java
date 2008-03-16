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
 * Class UnitDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class UnitDecl extends DimUnitDecl {
    private final boolean _si_unit;
    private final List<Id> _units;
    private final Option<Type> _dim;
    private final Option<Expr> _def;

    /**
     * Constructs a UnitDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public UnitDecl(Span in_span, boolean in_si_unit, List<Id> in_units, Option<Type> in_dim, Option<Expr> in_def) {
        super(in_span);
        _si_unit = in_si_unit;
        if (in_units == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'units' to the UnitDecl constructor was null");
        }
        _units = in_units;
        if (in_dim == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'dim' to the UnitDecl constructor was null");
        }
        _dim = in_dim;
        if (in_def == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'def' to the UnitDecl constructor was null");
        }
        _def = in_def;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Span in_span, boolean in_si_unit, List<Id> in_units, Option<Expr> in_def) {
        this(in_span, in_si_unit, in_units, Option.<Type>none(), in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Span in_span, boolean in_si_unit, Option<Type> in_dim, Option<Expr> in_def) {
        this(in_span, in_si_unit, Collections.<Id>emptyList(), in_dim, in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Span in_span, boolean in_si_unit, Option<Expr> in_def) {
        this(in_span, in_si_unit, Collections.<Id>emptyList(), Option.<Type>none(), in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Span in_span, List<Id> in_units, Option<Type> in_dim, Option<Expr> in_def) {
        this(in_span, false, in_units, in_dim, in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Span in_span, List<Id> in_units, Option<Expr> in_def) {
        this(in_span, false, in_units, Option.<Type>none(), in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Span in_span, Option<Type> in_dim, Option<Expr> in_def) {
        this(in_span, false, Collections.<Id>emptyList(), in_dim, in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Span in_span, Option<Expr> in_def) {
        this(in_span, false, Collections.<Id>emptyList(), Option.<Type>none(), in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(boolean in_si_unit, List<Id> in_units, Option<Type> in_dim, Option<Expr> in_def) {
        this(new Span(), in_si_unit, in_units, in_dim, in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(boolean in_si_unit, List<Id> in_units, Option<Expr> in_def) {
        this(new Span(), in_si_unit, in_units, Option.<Type>none(), in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(boolean in_si_unit, Option<Type> in_dim, Option<Expr> in_def) {
        this(new Span(), in_si_unit, Collections.<Id>emptyList(), in_dim, in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(boolean in_si_unit, Option<Expr> in_def) {
        this(new Span(), in_si_unit, Collections.<Id>emptyList(), Option.<Type>none(), in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(List<Id> in_units, Option<Type> in_dim, Option<Expr> in_def) {
        this(new Span(), false, in_units, in_dim, in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(List<Id> in_units, Option<Expr> in_def) {
        this(new Span(), false, in_units, Option.<Type>none(), in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Option<Type> in_dim, Option<Expr> in_def) {
        this(new Span(), false, Collections.<Id>emptyList(), in_dim, in_def);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public UnitDecl(Option<Expr> in_def) {
        this(new Span(), false, Collections.<Id>emptyList(), Option.<Type>none(), in_def);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected UnitDecl() {
        _si_unit = false;
        _units = null;
        _dim = null;
        _def = null;
    }

    final public boolean isSi_unit() { return _si_unit; }
    final public List<Id> getUnits() { return _units; }
    final public Option<Type> getDim() { return _dim; }
    final public Option<Expr> getDef() { return _def; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forUnitDecl(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forUnitDecl(this); }

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
        writer.print("UnitDecl:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        boolean temp_si_unit = isSi_unit();
        writer.startLine();
        writer.print("si_unit = ");
        writer.print(temp_si_unit);

        List<Id> temp_units = getUnits();
        writer.startLine();
        writer.print("units = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_units = true;
        for (Id elt_temp_units : temp_units) {
            isempty_temp_units = false;
            writer.startLine("* ");
            if (elt_temp_units == null) {
                writer.print("null");
            } else {
                elt_temp_units.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_units) writer.print(" }");
        else writer.startLine("}");

        Option<Type> temp_dim = getDim();
        writer.startLine();
        writer.print("dim = ");
        if (temp_dim.isSome()) {
            writer.print("(");
            Type elt_temp_dim = edu.rice.cs.plt.tuple.Option.unwrap(temp_dim);
            if (elt_temp_dim == null) {
                writer.print("null");
            } else {
                elt_temp_dim.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<Expr> temp_def = getDef();
        writer.startLine();
        writer.print("def = ");
        if (temp_def.isSome()) {
            writer.print("(");
            Expr elt_temp_def = edu.rice.cs.plt.tuple.Option.unwrap(temp_def);
            if (elt_temp_def == null) {
                writer.print("null");
            } else {
                elt_temp_def.outputHelp(writer, lossless);
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
            UnitDecl casted = (UnitDecl) obj;
            boolean temp_si_unit = isSi_unit();
            boolean casted_si_unit = casted.isSi_unit();
            if (!(temp_si_unit == casted_si_unit)) return false;
            List<Id> temp_units = getUnits();
            List<Id> casted_units = casted.getUnits();
            if (!(temp_units == casted_units || temp_units.equals(casted_units))) return false;
            Option<Type> temp_dim = getDim();
            Option<Type> casted_dim = casted.getDim();
            if (!(temp_dim == casted_dim || temp_dim.equals(casted_dim))) return false;
            Option<Expr> temp_def = getDef();
            Option<Expr> casted_def = casted.getDef();
            if (!(temp_def == casted_def || temp_def.equals(casted_def))) return false;
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
        boolean temp_si_unit = isSi_unit();
        code ^= temp_si_unit ? 1231 : 1237;
        List<Id> temp_units = getUnits();
        code ^= temp_units.hashCode();
        Option<Type> temp_dim = getDim();
        code ^= temp_dim.hashCode();
        Option<Expr> temp_def = getDef();
        code ^= temp_def.hashCode();
        return code;
    }
}
