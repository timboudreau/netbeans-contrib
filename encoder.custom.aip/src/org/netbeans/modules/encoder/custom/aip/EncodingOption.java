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

import com.sun.encoder.custom.appinfo.CustomEncoding;
import com.sun.encoder.custom.appinfo.Delimiter;
import com.sun.encoder.custom.appinfo.DelimiterLevel;
import com.sun.encoder.custom.appinfo.DelimiterSet;
import com.sun.encoder.custom.appinfo.NodeProperties;
import com.sun.encoder.runtime.provider.Misc;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.netbeans.modules.encoder.ui.basic.EncodingConst;
import org.netbeans.modules.encoder.ui.basic.InvalidAppInfoException;
import org.netbeans.modules.encoder.ui.basic.SchemaUtility;
import org.netbeans.modules.encoder.ui.basic.ValidationException;
import org.netbeans.modules.xml.schema.model.Annotation;
import org.netbeans.modules.xml.schema.model.AppInfo;
import org.netbeans.modules.xml.schema.model.ComplexType;
import org.netbeans.modules.xml.schema.model.Element;
import org.netbeans.modules.xml.schema.model.ElementReference;
import org.netbeans.modules.xml.schema.model.GlobalElement;
import org.netbeans.modules.xml.schema.model.LocalElement;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.SchemaModel;
import org.netbeans.modules.xml.schema.model.SimpleType;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The model of the custom encoding node.
 *
 * @author Jun Xu
 */
public class EncodingOption {
    
    private static final ResourceBundle _bundle =
            ResourceBundle.getBundle(
                EncodingOption.class.getPackage().getName() + ".Bundle"); //NOI18N
    public static final String NODE_TYPE_PREFIX = "nodeType"; //NOI18N
    public static final String ALIGNMENT_PREFIX = "align"; //NOI18N
    public static final String ORDER_PREFIX = "order"; //NOI18N
    
    private static final CustomEncoding mDefaultCustomEncoding =
            CustomEncoding.Factory.newInstance();
    
    private static final Map<String, String> mReverseTextMap =
            new HashMap<String, String>();
    private static final Map<String, String> mTextMap =
            new HashMap<String, String>();
    private static List<String> mNodeTypeTagList = new ArrayList<String>();
    private static List<String> mAlignmentTagList = new ArrayList<String>();
    private static List<String> mOrderTagList = new ArrayList<String>();
    
