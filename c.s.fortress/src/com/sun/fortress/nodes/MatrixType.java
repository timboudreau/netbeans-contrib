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
 * Class MatrixType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class MatrixType extends AbbreviatedType {
    private final List<ExtentRange> _dimensions;

    /**
     * Constructs a MatrixType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public MatrixType(Span in_span, boolean in_parenthesized, Type in_element, List<ExtentRange> in_dimensions) {
        super(in_span, in_parenthesized, in_element);
        if (in_dimensions == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'dimensions' to the MatrixType constructor was null");
        }
        _dimensions = in_dimensions;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MatrixType(Span in_span, Type in_element, List<ExtentRange> in_dimensions) {
        this(in_span, false, in_element, in_dimensions);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MatrixType(boolean in_parenthesized, Type in_element, List<ExtentRange> in_dimensions) {
        this(new Span(), in_parenthesized, in_element, in_dimensions);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MatrixType(Type in_element, List<ExtentRange> in_dimensions) {
        this(new Span(), false, in_element, in_dimensions);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected MatrixType() {
        _dimensions = null;
    }

    final public List<ExtentRange> getDimensions() { return _dimensions; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forMatrixType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forMatrixType(this); }

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
        writer.print("MatrixType:");
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

        Type temp_element = getElement();
        writer.startLine();
        writer.print("element = ");
        temp_element.outputHelp(writer, lossless);

        List<ExtentRange> temp_dimensions = getDimensions();
        writer.startLine();
        writer.print("dimensions = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_dimensions = true;
        for (ExtentRange elt_temp_dimensions : temp_dimensions) {
            isempty_temp_dimensions = false;
            writer.startLine("* ");
            if (elt_temp_dimensions == null) {
                writer.print("null");
            } else {
                elt_temp_dimensions.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_dimensions) writer.print(" }");
        else writer.startLine("}");
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
            MatrixType casted = (MatrixType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Type temp_element = getElement();
            Type casted_element = casted.getElement();
            if (!(temp_element == casted_element || temp_element.equals(casted_element))) return false;
            List<ExtentRange> temp_dimensions = getDimensions();
            List<ExtentRange> casted_dimensions = casted.getDimensions();
            if (!(temp_dimensions == casted_dimensions || temp_dimensions.equals(casted_dimensions))) return false;
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
        Type temp_element = getElement();
        code ^= temp_element.hashCode();
        List<ExtentRange> temp_dimensions = getDimensions();
        code ^= temp_dimensions.hashCode();
        return code;
    }
}
