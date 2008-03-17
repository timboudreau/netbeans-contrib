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
 * Class KeywordSymbol, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class KeywordSymbol extends SyntaxSymbol {
    private final String _token;

    /**
     * Constructs a KeywordSymbol.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public KeywordSymbol(Span in_span, String in_token) {
        super(in_span);
        if (in_token == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'token' to the KeywordSymbol constructor was null");
        }
        _token = in_token.intern();
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public KeywordSymbol(String in_token) {
        this(new Span(), in_token);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected KeywordSymbol() {
        _token = null;
    }

    final public String getToken() { return _token; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forKeywordSymbol(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forKeywordSymbol(this); }

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
        writer.print("KeywordSymbol:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        String temp_token = getToken();
        writer.startLine();
        writer.print("token = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_token);
            writer.print("\"");
        } else { writer.print(temp_token); }
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
            KeywordSymbol casted = (KeywordSymbol) obj;
            String temp_token = getToken();
            String casted_token = casted.getToken();
            if (!(temp_token == casted_token)) return false;
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
        String temp_token = getToken();
        code ^= temp_token.hashCode();
        return code;
    }
}
