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
 * Class ItemSymbol, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ItemSymbol extends SyntaxSymbol {
    private final String _item;

    /**
     * Constructs a ItemSymbol.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ItemSymbol(Span in_span, String in_item) {
        super(in_span);
        if (in_item == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'item' to the ItemSymbol constructor was null");
        }
        _item = in_item.intern();
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ItemSymbol(String in_item) {
        this(new Span(), in_item);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ItemSymbol() {
        _item = null;
    }

    final public String getItem() { return _item; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forItemSymbol(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forItemSymbol(this); }

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
        writer.print("ItemSymbol:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        String temp_item = getItem();
        writer.startLine();
        writer.print("item = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_item);
            writer.print("\"");
        } else { writer.print(temp_item); }
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
            ItemSymbol casted = (ItemSymbol) obj;
            String temp_item = getItem();
            String casted_item = casted.getItem();
            if (!(temp_item == casted_item)) return false;
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
        String temp_item = getItem();
        code ^= temp_item.hashCode();
        return code;
    }
}
