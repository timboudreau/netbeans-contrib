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
 * Class ExtentRange, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ExtentRange extends AbstractNode {
    private final Option<StaticArg> _base;
    private final Option<StaticArg> _size;

    /**
     * Constructs a ExtentRange.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ExtentRange(Span in_span, Option<StaticArg> in_base, Option<StaticArg> in_size) {
        super(in_span);
        if (in_base == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'base' to the ExtentRange constructor was null");
        }
        _base = in_base;
        if (in_size == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'size' to the ExtentRange constructor was null");
        }
        _size = in_size;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExtentRange(Option<StaticArg> in_base, Option<StaticArg> in_size) {
        this(new Span(), in_base, in_size);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ExtentRange() {
        _base = null;
        _size = null;
    }

    final public Option<StaticArg> getBase() { return _base; }
    final public Option<StaticArg> getSize() { return _size; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forExtentRange(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forExtentRange(this); }

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
        writer.print("ExtentRange:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Option<StaticArg> temp_base = getBase();
        writer.startLine();
        writer.print("base = ");
        if (temp_base.isSome()) {
            writer.print("(");
            StaticArg elt_temp_base = edu.rice.cs.plt.tuple.Option.unwrap(temp_base);
            if (elt_temp_base == null) {
                writer.print("null");
            } else {
                elt_temp_base.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<StaticArg> temp_size = getSize();
        writer.startLine();
        writer.print("size = ");
        if (temp_size.isSome()) {
            writer.print("(");
            StaticArg elt_temp_size = edu.rice.cs.plt.tuple.Option.unwrap(temp_size);
            if (elt_temp_size == null) {
                writer.print("null");
            } else {
                elt_temp_size.outputHelp(writer, lossless);
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
            ExtentRange casted = (ExtentRange) obj;
            Option<StaticArg> temp_base = getBase();
            Option<StaticArg> casted_base = casted.getBase();
            if (!(temp_base == casted_base || temp_base.equals(casted_base))) return false;
            Option<StaticArg> temp_size = getSize();
            Option<StaticArg> casted_size = casted.getSize();
            if (!(temp_size == casted_size || temp_size.equals(casted_size))) return false;
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
        Option<StaticArg> temp_base = getBase();
        code ^= temp_base.hashCode();
        Option<StaticArg> temp_size = getSize();
        code ^= temp_size.hashCode();
        return code;
    }
}
