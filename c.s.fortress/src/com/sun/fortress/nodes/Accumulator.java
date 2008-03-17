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
 * Class Accumulator, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Accumulator extends BigOprApp {
    private final OpName _opr;
    private final List<GeneratorClause> _gens;
    private final Expr _body;

    /**
     * Constructs a Accumulator.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Accumulator(Span in_span, boolean in_parenthesized, List<StaticArg> in_staticArgs, OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        super(in_span, in_parenthesized, in_staticArgs);
        if (in_opr == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'opr' to the Accumulator constructor was null");
        }
        _opr = in_opr;
        if (in_gens == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'gens' to the Accumulator constructor was null");
        }
        _gens = in_gens;
        if (in_body == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'body' to the Accumulator constructor was null");
        }
        _body = in_body;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Accumulator(Span in_span, boolean in_parenthesized, OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        this(in_span, in_parenthesized, Collections.<StaticArg>emptyList(), in_opr, in_gens, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Accumulator(Span in_span, List<StaticArg> in_staticArgs, OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        this(in_span, false, in_staticArgs, in_opr, in_gens, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Accumulator(Span in_span, OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        this(in_span, false, Collections.<StaticArg>emptyList(), in_opr, in_gens, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Accumulator(boolean in_parenthesized, List<StaticArg> in_staticArgs, OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        this(new Span(), in_parenthesized, in_staticArgs, in_opr, in_gens, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Accumulator(boolean in_parenthesized, OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        this(new Span(), in_parenthesized, Collections.<StaticArg>emptyList(), in_opr, in_gens, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Accumulator(List<StaticArg> in_staticArgs, OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        this(new Span(), false, in_staticArgs, in_opr, in_gens, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Accumulator(OpName in_opr, List<GeneratorClause> in_gens, Expr in_body) {
        this(new Span(), false, Collections.<StaticArg>emptyList(), in_opr, in_gens, in_body);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Accumulator() {
        _opr = null;
        _gens = null;
        _body = null;
    }

    final public OpName getOpr() { return _opr; }
    final public List<GeneratorClause> getGens() { return _gens; }
    final public Expr getBody() { return _body; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAccumulator(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAccumulator(this); }

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
        writer.print("Accumulator:");
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

        List<StaticArg> temp_staticArgs = getStaticArgs();
        writer.startLine();
        writer.print("staticArgs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_staticArgs = true;
        for (StaticArg elt_temp_staticArgs : temp_staticArgs) {
            isempty_temp_staticArgs = false;
            writer.startLine("* ");
            if (elt_temp_staticArgs == null) {
                writer.print("null");
            } else {
                elt_temp_staticArgs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_staticArgs) writer.print(" }");
        else writer.startLine("}");

        OpName temp_opr = getOpr();
        writer.startLine();
        writer.print("opr = ");
        temp_opr.outputHelp(writer, lossless);

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

        Expr temp_body = getBody();
        writer.startLine();
        writer.print("body = ");
        temp_body.outputHelp(writer, lossless);
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
            Accumulator casted = (Accumulator) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<StaticArg> temp_staticArgs = getStaticArgs();
            List<StaticArg> casted_staticArgs = casted.getStaticArgs();
            if (!(temp_staticArgs == casted_staticArgs || temp_staticArgs.equals(casted_staticArgs))) return false;
            OpName temp_opr = getOpr();
            OpName casted_opr = casted.getOpr();
            if (!(temp_opr == casted_opr || temp_opr.equals(casted_opr))) return false;
            List<GeneratorClause> temp_gens = getGens();
            List<GeneratorClause> casted_gens = casted.getGens();
            if (!(temp_gens == casted_gens || temp_gens.equals(casted_gens))) return false;
            Expr temp_body = getBody();
            Expr casted_body = casted.getBody();
            if (!(temp_body == casted_body || temp_body.equals(casted_body))) return false;
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
        List<StaticArg> temp_staticArgs = getStaticArgs();
        code ^= temp_staticArgs.hashCode();
        OpName temp_opr = getOpr();
        code ^= temp_opr.hashCode();
        List<GeneratorClause> temp_gens = getGens();
        code ^= temp_gens.hashCode();
        Expr temp_body = getBody();
        code ^= temp_body.hashCode();
        return code;
    }
}
