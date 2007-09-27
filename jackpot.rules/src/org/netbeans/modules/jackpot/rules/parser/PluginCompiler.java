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

import com.sun.tools.javac.main.*;
import com.sun.tools.javac.util.*;
import java.io.*;
import javax.tools.JavaFileObject;

/** Helper class for algorithmically generating java source code, then
    compiling it and plugging it back into the current system.  This is most
    often used when you want to use Java like a scripting language and allow
    users to imbed Java source code fragments in (for example) menu actions,
    and then have them transform into runnable classes.
    When compiling and running this class, you must pass in the jar file
    containing the javac classes to use, such as tools.jar from a JDK directory.
    The canonical usage pattern is:
    <pre>
    PluginCompiler pc = new PluginCompiler(); // don't have to "new" each time:
					      // instances can be reused 
    String scriptSourceName = ...;	      // some unique name to identify
					      // this script -- often it's
					      // source file
    if (pc.needsGeneration(scriptSourceName)) {
	// come here if the cached compiled class file is out of date w.r.t
	// the script.  Generate the .java file for the script:
	pc.startGeneration();
	pc.write("public class ");
	pc.writeClassName();
	pc.write("extends SomeUsefulInterface");
	pc.write(" { public ");
	pc.writeClassName();
	pc.write("() { System.err.println(\"Hello from the constructor of \"+this.getClass().getName());}\n");
	Reader in = new BufferedReader(new FileReader(scriptSourceName));
	Parse the script from "in", outputting the results via:
	pc.write(methods to implement SomeUsefulInterface);
	pc.write("}\n");
    }
    Class c = pc.loadClass();		      // Compile the .java file if
					      // necessary; load the .class
					      // regardless
    SomeUsefulInterface o = (SomeUsefulInterface) c.newInstance();
    // do whatever you want with it
</pre>
	*/
public class PluginCompiler {

    private static final File generatedCodeDir;
    private final ClassLoader classloader = new ClassLoader(getClass().getClassLoader()) {
	public Class findClass(String name) throws ClassNotFoundException {
	    try {
		FileInputStream in = new FileInputStream(new File(generatedCodeDir, name + ".class"));
		int len0 = in.available();
		byte[] b = new byte[len0];
		int len1 = in.read(b);
		in.close();
		if (len1 != len0)
		    throw new ClassNotFoundException("read failure");
		return defineClass(name, b, 0, b.length);
	    } catch(Throwable t) {
		ClassNotFoundException cnf;
		if (t instanceof ClassNotFoundException)
		    cnf = (ClassNotFoundException) t;
		else
		    cnf = new ClassNotFoundException("load failure", t);
		throw cnf;
	    }
	}
    };

    static {
        File cb;
        String userdir = System.getProperty("netbeans.user");
        if (userdir != null && userdir.length() > 0)
            cb = new File(userdir);
        else
            cb = new File(System.getProperty("user.home"), ".jackpot");
        cb = new File(cb, "var" + File.separatorChar + "cache" + File.separatorChar + "jackpot");
        if (!cb.exists() && !cb.mkdirs()) {
            File tmp = null;
            try {
                tmp = File.createTempFile("HOHO", null);
                cb = new File(tmp.getParentFile(),
                              "PluginCache-"+System.getProperty("user.name","user"));
            }
            catch(IOException ioe) {
                cb = new File("/tmp/AuxPluginCache");
            }
            finally {
                if (tmp != null)
                    tmp.delete();
            }
            cb.mkdirs();
        }
	generatedCodeDir = cb;
    }

    private String className;
    private File source;
    private File genJava;
    private LineWriter out;
    private static int seq = 0;
    public boolean needsGeneration(String s, long lastModified, boolean force) {
        int i;
        if ((i = s.lastIndexOf('/')) > 0)
            s = s.substring(i);
        if ((i = s.lastIndexOf(".rules")) > 0)
            s = s.substring(0, i);
	int src = 0;
	int dst = 0;
	char [] nbuf;
	if (s != null) {
	    source = new File(s);
	    int limit = s.length();
	    nbuf = new char[(limit < 2 ? 10 : limit) + 6];
	    while (src < limit) {
		char c = s.charAt(src++);
		if (dst > 0) {
		    if (Character.isJavaIdentifierPart(c))
			nbuf[dst++] = c;
		    else if (nbuf[dst - 1] != '_')
			nbuf[dst++] = '_';
		} else if (Character.isJavaIdentifierStart(c))
		    nbuf[dst++] = c;
	    }
	    while (dst > 0 && nbuf[dst - 1] == '_')
		dst--;
	} else {
	    source = null;
	    nbuf = new char[12];
	}
	if (dst == 0) {
	    nbuf[dst++] = 'C';
	    int ls = seq++;
	    do {
		nbuf[dst++] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef".charAt(ls & 31);
		ls >>>= 5;
	    } while (ls != 0);
	}
	className = new String(nbuf, 0, dst);
        String javaName = new String(nbuf, 0, addExt(nbuf, dst, ".java"));
	genJava = new File(generatedCodeDir, javaName);
        File genClass = new File(generatedCodeDir, javaName.replace(".java", ".class"));
	out = null;
	if(force) return true;
	long sm = lastModified > 0 ? lastModified : source != null ? source.lastModified() : 0;
	return sm <= 0 || sm >= genClass.lastModified();
    }
    private static int addExt(char[] buf, int st, String ext) {
	int limit = ext.length();
	for (int i = 0; i < limit; i++)
	    buf[st + i] = ext.charAt(i);
	return st + limit;
    }
    public void startGeneration()
	throws IOException
    {
	out = new LineWriter(new FileWriter(genJava));
    }
    public void write(String s)
	throws IOException
    {
	out.write(s);
    }
    public void write(int i) throws IOException {
	if(i<0) {
	    out.write('-');
	    i = -i;
	}
	if(i>=10) write(i/10);
	out.write((char)('0'+i%10));
    }
    public void write(long i) throws IOException {
	if(i<0) {
	    out.write('-');
	    i = -i;
	}
	if(i>=10) write(i/10);
	out.write((char)('0'+i%10));
    }
	    
