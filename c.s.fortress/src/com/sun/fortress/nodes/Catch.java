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
 * Class Catch, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Catch extends AbstractNode {
    private final Id _name;
    private final List<CatchClause> _clauses;

    /**
     * Constructs a Catch.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Catch(Span in_span, Id in_name, List<CatchClause> in_clauses) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the Catch constructor was null");
        }
        _name = in_name;
        if (in_clauses == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'clauses' to the Catch constructor was null");
        }
        _clauses = in_clauses;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Catch(Id in_name, List<CatchClause> in_clauses) {
        this(new Span(), in_name, in_clauses);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Catch() {
        _name = null;
        _clauses = null;
    }

    final public Id getName() { return _name; }
    final public List<CatchClause> getClauses() { return _clauses; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forCatch(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forCatch(this); }

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
        writer.print("Catch:");
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

        List<CatchClause> temp_clauses = getClauses();
        writer.startLine();
        writer.print("clauses = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_clauses = true;
        for (CatchClause elt_temp_clauses : temp_clauses) {
            isempty_temp_clauses = false;
            writer.startLine("* ");
            if (elt_temp_clauses == null) {
                writer.print("null");
            } else {
                elt_temp_clauses.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_clauses) writer.print(" }");
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
            Catch casted = (Catch) obj;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<CatchClause> temp_clauses = getClauses();
            List<CatchClause> casted_clauses = casted.getClauses();
            if (!(temp_clauses == casted_clauses || temp_clauses.equals(casted_clauses))) return false;
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
        List<CatchClause> temp_clauses = getClauses();
        code ^= temp_clauses.hashCode();
        return code;
    }
}
