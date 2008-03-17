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
 * Class Export, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Export extends AbstractNode {
    private final List<APIName> _apis;

    /**
     * Constructs a Export.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Export(Span in_span, List<APIName> in_apis) {
        super(in_span);
        if (in_apis == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'apis' to the Export constructor was null");
        }
        _apis = in_apis;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Export(List<APIName> in_apis) {
        this(new Span(), in_apis);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Export() {
        _apis = null;
    }

    final public List<APIName> getApis() { return _apis; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forExport(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forExport(this); }

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
        writer.print("Export:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<APIName> temp_apis = getApis();
        writer.startLine();
        writer.print("apis = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_apis = true;
        for (APIName elt_temp_apis : temp_apis) {
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
            Export casted = (Export) obj;
            List<APIName> temp_apis = getApis();
            List<APIName> casted_apis = casted.getApis();
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
        List<APIName> temp_apis = getApis();
        code ^= temp_apis.hashCode();
        return code;
    }
}
