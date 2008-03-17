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
 * Class ConstructorFnName, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ConstructorFnName extends SimpleName {
    private final GenericWithParams _def;

    /**
     * Constructs a ConstructorFnName.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ConstructorFnName(Span in_span, GenericWithParams in_def) {
        super(in_span);
        if (in_def == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'def' to the ConstructorFnName constructor was null");
        }
        _def = in_def;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ConstructorFnName(GenericWithParams in_def) {
        this(new Span(), in_def);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ConstructorFnName() {
        _def = null;
    }

    final public GenericWithParams getDef() { return _def; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forConstructorFnName(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forConstructorFnName(this); }

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
        writer.print("ConstructorFnName:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        GenericWithParams temp_def = getDef();
        writer.startLine();
        writer.print("def = ");
        temp_def.outputHelp(writer, lossless);
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
            ConstructorFnName casted = (ConstructorFnName) obj;
            GenericWithParams temp_def = getDef();
            GenericWithParams casted_def = casted.getDef();
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
        GenericWithParams temp_def = getDef();
        code ^= temp_def.hashCode();
        return code;
    }
}