    static {        
        //Populate the localized text map and the tag list for the node type property
        mReverseTextMap.put(NODE_TYPE_PREFIX + "_" + _bundle.getString("TAG_NodeType_group"), "group"); //NOI18N
        mReverseTextMap.put(NODE_TYPE_PREFIX + "_" + _bundle.getString("TAG_NodeType_array"), "array"); //NOI18N
        mReverseTextMap.put(NODE_TYPE_PREFIX + "_" + _bundle.getString("TAG_NodeType_delimited"), "delimited"); //NOI18N
        mReverseTextMap.put(NODE_TYPE_PREFIX + "_" + _bundle.getString("TAG_NodeType_fixedLength"), "fixedLength"); //NOI18N
        mReverseTextMap.put(NODE_TYPE_PREFIX + "_" + _bundle.getString("TAG_NodeType_transient"), "transient"); //NOI18N
        mTextMap.put(NODE_TYPE_PREFIX + "_" + "group", _bundle.getString("TAG_NodeType_group")); //NOI18N
        mTextMap.put(NODE_TYPE_PREFIX + "_" + "array", _bundle.getString("TAG_NodeType_array")); //NOI18N
        mTextMap.put(NODE_TYPE_PREFIX + "_" + "delimited", _bundle.getString("TAG_NodeType_delimited")); //NOI18N
        mTextMap.put(NODE_TYPE_PREFIX + "_" + "fixedLength", _bundle.getString("TAG_NodeType_fixedLength")); //NOI18N
        mTextMap.put(NODE_TYPE_PREFIX + "_" + "transient", _bundle.getString("TAG_NodeType_transient")); //NOI18N
        mNodeTypeTagList.add(_bundle.getString("TAG_NodeType_group"));
        mNodeTypeTagList.add(_bundle.getString("TAG_NodeType_array"));
        mNodeTypeTagList.add(_bundle.getString("TAG_NodeType_delimited"));
        mNodeTypeTagList.add(_bundle.getString("TAG_NodeType_fixedLength"));
        mNodeTypeTagList.add(_bundle.getString("TAG_NodeType_transient"));
        mNodeTypeTagList = Collections.unmodifiableList(mNodeTypeTagList);
        
        //Populate the localized text map and the tag list for the alignment property
        mReverseTextMap.put(ALIGNMENT_PREFIX + "_" + _bundle.getString("TAG_Alignment_blind"), "blind"); //NOI18N
        mReverseTextMap.put(ALIGNMENT_PREFIX + "_" + _bundle.getString("TAG_Alignment_exact"), "exact"); //NOI18N
        mReverseTextMap.put(ALIGNMENT_PREFIX + "_" + _bundle.getString("TAG_Alignment_begin"), "begin"); //NOI18N
        mReverseTextMap.put(ALIGNMENT_PREFIX + "_" + _bundle.getString("TAG_Alignment_final"), "final"); //NOI18N
        mReverseTextMap.put(ALIGNMENT_PREFIX + "_" + _bundle.getString("TAG_Alignment_inter"), "inter"); //NOI18N
        mReverseTextMap.put(ALIGNMENT_PREFIX + "_" + _bundle.getString("TAG_Alignment_super"), "super"); //NOI18N
        mReverseTextMap.put(ALIGNMENT_PREFIX + "_" + _bundle.getString("TAG_Alignment_oneof"), "oneof"); //NOI18N
        mTextMap.put(ALIGNMENT_PREFIX + "_" + "blind", _bundle.getString("TAG_Alignment_blind")); //NOI18N
        mTextMap.put(ALIGNMENT_PREFIX + "_" + "exact", _bundle.getString("TAG_Alignment_exact")); //NOI18N
        mTextMap.put(ALIGNMENT_PREFIX + "_" + "begin", _bundle.getString("TAG_Alignment_begin")); //NOI18N
        mTextMap.put(ALIGNMENT_PREFIX + "_" + "final", _bundle.getString("TAG_Alignment_final")); //NOI18N
        mTextMap.put(ALIGNMENT_PREFIX + "_" + "inter", _bundle.getString("TAG_Alignment_inter")); //NOI18N
        mTextMap.put(ALIGNMENT_PREFIX + "_" + "super", _bundle.getString("TAG_Alignment_super")); //NOI18N
        mTextMap.put(ALIGNMENT_PREFIX + "_" + "oneof", _bundle.getString("TAG_Alignment_oneof")); //NOI18N
        mAlignmentTagList.add(_bundle.getString("TAG_Alignment_blind"));
        mAlignmentTagList.add(_bundle.getString("TAG_Alignment_exact"));
        mAlignmentTagList.add(_bundle.getString("TAG_Alignment_begin"));
        mAlignmentTagList.add(_bundle.getString("TAG_Alignment_final"));
        mAlignmentTagList.add(_bundle.getString("TAG_Alignment_inter"));
        mAlignmentTagList.add(_bundle.getString("TAG_Alignment_super"));
        mAlignmentTagList.add(_bundle.getString("TAG_Alignment_oneof"));
        mAlignmentTagList = Collections.unmodifiableList(mAlignmentTagList);
        
        //Populate the localized text map and the tag list for the order property
        mReverseTextMap.put(ORDER_PREFIX + "_" + _bundle.getString("TAG_Order_sequence"), "sequence"); //NOI18N
        mReverseTextMap.put(ORDER_PREFIX + "_" + _bundle.getString("TAG_Order_any"), "any"); //NOI18N
        mReverseTextMap.put(ORDER_PREFIX + "_" + _bundle.getString("TAG_Order_mixed"), "mixed"); //NOI18N
        mTextMap.put(ORDER_PREFIX + "_" + "sequence", _bundle.getString("TAG_Order_sequence")); //NOI18N
        mTextMap.put(ORDER_PREFIX + "_" + "any", _bundle.getString("TAG_Order_any")); //NOI18N
        mTextMap.put(ORDER_PREFIX + "_" + "mixed", _bundle.getString("TAG_Order_mixed")); //NOI18N
        mOrderTagList.add(_bundle.getString("TAG_Order_sequence"));
        mOrderTagList.add(_bundle.getString("TAG_Order_any"));
        mOrderTagList.add(_bundle.getString("TAG_Order_mixed"));
        mOrderTagList = Collections.unmodifiableList(mOrderTagList);
        
        //Populate the default NodeProperties
        mDefaultCustomEncoding.addNewNodeProperties();
        mDefaultCustomEncoding.getNodeProperties().setNodeType(
                NodeProperties.NodeType.DELIMITED);
    }

    /* Bean property change listeners */
    private final List<PropertyChangeListener> propChangeListeners =
            Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    /* Component path from which the encoding options are read */
    private final SchemaComponent[] mComponentPath;
    
    /* Bean property variables */
    private String mNodeType = mTextMap.get(NODE_TYPE_PREFIX + "_" + NodeProperties.NodeType.DELIMITED); //NOI18N
    private boolean mTop = false;
    private String mInputCharset = ""; //NOI18N
    private String mParsingCharset = ""; //NOI18N
    private String mSerializingCharset = ""; //NOI18N
    private String mOutputCharset = ""; //NOI18N
    private DelimiterSet mDelimiterSet = null;
    private String mOrder = mTextMap.get(ORDER_PREFIX + "_" + NodeProperties.Order.SEQUENCE); //NOI18N
    private String mMatch = ""; //NOI18N
    private boolean mNoMatch = false;
    private String mAlignment = mTextMap.get(ALIGNMENT_PREFIX + "_" + NodeProperties.Alignment.BLIND); //NOI18N
    private int mLength = 0;
    private String mEscapeSequence = ""; //NOI18N
    private boolean mFineInherit = false;
    
    private CustomEncoding mCustomEncoding = null;
    private AppInfo mAppInfo = null;
    private PropertyChangeListener mSchemaPropChangeListener;
    
