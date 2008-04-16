/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.javafx.preview;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.Set;
import java.util.logging.Logger;
import javax.tools.Diagnostic;

import javax.tools.JavaFileObject.Kind;

import java.lang.reflect.Method;

import com.sun.javafx.api.JavafxcTask;
import com.sun.javafx.api.ToolProvider;
import com.sun.javafx.runtime.location.ObjectVariable;
import java.util.List;
import javax.tools.JavaFileObject;
import com.sun.javafx.runtime.sequence.Sequences;
import com.sun.javafx.runtime.sequence.Sequence;
import com.sun.scenario.scenegraph.JSGPanel;
import com.sun.tools.javac.util.JavacFileManager;
import com.sun.tools.javafx.api.JavafxcTool;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.text.Document;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.javafx.dataloader.JavaFXDataObject;
import org.netbeans.modules.javafx.editor.FXDocument;
import org.netbeans.modules.javafx.editor.JavaFXDocument;
import org.netbeans.modules.javafx.project.JavaFXProject;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import com.sun.scenario.scenegraph.SGNode;
import java.awt.Color;
import javax.swing.JDesktopPane;
import javax.tools.Diagnostic;

public class CodeManager {
    private static final String[] getComponentStrings = {"getComponent", "getJComponent"};
    private static final String[] frameStrings = {"frame", "window"};
    private static final String[] getVisualNodeStrings = {"getVisualNode", "getSGNode"};
    private static final DiagnosticCollector diagnostics = new DiagnosticCollector();
    
    public static Object execute(FXDocument doc) {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ToolProvider.class.getClassLoader());
        
        String code = null;
        try {
            code = doc.getText(0, doc.getLength());
        } catch (Exception ex) {
            return null;
        }
            
        FileObject fo = ((JavaFXDocument)doc).getDataObject().getPrimaryFile();
        
        ClassPath sourceCP = ClassPath.getClassPath(fo, ClassPath.SOURCE);
        ClassPath compileCP = ClassPath.getClassPath(fo, ClassPath.COMPILE);
        ClassPath executeCP = ClassPath.getClassPath(fo, ClassPath.EXECUTE);
        ClassPath bootCP = ClassPath.getClassPath(fo, ClassPath.BOOT);
        
        if (sourceCP == null) {
            Logger.getLogger(CodeManager.class.getName()).warning("No classpath was found for folder: " + fo); // NOI18N
            return null;
        }
        String className = sourceCP.getResourceName(fo, '.', false); // NOI18N
        
        diagnostics.clear();
        
        JavafxcTool tool = JavafxcTool.create();
        JavacFileManager standardManager = tool.getStandardFileManager(diagnostics, null, null);
        JavaFXProject project = (JavaFXProject) JavaFXModel.getProject(doc);
        List <JavaFileObject> javaFileObjects = getProjectJFOList(doc, project, sourceCP, standardManager);
        
        javaFileObjects.add(new MemoryFileObject(className, code, Kind.SOURCE));
        
        Map<String, byte[]> oldClassBytes = cut(JavaFXModel.getClassBytes(project), className);
        
        Map<String, byte[]> classBytes = compile(javaFileObjects, oldClassBytes, sourceCP, bootCP, compileCP, project, tool, standardManager, diagnostics);  
        JavaFXModel.putClassBytes(project, classBytes);
        
        Object obj = null;
        if (classBytes != null) {
            className = checkCase(classBytes, className);
            try {
                obj = run(className, executeCP, classBytes);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }        
        Thread.currentThread().setContextClassLoader(orig);
        return obj;
    }
    
    static public List<Diagnostic> getDiagnostics() {
	return diagnostics.diagnostics;
    }            
            
