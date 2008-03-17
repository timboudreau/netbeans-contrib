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
 * Class AbsVarDecl, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class AbsVarDecl extends VarAbsDeclOrDecl implements AbsDecl, Decl {

    /**
     * Constructs a AbsVarDecl.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbsVarDecl(Span in_span, List<LValueBind> in_lhs) {
        super(in_span, in_lhs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsVarDecl(List<LValueBind> in_lhs) {
        this(new Span(), in_lhs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbsVarDecl() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAbsVarDecl(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAbsVarDecl(this); }

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
        writer.print("AbsVarDecl:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<LValueBind> temp_lhs = getLhs();
        writer.startLine();
        writer.print("lhs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_lhs = true;
        for (LValueBind elt_temp_lhs : temp_lhs) {
            isempty_temp_lhs = false;
            writer.startLine("* ");
            if (elt_temp_lhs == null) {
                writer.print("null");
            } else {
                elt_temp_lhs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_lhs) writer.print(" }");
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
            AbsVarDecl casted = (AbsVarDecl) obj;
            List<LValueBind> temp_lhs = getLhs();
            List<LValueBind> casted_lhs = casted.getLhs();
            if (!(temp_lhs == casted_lhs || temp_lhs.equals(casted_lhs))) return false;
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
        List<LValueBind> temp_lhs = getLhs();
        code ^= temp_lhs.hashCode();
        return code;
    }
}
