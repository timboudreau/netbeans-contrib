package org.netbeans.modules.toolsintegration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

class Variables {

    private static List wdVariables;
    
    static List getWorkingDirectoryVariables () {
        if (wdVariables == null) {
            wdVariables = new ArrayList ();
            wdVariables.add (new DefaultVariable (
                "Current Directory",
                "The directory currently selected file in editor or explorer is saved in.",
                "${current_dir}"
            ));
            paramVariables.add (new DefaultVariable (
                "Source Root",
                "The the root directory containing all sources of current project.",
                "${source_root}"
            ));
            paramVariables.add (new DefaultVariable (
                "Class Root",
                "The the root directory containing all classes of current project.",
                "${class_root}"
            ));
            paramVariables.add (new DefaultVariable (
                "Argument",
                "Input Dialog will be opened when the process is started.",
                "${argument:Argument Name}"
            ));
        }
        return wdVariables;
    }
    
    private static List paramVariables;
    
    static List getParameterVariables () {
        if (paramVariables == null) {
            paramVariables = new ArrayList ();
            paramVariables.add (new DefaultVariable (
                "Current File",
                "The file currently selected in editor or explorer.",
                "${current_file}"
            ));
            paramVariables.add (new DefaultVariable (
                "Current Directory",
                "The directory currently selected file in editor or explorer is saved in.",
                "${current_dir}"
            ));
            paramVariables.add (new DefaultVariable (
                "Current Class Name",
                "The name of class currently selected in editor or explorer.",
                "${current_class_name}"
            ));
            paramVariables.add (new DefaultVariable (
                "Source Root",
                "The the root directory containing all sources of current project.",
                "${source_root}"
            ));
            paramVariables.add (new DefaultVariable (
                "Class Root",
                "The the root directory containing all classes of current project.",
                "${class_root}"
            ));
            paramVariables.add (new DefaultVariable (
                "Class File",
                "Represents class file for source file selected in editor or explorer.",
                "${class_file}"
            ));
            paramVariables.add (new DefaultVariable (
                "Compile Time Class Path",
                "Class path for file currently selected in editor or explorer.",
                "${compile_path}"
            ));
            paramVariables.add (new DefaultVariable (
                "Source Path",
                "Source path for file currently selected in editor or explorer.",
                "${source_path}"
            ));
            paramVariables.add (new DefaultVariable (
                "Execution Time Class Path",
                "Execution path for file currently selected in editor or explorer.",
                "${execution_path}"
            ));
            paramVariables.add (new DefaultVariable (
                "Argument",
                "Input Dialog will be opened when the process is started.",
                "${argument:Argument Name}"
            ));
        }
        return paramVariables;
    }
    
    static String resolveVariables (String text) {
        Iterator it = getParameterVariables ().iterator ();
        while (it.hasNext ()) {
            Variable v = (Variable) it.next ();
            String symbol = v.getSymbolicName ();
            if (symbol.startsWith ("${argument:")) {
                int index = text.indexOf ("${argument:");
                if (index < 0) continue;
                int end = text.indexOf ('}', index);
                symbol = text.substring (index, end - index + 1);
                String value = evaluate (symbol);
                if (value != null)
                    text = replaceAll (text, symbol, value);
            } else
            if (text.indexOf (symbol) >= 0) {
                String value = evaluate (symbol);
                if (value != null)
                    text = replaceAll (text, symbol, value);
            }
        }
        return text;
    }
    
    
    // private helper methods ..................................................
    
    private static String replaceAll (
        String where,
        String what,
        String by
    ) {
        int i;
        while ((i = where.indexOf (what)) >= 0) {
            where = where.substring (0, i) +
                by +
                where.substring (i + what.length ());
        }
        return where;
    }
    
