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
 * Class GrammarDef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class GrammarDef extends GrammarDecl {
    private final List<GrammarMemberDecl> _members;

    /**
     * Constructs a GrammarDef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public GrammarDef(Span in_span, QualifiedIdName in_name, List<QualifiedIdName> in_extends, List<GrammarMemberDecl> in_members) {
        super(in_span, in_name, in_extends);
        if (in_members == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'members' to the GrammarDef constructor was null");
        }
        _members = in_members;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public GrammarDef(QualifiedIdName in_name, List<QualifiedIdName> in_extends, List<GrammarMemberDecl> in_members) {
        this(new Span(), in_name, in_extends, in_members);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected GrammarDef() {
        _members = null;
    }

    final public List<GrammarMemberDecl> getMembers() { return _members; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forGrammarDef(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forGrammarDef(this); }

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
        writer.print("GrammarDef:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        QualifiedIdName temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        List<QualifiedIdName> temp_extends = getExtends();
        writer.startLine();
        writer.print("extends = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_extends = true;
        for (QualifiedIdName elt_temp_extends : temp_extends) {
            isempty_temp_extends = false;
            writer.startLine("* ");
            if (elt_temp_extends == null) {
                writer.print("null");
            } else {
                elt_temp_extends.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_extends) writer.print(" }");
        else writer.startLine("}");

        List<GrammarMemberDecl> temp_members = getMembers();
        writer.startLine();
        writer.print("members = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_members = true;
        for (GrammarMemberDecl elt_temp_members : temp_members) {
            isempty_temp_members = false;
            writer.startLine("* ");
            if (elt_temp_members == null) {
                writer.print("null");
            } else {
                elt_temp_members.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_members) writer.print(" }");
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
            GrammarDef casted = (GrammarDef) obj;
            QualifiedIdName temp_name = getName();
            QualifiedIdName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<QualifiedIdName> temp_extends = getExtends();
            List<QualifiedIdName> casted_extends = casted.getExtends();
            if (!(temp_extends == casted_extends || temp_extends.equals(casted_extends))) return false;
            List<GrammarMemberDecl> temp_members = getMembers();
            List<GrammarMemberDecl> casted_members = casted.getMembers();
            if (!(temp_members == casted_members || temp_members.equals(casted_members))) return false;
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
        QualifiedIdName temp_name = getName();
        code ^= temp_name.hashCode();
        List<QualifiedIdName> temp_extends = getExtends();
        code ^= temp_extends.hashCode();
        List<GrammarMemberDecl> temp_members = getMembers();
        code ^= temp_members.hashCode();
        return code;
    }
}
