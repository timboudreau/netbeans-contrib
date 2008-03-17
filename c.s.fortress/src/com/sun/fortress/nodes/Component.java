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
 * Class Component, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Component extends CompilationUnit {
    private final List<Export> _exports;
    private final List<Decl> _decls;

    /**
     * Constructs a Component.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Component(Span in_span, boolean in__native, APIName in_name, List<Import> in_imports, List<Export> in_exports, List<Decl> in_decls) {
        super(in_span, in__native, in_name, in_imports);
        if (in_exports == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'exports' to the Component constructor was null");
        }
        _exports = in_exports;
        if (in_decls == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'decls' to the Component constructor was null");
        }
        _decls = in_decls;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Component(Span in_span, APIName in_name, List<Import> in_imports, List<Export> in_exports, List<Decl> in_decls) {
        this(in_span, false, in_name, in_imports, in_exports, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Component(boolean in__native, APIName in_name, List<Import> in_imports, List<Export> in_exports, List<Decl> in_decls) {
        this(new Span(), in__native, in_name, in_imports, in_exports, in_decls);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Component(APIName in_name, List<Import> in_imports, List<Export> in_exports, List<Decl> in_decls) {
        this(new Span(), false, in_name, in_imports, in_exports, in_decls);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Component() {
        _exports = null;
        _decls = null;
    }

    final public List<Export> getExports() { return _exports; }
    final public List<Decl> getDecls() { return _decls; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forComponent(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forComponent(this); }

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
        writer.print("Component:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        boolean temp__native = is_native();
        writer.startLine();
        writer.print("_native = ");
        writer.print(temp__native);

        APIName temp_name = getName();
        writer.startLine();
        writer.print("name = ");
        temp_name.outputHelp(writer, lossless);

        List<Import> temp_imports = getImports();
        writer.startLine();
        writer.print("imports = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_imports = true;
        for (Import elt_temp_imports : temp_imports) {
            isempty_temp_imports = false;
            writer.startLine("* ");
            if (elt_temp_imports == null) {
                writer.print("null");
            } else {
                elt_temp_imports.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_imports) writer.print(" }");
        else writer.startLine("}");

        List<Export> temp_exports = getExports();
        writer.startLine();
        writer.print("exports = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_exports = true;
        for (Export elt_temp_exports : temp_exports) {
            isempty_temp_exports = false;
            writer.startLine("* ");
            if (elt_temp_exports == null) {
                writer.print("null");
            } else {
                elt_temp_exports.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_exports) writer.print(" }");
        else writer.startLine("}");

        List<Decl> temp_decls = getDecls();
        writer.startLine();
        writer.print("decls = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_decls = true;
        for (Decl elt_temp_decls : temp_decls) {
            isempty_temp_decls = false;
            writer.startLine("* ");
            if (elt_temp_decls == null) {
                writer.print("null");
            } else {
                elt_temp_decls.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_decls) writer.print(" }");
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
            Component casted = (Component) obj;
            boolean temp__native = is_native();
            boolean casted__native = casted.is_native();
            if (!(temp__native == casted__native)) return false;
            APIName temp_name = getName();
            APIName casted_name = casted.getName();
            if (!(temp_name == casted_name || temp_name.equals(casted_name))) return false;
            List<Import> temp_imports = getImports();
            List<Import> casted_imports = casted.getImports();
            if (!(temp_imports == casted_imports || temp_imports.equals(casted_imports))) return false;
            List<Export> temp_exports = getExports();
            List<Export> casted_exports = casted.getExports();
            if (!(temp_exports == casted_exports || temp_exports.equals(casted_exports))) return false;
            List<Decl> temp_decls = getDecls();
            List<Decl> casted_decls = casted.getDecls();
            if (!(temp_decls == casted_decls || temp_decls.equals(casted_decls))) return false;
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
        boolean temp__native = is_native();
        code ^= temp__native ? 1231 : 1237;
        APIName temp_name = getName();
        code ^= temp_name.hashCode();
        List<Import> temp_imports = getImports();
        code ^= temp_imports.hashCode();
        List<Export> temp_exports = getExports();
        code ^= temp_exports.hashCode();
        List<Decl> temp_decls = getDecls();
        code ^= temp_decls.hashCode();
        return code;
    }
}
