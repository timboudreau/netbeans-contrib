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
 * Class DimArg, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class DimArg extends StaticArg {
    private final DimExpr _dim;

    /**
     * Constructs a DimArg.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public DimArg(Span in_span, boolean in_parenthesized, DimExpr in_dim) {
        super(in_span, in_parenthesized);
        if (in_dim == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'dim' to the DimArg constructor was null");
        }
        _dim = in_dim;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimArg(Span in_span, DimExpr in_dim) {
        this(in_span, false, in_dim);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimArg(boolean in_parenthesized, DimExpr in_dim) {
        this(new Span(), in_parenthesized, in_dim);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public DimArg(DimExpr in_dim) {
        this(new Span(), false, in_dim);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected DimArg() {
        _dim = null;
    }

    final public DimExpr getDim() { return _dim; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forDimArg(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forDimArg(this); }

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
        writer.print("DimArg:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        boolean temp_parenthesized = isParenthesized();
        writer.startLine();
        writer.print("parenthesized = ");
        writer.print(temp_parenthesized);

        DimExpr temp_dim = getDim();
        writer.startLine();
        writer.print("dim = ");
        temp_dim.outputHelp(writer, lossless);
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
            DimArg casted = (DimArg) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            DimExpr temp_dim = getDim();
            DimExpr casted_dim = casted.getDim();
            if (!(temp_dim == casted_dim || temp_dim.equals(casted_dim))) return false;
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
        boolean temp_parenthesized = isParenthesized();
        code ^= temp_parenthesized ? 1231 : 1237;
        DimExpr temp_dim = getDim();
        code ^= temp_dim.hashCode();
        return code;
    }
}
