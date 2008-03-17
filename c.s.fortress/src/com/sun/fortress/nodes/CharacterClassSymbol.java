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
 * Class CharacterClassSymbol, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class CharacterClassSymbol extends SyntaxSymbol {
    private final List<CharacterSymbol> _characters;

    /**
     * Constructs a CharacterClassSymbol.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public CharacterClassSymbol(Span in_span, List<CharacterSymbol> in_characters) {
        super(in_span);
        if (in_characters == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'characters' to the CharacterClassSymbol constructor was null");
        }
        _characters = in_characters;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public CharacterClassSymbol(List<CharacterSymbol> in_characters) {
        this(new Span(), in_characters);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected CharacterClassSymbol() {
        _characters = null;
    }

    final public List<CharacterSymbol> getCharacters() { return _characters; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forCharacterClassSymbol(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forCharacterClassSymbol(this); }

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
        writer.print("CharacterClassSymbol:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        List<CharacterSymbol> temp_characters = getCharacters();
        writer.startLine();
        writer.print("characters = ");
        writer.print("{");
        writer.indent();
        boolean isempty_temp_characters = true;
        for (CharacterSymbol elt_temp_characters : temp_characters) {
            isempty_temp_characters = false;
            writer.startLine("* ");
            if (elt_temp_characters == null) {
                writer.print("null");
            } else {
                elt_temp_characters.outputHelp(writer, lossless);
            }
        }
        writer.unindent();
        if (isempty_temp_characters) writer.print(" }");
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
            CharacterClassSymbol casted = (CharacterClassSymbol) obj;
            List<CharacterSymbol> temp_characters = getCharacters();
            List<CharacterSymbol> casted_characters = casted.getCharacters();
            if (!(temp_characters == casted_characters || temp_characters.equals(casted_characters))) return false;
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
        List<CharacterSymbol> temp_characters = getCharacters();
        code ^= temp_characters.hashCode();
        return code;
    }
}
