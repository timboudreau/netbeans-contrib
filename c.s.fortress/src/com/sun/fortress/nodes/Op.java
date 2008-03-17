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
 * Class Op, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Op extends OpName {
    private final String _text;
    private final Option<Fixity> _fixity;

    /**
     * Constructs a Op.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Op(Span in_span, String in_text, Option<Fixity> in_fixity) {
        super(in_span);
        if (in_text == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'text' to the Op constructor was null");
        }
        _text = in_text.intern();
        if (in_fixity == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'fixity' to the Op constructor was null");
        }
        _fixity = in_fixity;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Op(Span in_span, String in_text) {
        this(in_span, in_text, Option.<Fixity>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Op(String in_text, Option<Fixity> in_fixity) {
        this(new Span(), in_text, in_fixity);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Op(String in_text) {
        this(new Span(), in_text, Option.<Fixity>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Op() {
        _text = null;
        _fixity = null;
    }

    final public String getText() { return _text; }
    final public Option<Fixity> getFixity() { return _fixity; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forOp(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forOp(this); }

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
        writer.print("Op:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        String temp_text = getText();
        writer.startLine();
        writer.print("text = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_text);
            writer.print("\"");
        } else { writer.print(temp_text); }

        Option<Fixity> temp_fixity = getFixity();
        writer.startLine();
        writer.print("fixity = ");
        if (temp_fixity.isSome()) {
            writer.print("(");
            Fixity elt_temp_fixity = edu.rice.cs.plt.tuple.Option.unwrap(temp_fixity);
            if (elt_temp_fixity == null) {
                writer.print("null");
            } else {
                elt_temp_fixity.outputHelp(writer, lossless);
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
            Op casted = (Op) obj;
            String temp_text = getText();
            String casted_text = casted.getText();
            if (!(temp_text == casted_text)) return false;
            Option<Fixity> temp_fixity = getFixity();
            Option<Fixity> casted_fixity = casted.getFixity();
            if (!(temp_fixity == casted_fixity || temp_fixity.equals(casted_fixity))) return false;
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
        String temp_text = getText();
        code ^= temp_text.hashCode();
        Option<Fixity> temp_fixity = getFixity();
        code ^= temp_fixity.hashCode();
        return code;
    }
}
