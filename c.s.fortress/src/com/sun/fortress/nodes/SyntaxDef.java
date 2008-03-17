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
 * Class SyntaxDef, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class SyntaxDef extends SyntaxDecl {
    private final List<SyntaxSymbol> _syntaxSymbols;
    private final Expr _transformationExpression;

    /**
     * Constructs a SyntaxDef.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public SyntaxDef(Span in_span, List<SyntaxSymbol> in_syntaxSymbols, Expr in_transformationExpression) {
        super(in_span);
        if (in_syntaxSymbols == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'syntaxSymbols' to the SyntaxDef constructor was null");
        }
        _syntaxSymbols = in_syntaxSymbols;
        if (in_transformationExpression == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'transformationExpression' to the SyntaxDef constructor was null");
        }
        _transformationExpression = in_transformationExpression;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public SyntaxDef(List<SyntaxSymbol> in_syntaxSymbols, Expr in_transformationExpression) {
        this(new Span(), in_syntaxSymbols, in_transformationExpression);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected SyntaxDef() {
        _syntaxSymbols = null;
        _transformationExpression = null;
    }

    final public List<SyntaxSymbol> getSyntaxSymbols() { return _syntaxSymbols; }
    final public Expr getTransformationExpression() { return _transformationExpression; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forSyntaxDef(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forSyntaxDef(this); }

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
        writer.print("SyntaxDef:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<SyntaxSymbol> temp_syntaxSymbols = getSyntaxSymbols();
        writer.startLine();
        writer.print("syntaxSymbols = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_syntaxSymbols = true;
        for (SyntaxSymbol elt_temp_syntaxSymbols : temp_syntaxSymbols) {
            isempty_temp_syntaxSymbols = false;
            writer.startLine("* ");
            if (elt_temp_syntaxSymbols == null) {
                writer.print("null");
            } else {
                elt_temp_syntaxSymbols.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_syntaxSymbols) writer.print(" }");
        else writer.startLine("}");

        Expr temp_transformationExpression = getTransformationExpression();
        writer.startLine();
        writer.print("transformationExpression = ");
        temp_transformationExpression.outputHelp(writer, lossless);
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
            SyntaxDef casted = (SyntaxDef) obj;
            List<SyntaxSymbol> temp_syntaxSymbols = getSyntaxSymbols();
            List<SyntaxSymbol> casted_syntaxSymbols = casted.getSyntaxSymbols();
            if (!(temp_syntaxSymbols == casted_syntaxSymbols || temp_syntaxSymbols.equals(casted_syntaxSymbols))) return false;
            Expr temp_transformationExpression = getTransformationExpression();
            Expr casted_transformationExpression = casted.getTransformationExpression();
            if (!(temp_transformationExpression == casted_transformationExpression || temp_transformationExpression.equals(casted_transformationExpression))) return false;
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
        List<SyntaxSymbol> temp_syntaxSymbols = getSyntaxSymbols();
        code ^= temp_syntaxSymbols.hashCode();
        Expr temp_transformationExpression = getTransformationExpression();
        code ^= temp_transformationExpression.hashCode();
        return code;
    }
}
