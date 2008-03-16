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
 * Class ImportStar, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ImportStar extends ImportedNames {
    private final List<SimpleName> _except;

    /**
     * Constructs a ImportStar.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ImportStar(Span in_span, APIName in_api, List<SimpleName> in_except) {
        super(in_span, in_api);
        if (in_except == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'except' to the ImportStar constructor was null");
        }
        _except = in_except;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ImportStar(APIName in_api, List<SimpleName> in_except) {
        this(new Span(), in_api, in_except);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ImportStar() {
        _except = null;
    }

    final public List<SimpleName> getExcept() { return _except; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forImportStar(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forImportStar(this); }

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
        writer.print("ImportStar:");
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

        List<SimpleName> temp_except = getExcept();
        writer.startLine();
        writer.print("except = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_except = true;
        for (SimpleName elt_temp_except : temp_except) {
            isempty_temp_except = false;
            writer.startLine("* ");
            if (elt_temp_except == null) {
                writer.print("null");
            } else {
                elt_temp_except.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_except) writer.print(" }");
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
            ImportStar casted = (ImportStar) obj;
            APIName temp_api = getApi();
            APIName casted_api = casted.getApi();
            if (!(temp_api == casted_api || temp_api.equals(casted_api))) return false;
            List<SimpleName> temp_except = getExcept();
            List<SimpleName> casted_except = casted.getExcept();
            if (!(temp_except == casted_except || temp_except.equals(casted_except))) return false;
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
        List<SimpleName> temp_except = getExcept();
        code ^= temp_except.hashCode();
        return code;
    }
}
