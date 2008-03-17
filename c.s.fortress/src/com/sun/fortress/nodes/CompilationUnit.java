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
 * Class CompilationUnit, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public abstract class CompilationUnit extends AbstractNode {
    private final boolean __native;
    private final APIName _name;
    private final List<Import> _imports;

    /**
     * Constructs a CompilationUnit.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public CompilationUnit(Span in_span, boolean in__native, APIName in_name, List<Import> in_imports) {
        super(in_span);
        __native = in__native;
        if (in_name == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'name' to the CompilationUnit constructor was null");
        }
        _name = in_name;
        if (in_imports == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'imports' to the CompilationUnit constructor was null");
        }
        _imports = in_imports;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CompilationUnit(Span in_span, APIName in_name, List<Import> in_imports) {
        this(in_span, false, in_name, in_imports);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CompilationUnit(boolean in__native, APIName in_name, List<Import> in_imports) {
        this(new Span(), in__native, in_name, in_imports);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CompilationUnit(APIName in_name, List<Import> in_imports) {
        this(new Span(), false, in_name, in_imports);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected CompilationUnit() {
        __native = false;
        _name = null;
        _imports = null;
    }

    public boolean is_native() { return __native; }
    public APIName getName() { return _name; }
    public List<Import> getImports() { return _imports; }

    public abstract <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public abstract void accept(NodeVisitor_void visitor);
    /** Generate a human-readable representation that can be deserialized. */
    public abstract java.lang.String serialize();
    /** Generate a human-readable representation that can be deserialized. */
    public abstract void serialize(java.io.Writer writer);
    public abstract void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