    private static String evaluate (String symbolicName) {
        if (symbolicName.startsWith ("${argument")) {
            String paramName = "Parameter";
            if (symbolicName.startsWith ("${argument:"))
                paramName = symbolicName.substring 
                    ("${argument:".length (), symbolicName.length () - 1);
            InputLine inputLine = new InputLine (paramName + ":", "Set Parameter");
            if (DialogDisplayer.getDefault ().notify (inputLine) == 
                inputLine.OK_OPTION
            )
                return (String) inputLine.getInputText ();
            return null;
        }
        if ("${current_file}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            File f = FileUtil.toFile (fo);
            if (f == null) return null;
            return f.getAbsolutePath ();
        }
        if ("${current_dir}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            fo = fo.getParent ();
            if (fo == null) return null;
            File f = FileUtil.toFile (fo);
            if (f == null) return null;
            return f.getAbsolutePath ();
        }
        if ("${current_class_name}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            ClassPath classPath = ClassPath.getClassPath (fo, ClassPath.SOURCE);
            return classPath.getResourceName (fo, '.', false);
        }
        if ("${source_root}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            ClassPath classPath = ClassPath.getClassPath (fo, ClassPath.SOURCE);
            fo = classPath.findOwnerRoot (fo);
            if (fo == null) return null;
            File f = FileUtil.toFile (fo);
            if (f == null) return null;
            return f.getAbsolutePath ();
        }
        if ("${class_root}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            ClassPath classPath = ClassPath.getClassPath (fo, ClassPath.SOURCE);
            String resourceName = classPath.getResourceName (fo, '/', false);
            System.out.println("resourceName " + resourceName);
            classPath = ClassPath.getClassPath (fo, ClassPath.EXECUTE);
            fo = classPath.findResource (resourceName + ".class");
            if (fo == null) return null;
            fo = classPath.findOwnerRoot (fo);
            if (fo == null) return null;
            File f = FileUtil.toFile (fo);
            if (f == null) return null;
            return f.getAbsolutePath ();
        }
        if ("${class_file}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            ClassPath classPath = ClassPath.getClassPath (fo, ClassPath.SOURCE);
            String resourceName = classPath.getResourceName (fo, '/', false);
            System.out.println("resourceName " + resourceName);
            classPath = ClassPath.getClassPath (fo, ClassPath.EXECUTE);
            fo = classPath.findResource (resourceName + ".class");
            if (fo == null) return null;
            File f = FileUtil.toFile (fo);
            if (f == null) return null;
            return f.getAbsolutePath ();
        }
        if ("${source_path}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            ClassPath classPath = ClassPath.getClassPath (fo, ClassPath.SOURCE);
            return pathToString (classPath);
        }
        if ("${compile_path}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            ClassPath classPath = ClassPath.getClassPath (fo, ClassPath.COMPILE);
            return pathToString (classPath);
        }
        if ("${execution_path}".equals (symbolicName)) {
            Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
            if (nodes.length != 1) return null;
            DataObject dob = (DataObject) nodes [0].getLookup ().
                lookup (DataObject.class);
            if (dob == null) return null;
            FileObject fo = dob.getPrimaryFile ();
            if (fo == null) return null;
            ClassPath classPath = ClassPath.getClassPath (fo, ClassPath.EXECUTE);
            return pathToString (classPath);
        }
        return null;
    }
    
    private static String pathToString (ClassPath classPath) {
        StringBuffer sb = new StringBuffer ();
        Iterator it = classPath.entries ().iterator ();
        while (it.hasNext ()) {
            ClassPath.Entry entry = (ClassPath.Entry) it.next ();
            FileObject fo = entry.getRoot ();
            if (fo == null) continue;
            if (fo.isRoot ())
                fo = FileUtil.getArchiveFile (fo);
            File f = FileUtil.toFile (fo);
            if (f == null) 
                continue;
            if (sb.length () > 0) sb.append (File.pathSeparatorChar);
            sb.append (f.getAbsolutePath ());
        }
        return sb.toString ();
    }
    
    
    // innerclasses ............................................................
    
    static abstract class Variable {
        abstract String getVariableName ();
        abstract String getVariableDescription ();
        abstract String getSymbolicName ();
    }
    
    static class DefaultVariable extends Variable {
        private String name;
        private String description;
        private String symbolicName;
        
        DefaultVariable (
            String name,
            String description,
            String symbolicName
        ) {
            this.name = name;
            this.description = description;
            this.symbolicName = symbolicName;
        }
        
        String getVariableName () {
            return name;
        }
        
        String getVariableDescription () {
            return description;
        }
        
        String getSymbolicName () {
            return symbolicName;
        }
    }
}

