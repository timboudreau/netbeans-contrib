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
 * Class ImportApi, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ImportApi extends Import {
    private final List<AliasedAPIName> _apis;

    /**
     * Constructs a ImportApi.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ImportApi(Span in_span, List<AliasedAPIName> in_apis) {
        super(in_span);
        if (in_apis == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'apis' to the ImportApi constructor was null");
        }
        _apis = in_apis;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ImportApi(List<AliasedAPIName> in_apis) {
        this(new Span(), in_apis);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ImportApi() {
        _apis = null;
    }

    final public List<AliasedAPIName> getApis() { return _apis; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forImportApi(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forImportApi(this); }

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
        writer.print("ImportApi:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<AliasedAPIName> temp_apis = getApis();
        writer.startLine();
        writer.print("apis = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_apis = true;
        for (AliasedAPIName elt_temp_apis : temp_apis) {
            isempty_temp_apis = false;
            writer.startLine("* ");
            if (elt_temp_apis == null) {
                writer.print("null");
            } else {
                elt_temp_apis.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_apis) writer.print(" }");
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
            ImportApi casted = (ImportApi) obj;
            List<AliasedAPIName> temp_apis = getApis();
            List<AliasedAPIName> casted_apis = casted.getApis();
            if (!(temp_apis == casted_apis || temp_apis.equals(casted_apis))) return false;
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
        List<AliasedAPIName> temp_apis = getApis();
        code ^= temp_apis.hashCode();
        return code;
    }
}
