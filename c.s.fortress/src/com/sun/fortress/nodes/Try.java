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
 * Class Try, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Try extends DelimitedExpr {
    private final Block _body;
    private final Option<Catch> _catchClause;
    private final List<TraitType> _forbid;
    private final Option<Block> _finallyClause;

    /**
     * Constructs a Try.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Try(Span in_span, boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        super(in_span, in_parenthesized);
        if (in_body == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'body' to the Try constructor was null");
        }
        _body = in_body;
        if (in_catchClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'catchClause' to the Try constructor was null");
        }
        _catchClause = in_catchClause;
        if (in_forbid == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'forbid' to the Try constructor was null");
        }
        _forbid = in_forbid;
        if (in_finallyClause == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'finallyClause' to the Try constructor was null");
        }
        _finallyClause = in_finallyClause;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid) {
        this(in_span, in_parenthesized, in_body, in_catchClause, in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause, Option<Block> in_finallyClause) {
        this(in_span, in_parenthesized, in_body, in_catchClause, Collections.<TraitType>emptyList(), in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause) {
        this(in_span, in_parenthesized, in_body, in_catchClause, Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, boolean in_parenthesized, Block in_body, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        this(in_span, in_parenthesized, in_body, Option.<Catch>none(), in_forbid, in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, boolean in_parenthesized, Block in_body, List<TraitType> in_forbid) {
        this(in_span, in_parenthesized, in_body, Option.<Catch>none(), in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, boolean in_parenthesized, Block in_body) {
        this(in_span, in_parenthesized, in_body, Option.<Catch>none(), Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        this(in_span, false, in_body, in_catchClause, in_forbid, in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid) {
        this(in_span, false, in_body, in_catchClause, in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, Block in_body, Option<Catch> in_catchClause, Option<Block> in_finallyClause) {
        this(in_span, false, in_body, in_catchClause, Collections.<TraitType>emptyList(), in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, Block in_body, Option<Catch> in_catchClause) {
        this(in_span, false, in_body, in_catchClause, Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, Block in_body, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        this(in_span, false, in_body, Option.<Catch>none(), in_forbid, in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, Block in_body, List<TraitType> in_forbid) {
        this(in_span, false, in_body, Option.<Catch>none(), in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Span in_span, Block in_body) {
        this(in_span, false, in_body, Option.<Catch>none(), Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        this(new Span(), in_parenthesized, in_body, in_catchClause, in_forbid, in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid) {
        this(new Span(), in_parenthesized, in_body, in_catchClause, in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause, Option<Block> in_finallyClause) {
        this(new Span(), in_parenthesized, in_body, in_catchClause, Collections.<TraitType>emptyList(), in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(boolean in_parenthesized, Block in_body, Option<Catch> in_catchClause) {
        this(new Span(), in_parenthesized, in_body, in_catchClause, Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(boolean in_parenthesized, Block in_body, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        this(new Span(), in_parenthesized, in_body, Option.<Catch>none(), in_forbid, in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(boolean in_parenthesized, Block in_body, List<TraitType> in_forbid) {
        this(new Span(), in_parenthesized, in_body, Option.<Catch>none(), in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(boolean in_parenthesized, Block in_body) {
        this(new Span(), in_parenthesized, in_body, Option.<Catch>none(), Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        this(new Span(), false, in_body, in_catchClause, in_forbid, in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Block in_body, Option<Catch> in_catchClause, List<TraitType> in_forbid) {
        this(new Span(), false, in_body, in_catchClause, in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Block in_body, Option<Catch> in_catchClause, Option<Block> in_finallyClause) {
        this(new Span(), false, in_body, in_catchClause, Collections.<TraitType>emptyList(), in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Block in_body, Option<Catch> in_catchClause) {
        this(new Span(), false, in_body, in_catchClause, Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Block in_body, List<TraitType> in_forbid, Option<Block> in_finallyClause) {
        this(new Span(), false, in_body, Option.<Catch>none(), in_forbid, in_finallyClause);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Block in_body, List<TraitType> in_forbid) {
        this(new Span(), false, in_body, Option.<Catch>none(), in_forbid, Option.<Block>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Try(Block in_body) {
        this(new Span(), false, in_body, Option.<Catch>none(), Collections.<TraitType>emptyList(), Option.<Block>none());
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected Try() {
        _body = null;
        _catchClause = null;
        _forbid = null;
        _finallyClause = null;
    }

    final public Block getBody() { return _body; }
    final public Option<Catch> getCatchClause() { return _catchClause; }
    final public List<TraitType> getForbid() { return _forbid; }
    final public Option<Block> getFinallyClause() { return _finallyClause; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forTry(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forTry(this); }

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
        writer.print("Try:");
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

        Block temp_body = getBody();
        writer.startLine();
        writer.print("body = ");
        temp_body.outputHelp(writer, lossless);

        Option<Catch> temp_catchClause = getCatchClause();
        writer.startLine();
        writer.print("catchClause = ");
        if (temp_catchClause.isSome()) {
            writer.print("(");
            Catch elt_temp_catchClause = edu.rice.cs.plt.tuple.Option.unwrap(temp_catchClause);
            if (elt_temp_catchClause == null) {
                writer.print("null");
            } else {
                elt_temp_catchClause.outputHelp(writer, lossless);
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        List<TraitType> temp_forbid = getForbid();
        writer.startLine();
        writer.print("forbid = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_forbid = true;
        for (TraitType elt_temp_forbid : temp_forbid) {
            isempty_temp_forbid = false;
            writer.startLine("* ");
            if (elt_temp_forbid == null) {
                writer.print("null");
            } else {
                elt_temp_forbid.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_forbid) writer.print(" }");
        else writer.startLine("}");

        Option<Block> temp_finallyClause = getFinallyClause();
        writer.startLine();
        writer.print("finallyClause = ");
        if (temp_finallyClause.isSome()) {
            writer.print("(");
            Block elt_temp_finallyClause = edu.rice.cs.plt.tuple.Option.unwrap(temp_finallyClause);
            if (elt_temp_finallyClause == null) {
                writer.print("null");
            } else {
                elt_temp_finallyClause.outputHelp(writer, lossless);
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
            Try casted = (Try) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Block temp_body = getBody();
            Block casted_body = casted.getBody();
            if (!(temp_body == casted_body || temp_body.equals(casted_body))) return false;
            Option<Catch> temp_catchClause = getCatchClause();
            Option<Catch> casted_catchClause = casted.getCatchClause();
            if (!(temp_catchClause == casted_catchClause || temp_catchClause.equals(casted_catchClause))) return false;
            List<TraitType> temp_forbid = getForbid();
            List<TraitType> casted_forbid = casted.getForbid();
            if (!(temp_forbid == casted_forbid || temp_forbid.equals(casted_forbid))) return false;
            Option<Block> temp_finallyClause = getFinallyClause();
            Option<Block> casted_finallyClause = casted.getFinallyClause();
            if (!(temp_finallyClause == casted_finallyClause || temp_finallyClause.equals(casted_finallyClause))) return false;
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
        Block temp_body = getBody();
        code ^= temp_body.hashCode();
        Option<Catch> temp_catchClause = getCatchClause();
        code ^= temp_catchClause.hashCode();
        List<TraitType> temp_forbid = getForbid();
        code ^= temp_forbid.hashCode();
        Option<Block> temp_finallyClause = getFinallyClause();
        code ^= temp_finallyClause.hashCode();
        return code;
    }
}
