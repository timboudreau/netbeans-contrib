/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.loaders;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Locale;
import javax.swing.Icon;

import org.openide.ErrorManager;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
//import org.openide.loaders.ExecutionSupport;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/** A node to represent this object.
 *
 * @author Jan Lahoda
 */
public class MyDataNode extends DataNode {
    
    public static final String TEXT_SET = "Text";
    
    public MyDataNode(BiBTexDataObject obj) {
        this(obj, Children.LEAF);
    }
    
    protected MyDataNode(BiBTexDataObject obj, Children ch) {
        super(obj, ch);
        setIconBase("org/netbeans/modules/latex/loaders/MyDataIcon");
    }
    
    protected BiBTexDataObject getMyDataObject() {
        return (BiBTexDataObject)getDataObject();
    }
    
//     Example of adding Executor / Debugger / Arguments to node:
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        
        Sheet.Set textSet = sheet.get(TEXT_SET);
        
        if (textSet == null) {
            textSet = new Sheet.Set();
            textSet.setName(TEXT_SET);
            textSet.setDisplayName(NbBundle.getMessage(MyDataNode.class, "LBL_DataNode_Text"));
            textSet.setShortDescription(NbBundle.getMessage(MyDataNode.class, "HINT_DataNode_Text"));
        }
        
        sheet.put(textSet);
        
        addTextProperties(textSet);
        
//        Sheet.Set set = sheet.get(ExecutionSupport.PROP_EXECUTION);
//        if (set == null) {
//            set = new Sheet.Set();
//            set.setName(ExecutionSupport.PROP_EXECUTION);
//            set.setDisplayName(NbBundle.getMessage(MyDataNode.class, "LBL_DataNode_exec_sheet"));
//            set.setShortDescription(NbBundle.getMessage(MyDataNode.class, "HINT_DataNode_exec_sheet"));
//        }
//        ((ExecutionSupport)getCookie(ExecutionSupport.class)).addProperties(set);
        
        // Maybe:
//        ((CompilerSupport)getCookie(CompilerSupport.class)).addProperties(set);
//        sheet.put(set);
        return sheet;
    }

    private void addTextProperties(Sheet.Set textSet) {
        textSet.put(createTextEncodingProperty());
    }
    
    private PropertySupport createTextEncodingProperty() {
        return new PropertySupport.ReadWrite(
                  BiBTexDataObject.ENCODING_PROPERTY_NAME,
                  String.class,
                  "Encoding",
                  "Encoding of the document") {
             public Object getValue() {
                 return ((BiBTexDataObject) getDataObject()).getCharSet();
             }
             
             public void setValue(Object value) throws InvocationTargetException {
                 try {
                     ((BiBTexDataObject) getDataObject()).setCharSet((String) value);
                 } catch (IOException e) {
                     throw new InvocationTargetException(e);
                 }
             }
             
             public boolean supportsDefaultValue() {
                 return true;
             }
             
             public void restoreDefaultValue() throws InvocationTargetException {
                 setValue(null);
             }
             
             public boolean canWrite() {
                 return getDataObject().getPrimaryFile().canWrite();
             }
        };
    }

}