    /** Creates a new instance of EncodingOption */
    private EncodingOption(List<SchemaComponent> path) {
        if (path == null) {
            throw new NullPointerException(_bundle.getString("encoding_opt.exp.no_component_path"));
        }
        if (path.size() < 1) {
            throw new IllegalArgumentException(_bundle.getString("encoding_opt.exp.illegal_comp_path"));
        }
        mComponentPath = path.toArray(new SchemaComponent[0]);
    }

    public static EncodingOption createFromAppInfo(List<SchemaComponent> path)
            throws InvalidAppInfoException {
        return createFromAppInfo(path, true);
    }
    
    public static EncodingOption createFromAppInfo(List<SchemaComponent> path,
            boolean hookUpListener)
            throws InvalidAppInfoException {
        
        EncodingOption option = new EncodingOption(path);
        if (!option.init(hookUpListener)) {
            return null;
        }
        return option;
    }
    
    public static Map<String, String> textMap() {
        return mTextMap;
    }
    
    public static Map<String, String> reverseTextMap() {
        return mReverseTextMap;
    }
    
    public static List<String> nodeTypeTagList() {
        return mNodeTypeTagList;
    }
    
    public static List<String> alignmentTagList() {
        return mAlignmentTagList;
    }
    
    public static List<String> orderTagList() {
        return mOrderTagList;
    }
    
    public String getAlignment() {
        return mAlignment;
    }

    public void setAlignment(String alignment) {
        String old = mAlignment;
        mAlignment = alignment;
        NodeProperties.Alignment.Enum enumAlignment =
                NodeProperties.Alignment.Enum.forString(
                    mReverseTextMap.get(ALIGNMENT_PREFIX + "_" + mAlignment)); //NOI18N
        firePropertyChange("alignment", old, mAlignment); //NOI18N
        mCustomEncoding.getNodeProperties().setAlignment(enumAlignment);
        commitToAppInfo();
    }

    public String getDelimiter() {
        String delim = computeDelimiter();
        if (delim == null) {
            delim = _bundle.getString("encoding_opt.lbl.delim_not_set");
        }
        return delim;
    }

    public DelimiterSet getDelimiterSet() {
        return mDelimiterSet;
    }

    public void setDelimiterSet(DelimiterSet delimiterSet) {
        DelimiterSet old = mDelimiterSet;
        mDelimiterSet = delimiterSet;
        if (delimiterSet == null) {
            if (mCustomEncoding.getNodeProperties().isSetDelimiterSet()) {
                mCustomEncoding.getNodeProperties().unsetDelimiterSet();
            }
        } else {
            mCustomEncoding.getNodeProperties().setDelimiterSet(mDelimiterSet);
        }
        commitToAppInfo();
        firePropertyChange("delimiterSet", old, mDelimiterSet); //NOI18N
    }

    public String getInputCharset() {
        return mInputCharset;
    }

    public void setInputCharset(String inputCharset) {
        String old = mInputCharset;
        mInputCharset = inputCharset;
        if (mInputCharset == null || mInputCharset.length() == 0) {
            if (mCustomEncoding.getNodeProperties().isSetInputCharset()) {
                mCustomEncoding.getNodeProperties().unsetInputCharset();
            }
        } else {
            mCustomEncoding.getNodeProperties().setInputCharset(inputCharset);
        }
        commitToAppInfo();
        firePropertyChange("inputCharset", old, mInputCharset); //NOI18N
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        Integer old = Integer.valueOf(mLength);
        mLength = length;
        mCustomEncoding.getNodeProperties().setLength(mLength);
        commitToAppInfo();
        firePropertyChange("length", old, Integer.valueOf(mLength)); //NOI18N
    }

    public String getMatch() {
        return mMatch;
    }

    public void setMatch(String match) {
        String old = mMatch;
        mMatch = match;
        if (mMatch == null || mMatch.length() == 0) {
            if (mCustomEncoding.getNodeProperties().isSetMatch()) {
                mCustomEncoding.getNodeProperties().unsetMatch();
            }
        } else {
            mCustomEncoding.getNodeProperties().setMatch(match);
        }
        commitToAppInfo();
        firePropertyChange("match", old, mMatch); //NOI18N
    }

    public String getEscapeSequence() {
        return mEscapeSequence;
    }

    public void setEscapeSequence(String escapeSequence) {
        String old = mEscapeSequence;
        mEscapeSequence = escapeSequence;
        if (mEscapeSequence == null || mEscapeSequence.length() == 0) {
            if (mCustomEncoding.getNodeProperties().isSetEscapeSequence()) {
                // if no value, then unset the "escapeSequence" element
                mCustomEncoding.getNodeProperties().unsetEscapeSequence();
            }
        } else {
            mCustomEncoding.getNodeProperties().setEscapeSequence(mEscapeSequence);
        }
        commitToAppInfo();
        firePropertyChange("escapeSequence", old, mEscapeSequence); //NOI18N
    }

    public boolean isFineInherit() {
        return mFineInherit;
    }

    public void setFineInherit(boolean fineInherit) {
        Boolean old = Boolean.valueOf(mFineInherit);
        mFineInherit = fineInherit;
        if (!mFineInherit) {
            // if false, remove the "fineInherit" element.
            mCustomEncoding.getNodeProperties().unsetFineInherit();
        } else {
            mCustomEncoding.getNodeProperties().setFineInherit(mFineInherit);
        }
        commitToAppInfo();
        firePropertyChange("fineInherit", old, Boolean.valueOf(mFineInherit)); //NOI18N
    }

