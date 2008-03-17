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
 * Class CharacterInterval, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class CharacterInterval extends CharacterSymbol {
    private final String _begin;
    private final String _end;

    /**
     * Constructs a CharacterInterval.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public CharacterInterval(Span in_span, String in_begin, String in_end) {
        super(in_span);
        if (in_begin == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'begin' to the CharacterInterval constructor was null");
        }
        _begin = in_begin.intern();
        if (in_end == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'end' to the CharacterInterval constructor was null");
        }
        _end = in_end.intern();
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CharacterInterval(String in_begin, String in_end) {
        this(new Span(), in_begin, in_end);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected CharacterInterval() {
        _begin = null;
        _end = null;
    }

    final public String getBegin() { return _begin; }
    final public String getEnd() { return _end; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forCharacterInterval(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forCharacterInterval(this); }

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
        writer.print("CharacterInterval:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        String temp_begin = getBegin();
        writer.startLine();
        writer.print("begin = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_begin);
            writer.print("\"");
        } else { writer.print(temp_begin); }

        String temp_end = getEnd();
        writer.startLine();
        writer.print("end = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_end);
            writer.print("\"");
        } else { writer.print(temp_end); }
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
            CharacterInterval casted = (CharacterInterval) obj;
            String temp_begin = getBegin();
            String casted_begin = casted.getBegin();
            if (!(temp_begin == casted_begin)) return false;
            String temp_end = getEnd();
            String casted_end = casted.getEnd();
            if (!(temp_end == casted_end)) return false;
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
        String temp_begin = getBegin();
        code ^= temp_begin.hashCode();
        String temp_end = getEnd();
        code ^= temp_end.hashCode();
        return code;
    }
}
