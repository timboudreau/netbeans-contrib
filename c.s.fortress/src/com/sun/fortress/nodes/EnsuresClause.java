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
 * Class EnsuresClause, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class EnsuresClause extends AbstractNode {
    private final Expr _post;
    private final Option<Expr> _pre;

    /**
     * Constructs a EnsuresClause.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public EnsuresClause(Span in_span, Expr in_post, Option<Expr> in_pre) {
        super(in_span);
        if (in_post == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'post' to the EnsuresClause constructor was null");
        }
        _post = in_post;
        if (in_pre == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'pre' to the EnsuresClause constructor was null");
        }
        _pre = in_pre;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public EnsuresClause(Span in_span, Expr in_post) {
        this(in_span, in_post, Option.<Expr>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public EnsuresClause(Expr in_post, Option<Expr> in_pre) {
        this(new Span(), in_post, in_pre);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public EnsuresClause(Expr in_post) {
        this(new Span(), in_post, Option.<Expr>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected EnsuresClause() {
        _post = null;
        _pre = null;
    }

    final public Expr getPost() { return _post; }
    final public Option<Expr> getPre() { return _pre; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forEnsuresClause(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forEnsuresClause(this); }

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
        writer.print("EnsuresClause:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Expr temp_post = getPost();
        writer.startLine();
        writer.print("post = ");
        temp_post.outputHelp(writer, lossless);

        Option<Expr> temp_pre = getPre();
        writer.startLine();
        writer.print("pre = ");
        if (temp_pre.isSome()) {
            writer.print("(");
            Expr elt_temp_pre = edu.rice.cs.plt.tuple.Option.unwrap(temp_pre);
            if (elt_temp_pre == null) {
                writer.print("null");
            } else {
                elt_temp_pre.outputHelp(writer, lossless);
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
            EnsuresClause casted = (EnsuresClause) obj;
            Expr temp_post = getPost();
            Expr casted_post = casted.getPost();
            if (!(temp_post == casted_post || temp_post.equals(casted_post))) return false;
            Option<Expr> temp_pre = getPre();
            Option<Expr> casted_pre = casted.getPre();
            if (!(temp_pre == casted_pre || temp_pre.equals(casted_pre))) return false;
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
        Expr temp_post = getPost();
        code ^= temp_post.hashCode();
        Option<Expr> temp_pre = getPre();
        code ^= temp_pre.hashCode();
        return code;
    }
}
