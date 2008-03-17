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
 * Class ObjectExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ObjectExpr extends AbstractObjectExpr {

    /**
     * Constructs a ObjectExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ObjectExpr(Span in_span, boolean in_parenthesized, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        super(in_span, in_parenthesized, in_extendsClause, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectExpr(Span in_span, boolean in_parenthesized, List<Decl> in_decls) {
        this(in_span, in_parenthesized, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectExpr(Span in_span, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        this(in_span, false, in_extendsClause, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectExpr(Span in_span, List<Decl> in_decls) {
        this(in_span, false, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectExpr(boolean in_parenthesized, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        this(new Span(), in_parenthesized, in_extendsClause, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectExpr(boolean in_parenthesized, List<Decl> in_decls) {
        this(new Span(), in_parenthesized, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectExpr(List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls) {
        this(new Span(), false, in_extendsClause, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ObjectExpr(List<Decl> in_decls) {
        this(new Span(), false, Collections.<TraitTypeWhere>emptyList(), in_decls);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ObjectExpr() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forObjectExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forObjectExpr(this); }

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
        writer.print("ObjectExpr:");
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

        List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
        writer.startLine();
        writer.print("extendsClause = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_extendsClause = true;
        for (TraitTypeWhere elt_temp_extendsClause : temp_extendsClause) {
            isempty_temp_extendsClause = false;
            writer.startLine("* ");
            if (elt_temp_extendsClause == null) {
                writer.print("null");
            } else {
                elt_temp_extendsClause.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_extendsClause) writer.print(" }");
        else writer.startLine("}");

        List<Decl> temp_decls = getDecls();
        writer.startLine();
        writer.print("decls = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_decls = true;
        for (Decl elt_temp_decls : temp_decls) {
            isempty_temp_decls = false;
            writer.startLine("* ");
            if (elt_temp_decls == null) {
                writer.print("null");
            } else {
                elt_temp_decls.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_decls) writer.print(" }");
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
            ObjectExpr casted = (ObjectExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
            List<TraitTypeWhere> casted_extendsClause = casted.getExtendsClause();
            if (!(temp_extendsClause == casted_extendsClause || temp_extendsClause.equals(casted_extendsClause))) return false;
            List<Decl> temp_decls = getDecls();
            List<Decl> casted_decls = casted.getDecls();
            if (!(temp_decls == casted_decls || temp_decls.equals(casted_decls))) return false;
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
        List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
        code ^= temp_extendsClause.hashCode();
        List<Decl> temp_decls = getDecls();
        code ^= temp_decls.hashCode();
        return code;
    }
}
