package org.netbeans.lib.javafx.lexer;

import com.sun.tools.javafx.antlr.v3Lexer;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Token;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 * @todo documentation
 */
public class JavaFXCLexerTest {


    public static void main(String[] args) {
        final StringReader stringReader = new StringReader(
                "/*\n" +
                        " * Main.fx\n" +
                        " *\n" +
                        " * Created on 17.3.2008, 16:44:20\n" +
                        " */\n" +
                        "\n" +
                        "package javafxapplication1;\n" +
                        "\n" +
                        "/**\n" +
                        " * @author moonko\n" +
                        " */\n" +
                        "\n" +
                        "class {\n" +
                        "    var text : String;\n" +
                        "    button:Button {\n" +
                        "        x = 45\n" +
                        "        y = 12\n" +
                        "        z = 458        \n" +
                        "\ttitle =\"{}\";\n" +
                        "\ttitle2 = \"{helolo}\";\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "// place your code here\n" +
                        ""
        );
        try {
            Lexer lexer = new v3Lexer(new ANTLRReaderStream(stringReader));
            Token token = lexer.nextToken();
            while (token.getType() != v3Lexer.EOF) {
                System.out.println(token);
                token = lexer.nextToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
