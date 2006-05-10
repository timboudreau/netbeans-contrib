/*
 * Model.java
 *
 * Created on 12. prosinec 2005, 0:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.toolsintegration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.netbeans.modules.toolsintegration.XMLStorage.Attribs;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author Administrator
 */
public class Model {
    
    private static Model model;
    
    public static Model getDefault () {
        if (model == null)
            model = new Model ();
        return model;
    }
    
    private List tools;
    List getTools () {
        if (tools == null) {
            tools = load ();
            if (tools == null || tools.size () == 0) {
                tools = new ArrayList ();
                tools.add (ExternalTool.NEW);
            }
        }
        return Collections.unmodifiableList (tools);
    }
    
    void setTools (List tools) {
        this.tools = tools;
        save (tools);
        this.tools = tools;
    }
    
    private static List load () {
        try {
            FileObject toolsFO = FileUtil.createFolder (
                Repository.getDefault ().getDefaultFileSystem ().getRoot (),
                "ExternalTools"
            );
            List result = new ArrayList ();
            Enumeration en = toolsFO.getChildren (false);
            while (en.hasMoreElements ()) {
                FileObject fo = (FileObject) en.nextElement ();
                if (!"et".equals (fo.getExt ())) continue;
                ExternalTool tool = read (fo);
                if (tool != null)
                    result.add (tool);
            }
            return result;
        } catch (IOException ex) {
            ErrorManager.getDefault ().notify (ex);
        }
        return null;
    }
    
    private static void save (List tools) {
        
        // 1) get root folder and delete old data
        try {
            FileObject toolsFO = FileUtil.createFolder (
                Repository.getDefault ().getDefaultFileSystem ().getRoot (),
                "ExternalTools"
            );
            Enumeration en = toolsFO.getChildren (false);
            while (en.hasMoreElements ()) {
                FileObject file = (FileObject) en.nextElement ();
                file.delete ();
            }
            FileObject menuFO = FileUtil.createFolder (
                Repository.getDefault ().getDefaultFileSystem ().getRoot (),
                "Menu/Tools/ExternalTools"
            );
            en = menuFO.getChildren (false);
            while (en.hasMoreElements ()) {
                FileObject file = (FileObject) en.nextElement ();
                file.delete ();
            }
            DataFolder menuDF = DataFolder.findFolder (menuFO);

            // 2) create new files    
            Iterator it = tools.iterator ();
            while (it.hasNext ()) {
                ExternalTool tool = (ExternalTool) it.next ();
                FileObject fo = toolsFO.createData (tool.getName (), "et");
                
                final StringBuffer sb = XMLStorage.generateHeader ();
                
                Attribs attributes = new Attribs (true);
                attributes.add ("name", tool.getName ());
                attributes.add ("default", "run");
                XMLStorage.generateFolderStart (sb, "project", attributes, "");
                
                attributes = new Attribs (true);
                attributes.add ("name", "run");
                XMLStorage.generateFolderStart (sb, "target", attributes, "    ");
                
                attributes = new Attribs (true);
                attributes.add ("executable", tool.getFileName ());
                if (tool.getWorkingDirectory () != null)
                    attributes.add ("dir", tool.getWorkingDirectory ());
                if (tool.isInheritIDEEnvironment ())
                    attributes.add ("inheritIDEEnvironment", "true");
                if (!tool.isShowOutput ())
                    attributes.add ("showOutput", "false");
                if (!tool.isShowError ())
                    attributes.add ("showError", "false");
                if (tool.isShowInput ())
                    attributes.add ("showInput", "true");
                if (tool.isNewTabAlways ())
                    attributes.add ("newTabAlways", "true");
                if (tool.isAppend ())
                    attributes.add ("append", "true");
                if (!tool.isHighlightOutput ())
                    attributes.add ("highlightOutput", "false");
                if (tool.getHighlightExpression () != null)
                    attributes.add ("highlightExpression", tool.getHighlightExpression ());
                if (tool.getAnnotateAs () != ExternalTool.NO_ANNOTATION)
                    attributes.add ("annotateAs", Integer.toString (tool.getAnnotateAs ()));
                XMLStorage.generateFolderStart (sb, "exec", attributes, "        ");
                
                // save properties
                Properties properties = new Properties ();
                properties.load (new ByteArrayInputStream (
                    tool.getVariablesAsText ().getBytes ())
                );
                en = properties.propertyNames ();
                while (en.hasMoreElements ()) {
                    String propertyName = (String) en.nextElement ();
                    attributes = new Attribs (true);
                    attributes.add ("key", propertyName);
                    attributes.add ("value", properties.getProperty (propertyName));
                    XMLStorage.generateLeaf (sb, "env", attributes, "            ");
                }
                
                // save parameters
                int i = 0;
                attributes = new Attribs (true);
                Iterator it2 = tool.getParameters ().iterator ();
                while (it2.hasNext ()) {
                    String param = (String) it2.next ();
                    attributes.add ("value", param);
                    XMLStorage.generateLeaf (sb, "arg", attributes, "            ");
                };
                
                XMLStorage.generateFolderEnd (sb, "exec", "        ");
                XMLStorage.generateFolderEnd (sb, "target", "    ");
                XMLStorage.generateFolderEnd (sb, "project", "");
                XMLStorage.save (fo, new String (sb));
                
                DataObject dob = DataObject.find (fo);
                DataShadow.create (menuDF, dob);
            }
        } catch (IOException ex) {
            ErrorManager.getDefault ().notify (ex);
        }
    }
    
