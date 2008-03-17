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
 * Class AliasedSimpleName, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class AliasedSimpleName extends AbstractNode {
    private final SimpleName _name;
    private final Option<SimpleName> _alias;

    /**
     * Constructs a AliasedSimpleName.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AliasedSimpleName(Span in_span, SimpleName in_name, Option<SimpleName> in_alias) {
        super(in_span);
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the AliasedSimpleName constructor was null");
        }
        _name = in_name;
        if (in_alias == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'alias' to the AliasedSimpleName constructor was null");
        }
        _alias = in_alias;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AliasedSimpleName(Span in_span, SimpleName in_name) {
        this(in_span, in_name, Option.<SimpleName>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AliasedSimpleName(SimpleName in_name, Option<SimpleName> in_alias) {
        this(new Span(), in_name, in_alias);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AliasedSimpleName(SimpleName in_name) {
        this(new Span(), in_name, Option.<SimpleName>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AliasedSimpleName() {
        _name = null;
        _alias = null;
    }

    final public SimpleName getName() { return _name; }
    final public Option<SimpleName> getAlias() { return _alias; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAliasedSimpleName(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAliasedSimpleName(this); }

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
        writer.print("AliasedSimpleName:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        SimpleName temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        Option<SimpleName> temp_alias = getAlias();
        writer.startLine();
        writer.print("alias = ");
        if (temp_alias.isSome()) {
            writer.print("(");
            SimpleName elt_temp_alias = edu.rice.cs.plt.tuple.Option.unwrap(temp_alias);
            if (elt_temp_alias == null) {
                writer.print("null");
            } else {
                elt_temp_alias.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }
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
            AliasedSimpleName casted = (AliasedSimpleName) obj;
            SimpleName temp_name = getName();
            SimpleName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            Option<SimpleName> temp_alias = getAlias();
            Option<SimpleName> casted_alias = casted.getAlias();
            if (!(temp_alias == casted_alias || temp_alias.equals(casted_alias))) return false;
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
        SimpleName temp_name = getName();
        code ^= temp_name.hashCode();
        Option<SimpleName> temp_alias = getAlias();
        code ^= temp_alias.hashCode();
        return code;
    }
}
