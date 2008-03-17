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
 * Class ProductDim, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ProductDim extends DimExpr {
    private final DimExpr _multiplier;
    private final DimExpr _multiplicand;

    /**
     * Constructs a ProductDim.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ProductDim(Span in_span, boolean in_parenthesized, DimExpr in_multiplier, DimExpr in_multiplicand) {
        super(in_span, in_parenthesized);
        if (in_multiplier == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'multiplier' to the ProductDim constructor was null");
        }
        _multiplier = in_multiplier;
        if (in_multiplicand == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'multiplicand' to the ProductDim constructor was null");
        }
        _multiplicand = in_multiplicand;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ProductDim(Span in_span, DimExpr in_multiplier, DimExpr in_multiplicand) {
        this(in_span, false, in_multiplier, in_multiplicand);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ProductDim(boolean in_parenthesized, DimExpr in_multiplier, DimExpr in_multiplicand) {
        this(new Span(), in_parenthesized, in_multiplier, in_multiplicand);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ProductDim(DimExpr in_multiplier, DimExpr in_multiplicand) {
        this(new Span(), false, in_multiplier, in_multiplicand);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ProductDim() {
        _multiplier = null;
        _multiplicand = null;
    }

    final public DimExpr getMultiplier() { return _multiplier; }
    final public DimExpr getMultiplicand() { return _multiplicand; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forProductDim(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forProductDim(this); }

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
        writer.print("ProductDim:");
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

        DimExpr temp_multiplier = getMultiplier();
        writer.startLine();
        writer.print("multiplier = ");
        temp_multiplier.outputHelp(writer, lossless);

        DimExpr temp_multiplicand = getMultiplicand();
        writer.startLine();
        writer.print("multiplicand = ");
        temp_multiplicand.outputHelp(writer, lossless);
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
            ProductDim casted = (ProductDim) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            DimExpr temp_multiplier = getMultiplier();
            DimExpr casted_multiplier = casted.getMultiplier();
            if (!(temp_multiplier == casted_multiplier || temp_multiplier.equals(casted_multiplier))) return false;
            DimExpr temp_multiplicand = getMultiplicand();
            DimExpr casted_multiplicand = casted.getMultiplicand();
            if (!(temp_multiplicand == casted_multiplicand || temp_multiplicand.equals(casted_multiplicand))) return false;
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
        DimExpr temp_multiplier = getMultiplier();
        code ^= temp_multiplier.hashCode();
        DimExpr temp_multiplicand = getMultiplicand();
        code ^= temp_multiplicand.hashCode();
        return code;
    }
}
