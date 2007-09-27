/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.jackpot.rules.parser;

import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.parser.*;
import com.sun.tools.javac.util.*;
import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.*;
import static com.sun.tools.javac.parser.Token.*;

public class ScriptParser {
    protected static final Logger logger = Logger.getLogger("org.netbeans.modules.jackpot.rules");

    public void setIn(String name, String s) {
	log.setSource(name, s);
	reset();
        scanner = makeScanner(s);
        parser = makeParser();
    }
    public void setIn(String name, Reader in) {
	char[] src;
	try {
	    CharArrayWriter out = new CharArrayWriter();
	    char[] buf = new char[8192];
	    int n;
	    while ((n = in.read(buf)) > 0)
		out.write(buf, 0, n);
	    out.write(0); // java scanner needs at least one extra end char.
	    src = out.toCharArray();
	} catch (IOException e) {
	    src = new char[1]; // just the scanner's extra char
	}
	log.setSource(name, new String(src, 0, src.length-1));
	reset();
	scanner = makeScanner(src, src.length-1);
        parser = makeParser();
    }
    public final JCStatement statement() {
	return parser.statement();
    }
    public final JCExpression expression() {
	return parser.expression();
    }
    public final boolean atEnd() {
	return scanner.token() == EOF;
    }
    public boolean hasErrors() { return log.hasErrors(); }
    public String getErrors() { return log.getErrors(); }
    public JCTree statement(String s) {
        setIn("Script", s);
	return parser.statement();
    }
    public JCTree expression(String s) {
        setIn("Script", s);
	return parser.expression();
    }
    static public void main(String[] argv) {
	System.err.println("t="+new ScriptParser().expression("2+2"));
    }

    public static class ErrorMessage {
	String msg;
	int pos;
	public ErrorMessage(int p, String m) { pos = p; msg = m; }
    }

    /** allows context to be toyed with before JavaCompiler gets ahold of it */
    protected Context createContext() { 
        Context ctx = new Context(); 
        log = new ScriptLog(ctx);
        JavacFileManager.preRegister(ctx);
        return ctx;
    }
    
    protected ScriptParser() {
        context = createContext();
        names = Name.Table.instance(context);
        make = TreeMaker.instance(context);
    }
	
    protected final Context context;
    protected ScriptLog log;
    protected Scanner scanner;
    protected Parser parser;
    
    protected final Name.Table names;
    protected final TreeMaker make;

    public boolean isToken(Name name) {
        return scanner.token() == CUSTOM && scanner.name().equals(name);
    }
    public Name tokenName(Token token) {
	return names.fromString(token.name());
    }
    public void reset() {
	log.reset();
    }
    protected Scanner makeScanner(String s) {
        char[] buf = new char[s.length() + 1];
        System.arraycopy(s.toCharArray(), 0, buf, 0, s.length());
	return makeScanner(buf, buf.length-1);
    }
    private Scanner makeScanner(char[] src, int length) {
        assert src.length > length; // scanner requires buffer to be larger
        Scanner.Factory scannerFactory = Scanner.Factory.instance(context);
	return scannerFactory.newScanner(src, length);
    }
    protected Parser makeParser() {
        Parser.Factory parserFactory = Parser.Factory.instance(context);
	return parserFactory.newParser(scanner, false, false);
    }

    static class ScriptLog extends Log {
	private ErrorMessage[] errors;
	private int errorListLength;
	private int maxErrors = 10;
	private String src = null;
	private boolean fatalError = false;
	ScriptLog(Context context) { 
	    super(context); 
	}
	public void reset() { errorListLength = 0; errors=null; }
	public void prompt() {}
	public void flush() {}
	public final void setSource(String name, String src) { 
	    this.src = src;
            try {
                useSource(new SimpleJavaFileObject(new URI(name.replace(' ', '_')), JavaFileObject.Kind.SOURCE) {
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		    return ScriptLog.this.src;
                }
                });
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
	}
        public final boolean hasErrors() { return fatalError; }
        public final String getErrors() {
            if(errorListLength<=0) return null;
            StringBuffer sb = new StringBuffer();
            if(errorListLength>errors.length) {
                sb.append(errorListLength+" errors\n");
                errorListLength = errors.length;
            }
            for(int i = 0; i<errorListLength; i++) {
		ErrorMessage e = errors[i];
		sb.append(source.getFile().getName());
		sb.append(':');
		sb.append(source.getLineNumber(e.pos));
		sb.append(": ");
                sb.append(e.msg);
            }
            return sb.toString();
        }
        public void report(JCDiagnostic diag) {
	    rawError((int)diag.getPosition(), diag.getMessage(null));
        }
	public void rawError(int pos, String msg) {
	    if(errors==null) errors = new ErrorMessage[maxErrors];
	    if (errorListLength < maxErrors) {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Log.printLines(writer, msg);
		writer.flush();
		errors[errorListLength++] = new ErrorMessage(pos, sw.toString());
	    }
	    fatalError = true;
	}
    }
}

