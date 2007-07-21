/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.loaders;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;


/** A node to represent this object.
 *
 * @author Jan Lahoda
 */
public class MyDataNode extends DataNode {
    
    public static final String TEXT_SET = "Text";
    
    private LaTeXSourceFactory.MainFileListener factoryListener;
    
    public MyDataNode(TexDataObject obj) {
        this(obj, Children.LEAF);
    }
    
    protected MyDataNode(TexDataObject obj, Children ch) {
        super(obj, ch);
        setIconBase("org/netbeans/modules/latex/loaders/MyDataIcon");
    }
    
    protected TexDataObject getMyDataObject() {
        return (TexDataObject)getDataObject();
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
        
        return sheet;
    }

    private void addTextProperties(Sheet.Set textSet) {
        textSet.put(createTextEncodingProperty());
        textSet.put(createLocaleProperty());
    }
    
    private PropertySupport createTextEncodingProperty() {
        return new PropertySupport.ReadWrite(
                  TexDataObject.ENCODING_PROPERTY_NAME,
                  String.class,
                  "Encoding",
                  "Encoding of the document") {
             public Object getValue() {
                 return ((TexDataObject) getDataObject()).getCharSet();
             }
             
             public void setValue(Object value) throws InvocationTargetException {
                 try {
                     ((TexDataObject) getDataObject()).setCharSet((String) value);
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

    private PropertySupport createLocaleProperty() {
        return new PropertySupport.ReadWrite(
        TexDataObject.LOCALE_PROPERTY_NAME,
        String.class,
        "Locale",
        "Locale of the document") {
            public Object getValue() {
                return ((TexDataObject) getDataObject()).getLocale();
            }
            
            public void setValue(Object value) throws InvocationTargetException {
                try {
                    ((TexDataObject) getDataObject()).setLocale((String) value);
                } catch (IOException e) {
                    throw new InvocationTargetException(e);
                }
            }
            
            public boolean supportsDefaultValue() {
                return true;
            }
            
            public void restoreDefaultValue() throws InvocationTargetException {
                setValue(Locale.getDefault().toString());
            }
            
            public boolean canWrite() {
                return getDataObject().getPrimaryFile().canWrite();
            }
        };
    }

    // Don't use getDefaultAction(); just make that first in the data loader's getActions list
    
}
