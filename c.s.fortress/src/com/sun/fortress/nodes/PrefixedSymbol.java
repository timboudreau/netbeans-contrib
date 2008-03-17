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
 * Class PrefixedSymbol, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class PrefixedSymbol extends SyntaxSymbol {
    private final Option<Id> _id;
    private final SyntaxSymbol _symbol;

    /**
     * Constructs a PrefixedSymbol.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public PrefixedSymbol(Span in_span, Option<Id> in_id, SyntaxSymbol in_symbol) {
        super(in_span);
        if (in_id == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'id' to the PrefixedSymbol constructor was null");
        }
        _id = in_id;
        if (in_symbol == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'symbol' to the PrefixedSymbol constructor was null");
        }
        _symbol = in_symbol;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public PrefixedSymbol(Option<Id> in_id, SyntaxSymbol in_symbol) {
        this(new Span(), in_id, in_symbol);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected PrefixedSymbol() {
        _id = null;
        _symbol = null;
    }

    final public Option<Id> getId() { return _id; }
    final public SyntaxSymbol getSymbol() { return _symbol; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forPrefixedSymbol(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forPrefixedSymbol(this); }

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
        writer.print("PrefixedSymbol:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Option<Id> temp_id = getId();
        writer.startLine();
        writer.print("id = ");
        if (temp_id.isSome()) {
            writer.print("(");
            Id elt_temp_id = edu.rice.cs.plt.tuple.Option.unwrap(temp_id);
            if (elt_temp_id == null) {
                writer.print("null");
            } else {
                elt_temp_id.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

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
            PrefixedSymbol casted = (PrefixedSymbol) obj;
            Option<Id> temp_id = getId();
            Option<Id> casted_id = casted.getId();
            if (!(temp_id == casted_id || temp_id.equals(casted_id))) return false;
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
        Option<Id> temp_id = getId();
        code ^= temp_id.hashCode();
        SyntaxSymbol temp_symbol = getSymbol();
        code ^= temp_symbol.hashCode();
        return code;
    }
}
