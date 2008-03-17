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
 * Class OptionalSymbol, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class OptionalSymbol extends SyntaxSymbol {
    private final SyntaxSymbol _symbol;

    /**
     * Constructs a OptionalSymbol.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public OptionalSymbol(Span in_span, SyntaxSymbol in_symbol) {
        super(in_span);
        if (in_symbol == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'symbol' to the OptionalSymbol constructor was null");
        }
        _symbol = in_symbol;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public OptionalSymbol(SyntaxSymbol in_symbol) {
        this(new Span(), in_symbol);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected OptionalSymbol() {
        _symbol = null;
    }

    final public SyntaxSymbol getSymbol() { return _symbol; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forOptionalSymbol(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forOptionalSymbol(this); }

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
        writer.print("OptionalSymbol:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        SyntaxSymbol temp_symbol = getSymbol();
        writer.startLine();
        writer.print("symbol = ");
        temp_symbol.outputHelp(writer, lossless);
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
            OptionalSymbol casted = (OptionalSymbol) obj;
            SyntaxSymbol temp_symbol = getSymbol();
            SyntaxSymbol casted_symbol = casted.getSymbol();
            if (!(temp_symbol == casted_symbol || temp_symbol.equals(casted_symbol))) return false;
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
        SyntaxSymbol temp_symbol = getSymbol();
        code ^= temp_symbol.hashCode();
        return code;
    }
}
