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
 * Class QualifiedIdName, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class QualifiedIdName extends QualifiedName {

    /**
     * Constructs a QualifiedIdName.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public QualifiedIdName(Span in_span, Option<APIName> in_api, Id in_name) {
        super(in_span, in_api, in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QualifiedIdName(Span in_span, Id in_name) {
        this(in_span, Option.<APIName>none(), in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QualifiedIdName(Option<APIName> in_api, Id in_name) {
        this(new Span(), in_api, in_name);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public QualifiedIdName(Id in_name) {
        this(new Span(), Option.<APIName>none(), in_name);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected QualifiedIdName() {
    }

    final public Id getName() { return (Id) super.getName(); }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forQualifiedIdName(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forQualifiedIdName(this); }

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
        writer.print("QualifiedIdName:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Option<APIName> temp_api = getApi();
        writer.startLine();
        writer.print("api = ");
        if (temp_api.isSome()) {
            writer.print("(");
            APIName elt_temp_api = edu.rice.cs.plt.tuple.Option.unwrap(temp_api);
            if (elt_temp_api == null) {
                writer.print("null");
            } else {
                elt_temp_api.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Id temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);
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
            QualifiedIdName casted = (QualifiedIdName) obj;
            Option<APIName> temp_api = getApi();
            Option<APIName> casted_api = casted.getApi();
            if (!(temp_api == casted_api || temp_api.equals(casted_api))) return false;
            Id temp_name = getName();
            Id casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
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
        Option<APIName> temp_api = getApi();
        code ^= temp_api.hashCode();
        Id temp_name = getName();
        code ^= temp_name.hashCode();
        return code;
    }
}
