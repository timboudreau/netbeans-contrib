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
 * Class AliasedAPIName, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class AliasedAPIName extends AbstractNode {
    private final APIName _api;
    private final Option<Id> _alias;

    /**
     * Constructs a AliasedAPIName.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public AliasedAPIName(Span in_span, APIName in_api, Option<Id> in_alias) {
        super(in_span);
        if (in_api == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'api' to the AliasedAPIName constructor was null");
        }
        _api = in_api;
        if (in_alias == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'alias' to the AliasedAPIName constructor was null");
        }
        _alias = in_alias;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AliasedAPIName(Span in_span, APIName in_api) {
        this(in_span, in_api, Option.<Id>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AliasedAPIName(APIName in_api, Option<Id> in_alias) {
        this(new Span(), in_api, in_alias);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public AliasedAPIName(APIName in_api) {
        this(new Span(), in_api, Option.<Id>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected AliasedAPIName() {
        _api = null;
        _alias = null;
    }

    final public APIName getApi() { return _api; }
    final public Option<Id> getAlias() { return _alias; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forAliasedAPIName(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forAliasedAPIName(this); }

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
        writer.print("AliasedAPIName:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        APIName temp_api = getApi();
        writer.startLine();
        writer.print("api = ");
        temp_api.outputHelp(writer, lossless);

        Option<Id> temp_alias = getAlias();
        writer.startLine();
        writer.print("alias = ");
        if (temp_alias.isSome()) {
            writer.print("(");
            Id elt_temp_alias = edu.rice.cs.plt.tuple.Option.unwrap(temp_alias);
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
            AliasedAPIName casted = (AliasedAPIName) obj;
            APIName temp_api = getApi();
            APIName casted_api = casted.getApi();
            if (!(temp_api == casted_api || temp_api.equals(casted_api))) return false;
            Option<Id> temp_alias = getAlias();
            Option<Id> casted_alias = casted.getAlias();
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
        APIName temp_api = getApi();
        code ^= temp_api.hashCode();
        Option<Id> temp_alias = getAlias();
        code ^= temp_alias.hashCode();
        return code;
    }
}
