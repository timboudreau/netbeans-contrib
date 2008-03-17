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
 * Class MethodInvocation, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class MethodInvocation extends AppExpr {
    private final Expr _obj;
    private final Id _method;
    private final List<StaticArg> _staticArgs;
    private final Expr _arg;

    /**
     * Constructs a MethodInvocation.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public MethodInvocation(Span in_span, boolean in_parenthesized, Expr in_obj, Id in_method, List<StaticArg> in_staticArgs, Expr in_arg) {
        super(in_span, in_parenthesized);
        if (in_obj == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'obj' to the MethodInvocation constructor was null");
        }
        _obj = in_obj;
        if (in_method == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'method' to the MethodInvocation constructor was null");
        }
        _method = in_method;
        if (in_staticArgs == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'staticArgs' to the MethodInvocation constructor was null");
        }
        _staticArgs = in_staticArgs;
        if (in_arg == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'arg' to the MethodInvocation constructor was null");
        }
        _arg = in_arg;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MethodInvocation(Span in_span, boolean in_parenthesized, Expr in_obj, Id in_method, Expr in_arg) {
        this(in_span, in_parenthesized, in_obj, in_method, Collections.<StaticArg>emptyList(), in_arg);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MethodInvocation(Span in_span, Expr in_obj, Id in_method, List<StaticArg> in_staticArgs, Expr in_arg) {
        this(in_span, false, in_obj, in_method, in_staticArgs, in_arg);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MethodInvocation(Span in_span, Expr in_obj, Id in_method, Expr in_arg) {
        this(in_span, false, in_obj, in_method, Collections.<StaticArg>emptyList(), in_arg);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MethodInvocation(boolean in_parenthesized, Expr in_obj, Id in_method, List<StaticArg> in_staticArgs, Expr in_arg) {
        this(new Span(), in_parenthesized, in_obj, in_method, in_staticArgs, in_arg);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MethodInvocation(boolean in_parenthesized, Expr in_obj, Id in_method, Expr in_arg) {
        this(new Span(), in_parenthesized, in_obj, in_method, Collections.<StaticArg>emptyList(), in_arg);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MethodInvocation(Expr in_obj, Id in_method, List<StaticArg> in_staticArgs, Expr in_arg) {
        this(new Span(), false, in_obj, in_method, in_staticArgs, in_arg);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public MethodInvocation(Expr in_obj, Id in_method, Expr in_arg) {
        this(new Span(), false, in_obj, in_method, Collections.<StaticArg>emptyList(), in_arg);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected MethodInvocation() {
        _obj = null;
        _method = null;
        _staticArgs = null;
        _arg = null;
    }

    final public Expr getObj() { return _obj; }
    final public Id getMethod() { return _method; }
    final public List<StaticArg> getStaticArgs() { return _staticArgs; }
    final public Expr getArg() { return _arg; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forMethodInvocation(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forMethodInvocation(this); }

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
        writer.print("MethodInvocation:");
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

        Expr temp_obj = getObj();
        writer.startLine();
        writer.print("obj = ");
        temp_obj.outputHelp(writer, lossless);

        Id temp_method = getMethod();
        writer.startLine();
        writer.print("method = ");
        temp_method.outputHelp(writer, lossless);

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

        Expr temp_arg = getArg();
        writer.startLine();
        writer.print("arg = ");
        temp_arg.outputHelp(writer, lossless);
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
            MethodInvocation casted = (MethodInvocation) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_obj = getObj();
            Expr casted_obj = casted.getObj();
            if (!(temp_obj == casted_obj || temp_obj.equals(casted_obj))) return false;
            Id temp_method = getMethod();
            Id casted_method = casted.getMethod();
            if (!(temp_method == casted_method || temp_method.equals(casted_method))) return false;
            List<StaticArg> temp_staticArgs = getStaticArgs();
            List<StaticArg> casted_staticArgs = casted.getStaticArgs();
            if (!(temp_staticArgs == casted_staticArgs || temp_staticArgs.equals(casted_staticArgs))) return false;
            Expr temp_arg = getArg();
            Expr casted_arg = casted.getArg();
            if (!(temp_arg == casted_arg || temp_arg.equals(casted_arg))) return false;
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
        Expr temp_obj = getObj();
        code ^= temp_obj.hashCode();
        Id temp_method = getMethod();
        code ^= temp_method.hashCode();
        List<StaticArg> temp_staticArgs = getStaticArgs();
        code ^= temp_staticArgs.hashCode();
        Expr temp_arg = getArg();
        code ^= temp_arg.hashCode();
        return code;
    }
}