    static ExternalTool read (FileObject fo) {
        return (ExternalTool) XMLStorage.load (fo, new ToolReader ());
    }
    
    
    // innerclasses ............................................................
    
    private static class ToolReader extends XMLStorage.Handler {
        
        private String  name;
        private String  fileName;
        private String  workingDirectory;
        private List    parameters = new ArrayList ();
        private List    variables = new ArrayList ();
        
        private boolean inheritIDEEnvironment;
        private boolean showOutput;
        private boolean showError;
        private boolean showInput;
        private boolean newTabAlways;
        private boolean append;
        private boolean highlightOutput;
        private String  highlightExpression;
        private int     annotateAs = ExternalTool.NO_ANNOTATION;
        
        private String  currentElement = null;
        
        
        Object getResult () {
            if (name == null) return null;
            if (fileName == null) return null;
            return new ExternalTool (
                name,
                fileName,
                workingDirectory,
                parameters,
                variables,
                inheritIDEEnvironment,
                showOutput,
                showError,
                showInput,
                newTabAlways,
                append,
                highlightOutput,
                highlightExpression,
                annotateAs
            );
        }
        
        public void startElement (
            String uri, 
            String localName,
            String name, 
            Attributes attributes
        ) {
            try {
                currentElement = null;
                if (name.equals ("project")) {
                    this.name = attributes.getValue ("name");
                    return;
                }
                if (name.equals ("target"))
                    return;
                if (name.equals ("exec")) {
                    fileName = attributes.getValue ("executable");
                    workingDirectory = attributes.getValue ("dir");
                    inheritIDEEnvironment = toBoolean (attributes, "inheritIDEEnvironment", false);
                    showOutput = toBoolean (attributes, "showOutput", true);
                    showError = toBoolean (attributes, "showError", true);
                    showInput = toBoolean (attributes, "showInput", false);
                    newTabAlways = toBoolean (attributes, "newTabAlways", false);
                    append = toBoolean (attributes, "append", false);
                    highlightOutput = toBoolean (attributes, "highlightOutput", true);
                    highlightExpression = attributes.getValue ("highlightExpression");
                    if (attributes.getValue ("annotateAs") != null)
                        annotateAs = Integer.parseInt (attributes.getValue ("annotateAs"));
                    return;
                }
                if (name.equals ("env")) {
                    variables.add (
                        attributes.getValue ("key") + "=" + 
                        attributes.getValue ("value")
                    );
                    return;
                }
                if (name.equals ("arg")) {
                    parameters.add (attributes.getValue ("value"));
                    return;
                }
            } catch (Exception ex) {
                ErrorManager.getDefault ().notify (ex);
            }
        }
        
        public InputSource resolveEntity (String pubid, String sysid) {
            return new InputSource (
		new java.io.ByteArrayInputStream (new byte [0])
	    );
        }
    }
    
    private static boolean toBoolean (
        Attributes attributes, 
        String paramName, 
        boolean def
    ) {
        String param = attributes.getValue (paramName);
        if (param == null) return def;
        return "true".equals (param);
    }
}

