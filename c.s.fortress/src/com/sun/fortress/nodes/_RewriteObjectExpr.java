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
 * Class _RewriteObjectExpr, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class _RewriteObjectExpr extends AbstractObjectExpr implements GenericWithParams {
    private final BATree<String, StaticParam> _implicitTypeParameters;
    private final String _genSymName;
    private final List<StaticParam> _staticParams;
    private final List<StaticArg> _staticArgs;
    private final Option<List<Param>> _params;

    /**
     * Constructs a _RewriteObjectExpr.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public _RewriteObjectExpr(Span in_span, boolean in_parenthesized, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        super(in_span, in_parenthesized, in_extendsClause, in_decls);
        if (in_implicitTypeParameters == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'implicitTypeParameters' to the _RewriteObjectExpr constructor was null");
        }
        _implicitTypeParameters = in_implicitTypeParameters;
        if (in_genSymName == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'genSymName' to the _RewriteObjectExpr constructor was null");
        }
        _genSymName = in_genSymName.intern();
        if (in_staticParams == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticParams' to the _RewriteObjectExpr constructor was null");
        }
        _staticParams = in_staticParams;
        if (in_staticArgs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticArgs' to the _RewriteObjectExpr constructor was null");
        }
        _staticArgs = in_staticArgs;
        if (in_params == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'params' to the _RewriteObjectExpr constructor was null");
        }
        _params = in_params;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteObjectExpr(Span in_span, boolean in_parenthesized, List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        this(in_span, in_parenthesized, Collections.<TraitTypeWhere>emptyList(), in_decls, in_implicitTypeParameters, in_genSymName, in_staticParams, in_staticArgs, in_params);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteObjectExpr(Span in_span, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        this(in_span, false, in_extendsClause, in_decls, in_implicitTypeParameters, in_genSymName, in_staticParams, in_staticArgs, in_params);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteObjectExpr(Span in_span, List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        this(in_span, false, Collections.<TraitTypeWhere>emptyList(), in_decls, in_implicitTypeParameters, in_genSymName, in_staticParams, in_staticArgs, in_params);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteObjectExpr(boolean in_parenthesized, List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        this(new Span(), in_parenthesized, in_extendsClause, in_decls, in_implicitTypeParameters, in_genSymName, in_staticParams, in_staticArgs, in_params);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteObjectExpr(boolean in_parenthesized, List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        this(new Span(), in_parenthesized, Collections.<TraitTypeWhere>emptyList(), in_decls, in_implicitTypeParameters, in_genSymName, in_staticParams, in_staticArgs, in_params);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteObjectExpr(List<TraitTypeWhere> in_extendsClause, List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        this(new Span(), false, in_extendsClause, in_decls, in_implicitTypeParameters, in_genSymName, in_staticParams, in_staticArgs, in_params);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public _RewriteObjectExpr(List<Decl> in_decls, BATree<String, StaticParam> in_implicitTypeParameters, String in_genSymName, List<StaticParam> in_staticParams, List<StaticArg> in_staticArgs, Option<List<Param>> in_params) {
        this(new Span(), false, Collections.<TraitTypeWhere>emptyList(), in_decls, in_implicitTypeParameters, in_genSymName, in_staticParams, in_staticArgs, in_params);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected _RewriteObjectExpr() {
        _implicitTypeParameters = null;
        _genSymName = null;
        _staticParams = null;
        _staticArgs = null;
        _params = null;
    }

    final public BATree<String, StaticParam> getImplicitTypeParameters() { return _implicitTypeParameters; }
    final public String getGenSymName() { return _genSymName; }
    final public List<StaticParam> getStaticParams() { return _staticParams; }
    final public List<StaticArg> getStaticArgs() { return _staticArgs; }
    final public Option<List<Param>> getParams() { return _params; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.for_RewriteObjectExpr(this); }
    public void accept(NodeVisitor_void visitor) { visitor.for_RewriteObjectExpr(this); }

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
        writer.print("_RewriteObjectExpr:");
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

        List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
        writer.startLine();
        writer.print("extendsClause = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_extendsClause = true;
        for (TraitTypeWhere elt_temp_extendsClause : temp_extendsClause) {
            isempty_temp_extendsClause = false;
            writer.startLine("* ");
            if (elt_temp_extendsClause == null) {
                writer.print("null");
            } else {
                elt_temp_extendsClause.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_extendsClause) writer.print(" }");
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

        BATree<String, StaticParam> temp_implicitTypeParameters = getImplicitTypeParameters();
        writer.startLine();
        writer.print("implicitTypeParameters = ");
        if (lossless) {
            writer.printSerialized(temp_implicitTypeParameters);
            writer.print(" ");
            writer.printEscaped(temp_implicitTypeParameters);
        } else { writer.print(temp_implicitTypeParameters); }

        String temp_genSymName = getGenSymName();
        writer.startLine();
        writer.print("genSymName = ");
        if (lossless) {
            writer.print("\"");
            writer.printEscaped(temp_genSymName);
            writer.print("\"");
        } else { writer.print(temp_genSymName); }

        List<StaticParam> temp_staticParams = getStaticParams();
        writer.startLine();
        writer.print("staticParams = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_staticParams = true;
        for (StaticParam elt_temp_staticParams : temp_staticParams) {
            isempty_temp_staticParams = false;
            writer.startLine("* ");
            if (elt_temp_staticParams == null) {
                writer.print("null");
            } else {
                elt_temp_staticParams.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_staticParams) writer.print(" }");
        else writer.startLine("}");

        List<StaticArg> temp_staticArgs = getStaticArgs();
        writer.startLine();
        writer.print("staticArgs = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_staticArgs = true;
        for (StaticArg elt_temp_staticArgs : temp_staticArgs) {
            isempty_temp_staticArgs = false;
            writer.startLine("* ");
            if (elt_temp_staticArgs == null) {
                writer.print("null");
            } else {
                elt_temp_staticArgs.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_staticArgs) writer.print(" }");
        else writer.startLine("}");

        Option<List<Param>> temp_params = getParams();
        writer.startLine();
        writer.print("params = ");
        if (temp_params.isSome()) {
            writer.print("(");
            List<Param> elt_temp_params = edu.rice.cs.plt.tuple.Option.unwrap(temp_params);
            if (elt_temp_params == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_params = true;
                for (Param elt_elt_temp_params : elt_temp_params) {
                    isempty_elt_temp_params = false;
                    writer.startLine("* ");
                    if (elt_elt_temp_params == null) {
                        writer.print("null");
                    } else {
                        elt_elt_temp_params.outputHelp(writer, lossless);
                    }
                }
                writer.unindent();
                if (isempty_elt_temp_params) writer.print(" }");
                else writer.startLine("}");
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
            _RewriteObjectExpr casted = (_RewriteObjectExpr) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
            List<TraitTypeWhere> casted_extendsClause = casted.getExtendsClause();
            if (!(temp_extendsClause == casted_extendsClause || temp_extendsClause.equals(casted_extendsClause))) return false;
            List<Decl> temp_decls = getDecls();
            List<Decl> casted_decls = casted.getDecls();
            if (!(temp_decls == casted_decls || temp_decls.equals(casted_decls))) return false;
            BATree<String, StaticParam> temp_implicitTypeParameters = getImplicitTypeParameters();
            BATree<String, StaticParam> casted_implicitTypeParameters = casted.getImplicitTypeParameters();
            if (!(temp_implicitTypeParameters == casted_implicitTypeParameters || temp_implicitTypeParameters.equals(casted_implicitTypeParameters))) return false;
            String temp_genSymName = getGenSymName();
            String casted_genSymName = casted.getGenSymName();
            if (!(temp_genSymName == casted_genSymName)) return false;
            List<StaticParam> temp_staticParams = getStaticParams();
            List<StaticParam> casted_staticParams = casted.getStaticParams();
            if (!(temp_staticParams == casted_staticParams || temp_staticParams.equals(casted_staticParams))) return false;
            List<StaticArg> temp_staticArgs = getStaticArgs();
            List<StaticArg> casted_staticArgs = casted.getStaticArgs();
            if (!(temp_staticArgs == casted_staticArgs || temp_staticArgs.equals(casted_staticArgs))) return false;
            Option<List<Param>> temp_params = getParams();
            Option<List<Param>> casted_params = casted.getParams();
            if (!(temp_params == casted_params || temp_params.equals(casted_params))) return false;
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
        List<TraitTypeWhere> temp_extendsClause = getExtendsClause();
        code ^= temp_extendsClause.hashCode();
        List<Decl> temp_decls = getDecls();
        code ^= temp_decls.hashCode();
        BATree<String, StaticParam> temp_implicitTypeParameters = getImplicitTypeParameters();
        code ^= temp_implicitTypeParameters.hashCode();
        String temp_genSymName = getGenSymName();
        code ^= temp_genSymName.hashCode();
        List<StaticParam> temp_staticParams = getStaticParams();
        code ^= temp_staticParams.hashCode();
        List<StaticArg> temp_staticArgs = getStaticArgs();
        code ^= temp_staticArgs.hashCode();
        Option<List<Param>> temp_params = getParams();
        code ^= temp_params.hashCode();
        return code;
    }
}
