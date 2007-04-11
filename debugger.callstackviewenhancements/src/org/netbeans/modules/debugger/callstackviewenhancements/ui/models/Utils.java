/*
 * Utils.java
 *
 */

package org.netbeans.modules.debugger.callstackviewenhancements.ui.models;

import com.sun.jdi.StackFrame;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.List;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerInfo;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.UiUtils;
import org.netbeans.spi.debugger.jpda.EditorContext;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

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
        if (callStackFrame == null || callStackFrame.isObsolete()) {
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
        
        final String finalTypeName = typeName.replace('$', '.');
        
        typeName = stripInner(typeName);
        
        typeName = typeName.replace('.', '/') + ".java";
        
        DebuggerManager debuggerManager = DebuggerManager.getDebuggerManager();
        DebuggerEngine debuggerEngine = debuggerManager.getCurrentEngine();
        List sourcePathProviders = debuggerEngine.lookup(null, SourcePathProvider.class);
        
        String url = null;
        int count = sourcePathProviders.size();
        for (int i = 0; i < count; i++) {
            SourcePathProvider sourcePathProvider = (SourcePathProvider) sourcePathProviders.get(i);
            url = sourcePathProvider.getURL(typeName, false);
            if (url == null) {
                url = sourcePathProvider.getURL(typeName, true);
            }
            if (url != null) {
                break;
            }
        }
        
        if (url != null) {
            try         {
                FileObject fileObject = URLMapper.findFileObject(new java.net.URL(url));
                if (fileObject != null) {
                    JavaSource javaSource = JavaSource.forFileObject(fileObject);
                    if (javaSource != null) {
                         try {
                            javaSource.runUserActionTask(new CancellableTask<CompilationController>() {
                                public void cancel() {
                                }

                                public void run(CompilationController compilationController)
                                    throws Exception {
                                    compilationController.toPhase(Phase.RESOLVED);
                                    TypeElement typeElement = compilationController.getElements().getTypeElement(finalTypeName);
                                    if (typeElement != null) {
                                        UiUtils.open(compilationController.getClasspathInfo(), typeElement);
                                    }
                                }
                            }, true);                                                    
                        } catch (IOException ex) {
                        }
                        return;
                    }
                }
            }
            catch (MalformedURLException ex) {
                
            }
            EditorContext editorContext = (EditorContext) debuggerManager.lookupFirst(null, EditorContext.class);

            if (editorContext != null) {
                editorContext.showSource(url, 1, null);
            }
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
        
        typeName = typeName.replace('.', '/') + ".java";

        Session session = DebuggerManager.getDebuggerManager().getCurrentSession();
        if (session != null) {
            DebuggerEngine debuggerEngine = session.getCurrentEngine();
            if (debuggerEngine != null) {
                SourcePathProvider sourcePathProvider =
                        (SourcePathProvider) debuggerEngine.lookupFirst(null, SourcePathProvider.class);
                if (sourcePathProvider != null) {
                    String url = sourcePathProvider.getURL(typeName, false);
                    if (url == null) {
                        url = sourcePathProvider.getURL(typeName, true);
                    }
                    return url;
                }
            }
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
