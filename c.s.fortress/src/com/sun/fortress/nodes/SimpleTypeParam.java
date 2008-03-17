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
 * Class SimpleTypeParam, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class SimpleTypeParam extends IdStaticParam {
    private final List<TraitType> _extendsClause;
    private final boolean _absorbs;

    /**
     * Constructs a SimpleTypeParam.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public SimpleTypeParam(Span in_span, Id in_name, List<TraitType> in_extendsClause, boolean in_absorbs) {
        super(in_span, in_name);
        if (in_extendsClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'extendsClause' to the SimpleTypeParam constructor was null");
        }
        _extendsClause = in_extendsClause;
        _absorbs = in_absorbs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SimpleTypeParam(Span in_span, Id in_name, List<TraitType> in_extendsClause) {
        this(in_span, in_name, in_extendsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SimpleTypeParam(Span in_span, Id in_name, boolean in_absorbs) {
        this(in_span, in_name, Collections.<TraitType>emptyList(), in_absorbs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SimpleTypeParam(Span in_span, Id in_name) {
        this(in_span, in_name, Collections.<TraitType>emptyList(), false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SimpleTypeParam(Id in_name, List<TraitType> in_extendsClause, boolean in_absorbs) {
        this(new Span(), in_name, in_extendsClause, in_absorbs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SimpleTypeParam(Id in_name, List<TraitType> in_extendsClause) {
        this(new Span(), in_name, in_extendsClause, false);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SimpleTypeParam(Id in_name, boolean in_absorbs) {
        this(new Span(), in_name, Collections.<TraitType>emptyList(), in_absorbs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SimpleTypeParam(Id in_name) {
        this(new Span(), in_name, Collections.<TraitType>emptyList(), false);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected SimpleTypeParam() {
        _extendsClause = null;
        _absorbs = false;
    }

    final public List<TraitType> getExtendsClause() { return _extendsClause; }
    final public boolean isAbsorbs() { return _absorbs; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forSimpleTypeParam(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forSimpleTypeParam(this); }

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
        writer.print("SimpleTypeParam:");
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

        List<TraitType> temp_extendsClause = getExtendsClause();
        writer.startLine();
        writer.print("extendsClause = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_extendsClause = true;
        for (TraitType elt_temp_extendsClause : temp_extendsClause) {
            isempty_temp_extendsClause = false;
            writer.startLine("* ");
            if (elt_temp_extendsClause == null) {
                writer.print("null");
            } else {
                elt_temp_extendsClause.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_extendsClause) writer.print(" }");
        else writer.startLine("}");

        boolean temp_absorbs = isAbsorbs();
        writer.startLine();
        writer.print("absorbs = ");
        writer.print(temp_absorbs);
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
            SimpleTypeParam casted = (SimpleTypeParam) obj;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<TraitType> temp_extendsClause = getExtendsClause();
            List<TraitType> casted_extendsClause = casted.getExtendsClause();
            if (!(temp_extendsClause == casted_extendsClause || temp_extendsClause.equals(casted_extendsClause))) return false;
            boolean temp_absorbs = isAbsorbs();
            boolean casted_absorbs = casted.isAbsorbs();
            if (!(temp_absorbs == casted_absorbs)) return false;
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
        List<TraitType> temp_extendsClause = getExtendsClause();
        code ^= temp_extendsClause.hashCode();
        boolean temp_absorbs = isAbsorbs();
        code ^= temp_absorbs ? 1231 : 1237;
        return code;
    }
}
