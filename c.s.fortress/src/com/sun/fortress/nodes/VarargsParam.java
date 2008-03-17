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
 * Class VarargsParam, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class VarargsParam extends Param {
    private final VarargsType _varargsType;

    /**
     * Constructs a VarargsParam.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public VarargsParam(Span in_span, List<Modifier> in_mods, Id in_name, VarargsType in_varargsType) {
        super(in_span, in_mods, in_name);
        if (in_varargsType == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'varargsType' to the VarargsParam constructor was null");
        }
        _varargsType = in_varargsType;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarargsParam(Span in_span, Id in_name, VarargsType in_varargsType) {
        this(in_span, Collections.<Modifier>emptyList(), in_name, in_varargsType);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarargsParam(List<Modifier> in_mods, Id in_name, VarargsType in_varargsType) {
        this(new Span(), in_mods, in_name, in_varargsType);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarargsParam(Id in_name, VarargsType in_varargsType) {
        this(new Span(), Collections.<Modifier>emptyList(), in_name, in_varargsType);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected VarargsParam() {
        _varargsType = null;
    }

    final public VarargsType getVarargsType() { return _varargsType; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forVarargsParam(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forVarargsParam(this); }

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
        writer.print("VarargsParam:");
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

        VarargsType temp_varargsType = getVarargsType();
        writer.startLine();
        writer.print("varargsType = ");
        temp_varargsType.outputHelp(writer, lossless);
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
            VarargsParam casted = (VarargsParam) obj;
            List<Modifier> temp_mods = getMods();
            List<Modifier> casted_mods = casted.getMods();
            if (!(temp_mods == casted_mods || temp_mods.equals(casted_mods))) return false;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            VarargsType temp_varargsType = getVarargsType();
            VarargsType casted_varargsType = casted.getVarargsType();
            if (!(temp_varargsType == casted_varargsType || temp_varargsType.equals(casted_varargsType))) return false;
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
        VarargsType temp_varargsType = getVarargsType();
        code ^= temp_varargsType.hashCode();
        return code;
    }
}
