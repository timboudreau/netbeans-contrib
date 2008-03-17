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
 * Class PropertyDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class PropertyDecl extends AbstractNode implements Decl, AbsDecl {
    private final Option<Id> _name;
    private final List<Param> _params;
    private final Expr _expr;

    /**
     * Constructs a PropertyDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public PropertyDecl(Span in_span, Option<Id> in_name, List<Param> in_params, Expr in_expr) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the PropertyDecl constructor was null");
        }
        _name = in_name;
        if (in_params == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'params' to the PropertyDecl constructor was null");
        }
        _params = in_params;
        if (in_expr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'expr' to the PropertyDecl constructor was null");
        }
        _expr = in_expr;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public PropertyDecl(Span in_span, List<Param> in_params, Expr in_expr) {
        this(in_span, Option.<Id>none(), in_params, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public PropertyDecl(Option<Id> in_name, List<Param> in_params, Expr in_expr) {
        this(new Span(), in_name, in_params, in_expr);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public PropertyDecl(List<Param> in_params, Expr in_expr) {
        this(new Span(), Option.<Id>none(), in_params, in_expr);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected PropertyDecl() {
        _name = null;
        _params = null;
        _expr = null;
    }

    final public Option<Id> getName() { return _name; }
    final public List<Param> getParams() { return _params; }
    final public Expr getExpr() { return _expr; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forPropertyDecl(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forPropertyDecl(this); }

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
        writer.print("PropertyDecl:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Option<Id> temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        if (temp_name.isSome()) {
            writer.print("(");
            Id elt_temp_name = edu.rice.cs.plt.tuple.Option.unwrap(temp_name);
            if (elt_temp_name == null) {
                writer.print("null");
            } else {
                elt_temp_name.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        List<Param> temp_params = getParams();
        writer.startLine();
        writer.print("params = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_params = true;
        for (Param elt_temp_params : temp_params) {
            isempty_temp_params = false;
            writer.startLine("* ");
            if (elt_temp_params == null) {
                writer.print("null");
            } else {
                elt_temp_params.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_params) writer.print(" }");
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
            PropertyDecl casted = (PropertyDecl) obj;
            Option<Id> temp_name = getName();
            Option<Id> casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<Param> temp_params = getParams();
            List<Param> casted_params = casted.getParams();
            if (!(temp_params == casted_params || temp_params.equals(casted_params))) return false;
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
        Option<Id> temp_name = getName();
        code ^= temp_name.hashCode();
        List<Param> temp_params = getParams();
        code ^= temp_params.hashCode();
        Expr temp_expr = getExpr();
        code ^= temp_expr.hashCode();
        return code;
    }
}
