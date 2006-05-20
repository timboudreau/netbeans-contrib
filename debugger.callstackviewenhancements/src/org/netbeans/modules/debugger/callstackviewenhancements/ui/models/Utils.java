/*
 * Utils.java
 *
 */

package org.netbeans.modules.debugger.callstackviewenhancements.ui.models;

import com.sun.jdi.StackFrame;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.modules.editor.java.JMIUtils;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.spi.debugger.jpda.EditorContext;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class Utils {
    private static final String[] primitivesArray = new String[] {
        "boolean", // NOI18N
        "byte",    // NOI18N
        "char",    // NOI18N
        "double",  // NOI18N
        "float",   // NOI18N
        "int",     // NOI18N
        "long",    // NOI18N
        "short",   // NOI18N
    };
    
    static final List primitivesList = Arrays.asList(primitivesArray);
    
    static void gotoTypeOf(CallStackFrame callStackFrame) {
        if (callStackFrame == null) {
            return;
        }
        This thisOfCallStackFrame = callStackFrame.getThisVariable();
        if (thisOfCallStackFrame != null) {
            if (!callStackFrame.getClassName().equals(thisOfCallStackFrame.getType())) {
                showType(thisOfCallStackFrame.getType());
            }
        }
    }
    
    static void showType(String typeName) {
        if (typeName == null) {
            return;
        }
        
        typeName = stripArray(typeName);
        
        if (primitivesList.contains(typeName)) {
            return;
        }
        
        JavaClass clazz = (JavaClass) JavaModel.getDefaultExtent().getType().resolve(typeName.replace('$', '.'));
        if (clazz == null) {
            typeName = stripInner(typeName);
            String url = getLocation(typeName);
            if (url != null) {
                EditorContext editorContext = (EditorContext) DebuggerManager.getDebuggerManager().lookupFirst(null, EditorContext.class);
                if (editorContext != null) {
                    editorContext.showSource(url, 1, null);
                }
            }
        } else {
            openElement(clazz);
        }
    }
    
    static void openElement(ClassDefinition element) {
        try {
            try {
                JavaModel.getJavaRepository().beginTrans(false);
                ClassDefinition classDefinition = JMIUtils.getSourceElementIfExists((ClassDefinition) element);
                if (classDefinition != null) {
                    element = classDefinition;
                }
                JMIUtils.openElement(element);
            } finally {
                JavaModel.getJavaRepository().endTrans();
            }
        } catch (javax.jmi.reflect.InvalidObjectException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    static String getLocation(CallStackFrame callStackFrame) {
        if (callStackFrame == null) {
            return null;
        }
        return getLocation(callStackFrame.getClassName());
    }
    
    static String getLocation(String typeName) {
        if (typeName == null) {
            return null;
        }
        
        typeName = stripArray(typeName);
        
        if (primitivesList.contains(typeName)) {
            return null;
        }
        
        typeName = stripInner(typeName);
        try {
            JavaModel.getJavaRepository().beginTrans(false);
            ClassDefinition clazz = (JavaClass) JavaModel.getDefaultExtent().getType().resolve(typeName.replace('$', '.'));
            if (clazz == null) {
                Session session = DebuggerManager.getDebuggerManager().getCurrentSession();
                if (session != null) {
                    DebuggerEngine debuggerEngine = session.getCurrentEngine();
                    if (debuggerEngine != null) {
                        SourcePathProvider sourcePathProvider =
                                (SourcePathProvider) debuggerEngine.lookupFirst(null, SourcePathProvider.class);
                        if (sourcePathProvider != null) {
                            return sourcePathProvider.getURL(typeName.replace('.', '/') + ".java", true);
                        }
                    }
                }
            } else {
                ClassDefinition classDefinition = JMIUtils.getSourceElementIfExists((ClassDefinition) clazz);
                if (classDefinition != null) {
                    clazz = classDefinition;
                    FileObject fileObject = JavaModel.getFileObject(clazz.getResource());
                    if (fileObject == null) {
                        return null;
                    }
                    File file = FileUtil.toFile(fileObject);
                    if (file == null) {
                        try {
                            return fileObject.getURL().toString();
                        } catch (FileStateInvalidException fse) {
                            return fileObject.getPath();
                        }
                    } else {
                        return file.getAbsolutePath();
                    }
                } else {
                    
                }
            }
        } finally {
            JavaModel.getJavaRepository().endTrans();
        }
        return null;
    }
    
    static String stripInner(String typeName) {
        if (typeName == null) {
            return null;
        }
        
        typeName = stripArray(typeName);
        
        int dollarAt = typeName.indexOf("$");
        if (dollarAt != -1) {
            // strip inner classes
            typeName = typeName.substring(0, dollarAt);
        }
        
        return typeName;
    }
    
    static String stripArray(String typeName) {
        if (typeName == null) {
            return null;
        }
        
        // strip array
        while (typeName.endsWith("[]")) {
            typeName = typeName.substring(0, typeName.length() - 2);
        }
        
        return typeName;
    }
    
    static com.sun.jdi.Method getMethod(CallStackFrame callStackFrame) {
        Class callStackFrameClass = callStackFrame.getClass();
        try {
            Method method = callStackFrameClass.getMethod("getStackFrame", new Class[0]);
            try {
                Object stackFrameObject = method.invoke(callStackFrame, new Object[0]);
                if (stackFrameObject instanceof StackFrame) {
                    StackFrame stackFrame = (StackFrame) stackFrameObject;
                    com.sun.jdi.Method jdiMethod = stackFrame.location().method();
                    return jdiMethod;
                }
            } catch (IllegalArgumentException ex) {
            } catch (InvocationTargetException ex) {
            } catch (IllegalAccessException ex) {
            }
        } catch (SecurityException ex) {
        } catch (NoSuchMethodException ex) {
        }

        return null;
    }
}
