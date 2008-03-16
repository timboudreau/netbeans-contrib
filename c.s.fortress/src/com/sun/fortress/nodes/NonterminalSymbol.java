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
 * Class NonterminalSymbol, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class NonterminalSymbol extends SyntaxSymbol {
    private final QualifiedIdName _nonterminal;

    /**
     * Constructs a NonterminalSymbol.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public NonterminalSymbol(Span in_span, QualifiedIdName in_nonterminal) {
        super(in_span);
        if (in_nonterminal == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'nonterminal' to the NonterminalSymbol constructor was null");
        }
        _nonterminal = in_nonterminal;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NonterminalSymbol(QualifiedIdName in_nonterminal) {
        this(new Span(), in_nonterminal);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected NonterminalSymbol() {
        _nonterminal = null;
    }

    final public QualifiedIdName getNonterminal() { return _nonterminal; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forNonterminalSymbol(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forNonterminalSymbol(this); }

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
        writer.print("NonterminalSymbol:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        QualifiedIdName temp_nonterminal = getNonterminal();
        writer.startLine();
        writer.print("nonterminal = ");
        temp_nonterminal.outputHelp(writer, lossless);
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
            NonterminalSymbol casted = (NonterminalSymbol) obj;
            QualifiedIdName temp_nonterminal = getNonterminal();
            QualifiedIdName casted_nonterminal = casted.getNonterminal();
            if (!(temp_nonterminal == casted_nonterminal || temp_nonterminal.equals(casted_nonterminal))) return false;
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
        QualifiedIdName temp_nonterminal = getNonterminal();
        code ^= temp_nonterminal.hashCode();
        return code;
    }
}