    public String getNodeType() {
        return mNodeType;
    }

    public void setNodeType(String nodeType) {
        String old = mNodeType;
        mNodeType = nodeType;
        NodeProperties.NodeType.Enum enumNodeType =
                NodeProperties.NodeType.Enum.forString(
                    mReverseTextMap.get(NODE_TYPE_PREFIX + "_" + mNodeType)); //NOI18N
        mCustomEncoding.getNodeProperties().setNodeType(enumNodeType);
        if (!NodeProperties.NodeType.FIXED_LENGTH.equals(enumNodeType)) {
            if (mCustomEncoding.getNodeProperties().isSetLength()) {
                mCustomEncoding.getNodeProperties().unsetLength();
                mLength = 0;
            }
            if (!mTop) {
                if (mCustomEncoding.getNodeProperties().isSetParsingCharset()) {
                    mParsingCharset = "";
                    mCustomEncoding.getNodeProperties().unsetParsingCharset();
                }
                if (mCustomEncoding.getNodeProperties().isSetSerializingCharset()) {
                    mSerializingCharset = "";
                    mCustomEncoding.getNodeProperties().unsetSerializingCharset();
                }
            }
        }
        if (NodeProperties.NodeType.GROUP.equals(enumNodeType)
                || NodeProperties.NodeType.TRANSIENT.equals(enumNodeType)
                || !testIsSimple()) {
            if (mCustomEncoding.getNodeProperties().isSetAlignment()) {
                mAlignment = mTextMap.get(ALIGNMENT_PREFIX + "_" + NodeProperties.Alignment.BLIND); //NOI18N
                mCustomEncoding.getNodeProperties().unsetAlignment();
            }
            if (mCustomEncoding.getNodeProperties().isSetMatch()) {
                mMatch = "";
                mCustomEncoding.getNodeProperties().unsetMatch();
            }
        }
        firePropertyChange("nodeType", old, mNodeType); //NOI18N
        commitToAppInfo();
    }

    public boolean isNoMatch() {
        return mNoMatch;
    }

    public void setNoMatch(boolean noMatch) {
        // if No Match is selected but there is no "Match" value, then skip
        // to make No Match not selected
        if (noMatch && (mMatch == null || mMatch.length() == 0)) {
            return;
        }
        Boolean old = Boolean.valueOf(mNoMatch);
        mNoMatch = noMatch;
        if (mNoMatch) {
            mCustomEncoding.getNodeProperties().setNoMatch(mNoMatch);
        } else {
            // remove the "noMatch" element
            mCustomEncoding.getNodeProperties().unsetNoMatch();
        }
        commitToAppInfo();
        firePropertyChange("noMatch", old, Boolean.valueOf(mNoMatch)); //NOI18N
    }
    
    public String getOrder() {
        return mOrder;
    }

    public void setOrder(String order) {
        String old = mOrder;
        mOrder = order;
        NodeProperties.Order.Enum enumOrder =
                NodeProperties.Order.Enum.forString(
                    mReverseTextMap.get(ORDER_PREFIX + "_" + mOrder)); //NOI18N
        firePropertyChange("order", old, mOrder); //NOI18N
        mCustomEncoding.getNodeProperties().setOrder(enumOrder);
        commitToAppInfo();
    }

    public String getOutputCharset() {
        return mOutputCharset;
    }

    public void setOutputCharset(String outputCharset) {
        String old = mOutputCharset;
        mOutputCharset = outputCharset;
        if (mOutputCharset == null || mOutputCharset.length() == 0) {
            if (mCustomEncoding.getNodeProperties().isSetOutputCharset()) {
                mCustomEncoding.getNodeProperties().unsetOutputCharset();
            }
        } else {
            mCustomEncoding.getNodeProperties().setOutputCharset(outputCharset);
        }
        commitToAppInfo();
        firePropertyChange("outputCharset", old, mOutputCharset); //NOI18N
    }

    public String getParsingCharset() {
        return mParsingCharset;
    }

    public void setParsingCharset(String parsingCharset) {
        String old = mParsingCharset;
        mParsingCharset = parsingCharset;
        if (mParsingCharset == null || mParsingCharset.length() == 0) {
            if (mCustomEncoding.getNodeProperties().isSetParsingCharset()) {
                mCustomEncoding.getNodeProperties().unsetParsingCharset();
            }
        } else {
            mCustomEncoding.getNodeProperties().setParsingCharset(parsingCharset);
        }
        commitToAppInfo();
        firePropertyChange("parsingCharset", old, mParsingCharset); //NOI18N
    }

    public String getSerializingCharset() {
        return mSerializingCharset;
    }

    public void setSerializingCharset(String serializingCharset) {
        String old = mSerializingCharset;
        mSerializingCharset = serializingCharset;
        if (mSerializingCharset == null || mSerializingCharset.length() == 0) {
            if (mCustomEncoding.getNodeProperties().isSetSerializingCharset()) {
                mCustomEncoding.getNodeProperties().unsetSerializingCharset();
            }
        } else {
            mCustomEncoding.getNodeProperties().setSerializingCharset(serializingCharset);
        }
        commitToAppInfo();
        firePropertyChange("serializingCharset", old, mSerializingCharset); //NOI18N
    }

