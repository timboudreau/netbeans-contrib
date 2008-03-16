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
 * Class CharSymbol, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class CharSymbol extends CharacterSymbol {
    private final String _string;

    /**
     * Constructs a CharSymbol.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public CharSymbol(Span in_span, String in_string) {
        super(in_span);
        if (in_string == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'string' to the CharSymbol constructor was null");
        }
        _string = in_string.intern();
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CharSymbol(String in_string) {
        this(new Span(), in_string);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected CharSymbol() {
        _string = null;
    }

    final public String getString() { return _string; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forCharSymbol(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forCharSymbol(this); }

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
        writer.print("CharSymbol:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        String temp_string = getString();
        writer.startLine();
        writer.print("string = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_string);
            writer.print("\"");
        } else { writer.print(temp_string); }
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
            CharSymbol casted = (CharSymbol) obj;
            String temp_string = getString();
            String casted_string = casted.getString();
            if (!(temp_string == casted_string)) return false;
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
        String temp_string = getString();
        code ^= temp_string.hashCode();
        return code;
    }
}
