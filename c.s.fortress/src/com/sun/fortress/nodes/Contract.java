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
 * Class Contract, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class Contract extends AbstractNode {
    private final Option<List<Expr>> _requires;
    private final Option<List<EnsuresClause>> _ensures;
    private final Option<List<Expr>> _invariants;

    /**
     * Constructs a Contract.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public Contract(Span in_span, Option<List<Expr>> in_requires, Option<List<EnsuresClause>> in_ensures, Option<List<Expr>> in_invariants) {
        super(in_span);
        if (in_requires == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'requires' to the Contract constructor was null");
        }
        _requires = in_requires;
        if (in_ensures == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'ensures' to the Contract constructor was null");
        }
        _ensures = in_ensures;
        if (in_invariants == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'invariants' to the Contract constructor was null");
        }
        _invariants = in_invariants;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Contract(Span in_span, Option<List<Expr>> in_requires, Option<List<EnsuresClause>> in_ensures) {
        this(in_span, in_requires, in_ensures, Option.<List<Expr>>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Contract(Span in_span, Option<List<Expr>> in_requires) {
        this(in_span, in_requires, Option.<List<EnsuresClause>>none(), Option.<List<Expr>>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Contract(Span in_span) {
        this(in_span, Option.<List<Expr>>none(), Option.<List<EnsuresClause>>none(), Option.<List<Expr>>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Contract(Option<List<Expr>> in_requires, Option<List<EnsuresClause>> in_ensures, Option<List<Expr>> in_invariants) {
        this(new Span(), in_requires, in_ensures, in_invariants);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Contract(Option<List<Expr>> in_requires, Option<List<EnsuresClause>> in_ensures) {
        this(new Span(), in_requires, in_ensures, Option.<List<Expr>>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Contract(Option<List<Expr>> in_requires) {
        this(new Span(), in_requires, Option.<List<EnsuresClause>>none(), Option.<List<Expr>>none());
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public Contract() {
        this(new Span(), Option.<List<Expr>>none(), Option.<List<EnsuresClause>>none(), Option.<List<Expr>>none());
    }

    final public Option<List<Expr>> getRequires() { return _requires; }
    final public Option<List<EnsuresClause>> getEnsures() { return _ensures; }
    final public Option<List<Expr>> getInvariants() { return _invariants; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forContract(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forContract(this); }

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
        writer.print("Contract:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        Option<List<Expr>> temp_requires = getRequires();
        writer.startLine();
        writer.print("requires = ");
        if (temp_requires.isSome()) {
            writer.print("(");
            List<Expr> elt_temp_requires = edu.rice.cs.plt.tuple.Option.unwrap(temp_requires);
            if (elt_temp_requires == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_requires = true;
                for (Expr elt_elt_temp_requires : elt_temp_requires) {
                    isempty_elt_temp_requires = false;
                    writer.startLine("* ");
                    if (elt_elt_temp_requires == null) {
                        writer.print("null");
                    } else {
                        elt_elt_temp_requires.outputHelp(writer, lossless);
                    }
                }
                writer.unindent();
                if (isempty_elt_temp_requires) writer.print(" }");
                else writer.startLine("}");
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<List<EnsuresClause>> temp_ensures = getEnsures();
        writer.startLine();
        writer.print("ensures = ");
        if (temp_ensures.isSome()) {
            writer.print("(");
            List<EnsuresClause> elt_temp_ensures = edu.rice.cs.plt.tuple.Option.unwrap(temp_ensures);
            if (elt_temp_ensures == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_ensures = true;
                for (EnsuresClause elt_elt_temp_ensures : elt_temp_ensures) {
                    isempty_elt_temp_ensures = false;
                    writer.startLine("* ");
                    if (elt_elt_temp_ensures == null) {
                        writer.print("null");
                    } else {
                        elt_elt_temp_ensures.outputHelp(writer, lossless);
                    }
                }
                writer.unindent();
                if (isempty_elt_temp_ensures) writer.print(" }");
                else writer.startLine("}");
            }
            writer.print(")");
        }
        else { writer.print(lossless ? "~" : "()"); }

        Option<List<Expr>> temp_invariants = getInvariants();
        writer.startLine();
        writer.print("invariants = ");
        if (temp_invariants.isSome()) {
            writer.print("(");
            List<Expr> elt_temp_invariants = edu.rice.cs.plt.tuple.Option.unwrap(temp_invariants);
            if (elt_temp_invariants == null) {
                writer.print("null");
            } else {
                writer.print("{");
                writer.indent();
                boolean isempty_elt_temp_invariants = true;
                for (Expr elt_elt_temp_invariants : elt_temp_invariants) {
                    isempty_elt_temp_invariants = false;
                    writer.startLine("* ");
                    if (elt_elt_temp_invariants == null) {
                        writer.print("null");
                    } else {
                        elt_elt_temp_invariants.outputHelp(writer, lossless);
                    }
                }
                writer.unindent();
                if (isempty_elt_temp_invariants) writer.print(" }");
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
            Contract casted = (Contract) obj;
            Option<List<Expr>> temp_requires = getRequires();
            Option<List<Expr>> casted_requires = casted.getRequires();
            if (!(temp_requires == casted_requires || temp_requires.equals(casted_requires))) return false;
            Option<List<EnsuresClause>> temp_ensures = getEnsures();
            Option<List<EnsuresClause>> casted_ensures = casted.getEnsures();
            if (!(temp_ensures == casted_ensures || temp_ensures.equals(casted_ensures))) return false;
            Option<List<Expr>> temp_invariants = getInvariants();
            Option<List<Expr>> casted_invariants = casted.getInvariants();
            if (!(temp_invariants == casted_invariants || temp_invariants.equals(casted_invariants))) return false;
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
        Option<List<Expr>> temp_requires = getRequires();
        code ^= temp_requires.hashCode();
        Option<List<EnsuresClause>> temp_ensures = getEnsures();
        code ^= temp_ensures.hashCode();
        Option<List<Expr>> temp_invariants = getInvariants();
        code ^= temp_invariants.hashCode();
        return code;
    }
}
