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
 * Class ArrayComprehensionClause, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ArrayComprehensionClause extends AbstractNode {
    private final List<Expr> _bind;
    private final Expr _init;
    private final List<GeneratorClause> _gens;

    /**
     * Constructs a ArrayComprehensionClause.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ArrayComprehensionClause(Span in_span, List<Expr> in_bind, Expr in_init, List<GeneratorClause> in_gens) {
        super(in_span);
        if (in_bind == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'bind' to the ArrayComprehensionClause constructor was null");
        }
        _bind = in_bind;
        if (in_init == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'init' to the ArrayComprehensionClause constructor was null");
        }
        _init = in_init;
        if (in_gens == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'gens' to the ArrayComprehensionClause constructor was null");
        }
        _gens = in_gens;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ArrayComprehensionClause(List<Expr> in_bind, Expr in_init, List<GeneratorClause> in_gens) {
        this(new Span(), in_bind, in_init, in_gens);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ArrayComprehensionClause() {
        _bind = null;
        _init = null;
        _gens = null;
    }

    final public List<Expr> getBind() { return _bind; }
    final public Expr getInit() { return _init; }
    final public List<GeneratorClause> getGens() { return _gens; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forArrayComprehensionClause(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forArrayComprehensionClause(this); }

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
        writer.print("ArrayComprehensionClause:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<Expr> temp_bind = getBind();
        writer.startLine();
        writer.print("bind = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_bind = true;
        for (Expr elt_temp_bind : temp_bind) {
            isempty_temp_bind = false;
            writer.startLine("* ");
            if (elt_temp_bind == null) {
                writer.print("null");
            } else {
                elt_temp_bind.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_bind) writer.print(" }");
        else writer.startLine("}");

        Expr temp_init = getInit();
        writer.startLine();
        writer.print("init = ");
        temp_init.outputHelp(writer, lossless);

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
            ArrayComprehensionClause casted = (ArrayComprehensionClause) obj;
            List<Expr> temp_bind = getBind();
            List<Expr> casted_bind = casted.getBind();
            if (!(temp_bind == casted_bind || temp_bind.equals(casted_bind))) return false;
            Expr temp_init = getInit();
            Expr casted_init = casted.getInit();
            if (!(temp_init == casted_init || temp_init.equals(casted_init))) return false;
            List<GeneratorClause> temp_gens = getGens();
            List<GeneratorClause> casted_gens = casted.getGens();
            if (!(temp_gens == casted_gens || temp_gens.equals(casted_gens))) return false;
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
        List<Expr> temp_bind = getBind();
        code ^= temp_bind.hashCode();
        Expr temp_init = getInit();
        code ^= temp_init.hashCode();
        List<GeneratorClause> temp_gens = getGens();
        code ^= temp_gens.hashCode();
        return code;
    }
}
