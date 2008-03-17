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
 * Class VarRef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class VarRef extends Primary implements LHS {
    private final QualifiedIdName _var;

    /**
     * Constructs a VarRef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public VarRef(Span in_span, boolean in_parenthesized, QualifiedIdName in_var) {
        super(in_span, in_parenthesized);
        if (in_var == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'var' to the VarRef constructor was null");
        }
        _var = in_var;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarRef(Span in_span, QualifiedIdName in_var) {
        this(in_span, false, in_var);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarRef(boolean in_parenthesized, QualifiedIdName in_var) {
        this(new Span(), in_parenthesized, in_var);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public VarRef(QualifiedIdName in_var) {
        this(new Span(), false, in_var);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected VarRef() {
        _var = null;
    }

    final public QualifiedIdName getVar() { return _var; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forVarRef(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forVarRef(this); }

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
        writer.print("VarRef:");
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

        QualifiedIdName temp_var = getVar();
        writer.startLine();
        writer.print("var = ");
        temp_var.outputHelp(writer, lossless);
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
            VarRef casted = (VarRef) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            QualifiedIdName temp_var = getVar();
            QualifiedIdName casted_var = casted.getVar();
            if (!(temp_var == casted_var || temp_var.equals(casted_var))) return false;
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
        QualifiedIdName temp_var = getVar();
        code ^= temp_var.hashCode();
        return code;
    }
}
