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
 * Class AbsExternalSyntax, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class AbsExternalSyntax extends ExternalSyntaxAbsDeclOrDecl implements AbsDecl {

    /**
     * Constructs a AbsExternalSyntax.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AbsExternalSyntax(Span in_span, SimpleName in_openExpander, Id in_name, SimpleName in_closeExpander) {
        super(in_span, in_openExpander, in_name, in_closeExpander);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AbsExternalSyntax(SimpleName in_openExpander, Id in_name, SimpleName in_closeExpander) {
        this(new Span(), in_openExpander, in_name, in_closeExpander);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AbsExternalSyntax() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAbsExternalSyntax(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAbsExternalSyntax(this); }

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
        writer.print("AbsExternalSyntax:");
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
            AbsExternalSyntax casted = (AbsExternalSyntax) obj;
            SimpleName temp_openExpander = getOpenExpander();
            SimpleName casted_openExpander = casted.getOpenExpander();
            if (!(temp_openExpander == casted_openExpander || temp_openExpander.equals(casted_openExpander))) return false;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            SimpleName temp_closeExpander = getCloseExpander();
            SimpleName casted_closeExpander = casted.getCloseExpander();
            if (!(temp_closeExpander == casted_closeExpander || temp_closeExpander.equals(casted_closeExpander))) return false;
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
        return code;
    }
}