    public boolean isTop() {
        return mTop;
    }

    public void setTop(boolean top) {
        Boolean old = Boolean.valueOf(mTop);
        mTop = top;
        mCustomEncoding.setTop(mTop);
        if (!mTop) {
            if (mCustomEncoding.getNodeProperties().isSetInputCharset()) {
                mInputCharset = "";
                mCustomEncoding.getNodeProperties().unsetInputCharset();
            }
            if (mCustomEncoding.getNodeProperties().isSetOutputCharset()) {
                mOutputCharset = "";
                mCustomEncoding.getNodeProperties().unsetOutputCharset();
            }
            if (!NodeProperties.NodeType.FIXED_LENGTH.equals(
                    mCustomEncoding.getNodeProperties().getNodeType())) {
                if (mCustomEncoding.getNodeProperties().isSetParsingCharset()) {
                    mParsingCharset = "";
                    mCustomEncoding.getNodeProperties().unsetParsingCharset();
                }
                if (mCustomEncoding.getNodeProperties().isSetSerializingCharset()) {
                    mSerializingCharset = "";
                    mCustomEncoding.getNodeProperties().unsetSerializingCharset();
                }
            }
        }
        commitToAppInfo();
        firePropertyChange("top", old, Boolean.valueOf(mTop)); //NOI18N
    }
    
    public boolean testIsGlobal() {
        int count = 0;
        for (int i = 0; i < mComponentPath.length; i++) {
            if (mComponentPath[i] instanceof ElementReference) {
                return false;
            }
        }
        return annotation().getParent() instanceof GlobalElement;
    }
    
    public boolean testIsSimple() {
        if (!(annotation().getParent() instanceof Element)) {
            return false;
        }
        return SchemaUtility.isSimpleContent((Element) annotation().getParent());
    }

    public boolean testIsChoice() {
        if (!(annotation().getParent() instanceof Element)) {
            return false;
        }
        return SchemaUtility.isChoice((Element) annotation().getParent());
    }

