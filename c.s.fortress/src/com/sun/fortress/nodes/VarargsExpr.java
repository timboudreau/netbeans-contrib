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
 * Class VarargsExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class VarargsExpr extends AbstractNode {
    private final Expr _varargs;

    /**
     * Constructs a VarargsExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public VarargsExpr(Span in_span, Expr in_varargs) {
        super(in_span);
        if (in_varargs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'varargs' to the VarargsExpr constructor was null");
        }
        _varargs = in_varargs;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarargsExpr(Expr in_varargs) {
        this(new Span(), in_varargs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected VarargsExpr() {
        _varargs = null;
    }

    final public Expr getVarargs() { return _varargs; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forVarargsExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forVarargsExpr(this); }

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
        writer.print("VarargsExpr:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Expr temp_varargs = getVarargs();
        writer.startLine();
        writer.print("varargs = ");
        temp_varargs.outputHelp(writer, lossless);
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
            VarargsExpr casted = (VarargsExpr) obj;
            Expr temp_varargs = getVarargs();
            Expr casted_varargs = casted.getVarargs();
            if (!(temp_varargs == casted_varargs || temp_varargs.equals(casted_varargs))) return false;
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
        Expr temp_varargs = getVarargs();
        code ^= temp_varargs.hashCode();
        return code;
    }
}
