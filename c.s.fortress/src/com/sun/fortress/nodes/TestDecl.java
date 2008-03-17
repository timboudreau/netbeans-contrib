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
 * Class TestDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class TestDecl extends AbstractNode implements Decl, AbsDecl {
    private final Id _name;
    private final List<GeneratorClause> _gens;
    private final Expr _expr;

    /**
     * Constructs a TestDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TestDecl(Span in_span, Id in_name, List<GeneratorClause> in_gens, Expr in_expr) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the TestDecl constructor was null");
        }
        _name = in_name;
        if (in_gens == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'gens' to the TestDecl constructor was null");
        }
        _gens = in_gens;
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the TestDecl constructor was null");
        }
        _expr = in_expr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TestDecl(Id in_name, List<GeneratorClause> in_gens, Expr in_expr) {
        this(new Span(), in_name, in_gens, in_expr);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TestDecl() {
        _name = null;
        _gens = null;
        _expr = null;
    }

    final public Id getName() { return _name; }
    final public List<GeneratorClause> getGens() { return _gens; }
    final public Expr getExpr() { return _expr; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forTestDecl(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forTestDecl(this); }

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
        writer.print("TestDecl:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Id temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        List<GeneratorClause> temp_gens = getGens();
        writer.startLine();
        writer.print("gens = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_gens = true;
        for (GeneratorClause elt_temp_gens : temp_gens) {
            isempty_temp_gens = false;
            writer.startLine("* ");
            if (elt_temp_gens == null) {
                writer.print("null");
            } else {
                elt_temp_gens.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_gens) writer.print(" }");
        else writer.startLine("}");

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
            TestDecl casted = (TestDecl) obj;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<GeneratorClause> temp_gens = getGens();
            List<GeneratorClause> casted_gens = casted.getGens();
            if (!(temp_gens == casted_gens || temp_gens.equals(casted_gens))) return false;
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
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        List<GeneratorClause> temp_gens = getGens();
        code ^= temp_gens.hashCode();
        Expr temp_expr = getExpr();
        code ^= temp_expr.hashCode();
        return code;
    }
}