    public NodeProperties.NodeType.Enum xgetNodeType() {
        return mCustomEncoding.getNodeProperties().getNodeType();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeListeners.add(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propChangeListeners.remove(listener);
    }

    public void validate(ErrorHandler handler)
            throws ValidationException, SAXException {
        if (NodeProperties.NodeType.DELIMITED.equals(
                mCustomEncoding.getNodeProperties().getNodeType())) {
            //is delimited
            if (computeDelimiter() == null) {
                handler.error(
                        new SAXParseException(
                            NbBundle.getMessage(
                                    EncodingOption.class,
                                    "encoding_opt.exp.delim_not_set", //NOI18N
                                    SchemaUtility.getNCNamePath(mComponentPath)),
                            annotation().getModel().getSchema().getTargetNamespace(),
                            null /*ModelUtils.getFilePath(annotation().getModel())*/,
                            -1,
                            -1));
            }
        } else if (NodeProperties.NodeType.FIXED_LENGTH.equals(
                mCustomEncoding.getNodeProperties().getNodeType())) {
            if (getLength() == 0) {
                handler.warning(
                        new SAXParseException(
                            NbBundle.getMessage(
                                    EncodingOption.class,
                                    "encoding_opt.exp.zero_fixed_length", //NOI18N
                                    SchemaUtility.getNCNamePath(mComponentPath)),
                            annotation().getModel().getSchema().getTargetNamespace(),
                            null /*ModelUtils.getFilePath(annotation().getModel())*/,
                            -1,
                            -1));
            }
            if (mCustomEncoding.getNodeProperties().isSetAlignment()
                    && NodeProperties.Alignment.EXACT.equals(
                        mCustomEncoding.getNodeProperties().getAlignment())
                    && mMatch != null && mLength > 0
                    && Misc.str2bytes(Misc.nonPrintable(mMatch)).length > mLength) {
                handler.error(
                        new SAXParseException(
                            NbBundle.getMessage(
                                    EncodingOption.class,
                                    "encoding_opt.exp.match_len_gt_fld_len", //NOI18N
                                    SchemaUtility.getNCNamePath(mComponentPath)),
                            annotation().getModel().getSchema().getTargetNamespace(),
                            null /*ModelUtils.getFilePath(annotation().getModel())*/,
                            -1,
                            -1));
            }
        }
    }
    
    /**
     * Return all delimiters at the given delimiter level
     * as a comma separate string, e.g. "], }". It returns null
     * if there is no delimiter defined.
     * 
     * @param delimLevel a given delimiter level
     * @return a comma separate string, e.g. "], }"
     */
    private String getDelimitersAsString(DelimiterLevel delimLevel) {
        String delim = null;
        Delimiter[] delimiters;
        // get all delimiters at this delimiter level
        delimiters = delimLevel.getDelimiterArray();
        delim = ""; //NOI18N
        for (int i = 0; i < delimiters.length; i++) {
            if (!delimiters[i].isSetBytes() || !delimiters[i].getBytes().isSetConstant()) {
                // skip non-constant delimiter(s)
                continue;
            }
            if (delim.length() > 0) {
                delim += ", "; //NOI18N
            }
            delim += delimiters[i].getBytes().getConstant();
        }
        if (delim.length() == 0) {
            delim = null;
        }
        return delim;
    }
    
    /**
     * Compute the delimiter value at current node.
     * @return the computed delimiter value.
     */
    private String computeDelimiter() {
        // Only Delinited or Array node needs to compute delimiter
        if (!NodeProperties.NodeType.DELIMITED.equals(
                    mCustomEncoding.getNodeProperties().getNodeType())
                && !NodeProperties.NodeType.ARRAY.equals(
                        mCustomEncoding.getNodeProperties().getNodeType())) {
            return null;
        }
        String delim = null;
        DelimiterLevel delimLevel;
        Delimiter[] delimiters;
        if (mDelimiterSet != null) {
            // get first delimiter level
            delimLevel = mDelimiterSet.getLevelArray(0);
            delim = getDelimitersAsString(delimLevel);
        }
        if (delim != null) {
            return delim;
        }
        SchemaComponent comp;
        Annotation anno;
        CustomEncoding customEncoding;
        //Starting from mComponentPath.length - 3, so the current element declaration can be skipped
        int level = 0;
        for (int i = mComponentPath.length - 3; i >= 0; i--) {
            comp = mComponentPath[i];
            if (!(comp instanceof Element)
                    || comp instanceof ElementReference) {
                continue;
            }
            anno = ((Element) comp).getAnnotation();
            if (anno == null) {
                level++;
                continue;
            }
            try {
                customEncoding = fetchCustomEncoding(anno, null);
            } catch (InvalidAppInfoException ex) {
                return _bundle.getString("encoding_opt.lbl.error_retrieving_delim");
            }
            if (customEncoding == null || !customEncoding.isSetNodeProperties()) {
                level++;
                continue;
            }
            if (NodeProperties.NodeType.DELIMITED.equals(
                    customEncoding.getNodeProperties().getNodeType())
                || NodeProperties.NodeType.ARRAY.equals(
                        customEncoding.getNodeProperties().getNodeType())) {
                level++;
            }
            if (!customEncoding.getNodeProperties().isSetDelimiterSet()) {
                continue;
            }
            if (customEncoding.getNodeProperties().getDelimiterSet().sizeOfLevelArray()
                    <= level) {
                break;
            }
            delimLevel = customEncoding.getNodeProperties().getDelimiterSet().getLevelArray(level);
            delimiters = delimLevel.getDelimiterArray();
            delim = ""; //NOI18N
            for (int j = 0; j < delimiters.length; j++) {
                if (!delimiters[j].isSetBytes()) {
                    continue;
                }
                if (delimiters[j].getBytes().isSetEmbedded()) {
                    if (delim.length() > 0) {
                        delim += ", "; //NOI18N
                    }
                    delim += (
                            "{" //NOI18N
                            + _bundle.getString("encoding_opt.lbl.embedded")
                            + delimiters[j].getBytes().getEmbedded().getOffset()
                            + "," //NOI18N
                            + delimiters[j].getBytes().getEmbedded().getLength()
                            + "}"); //NOI18N
                } else if (delimiters[j].getBytes().isSetConstant()) {
                    if (delim.length() > 0) {
                        delim += ", "; //NOI18N
                    }
                    delim += delimiters[j].getBytes().getConstant();
                }
            }
            if (delim.length() == 0) {
                delim = null;
            }
            break;
        }
        return delim;
    }
    
    private Annotation annotation() {
        return (Annotation) mComponentPath[mComponentPath.length - 1];
    }
    
    private String elementName() {
        Element elem = (Element) annotation().getParent();
        if (elem instanceof GlobalElement) {
            return ((GlobalElement) elem).getName();
        } else if (elem instanceof LocalElement) {
            return ((LocalElement) elem).getName();
        } else if (elem instanceof ElementReference) {
            GlobalElement ref = ((ElementReference) elem).getRef().get();
            return ref.getName();
        }
        return null;
    }
    
    private boolean init(boolean hookUpListener) throws InvalidAppInfoException {
        SchemaComponent comp = mComponentPath[mComponentPath.length - 1];
        if (!(comp instanceof Annotation)) {
            throw new IllegalArgumentException(
                    _bundle.getString("encoding_opt.exp.must_be_annotation"));
        }
        AppInfo[] appinfoReturned = new AppInfo[1];
        CustomEncoding customEncoding =
                fetchCustomEncoding((Annotation) comp, appinfoReturned);
        if (customEncoding == null || !customEncoding.isSetNodeProperties()) {
            if (appinfoReturned[0] != null) {
                mAppInfo = appinfoReturned[0];
                boolean top = false;
                if (customEncoding != null && customEncoding.isSetTop()
                        && customEncoding.getTop()) {
                    top = true;
                }
                customEncoding = (CustomEncoding) mDefaultCustomEncoding.copy();
                if (top) {
                    customEncoding.setTop(true);
                }
            } else {
                return false;
            }
        } else {
            mAppInfo = appinfoReturned[0];
        }
        mNodeType = mTextMap.get(NODE_TYPE_PREFIX + "_" //NOI18N
                + customEncoding.getNodeProperties().getNodeType().toString());
        if (customEncoding.getNodeProperties().isSetAlignment()) {
            mAlignment = mTextMap.get(ALIGNMENT_PREFIX + "_" //NOI18N
                    + customEncoding.getNodeProperties().getAlignment().toString());
        }
        if (customEncoding.getNodeProperties().isSetOrder()) {
            mOrder = mTextMap.get(ORDER_PREFIX + "_" //NOI18N
                    + customEncoding.getNodeProperties().getOrder().toString());
        }
        if (customEncoding.isSetTop()) {
            mTop = customEncoding.getTop();
        }
        if (mTop) {
            if (customEncoding.getNodeProperties().isSetInputCharset()) {
                mInputCharset = customEncoding.getNodeProperties().getInputCharset();
            }
            if (customEncoding.getNodeProperties().isSetOutputCharset()) {
                mOutputCharset = customEncoding.getNodeProperties().getOutputCharset();
            }
        }
        if (mTop || customEncoding.getNodeProperties().getNodeType().intValue()
                == NodeProperties.NodeType.INT_FIXED_LENGTH) {
            if (customEncoding.getNodeProperties().isSetParsingCharset()) {
                mParsingCharset =
                        customEncoding.getNodeProperties().getParsingCharset();
            }
            if (customEncoding.getNodeProperties().isSetSerializingCharset()) {
                mSerializingCharset =
                        customEncoding.getNodeProperties().getSerializingCharset();
            }
        }
        if (customEncoding.getNodeProperties().isSetMatch()) {
            mMatch = customEncoding.getNodeProperties().getMatch();
        }
        //Populates the NoMatch field
        if (customEncoding.getNodeProperties().isSetNoMatch()) {
            mNoMatch = customEncoding.getNodeProperties().getNoMatch();
        }
        if (customEncoding.getNodeProperties().isSetLength()) {
            mLength = customEncoding.getNodeProperties().getLength();
        }
        if (customEncoding.getNodeProperties().isSetDelimiterSet()) {
            mDelimiterSet = customEncoding.getNodeProperties().getDelimiterSet();
        }
        mCustomEncoding = customEncoding;
        //Populates the Escape Sequence field
        if (customEncoding.getNodeProperties().isSetEscapeSequence()) {
            mEscapeSequence = customEncoding.getNodeProperties().getEscapeSequence();
        }
        //Populates the FineInherit field
        if (customEncoding.getNodeProperties().isSetFineInherit()) {
            mFineInherit = customEncoding.getNodeProperties().getFineInherit();
        }

        // I guess that following lines will cause recursive loop when
        // the AppInfo is removed from the text editing pane.
        //if (mAppInfo == null) {
        //    commitToAppInfo();
        //}
        
        if (!(((Annotation) comp).getParent() instanceof Element)) {
            throw new IllegalArgumentException(
                    _bundle.getString("encoding_opt.exp.anno_must_under_elem"));
        }
        if (hookUpListener) {
            Element elem = (Element) ((Annotation) comp).getParent();
            Object xmlType = SchemaUtility.getXMLType(elem);
            SchemaModel refModel = null;
            if ((xmlType instanceof SimpleType)
                    || (xmlType instanceof ComplexType)) {
                refModel = ((SchemaComponent) xmlType).getModel();
            }
            mSchemaPropChangeListener =
                    new SchemaPropertyChangeListener(elem, xmlType);
            elem.getModel().addPropertyChangeListener(
                    WeakListeners.propertyChange(
                        mSchemaPropChangeListener, elem.getModel()));
            if (refModel != null && elem.getModel() != refModel) {
                refModel.addPropertyChangeListener(
                    WeakListeners.propertyChange(
                        mSchemaPropChangeListener, refModel));
            }
        }
        return true;
    }

    private CustomEncoding fetchCustomEncoding(Annotation anno,
            AppInfo[] appinfoReturned)
            throws InvalidAppInfoException {
        CustomEncoding customEncoding = null;
        Collection<AppInfo> appinfos = anno.getAppInfos();
        if (appinfos != null) {
            for (AppInfo appinfo : appinfos) {
                // ensure the appinfo's uri is expected as "urn:com.sun:encoder"
                if (!EncodingConst.URI.equals(appinfo.getURI())) {
                    continue;
                }
                if (appinfoReturned != null) {
                    appinfoReturned[0] = appinfo;
                }
                try {                    
                    XmlOptions xmlOptions = new XmlOptions();
                    // set this option so that the document element is replaced
                    // with the given QName (null) when parsing.
                    xmlOptions.setLoadReplaceDocumentElement(null);
                    customEncoding = CustomEncoding.Factory.parse(
                            new StringReader(xmlFragFromAppInfo(appinfo)),
                            xmlOptions);
                    xmlOptions = new XmlOptions();
                    List errorList = new ArrayList();
                    // errorList will contain all the errors after the validate
                    // operation takes place.
                    xmlOptions.setErrorListener(errorList);
                    if (!customEncoding.validate(xmlOptions)) {
                        throw new XmlException(errorList.toString());
                    }
                } catch (XmlException ex) {
                    throw new InvalidAppInfoException(
                            NbBundle.getMessage(
                                    EncodingOption.class,
                                    "encoding_opt.exp.invalid_appinfo", //NOI18N
                                    SchemaUtility.getNCNamePath(mComponentPath),
                                    ex.getMessage()),
                            ex);
                } catch (IOException ex) {
                    throw new InvalidAppInfoException(
                            NbBundle.getMessage(
                                    EncodingOption.class,
                                    "encoding_opt.exp.io_exception", //NOI18N
                                    SchemaUtility.getNCNamePath(mComponentPath),
                                    ex.getMessage()),
                            ex);
                }
                break;
            }
        }
        return customEncoding;
    }
    
    private synchronized void commitToAppInfo() {
        boolean startedTrans = false;
        SchemaModel model = null;
        if (mAppInfo == null) {
            Annotation anno = annotation();
            model = anno.getModel();
            if (!model.isIntransaction()) {
                if (!model.startTransaction()) {
                    // happens if failed to acquire transaction, for e.g.
                    // when model has transitioned into invalid state.
                    //TODO how to handle???
                    }
                startedTrans = true;
            }
            // create a new AppInfo object
            mAppInfo = anno.getModel().getFactory().createAppInfo();
            anno.addAppInfo(mAppInfo);
            mAppInfo.setURI(EncodingConst.URI);
        } else {
            model = mAppInfo.getModel();
            if (!model.isIntransaction()) {
                if (!model.startTransaction()) {
                    // happens if failed to acquire transaction, for e.g.
                    // when model has transitioned into invalid state.
                    //TODO how to handle???
                    }
                startedTrans = true;
            }
        }
        try {
            String contentFrag = contentFragFromXmlObject(mCustomEncoding);
            mAppInfo.setContentFragment(contentFrag);
        } catch (IOException ex) {
            //TODO how to handle???
            } finally {
            if (startedTrans) {
                model.endTransaction();
            }
        }
    }
    
    private void firePropertyChange(String name, Object oldObj, Object newObj) {
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) 
                propChangeListeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(
                    new PropertyChangeEvent (this, name, oldObj, newObj));
        }
    }
    
    private String xmlFragFromAppInfo(AppInfo appInfo) {
        StringBuffer sb = new StringBuffer("<xml-fragment"); //NOI18N
        sb.append(" ").append("source=\"").append(EncodingConst.URI).append("\""); //NOI18N
        if (appInfo.getPeer() != null) {
            String prefix = appInfo.getPeer().lookupPrefix(EncodingConst.URI);
            if (prefix != null) {
                sb.append(" xmlns"); //NOI18N
                if (prefix.length() > 0) {
                    sb.append(":").append(prefix); //NOI18N
                }
                sb.append("=\"").append(EncodingConst.URI).append("\""); //NOI18N
            }
            prefix = appInfo.getPeer().lookupPrefix(CustomEncodingConst.URI);
            if (prefix != null) {
                sb.append(" xmlns"); //NOI18N
                if (prefix.length() > 0) {
                    sb.append(":").append(prefix); //NOI18N
                }
                sb.append("=\"").append(CustomEncodingConst.URI).append("\""); //NOI18N
            }
        }
        sb.append(">"); //NOI18N
        sb.append(appInfo.getContentFragment());
        sb.append("</xml-fragment>"); //NOI18N
        return sb.toString();
    }
    
    private String contentFragFromXmlObject(XmlObject xmlObject) {
        XmlCursor cursor = null;
        try {
            cursor = xmlObject.newCursor();
            StringBuffer buff = new StringBuffer();
            if (!cursor.toFirstChild()) {
                return ""; //NOI18N
            }
            buff.append(cursor.xmlText());
            while (cursor.toNextSibling()) {
                buff.append(cursor.xmlText());
            }
            return buff.toString();
        } finally {
            if (cursor != null) {
                cursor.dispose();
            }
        }
    }
    
    private class SchemaPropertyChangeListener implements PropertyChangeListener {
        
        private final Element mElem;
        private final Set<SchemaModel> mModelSet = new HashSet<SchemaModel>();
        private Object mXMLType;
        
        SchemaPropertyChangeListener(Element elem, Object xmlType) {
            mElem = elem;
            mXMLType = xmlType;
            if (xmlType instanceof SchemaComponent) {
                SchemaModel refModel = ((SchemaComponent) xmlType).getModel();
                if (elem.getModel() != refModel) {
                    mModelSet.add(refModel);
                }
            }
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (mElem == evt.getSource() && "type".equals(evt.getPropertyName())) {   //NOI18N
                mXMLType = evt.getNewValue();
                if (mXMLType instanceof SchemaComponent) {
                    SchemaModel refModel = ((SchemaComponent) mXMLType).getModel();
                    if (mElem.getModel() != refModel
                            && !mModelSet.contains(refModel)) {
                        refModel.addPropertyChangeListener(
                            WeakListeners.propertyChange(
                                mSchemaPropChangeListener, refModel));
                        mModelSet.add(refModel);
                    }
                }
                firePropertyChange("xmlType", evt.getOldValue(), evt.getNewValue());   //NOI18N
                return;
            }
            if (mXMLType != null && mXMLType == evt.getSource()
                    && "definition".equals(evt.getPropertyName())) {   //NOI18N
                firePropertyChange("typeDef", evt.getOldValue(), evt.getNewValue());   //NOI18N
                return;
            }
        }
    }
}
