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
 * Class ImportNames, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class ImportNames extends ImportedNames {
    private final List<AliasedSimpleName> _aliasedNames;

    /**
     * Constructs a ImportNames.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public ImportNames(Span in_span, APIName in_api, List<AliasedSimpleName> in_aliasedNames) {
        super(in_span, in_api);
        if (in_aliasedNames == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'aliasedNames' to the ImportNames constructor was null");
        }
        _aliasedNames = in_aliasedNames;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public ImportNames(APIName in_api, List<AliasedSimpleName> in_aliasedNames) {
        this(new Span(), in_api, in_aliasedNames);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected ImportNames() {
        _aliasedNames = null;
    }

    final public List<AliasedSimpleName> getAliasedNames() { return _aliasedNames; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forImportNames(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forImportNames(this); }

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
        writer.print("ImportNames:");
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

        List<AliasedSimpleName> temp_aliasedNames = getAliasedNames();
        writer.startLine();
        writer.print("aliasedNames = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_aliasedNames = true;
        for (AliasedSimpleName elt_temp_aliasedNames : temp_aliasedNames) {
            isempty_temp_aliasedNames = false;
            writer.startLine("* ");
            if (elt_temp_aliasedNames == null) {
                writer.print("null");
            } else {
                elt_temp_aliasedNames.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_aliasedNames) writer.print(" }");
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
            ImportNames casted = (ImportNames) obj;
            APIName temp_api = getApi();
            APIName casted_api = casted.getApi();
            if (!(temp_api == casted_api || temp_api.equals(casted_api))) return false;
            List<AliasedSimpleName> temp_aliasedNames = getAliasedNames();
            List<AliasedSimpleName> casted_aliasedNames = casted.getAliasedNames();
            if (!(temp_aliasedNames == casted_aliasedNames || temp_aliasedNames.equals(casted_aliasedNames))) return false;
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
        List<AliasedSimpleName> temp_aliasedNames = getAliasedNames();
        code ^= temp_aliasedNames.hashCode();
        return code;
    }
}
