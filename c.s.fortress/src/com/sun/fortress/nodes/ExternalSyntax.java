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
 * Class ExternalSyntax, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ExternalSyntax extends ExternalSyntaxAbsDeclOrDecl implements Decl {
    private final Expr _expr;

    /**
     * Constructs a ExternalSyntax.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ExternalSyntax(Span in_span, SimpleName in_openExpander, Id in_name, SimpleName in_closeExpander, Expr in_expr) {
        super(in_span, in_openExpander, in_name, in_closeExpander);
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the ExternalSyntax constructor was null");
        }
        _expr = in_expr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ExternalSyntax(SimpleName in_openExpander, Id in_name, SimpleName in_closeExpander, Expr in_expr) {
        this(new Span(), in_openExpander, in_name, in_closeExpander, in_expr);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ExternalSyntax() {
        _expr = null;
    }

    final public Expr getExpr() { return _expr; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forExternalSyntax(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forExternalSyntax(this); }

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
        writer.print("ExternalSyntax:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        SimpleName temp_openExpander = getOpenExpander();
        writer.startLine();
        writer.print("openExpander = ");
        temp_openExpander.outputHelp(writer, lossless);

        Id temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        SimpleName temp_closeExpander = getCloseExpander();
        writer.startLine();
        writer.print("closeExpander = ");
        temp_closeExpander.outputHelp(writer, lossless);

        Expr temp_expr = getExpr();
        writer.startLine();
        writer.print("expr = ");
        temp_expr.outputHelp(writer, lossless);
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
            ExternalSyntax casted = (ExternalSyntax) obj;
            SimpleName temp_openExpander = getOpenExpander();
            SimpleName casted_openExpander = casted.getOpenExpander();
            if (!(temp_openExpander == casted_openExpander || temp_openExpander.equals(casted_openExpander))) return false;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            SimpleName temp_closeExpander = getCloseExpander();
            SimpleName casted_closeExpander = casted.getCloseExpander();
            if (!(temp_closeExpander == casted_closeExpander || temp_closeExpander.equals(casted_closeExpander))) return false;
            Expr temp_expr = getExpr();
            Expr casted_expr = casted.getExpr();
            if (!(temp_expr == casted_expr || temp_expr.equals(casted_expr))) return false;
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
        SimpleName temp_openExpander = getOpenExpander();
        code ^= temp_openExpander.hashCode();
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        SimpleName temp_closeExpander = getCloseExpander();
        code ^= temp_closeExpander.hashCode();
        Expr temp_expr = getExpr();
        code ^= temp_expr.hashCode();
        return code;
    }
}