    public void writeClassName()
	throws IOException
    {
	out.write(className);
    }
    public void write(char c)
	throws IOException
    {
	out.write(c);
    }
    private static final char[] hexChars = "0123456789ABCDEF".toCharArray();
    private static final char backslash = '\\';
    private static void oneChar(Writer out, char c)
	throws IOException
    {
	if (c < 040 || c == '\'' || c >= 127 && c <= 0377) {
	    out.write(backslash);
	    out.write(hexChars[(c >>> 6) & 7]);
	    out.write(hexChars[(c >>> 3) & 7]);
	    out.write(hexChars[(c >>> 0) & 7]);
        } else if (c == '"') {
            out.write('\\');
            out.write('"');
	} else if (c < 127)
	    out.write(c);
	else {
	    out.write(backslash);
	    out.write('u');
	    out.write(hexChars[(c >>> 12) & 0xF]);
	    out.write(hexChars[(c >>> 8) & 0xF]);
	    out.write(hexChars[(c >>> 4) & 0xF]);
	    out.write(hexChars[(c >>> 0) & 0xF]);
	}
    }
    public void writeQuoted(String s)
	throws IOException 
   {
	Writer out = this.out;
	if (s == null)
	    out.write("null");
	else {
	    int limit = s.length();
	    out.write('"');
	    for (int i = 0; i < limit; i++)
		oneChar(out, s.charAt(i));
	    out.write('"');
	}
    }
    public void writeCapitalizedIdent(String s)
	throws IOException 
   {
	Writer out = this.out;
	if (s == null)
	    out.write("NULL");
	else {
	    int limit = s.length();
	    boolean start = true;
	    for (int i = 0; i < limit; i++) {
		char c = s.charAt(i);
		if(Character.isJavaIdentifierPart(c)) {
		    if(start && Character.isLowerCase(c))
			c = Character.toLowerCase(c);
		    out.write(c);
		    start = false;
		} else start=true;
	    }
	}
    }
    public void writeQuoted(char c)
	throws IOException
    {
	out.write('\'');
	oneChar(out, c);
	out.write('\'');
    }
    public Writer getWriter() { return out; }
    
    public int getCurrentLineNumber() {
        return out.getLineNumber();
    }

    ScriptParser.ScriptLog log;

    public Class loadClass(String javacpath) throws IOException {
	if (out != null) {
	    out.close();
	    Context context = new Context();
            JavacFileManager.preRegister(context);
	    log = new ScriptParser.ScriptLog(context);
	    Options options = Options.instance(context);
	    String gcd = generatedCodeDir.toString();
	    options.put("-source", "1.5");
            options.put("-target", "1.5");
	    options.put("-d", gcd);
	    options.put("-sourcepath", gcd);
            options.put("-g", "-g");
            if (javacpath != null)
                options.put("-Xbootclasspath/p:", javacpath);
            JavacFileManager fileManager = new JavacFileManager(context, true, null);
            JavaFileObject fileobject = fileManager.getFileForInput(genJava.toString());
	    List<JavaFileObject> filenames = List.of(fileobject);
	    JavaCompiler comp = new JavaCompiler(context);
	    log.useSource(fileobject);
	    try {
		comp.compile(filenames);
		if(hasErrors())
		    throw new IOException("Compilation errors");
	    } catch(Throwable t) {
		IOException ioe = new IOException("Class " + className + " compilation exception");
		ioe.initCause(t);
		throw ioe;
	    }
	}
	try {
	    return classloader.loadClass(className);
	} catch(ClassNotFoundException cnf) {
	    IOException ioe = new IOException("Class " + className + " not found");
	    ioe.initCause(cnf);
	    throw ioe;
	}
    }
    public boolean hasErrors() { return log!=null && log.hasErrors(); }
    public String getErrors() { return log==null ? null : log.getErrors(); }
    
    /**
     * BufferedWriter which tracks the current line number of written text.
     */
    private static class LineWriter extends BufferedWriter {
        int line = 0;
        LineWriter(Writer out) {
            super(out);
        }
        public void write(String s, int off, int len) throws IOException {
            write(s.toCharArray(), off, len);
        }
        public void write(char[] cbuf, int off, int len) throws IOException {
            super.write(cbuf, off, len);
            for (int i = off; i < len; i++)
                if (cbuf[i] == '\n')
                    line++;
        }
        public void write(int c) throws IOException {
            super.write(c);
            if (c == '\n')
                line++;
        }
        public void newLine() throws IOException {
            super.newLine();
            line++;
        }
        int getLineNumber() {
            return line;
        }
    }
} 
