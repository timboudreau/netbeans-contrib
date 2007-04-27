/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.encoder.hl7.aip;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 * The node implementation for displaying encoding information.
 *
 * @author Jun Xu
 */
public class EncodingNode extends AbstractNode
        implements PropertyChangeListener {
    
    private static final ResourceBundle _bundle =
            ResourceBundle.getBundle("org/netbeans/modules/encoder/hl7/aip/Bundle");
    private static final Set<String> mChangeSheetPropNames = new HashSet<String>();
    static {
        mChangeSheetPropNames.add("setHL7Props");  //NOI18N
    }
    
    private final EncodingOption mEncodingOption;
    
    /** Creates a new instance of EncodingInfoNode */
    public EncodingNode(EncodingOption encodingOption, Lookup lookup) {
        super(new Children.Array(), lookup);
        mEncodingOption = encodingOption;
        encodingOption.addPropertyChangeListener(
                WeakListeners.propertyChange(this, encodingOption));
    }

    public String getDisplayName() {
        return _bundle.getString("encoding_node.lbl.encoding");
    }

    public String getName() {
        return "encoding";  //NOI18N
    }

    public boolean canRename() {
        return false;
    }

    public boolean canCut() {
        return false;
    }

    public boolean canCopy() {
        return false;
    }
    
    public String getHtmlDisplayName() {
        if (mEncodingOption == null) {
            //Must be some kind of invalid XML causing this.
            //Display it using warning color
            return "<font color='!controlShadow'><i>" + getDisplayName() + "</i></font>"; //NOI18N
        }
        return null;
    }

    public Image getIcon(int i) {
        return Utilities.loadImage("org/netbeans/modules/encoder/hl7/aip/icon.PNG");  //NOI18N
    }

    public Image getOpenedIcon(int i) {
        return Utilities.loadImage("org/netbeans/modules/encoder/hl7/aip/openIcon.PNG");  //NOI18N
    }

    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set propSet = Sheet.createPropertiesSet();
        try {
            //The read-only encoding style property
            propSet.put(new EncodingStyleProperty(
                    "encodingStyle",  //NOI18N
                    String.class,
                    _bundle.getString("encoding_node.lbl.encoding_style"),
                    _bundle.getString("encoding_node.lbl.encoding_style_s")));
            
            //The Node Type Property
            if (mEncodingOption.testIsGlobal()) {
                //The Top Property
                PropertySupport.Reflection topProp =
                        new PropertySupport.Reflection(mEncodingOption,
                                boolean.class, "top");  //NOI18N
                topProp.setName("top");  //NOI18N
                topProp.setDisplayName(_bundle.getString("encoding_node.lbl.top"));
                propSet.put(topProp);
            }

            if (!mEncodingOption.testIsUnderElement()) {
                PropertySupport.Reflection setHL7PropsProp =
                        new PropertySupport.Reflection(mEncodingOption,
                                boolean.class, "setHL7Props");  //NOI18N
                setHL7PropsProp.setName("setHL7Props");  //NOI18N
                setHL7PropsProp.setDisplayName(
                        _bundle.getString("encoding_node.lbl.set_HL7_properties"));
                propSet.put(setHL7PropsProp);
            }
            
            if (mEncodingOption.isSetHL7Props()) {
                PropertySupport.Reflection itemProp =
                        new PropertySupport.Reflection(mEncodingOption,
                                String.class, "item");  //NOI18N
                itemProp.setName("item");  //NOI18N
                itemProp.setDisplayName(_bundle.getString("encoding_node.lbl.item"));
                propSet.put(itemProp);
                
                PropertySupport.Reflection typeProp =
                        new PropertySupport.Reflection(mEncodingOption,
                                String.class, "type");  //NOI18N
                typeProp.setName("type");  //NOI18N
                typeProp.setDisplayName(_bundle.getString("encoding_node.lbl.type"));
                propSet.put(typeProp);
                
                PropertySupport.Reflection tableProp =
                        new PropertySupport.Reflection(mEncodingOption,
                                String.class, "table");  //NOI18N
                tableProp.setName("table");  //NOI18N
                tableProp.setDisplayName(_bundle.getString("encoding_node.lbl.table"));
                propSet.put(tableProp);
                
                PropertySupport.Reflection longNameProp =
                        new PropertySupport.Reflection(mEncodingOption,
                                String.class, "longName");  //NOI18N
                longNameProp.setName("longName");  //NOI18N
                longNameProp.setDisplayName(_bundle.getString("encoding_node.lbl.long_name"));
                propSet.put(longNameProp);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(_bundle.getString("encoding_node.exp.no_such_mthd"), e);
        }
        sheet.put(propSet);
        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (mChangeSheetPropNames.contains(evt.getPropertyName())) {
            setSheet(createSheet());
        }
    }
    
    private static class EncodingStyleProperty extends PropertySupport.ReadOnly {
        
        EncodingStyleProperty(String name, Class clazz, String displayName, String desc) {
            super(name, clazz, displayName, desc);
        }

        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return HL7EncodingConst.STYLE;
        }
    }
}
