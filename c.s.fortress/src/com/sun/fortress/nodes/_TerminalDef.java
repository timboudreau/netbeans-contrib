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
 * Class _TerminalDef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class _TerminalDef extends TerminalDecl {

    /**
     * Constructs a _TerminalDef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public _TerminalDef(Span in_span, QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier, SyntaxDef in_syntaxDef) {
        super(in_span, in_name, in_type, in_modifier, in_syntaxDef);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _TerminalDef(QualifiedIdName in_name, Option<TraitType> in_type, Option<? extends Modifier> in_modifier, SyntaxDef in_syntaxDef) {
        this(new Span(), in_name, in_type, in_modifier, in_syntaxDef);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected _TerminalDef() {
    }


    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.for_TerminalDef(this); }
    public void accept(NodeVisitor_void visitor) { visitor.for_TerminalDef(this); }

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
        writer.print("_TerminalDef:");
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

        SyntaxDef temp_syntaxDef = getSyntaxDef();
        writer.startLine();
        writer.print("syntaxDef = ");
        temp_syntaxDef.outputHelp(writer, lossless);
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
            _TerminalDef casted = (_TerminalDef) obj;
            QualifiedIdName temp_name = getName();
            QualifiedIdName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Option<TraitType> temp_type = getType();
            Option<TraitType> casted_type = casted.getType();
            if (!(temp_type == casted_type || temp_type.equals(casted_type))) return false;
            Option<? extends Modifier> temp_modifier = getModifier();
            Option<? extends Modifier> casted_modifier = casted.getModifier();
            if (!(temp_modifier == casted_modifier || temp_modifier.equals(casted_modifier))) return false;
            SyntaxDef temp_syntaxDef = getSyntaxDef();
            SyntaxDef casted_syntaxDef = casted.getSyntaxDef();
            if (!(temp_syntaxDef == casted_syntaxDef || temp_syntaxDef.equals(casted_syntaxDef))) return false;
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
        SyntaxDef temp_syntaxDef = getSyntaxDef();
        code ^= temp_syntaxDef.hashCode();
        return code;
    }
}
