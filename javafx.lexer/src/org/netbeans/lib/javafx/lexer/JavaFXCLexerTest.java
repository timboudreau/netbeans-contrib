/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

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
