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
 * Class TypecaseClause, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class TypecaseClause extends AbstractNode {
    private final List<Type> _match;
    private final Block _body;

    /**
     * Constructs a TypecaseClause.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public TypecaseClause(Span in_span, List<Type> in_match, Block in_body) {
        super(in_span);
        if (in_match == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'match' to the TypecaseClause constructor was null");
        }
        _match = in_match;
        if (in_body == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'body' to the TypecaseClause constructor was null");
        }
        _body = in_body;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public TypecaseClause(List<Type> in_match, Block in_body) {
        this(new Span(), in_match, in_body);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected TypecaseClause() {
        _match = null;
        _body = null;
    }

    final public List<Type> getMatch() { return _match; }
    final public Block getBody() { return _body; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forTypecaseClause(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forTypecaseClause(this); }

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
        writer.print("TypecaseClause:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<Type> temp_match = getMatch();
        writer.startLine();
        writer.print("match = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_match = true;
        for (Type elt_temp_match : temp_match) {
            isempty_temp_match = false;
            writer.startLine("* ");
            if (elt_temp_match == null) {
                writer.print("null");
            } else {
                elt_temp_match.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_match) writer.print(" }");
        else writer.startLine("}");

        Block temp_body = getBody();
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
            TypecaseClause casted = (TypecaseClause) obj;
            List<Type> temp_match = getMatch();
            List<Type> casted_match = casted.getMatch();
            if (!(temp_match == casted_match || temp_match.equals(casted_match))) return false;
            Block temp_body = getBody();
            Block casted_body = casted.getBody();
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
        List<Type> temp_match = getMatch();
        code ^= temp_match.hashCode();
        Block temp_body = getBody();
        code ^= temp_body.hashCode();
        return code;
    }
}
