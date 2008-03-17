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
 * Class Enclosing, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Enclosing extends OpName {
    private final Op _open;
    private final Op _close;

    /**
     * Constructs a Enclosing.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Enclosing(Span in_span, Op in_open, Op in_close) {
        super(in_span);
        if (in_open == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'open' to the Enclosing constructor was null");
        }
        _open = in_open;
        if (in_close == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'close' to the Enclosing constructor was null");
        }
        _close = in_close;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Enclosing(Op in_open, Op in_close) {
        this(new Span(), in_open, in_close);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Enclosing() {
        _open = null;
        _close = null;
    }

    final public Op getOpen() { return _open; }
    final public Op getClose() { return _close; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forEnclosing(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forEnclosing(this); }

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
        writer.print("Enclosing:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Op temp_open = getOpen();
        writer.startLine();
        writer.print("open = ");
        temp_open.outputHelp(writer, lossless);

        Op temp_close = getClose();
        writer.startLine();
        writer.print("close = ");
        temp_close.outputHelp(writer, lossless);
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
            Enclosing casted = (Enclosing) obj;
            Op temp_open = getOpen();
            Op casted_open = casted.getOpen();
            if (!(temp_open == casted_open || temp_open.equals(casted_open))) return false;
            Op temp_close = getClose();
            Op casted_close = casted.getClose();
            if (!(temp_close == casted_close || temp_close.equals(casted_close))) return false;
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
        Op temp_open = getOpen();
        code ^= temp_open.hashCode();
        Op temp_close = getClose();
        code ^= temp_close.hashCode();
        return code;
    }
}
