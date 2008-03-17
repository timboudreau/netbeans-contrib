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
 * Class NonterminalExtensionDef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class NonterminalExtensionDef extends NonterminalDecl {

    /**
     * Constructs a NonterminalExtensionDef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public NonterminalExtensionDef(Span in_span, QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier, List<SyntaxDef> in_syntaxDefs) {
        super(in_span, in_name, in_type, in_modifier, in_syntaxDefs);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public NonterminalExtensionDef(QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier, List<SyntaxDef> in_syntaxDefs) {
        this(new Span(), in_name, in_type, in_modifier, in_syntaxDefs);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected NonterminalExtensionDef() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forNonterminalExtensionDef(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forNonterminalExtensionDef(this); }

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
        writer.print("NonterminalExtensionDef:");
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

        Option<TraitType> temp_type = getType();
        writer.startLine();
        writer.print("type = ");
        if (temp_type.isSome()) {
            writer.print("(");
            TraitType elt_temp_type = edu.rice.cs.plt.tuple.Option.unwrap(temp_type);
            if (elt_temp_type == null) {
                writer.print("null");
            } else {
                elt_temp_type.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<? extends Modifier> temp_modifier = getModifier();
        writer.startLine();
        writer.print("modifier = ");
        if (temp_modifier.isSome()) {
            writer.print("(");
            Modifier elt_temp_modifier = edu.rice.cs.plt.tuple.Option.unwrap(temp_modifier);
            if (elt_temp_modifier == null) {
                writer.print("null");
            } else {
                elt_temp_modifier.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        List<SyntaxDef> temp_syntaxDefs = getSyntaxDefs();
        writer.startLine();
        writer.print("syntaxDefs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_syntaxDefs = true;
        for (SyntaxDef elt_temp_syntaxDefs : temp_syntaxDefs) {
            isempty_temp_syntaxDefs = false;
            writer.startLine("* ");
            if (elt_temp_syntaxDefs == null) {
                writer.print("null");
            } else {
                elt_temp_syntaxDefs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_syntaxDefs) writer.print(" }");
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
            NonterminalExtensionDef casted = (NonterminalExtensionDef) obj;
            QualifiedIdName temp_name = getName();
            QualifiedIdName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Option<TraitType> temp_type = getType();
            Option<TraitType> casted_type = casted.getType();
            if (!(temp_type == casted_type || temp_type.equals(casted_type))) return false;
            Option<? extends Modifier> temp_modifier = getModifier();
            Option<? extends Modifier> casted_modifier = casted.getModifier();
            if (!(temp_modifier == casted_modifier || temp_modifier.equals(casted_modifier))) return false;
            List<SyntaxDef> temp_syntaxDefs = getSyntaxDefs();
            List<SyntaxDef> casted_syntaxDefs = casted.getSyntaxDefs();
            if (!(temp_syntaxDefs == casted_syntaxDefs || temp_syntaxDefs.equals(casted_syntaxDefs))) return false;
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
        Option<TraitType> temp_type = getType();
        code ^= temp_type.hashCode();
        Option<? extends Modifier> temp_modifier = getModifier();
        code ^= temp_modifier.hashCode();
        List<SyntaxDef> temp_syntaxDefs = getSyntaxDefs();
        code ^= temp_syntaxDefs.hashCode();
        return code;
    }
}
