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
 * Class InstantiatedType, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class InstantiatedType extends NamedType {
    private final List<StaticArg> _args;

    /**
     * Constructs a InstantiatedType.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public InstantiatedType(Span in_span, boolean in_parenthesized, QualifiedIdName in_name, List<StaticArg> in_args) {
        super(in_span, in_parenthesized, in_name);
        if (in_args == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'args' to the InstantiatedType constructor was null");
        }
        _args = in_args;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InstantiatedType(Span in_span, QualifiedIdName in_name, List<StaticArg> in_args) {
        this(in_span, false, in_name, in_args);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InstantiatedType(boolean in_parenthesized, QualifiedIdName in_name, List<StaticArg> in_args) {
        this(new Span(), in_parenthesized, in_name, in_args);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public InstantiatedType(QualifiedIdName in_name, List<StaticArg> in_args) {
        this(new Span(), false, in_name, in_args);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected InstantiatedType() {
        _args = null;
    }

    final public List<StaticArg> getArgs() { return _args; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forInstantiatedType(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forInstantiatedType(this); }

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
        writer.print("InstantiatedType:");
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

        QualifiedIdName temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        List<StaticArg> temp_args = getArgs();
        writer.startLine();
        writer.print("args = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_args = true;
        for (StaticArg elt_temp_args : temp_args) {
            isempty_temp_args = false;
            writer.startLine("* ");
            if (elt_temp_args == null) {
                writer.print("null");
            } else {
                elt_temp_args.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_args) writer.print(" }");
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
            InstantiatedType casted = (InstantiatedType) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            QualifiedIdName temp_name = getName();
            QualifiedIdName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<StaticArg> temp_args = getArgs();
            List<StaticArg> casted_args = casted.getArgs();
            if (!(temp_args == casted_args || temp_args.equals(casted_args))) return false;
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
        QualifiedIdName temp_name = getName();
        code ^= temp_name.hashCode();
        List<StaticArg> temp_args = getArgs();
        code ^= temp_args.hashCode();
        return code;
    }
}
