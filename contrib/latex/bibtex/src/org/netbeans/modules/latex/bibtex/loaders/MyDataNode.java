/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.loaders;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.netbeans.modules.latex.bibtex.nodes.BiBTeXModelChildren;
import org.openide.loaders.DataNode;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/** A node to represent this object.
 *
 * @author Jan Lahoda
 */
public class MyDataNode extends DataNode {
    
    public static final String TEXT_SET = "Text";//NOI18N
    
    public MyDataNode(BiBTexDataObject obj) {
        this(obj, new BiBTeXModelChildren(obj.getPrimaryFile()));
    }
    
    protected MyDataNode(BiBTexDataObject obj, org.netbeans.modules.latex.bibtex.nodes.BiBTeXModelChildren ch) {
        super(obj, ch);
        setIconBase("org/netbeans/modules/latex/bibtex/loaders/bib_file_icon");//NOI18N
    }
    
    protected BiBTexDataObject getMyDataObject() {
        return (BiBTexDataObject)getDataObject();
    }
    
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
    }
    
    private PropertySupport createTextEncodingProperty() {
        return new PropertySupport.ReadWrite(
                  BiBTexDataObject.ENCODING_PROPERTY_NAME,
                  String.class,
                  org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Encoding"),
                  org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Encoding_of_the_document")) {
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
