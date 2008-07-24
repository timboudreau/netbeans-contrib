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

package org.netbeans.modules.encoder.custom.aip;

import com.sun.encoder.custom.appinfo.DelimiterSet;
import com.sun.encoder.custom.appinfo.NodeProperties;
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
            ResourceBundle.getBundle("org/netbeans/modules/encoder/custom/aip/Bundle");
    private static final Set<String> mChangeSheetPropNames = new HashSet<String>();
    static {
        mChangeSheetPropNames.add("nodeType"); //NOI18N
        mChangeSheetPropNames.add("xmlType"); //NOI18N
        mChangeSheetPropNames.add("typeDef"); //NOI18N
        mChangeSheetPropNames.add("top");  //NOI18N
    }
    
    private final EncodingOption mEncodingOption;
    
    /** Creates a new instance of EncodingInfoNode */
    public EncodingNode(EncodingOption encodingOption, Lookup lookup) {
        super(new Children.Array(), lookup);
        mEncodingOption = encodingOption;
        encodingOption.addPropertyChangeListener(
                WeakListeners.propertyChange(this, encodingOption));
    }

    @Override
    public String getDisplayName() {
        return _bundle.getString("encoding_node.lbl.encoding");
    }

    @Override
    public String getName() {
        return "encoding"; //NOI18N
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }
    
    @Override
    public String getHtmlDisplayName() {
        if (mEncodingOption == null) {
            //Must be some kind of invalid XML causing this.
            //Display it using warning color
            return "<font color='!controlShadow'><i>" + getDisplayName() + "</i></font>"; //NOI18N
        }
        return null;
    }

    @Override
    public Image getIcon(int i) {
        return Utilities.loadImage("org/netbeans/modules/encoder/custom/aip/icon.PNG");  //NOI18N
    }

    @Override
    public Image getOpenedIcon(int i) {
        return Utilities.loadImage("org/netbeans/modules/encoder/custom/aip/openIcon.PNG");  //NOI18N
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set propSet = Sheet.createPropertiesSet();
        try {
            //The read-only encoding style property
            propSet.put(
                    new EncodingStyleProperty(
                            "encodingStyle",  //NOI18N
                            String.class,
                            _bundle.getString("encoding_node.lbl.encoding_style"),
                            _bundle.getString("encoding_node.lbl.encoding_style_short")));
            
            //The Node Type Property
            PropertySupport.Reflection<String> nodeTypeProp =
                    new PropertySupport.Reflection<String>(mEncodingOption,
                            String.class, "nodeType");  //NOI18N
            nodeTypeProp.setName("nodeType");  //NOI18N
            nodeTypeProp.setDisplayName(_bundle.getString("encoding_node.lbl.node_type"));
            nodeTypeProp.setPropertyEditorClass(NodeTypePropertyEditor.class);
            propSet.put(nodeTypeProp);
            
            if (!NodeProperties.NodeType.TRANSIENT.equals(mEncodingOption.xgetNodeType())) {
                PropertySupport.Reflection delimSetProp =
                        new DelimiterSetProperty(mEncodingOption,
                                DelimiterSet.class, "delimiterSet");  //NOI18N
                delimSetProp.setName("delimiterSet");  //NOI18N
                delimSetProp.setDisplayName(_bundle.getString("encoding_node.lbl.delim_list"));
                propSet.put(delimSetProp);
            }
            
            if (mEncodingOption.testIsGlobal()) {
                //The Top Property
                PropertySupport.Reflection<Boolean> topProp =
                        new PropertySupport.Reflection<Boolean>(mEncodingOption,
                                boolean.class, "top");  //NOI18N
                topProp.setName("top");  //NOI18N
                topProp.setDisplayName(_bundle.getString("encoding_node.lbl.top"));
                propSet.put(topProp);
            }

            if (mEncodingOption.testIsGlobal() && mEncodingOption.isTop()) {
                //The Input Character Set Property
                PropertySupport.Reflection<String> inputCharsetProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "inputCharset");  //NOI18N
                inputCharsetProp.setName("inputCharset");  //NOI18N
                inputCharsetProp.setDisplayName(_bundle.getString("encoding_node.lbl.input_charset"));
                propSet.put(inputCharsetProp);
                
                //The Output Character Set Property
                PropertySupport.Reflection<String> outputCharsetProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "outputCharset");  //NOI18N
                outputCharsetProp.setName("outputCharset");  //NOI18N
                outputCharsetProp.setDisplayName(_bundle.getString("encoding_node.lbl.output_charset"));
                propSet.put(outputCharsetProp);
            }
            
            if (mEncodingOption.testIsGlobal() && mEncodingOption.isTop()
                    || NodeProperties.NodeType.FIXED_LENGTH.equals(
                            mEncodingOption.xgetNodeType())) {
                //The Parsing Character Set Property
                PropertySupport.Reflection<String> parsingCharsetProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "parsingCharset");  //NOI18N
                parsingCharsetProp.setName("parsingCharset");  //NOI18N
                parsingCharsetProp.setDisplayName(_bundle.getString("encoding_node.lbl.parsing_charset"));
                propSet.put(parsingCharsetProp);
                
                //The Serializing Character Set Property
                PropertySupport.Reflection<String> serializingCharsetProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "serializingCharset");  //NOI18N
                serializingCharsetProp.setName("serializingCharset");  //NOI18N
                serializingCharsetProp.setDisplayName(_bundle.getString("encoding_node.lbl.serial_charset"));
                propSet.put(serializingCharsetProp);
            }
            
            if (!NodeProperties.NodeType.GROUP.equals(mEncodingOption.xgetNodeType())
                    && mEncodingOption.testIsSimple()
                    && !NodeProperties.NodeType.TRANSIENT.equals(mEncodingOption.xgetNodeType())) {
                PropertySupport.Reflection<String> alignmentProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "alignment");  //NOI18N
                alignmentProp.setName("alignment");  //NOI18N
                alignmentProp.setDisplayName(_bundle.getString("encoding_node.lbl.alignment"));
                alignmentProp.setPropertyEditorClass(AlignmentPropertyEditor.class);
                propSet.put(alignmentProp);
                
                PropertySupport.Reflection<String> matchProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "match");  //NOI18N
                matchProp.setName("match");  //NOI18N
                matchProp.setDisplayName(_bundle.getString("encoding_node.lbl.match"));
                propSet.put(matchProp);

                //The NoMatch (boolean) Property
                PropertySupport.Reflection<Boolean> noMatchProp =
                        new PropertySupport.Reflection<Boolean>(mEncodingOption,
                                boolean.class, "noMatch");  //NOI18N
                noMatchProp.setName("noMatch");  //NOI18N
                noMatchProp.setDisplayName(_bundle.getString("encoding_node.lbl.no_match"));
                propSet.put(noMatchProp);
            }
            
            if (!NodeProperties.NodeType.TRANSIENT.equals(mEncodingOption.xgetNodeType())
                    && !mEncodingOption.testIsSimple() && !mEncodingOption.testIsChoice()) {
                PropertySupport.Reflection<String> orderProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "order");  //NOI18N
                orderProp.setName("order");  //NOI18N
                orderProp.setDisplayName(_bundle.getString("encoding_node.lbl.order"));
                orderProp.setPropertyEditorClass(OrderPropertyEditor.class);
                propSet.put(orderProp);
            }
            
            if (NodeProperties.NodeType.DELIMITED.equals(mEncodingOption.xgetNodeType())
                    || NodeProperties.NodeType.ARRAY.equals(mEncodingOption.xgetNodeType())) {
                PropertySupport.Reflection<String> delimiterProp =
                        new ReadOnlyDelimiterProperty<String>(mEncodingOption,
                                String.class, "getDelimiter");  //NOI18N
                delimiterProp.setName("delimiter");  //NOI18N
                delimiterProp.setDisplayName(_bundle.getString("encoding_node.lbl.delimiter"));
                propSet.put(delimiterProp);
            }
            
            if (NodeProperties.NodeType.FIXED_LENGTH.equals(mEncodingOption.xgetNodeType())) {
                PropertySupport.Reflection<Integer> lengthProp =
                        new PropertySupport.Reflection<Integer>(mEncodingOption,
                                int.class, "length");  //NOI18N
                lengthProp.setName("length");  //NOI18N
                lengthProp.setDisplayName(_bundle.getString("encoding_node.lbl.length"));
                propSet.put(lengthProp);
            }

            if (NodeProperties.NodeType.DELIMITED.equals(mEncodingOption.xgetNodeType())
                    || NodeProperties.NodeType.ARRAY.equals(mEncodingOption.xgetNodeType())) {
                PropertySupport.Reflection<String> delimiterProp =
                        new ReadOnlyDelimiterProperty<String>(mEncodingOption,
                                String.class, "getDelimiter");  //NOI18N
                delimiterProp.setName("delimiter");  //NOI18N
                delimiterProp.setDisplayName(_bundle.getString("encoding_node.lbl.delimiter"));
                propSet.put(delimiterProp);
            }
            
            if (NodeProperties.NodeType.FIXED_LENGTH.equals(mEncodingOption.xgetNodeType())) {
                PropertySupport.Reflection<Integer> lengthProp =
                        new PropertySupport.Reflection<Integer>(mEncodingOption,
                                int.class, "length");  //NOI18N
                lengthProp.setName("length");  //NOI18N
                lengthProp.setDisplayName(_bundle.getString("encoding_node.lbl.length"));
                propSet.put(lengthProp);
            }
                
            if (mEncodingOption.testIsGlobal() && mEncodingOption.isTop()) {
                //The Escape Sequence Property
                PropertySupport.Reflection<String> escapeSequenceProp =
                        new PropertySupport.Reflection<String>(mEncodingOption,
                                String.class, "escapeSequence");  //NOI18N
                escapeSequenceProp.setName("escapeSequence");  //NOI18N
                escapeSequenceProp.setDisplayName(_bundle.getString("encoding_node.lbl.escape_sequence"));
                propSet.put(escapeSequenceProp);
            }
                
            if (mEncodingOption.testIsGlobal() && mEncodingOption.isTop()) {
                //The Fine Inherit Property
                PropertySupport.Reflection<Boolean> fineInheritProp =
                        new PropertySupport.Reflection<Boolean>(mEncodingOption,
                                boolean.class, "fineInherit");  //NOI18N
                fineInheritProp.setName("fineInherit");  //NOI18N
                fineInheritProp.setDisplayName(_bundle.getString("encoding_node.lbl.fine_inherit"));
                propSet.put(fineInheritProp);
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
    
    private static class EncodingStyleProperty<T> extends PropertySupport.ReadOnly<T> {
        
        EncodingStyleProperty(String name, Class<T> clazz, String displayName, String desc) {
            super(name, clazz, displayName, desc);
        }

        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return (T) CustomEncodingConst.STYLE;
        }
    }
}
