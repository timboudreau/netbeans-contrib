/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.javafx.preview;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;

import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

import java.lang.reflect.Method;

import com.sun.javafx.api.JavafxcTask;
import com.sun.javafx.api.ToolProvider;
import java.util.List;
import javax.tools.JavaFileObject;
import com.sun.tools.javafx.script.MemoryFileManager;
import com.sun.javafx.runtime.sequence.Sequences;
import com.sun.javafx.runtime.sequence.Sequence;
import com.sun.tools.javafx.api.JavafxcTool;
import java.io.File;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.tools.JavaFileManager;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.javafx.source.JavaFXSourceUtils;
import org.netbeans.modules.javafx.dataloader.JavaFXDataObject;
import org.netbeans.modules.javafx.editor.FXDocument;
import org.netbeans.modules.javafx.editor.JavaFXDocument;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

public class CodeManager {

    public static Object execute(FXDocument doc) throws BadLocationException {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ToolProvider.class.getClassLoader());
        
        String code = doc.getText(0, doc.getLength());
            
        FileObject fo = ((JavaFXDocument)doc).getDataObject().getPrimaryFile();
        
        ClassPath sourceCP = ClassPath.getClassPath(fo, ClassPath.SOURCE);
        ClassPath compileCP = ClassPath.getClassPath(fo, ClassPath.COMPILE);
        ClassPath executeCP = ClassPath.getClassPath(fo, ClassPath.EXECUTE);
        
        if (sourceCP == null) {
            throw new IllegalStateException("No classpath was found for folder: " + fo); // NOI18N
        }
        String className = sourceCP.getResourceName(fo, '.', false); // NOI18N

        Map<String, byte[]> classBytes = compile(className, code, sourceCP, compileCP);
        
        Object obj = null;
        if (classBytes != null) {
            className = checkCase(classBytes, className);
            try {
                obj = runFXFile(className, executeCP, classBytes);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }        
        Thread.currentThread().setContextClassLoader(orig);
        return obj;
    }

    public static Map<String, byte[]> compile(String className, String code, ClassPath sourceCP, ClassPath compileCP) {
        PrintWriter err = new PrintWriter(System.err);
    
        List<String> options = new ArrayList<String>();
        DiagnosticCollector diagnostics = new DiagnosticCollector(); // <JavaFileObject>();
        
        /*if (!code.contains("package")){
            String pack = className.substring(0, className.lastIndexOf('.'));
            code = "package " + pack + ";\n" + code;
        }*/
        
        List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>(1);
        
        compUnits.add(new FXFileObject(className, code));

        JavafxcTool tool = JavafxcTool.create();
        
        String cp = System.getProperty("env.class.path");
        System.setProperty("env.class.path", JavaFXSourceUtils.getAdditionalCP(cp));

        MemoryFileManager manager = new MemoryFileManager(tool.getStandardFileManager(diagnostics, null, null), ToolProvider.class.getClassLoader());
        
        options.add("-target");
        options.add("1.5");
        
        options.add("-classpath");
        options.add(JavaFXSourceUtils.getAdditionalCP(compileCP.toString()));
        
        options.add("-sourcepath");
        options.add(JavaFXSourceUtils.getAdditionalCP(sourceCP.toString()));
        
        options.add("-implicit:class");
        
        JavafxcTask task = tool.getTask(err, manager, diagnostics, options, compUnits);
        
        if (!task.call()) {
            for (Diagnostic diag : diagnostics.diagnostics)
            System.out.println("gets diagnostics: " + diag.getMessage(null));
            return null;
        }

        Map<String, byte[]> classBytes = manager.getClassBytes();
        
        return classBytes;
    }
        
    public static Object runFXFile(String name, ClassPath classPath, Map<String, byte[]> classBytes) throws Exception {
        MemoryClassLoader memoryClassLoader = new MemoryClassLoader(classPath);
        memoryClassLoader.loadMap(classBytes);
        return runFXFile(name, memoryClassLoader);
    }
    
    public static Object runFXFile(String name, ClassLoader classLoader) throws Exception {
        Class cls = classLoader.loadClass(name); 
        Method run = cls.getDeclaredMethod("javafx$run$", Sequence.class);
        String[] commandLineArgs = new String[]{};
        Object args = Sequences.make(String.class, commandLineArgs);
        Object obj = run.invoke(null, args);
        return obj;
    }
    
    private static String checkCase(Map<String, byte[]> classBytes, String name) {
        for (String key : classBytes.keySet()) {
            if (key.toLowerCase().contentEquals(name.toLowerCase())) {
                return key;
            }
        }
        return name;
    }
}

class DiagnosticCollector implements DiagnosticListener{

    List<Diagnostic> diagnostics = new LinkedList<Diagnostic>();
    
    public List<Diagnostic> getDiagnostics() {
	return diagnostics;
    }
    
    public void clear(){
        diagnostics.clear();
    }
    public void report(Diagnostic diagnostic) {
        diagnostics.add(diagnostic);                
    }
    
}

class MemoryClassLoader extends ClassLoader {

    Map<String, byte[]> classBytes;
    ClassPath classPath;

    public MemoryClassLoader(ClassPath classPath) {
        classBytes = new HashMap<String, byte[]>();
        this.classPath = classPath;
    }

    public void loadMap(Map<String, byte[]> classBytes) throws ClassNotFoundException {
        for (String key : classBytes.keySet()) {
            this.classBytes.put(key, classBytes.get(key));
        }
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
 
        if (classBytes.get(name) == null) {
            try {
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                try {
                    return Thread.currentThread().getContextClassLoader().loadClass(name);
                } catch  (ClassNotFoundException ex) {
                    return classPath.getClassLoader(false).loadClass(name);
                }
            }
        }

        Class result = findClass(name);

        if (resolve) {
            resolveClass(result);
        }

        return result;
    }

    @Override
    protected Class findClass(String className) throws ClassNotFoundException {
        byte[] buf = classBytes.get(className);
        if (buf != null) {
            classBytes.put(className, null);
            return defineClass(className, buf, 0, buf.length);
        } else {
            return super.findClass(className);
        }
    }
}

class FXFileObject extends SimpleJavaFileObject {

    CharSequence code;
    String className;

    public FXFileObject(String className, CharSequence code) {
        super(toURI(className), Kind.SOURCE);
        this.code = code;
        this.className = className;
    }

    public boolean isNameCompatible(String simpleName, Kind kind) {
        return true;
    }

    public URI toUri() {
        return toURI(className);
    }

    public String getName() {
        return getFileName(className);
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return code;
    }

    private static URI toURI(String className) {
        return URI.create("./" + getFilePath(className));
    }
    
    public static String getFileName(String className){
        return className.substring(className.lastIndexOf('.') + 1) + ".fx";
    }
    
    public static String getFilePath(String className){
        return className.replace('.','/') + ".fx";
    }

    private void print(String text) {
        System.out.println("[file object] " + text);
    }    
}