    private static Map<String, byte[]> compile( 
                                                List<JavaFileObject> javaFileObjects,
                                                Map<String, byte[]> classBytes,
                                                ClassPath sourceCP,
                                                ClassPath compileCP,
                                                ClassPath bootCP,
                                                JavaFXProject project,
                                                JavafxcTool tool,
                                                JavacFileManager standardManager,
                                                DiagnosticCollector diagnostics) {
        List<String> options = new ArrayList<String>();
        PrintWriter err = new PrintWriter(System.err);
        
        /*if (!code.contains("package")){
            String pack = className.substring(0, className.lastIndexOf('.'));
            code = "package " + pack + ";\n" + code;
        }
        String cp = System.getProperty("env.class.path");
        System.setProperty("env.class.path", JavaFXSourceUtils.getAdditionalCP(cp));
        */
        
        MemoryClassLoader mcl = new MemoryClassLoader(compileCP);
        
        MemoryFileManager manager = new MemoryFileManager(standardManager, mcl);
        manager.setClassBytes(classBytes);
        
        try {
            mcl.loadMap(JavaFXModel.getClassBytes(project));
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        options.add("-target");
        options.add("1.5");
        
        options.add("-classpath");
        options.add(compileCP.toString());
        
        options.add("-sourcepath");
        options.add(sourceCP.toString());
        
        options.add("-bootclasspath");
        options.add(bootCP.toString());
        
        options.add("-implicit:class");
        
        JavafxcTask task = tool.getTask(err, manager, diagnostics, options, javaFileObjects);
        
        if (!task.call()) {
            for (Diagnostic diag : diagnostics.diagnostics)
            System.out.println("gets diagnostics: " + diag.getMessage(null));
            return null;
        }

        Map<String, byte[]> classBytesDone = manager.getClassBytes();
        classBytesDone.putAll(classBytes);
        
        return classBytesDone;
    }
        
    private static Object run(String name, ClassPath classPath, Map<String, byte[]> classBytes) throws Exception {
        MemoryClassLoader memoryClassLoader = new MemoryClassLoader(classPath);
        memoryClassLoader.loadMap(classBytes);
        return run(name, memoryClassLoader);
    }
    
    private static Object run(String name, ClassLoader classLoader) throws Exception {
        Class cls = classLoader.loadClass(name); 
        Method run = cls.getDeclaredMethod("javafx$run$", Sequence.class);
        String[] commandLineArgs = new String[]{};
        Object args = Sequences.make(String.class, commandLineArgs);
        Object obj = run.invoke(null, args);
        return obj;
    }
    
    private static JComponent parseJComponentObj(Object obj) {
        JComponent comp = null;
        try {
            Method getComponent = null;
            for (String getComponentStr : getComponentStrings) {
                try {
                    getComponent = obj.getClass().getDeclaredMethod(getComponentStr);
                } catch (Exception ex) {
                }
                if (getComponent != null) break;
            }
            if (getComponent != null)
                comp = (JComponent)getComponent.invoke(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return comp;
    }
    
    private static JComponent parseJFrameObj(Object obj) {
        JComponent comp = null;
        try {
            Field field = null;
            for (String frameStr : frameStrings) {
                try {
                    field = obj.getClass().getDeclaredField(frameStr);
                } catch (Exception ex) {
                }
                if (field != null) break;
            }
            if (field != null) {
                ObjectVariable frameObj = (ObjectVariable)field.get(obj);
                if (frameObj != null) {
                    JFrame frame = (JFrame)frameObj.get();
                    if (frame != null) {
                        frame.setVisible(false);
                        JInternalFrame intFrame = new JInternalFrame();
                        intFrame.setSize(frame.getSize());
                        intFrame.setContentPane(frame.getContentPane());
                        intFrame.setTitle(frame.getTitle());
                        intFrame.setJMenuBar(frame.getJMenuBar());
                        intFrame.setBackground(frame.getBackground());
                        intFrame.getContentPane().setBackground(frame.getBackground());
                        intFrame.setForeground(frame.getForeground());
                        intFrame.setResizable(true);
                        intFrame.setClosable(true);
                        intFrame.setMaximizable(true);
                        intFrame.setIconifiable(true);
                        intFrame.setVisible(true);
                        frame.dispose();
                        JDesktopPane jdp = new JDesktopPane();
                        jdp.setBackground(Color.WHITE);
                        jdp.add(intFrame);
                        comp = jdp;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return comp;
    }

    private static JComponent parseSGNodeObj(Object obj) {
        JComponent comp = null;
        try {
            Method getVisualNode = null;
            for (String getVisualNodeStr : getVisualNodeStrings) {
                try {
                    getVisualNode = obj.getClass().getDeclaredMethod(getVisualNodeStr);
                } catch (Exception ex) {
                }
                if (getVisualNode != null) break;
            }
            if (getVisualNode != null) {
                SGNode sgNode = (SGNode)getVisualNode.invoke(obj);
                if (sgNode != null) {
                    JSGPanel panel = new JSGPanel();
                    panel.setScene(sgNode);
                    comp = panel;
                }
            }  
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return comp;
    }
        
    public static JComponent parseObj(Object obj) {
        JComponent comp = null;
        if ((comp = parseJComponentObj(obj)) == null)
            if ((comp = parseJFrameObj(obj)) == null)
                comp = parseSGNodeObj(obj);
        return comp;
    }
    
    private static List<JavaFileObject> getProjectJFOList(  FXDocument document,
                                                            JavaFXProject project,
                                                            ClassPath sourceCP,
                                                            JavacFileManager fileManager)
    {
        List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>(1);
        FileObject fo = ((JavaFXDocument)document).getDataObject().getPrimaryFile();
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sourceGrupps = sources.getSourceGroups(Sources.TYPE_GENERIC);
        for (SourceGroup srcGrupp : sourceGrupps) {
            FileObject rootFileObject = srcGrupp.getRootFolder();
            Enumeration <FileObject> fileObjectEnum = (Enumeration<FileObject>) rootFileObject.getChildren(true);
            while (fileObjectEnum.hasMoreElements()) {
                FileObject fileObject = fileObjectEnum.nextElement();
                if (!fo.equals(fileObject)) {
                    try {
                        DataObject dataObject = DataObject.find(fileObject);
                        if (dataObject instanceof JavaFXDataObject) {
                            String name = sourceCP.getResourceName(fileObject, '.', false); // NOI18N
                            if (!contains(JavaFXModel.getClassBytes(project), name)) {
                                Document doc = null;
                                try {
                                    EditorCookie editorCookie = dataObject.getCookie(EditorCookie.class);
                                    doc = editorCookie.getDocument();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                if (doc != null) {
                                    String docsCode = null;
                                    try {
                                        docsCode = doc.getText(0, doc.getLength());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    String docClassName = sourceCP.getResourceName(NbEditorUtilities.getFileObject(doc), '.', false); // NOI18N
                                    compUnits.add(new MemoryFileObject(docClassName, docsCode, Kind.SOURCE));
                                } else {
                                    Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjects(FileUtil.toFile(fileObject));
                                    for (JavaFileObject javaFileObject : javaFileObjects) {
                                        compUnits.add(javaFileObject);
                                    }
                                }
                            }
                            
                        }
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        return compUnits;
    }
    
    private static String checkCase(Map<String, byte[]> classBytes, String name) {
        for (String key : classBytes.keySet()) {
            if (key.toLowerCase().contentEquals(name.toLowerCase())) {
                return key;
            }
        }
        return name;
    }
    
    private static boolean contains(Map<String, byte[]> classBytes, String name) {
        for (String key : classBytes.keySet()) {
            if (key.toLowerCase().contentEquals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private static Map<String, byte[]> cut(Map<String, byte[]> classBytes, String className) {
        Map<String, byte[]> newMap = new HashMap<String, byte[]>();
        Set <Entry<String, byte[]>> set = classBytes.entrySet();
        Iterator <Entry <String, byte[]>> it = set.iterator();
        while (it.hasNext()) {
            Entry <String, byte[]> entry = it.next();
            String name = entry.getKey();
            if (!name.toLowerCase().startsWith(className.toLowerCase())) {
                newMap.put(name, entry.getValue());
            }
        }
        return newMap; 
    }
    
    public static void cut(FXDocument doc) {
        JavaFXProject project = (JavaFXProject) JavaFXModel.getProject(doc);      
        FileObject fo = ((JavaFXDocument)doc).getDataObject().getPrimaryFile();
        ClassPath sourceCP = ClassPath.getClassPath(fo, ClassPath.SOURCE);
        String className = sourceCP.getResourceName(fo, '.', false); // NOI18N
        Map<String, byte[]> newMap = JavaFXModel.getClassBytes(project);
        JavaFXModel.putClassBytes(project, cut(newMap, className));
    }
}

