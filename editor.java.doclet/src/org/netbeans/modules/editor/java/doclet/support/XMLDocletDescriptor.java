/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Leon Chiver. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.editor.java.doclet.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.modules.editor.java.doclet.ast.Attribute;
import org.netbeans.modules.editor.java.doclet.ast.Javadoc;
import org.netbeans.modules.editor.java.doclet.ast.Tag;
import org.openide.xml.EntityCatalog;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.netbeans.modules.editor.java.doclet.DocletDescriptor;

/**
 * @author chiver
 */
public abstract class XMLDocletDescriptor implements DocletDescriptor {
    
    private final static String TAG_CLASS_TAGS = "class-tags";
    
    private final static String TAG_FIELD_TAGS = "field-tags";
    
    private final static String TAG_METHOD_TAGS = "method-tags";
    
    private final static String TAG_TAG = "tag";
    
    private final static String TAG_ATTRIBUTE = "attribute";
    
    private final static String TAG_VALUE = "value";
    
    private final static String ATTR_NAME = "name";
    
    private final static String ATTR_REQUIRED = "required";
    
    private final static String ATTR_METHOD_PREFIX = "method-prefix";
    
    private final static String ATTR_TYPE = "type";
    
    private final static String TYPE_BOOL = "bool";
    
    private final static int SORT_PRIORITY = -500;
        
    private List methodTags = new ArrayList();
    
    private List classTags = new ArrayList();
    
    private List fieldTags = new ArrayList();
    
    private static Comparator attributeComparator = new Comparator() {
        
        public int compare(Object o1, Object o2) {
            AttributeDescriptor a1 = (AttributeDescriptor) o1;
            AttributeDescriptor a2 = (AttributeDescriptor) o2;
            if (a1.isRequired()) {
                return -1;
            } else if (a2.isRequired()) {
                return -1;
            }
            return 0;
        }
    };
  
    private String completionPrefix;
    
    public XMLDocletDescriptor() throws IOException, SAXException {
        loadTags(getDescriptorInputStream());
        initCompletionPrefix();
    }
    
    public void addContents(
            BaseDocument doc, int offset, CallableFeature callableFeat, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List/*<DocletCompletionItem>*/ result) {
        if (!(callableFeat instanceof Method)) {
            return;
        }
        addContents(methodTags, currentTag, currentAttribute, result);
    }
    
    public void addContents(
            BaseDocument doc, int offset, ClassDefinition def, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List/*<DocletCompletionItem>*/ result) {
        addContents(classTags, currentTag, currentAttribute, result);
    }
    
    public void addContents(
            BaseDocument doc, int offset, Field field, 
            Tag currentTag, Attribute currentAttribute, Javadoc jd, List/*<DocletCompletionItem>*/ result) {
        addContents(fieldTags, currentTag, currentAttribute, result);
    }
    
    public void addTags(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Javadoc jd, List/*<DocletCompletionItem>*/ result) {
        if (!(callableFeat instanceof Method)) {
            return;
        }
        addTagsByPrefix(jd, currentTag != null ? currentTag.getName() : null, methodTags, result);
    }
    
    public void addTags(
            BaseDocument doc, int offset, ClassDefinition def, Tag currentTag, 
            Javadoc jd, List/*<DocletCompletionItem>*/ result) {
        addTagsByPrefix(jd, currentTag != null ? currentTag.getName() : null, classTags, result);
    }
    
    public void addTags(
            BaseDocument doc, int offset, Field field, Tag currentTag, 
            Javadoc jd, List/*<DocletCompletionItem>*/ result) {
        addTagsByPrefix(jd, currentTag != null ? currentTag.getName() : null, fieldTags, result);
    }
    
    public void addAttributeValues(
            BaseDocument doc, int offset, Field field, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result) {
        addAttributeValues(fieldTags, currentTag, currentAttribute, result);
    }
    
    public void addAttributeValues(
            BaseDocument doc, int offset, ClassDefinition def, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result) {
        addAttributeValues(classTags, currentTag, currentAttribute, result);
    }
    
    public void addAttributeValues(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Attribute currentAttribute, Javadoc jd, List result) {
        if (!(callableFeat instanceof Method)) {
            return;
        }
        addAttributeValues(methodTags, currentTag, currentAttribute, result);
    }
    
    private void addContents(
            List descriptors, Tag currentTag, Attribute currentAttribute, 
            List/*<DocletCompletionItem>*/ result) {
        TagDescriptor td = findTagDescriptorByName(currentTag.getName(), descriptors);
        if (td != null && td.hasAttributes()) {
            addMissingAttributes(currentTag, td, currentAttribute, result);
        }
    }
    
    private void addAttributeValues(
            List descriptors, Tag currentTag, Attribute attribute, 
            List/*<DocletCompletionItem>*/ result) {
        TagDescriptor td = findTagDescriptorByName(currentTag.getName(), descriptors);
        if (td == null || !td.hasAttributes()) {
            return;
        }
        String aValue = null;
        if (attribute != null) {
            aValue = attribute.getValue();
        }
        // TODO - handle this in the parser
        if (aValue != null) {
            if (aValue.startsWith("\"") || aValue.startsWith("'")) {
                aValue = aValue.substring(1);
            }
            if (aValue.endsWith("\"") || aValue.endsWith("'")) {
                aValue = aValue.substring(0, aValue.length() - 1);
            }
        }
        AttributeDescriptor ad = findAttributeDescriptorByName(attribute.getName(), td);
        if (ad == null) {
            return;
        }
        if (ad.hasValues()) {
            List v = ad.getValues();
            int sz = v.size();
            for (int i = 0; i < sz; i++) {
                String s = (String) v.get(i);
                if (aValue == null || s.startsWith(aValue)) {
                    result.add(DocletCompletionItem.createAttributeValueItem(
                            s, s, SORT_PRIORITY));
                }
            }
        } else if (TYPE_BOOL.equals(ad.getType())) {
            if (aValue == null || "true".startsWith(aValue)) {
                result.add(DocletCompletionItem.createAttributeValueItem(
                        "true", "true", SORT_PRIORITY - 1));
            } 
            if (aValue == null || "false".startsWith(aValue)) {
                result.add(DocletCompletionItem.createAttributeValueItem(
                        "false", "false", SORT_PRIORITY));
            }
        }
    }
    
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
    
    private void loadTags(InputStream is) throws IOException, SAXException {
        Document doc = XMLUtil.parse(new InputSource(is), true, false, null, EntityCatalog.getDefault());
        processDescriptorElement(doc.getDocumentElement());
    }
    
    private void addMissingAttributes(Tag tag, TagDescriptor desc, Attribute currentAttribute, List/*<DocletCompletionItem>*/ result) {
        Set existingNames = tag.getTagNames();
        List/*<AttributeDescriptor>*/ descriptors = desc.getAttributeDescriptors();
        int sz = descriptors.size();
        for (int i = 0; i < sz; i++) {
            AttributeDescriptor ad = (AttributeDescriptor) descriptors.get(i);
            String name = ad.getName();
            String currentAttrName = currentAttribute != null ? currentAttribute.getName() : null;
            if (!existingNames.contains(name) &&
                    (currentAttrName == null || name.startsWith(currentAttrName))) {
                result.add(DocletCompletionItem.createAttributeItem(
                        name, name + "=", ad.isRequired(), SORT_PRIORITY));
            }
        }
    }
    
    private void processDescriptorElement(Element e) {
        processElements(e, TAG_CLASS_TAGS, classTags);
        processElements(e, TAG_METHOD_TAGS, methodTags);
        processElements(e, TAG_FIELD_TAGS, fieldTags);
    }
    
    private void addTagsByPrefix(Javadoc doc, String prefix, List descriptorList, List result) {
        if (descriptorList.isEmpty()) {
            return;
        }
        int sz = descriptorList.size();
        for (int i = 0; i < descriptorList.size(); i++) {
            TagDescriptor td = (TagDescriptor) descriptorList.get(i);
            String tagName = td.getName();
            if (prefix == null || prefix.length() == 0 || tagName.startsWith(prefix)) {
                if (td.getMaxOccurs() > 0) {
                    List existing = doc.getTagsByName(tagName);
                    if (existing.size() >= td.getMaxOccurs()) {
                        continue;
                    }
                }
                String compl = "@" + tagName;
                result.add(DocletCompletionItem.createTextItem(
                        compl, compl, SORT_PRIORITY));
            }
        }
    }
    
    private static AttributeDescriptor findAttributeDescriptorByName(String name, TagDescriptor td) {
        List l = td.getAttributeDescriptors();
        int sz = l.size();
        for (int i = 0; i < sz; i++) {
            AttributeDescriptor ad = (AttributeDescriptor) l.get(i);
            if (name.equals(ad.getName())) {
                return ad;
            }
        }
        return null;
    }
    
    private static TagDescriptor findTagDescriptorByName(String name, List descriptorList) {
        int sz = descriptorList.size();
        for (int i = 0; i < sz; i++) {
            TagDescriptor td = (TagDescriptor) descriptorList.get(i);
            if (name.equals(td.getName())) {
                return td;
            }
        }
        return null;
    }
    
    private void processElements(Element descriptorElement, String tagName, List addTo) {
        NodeList l = descriptorElement.getElementsByTagName(tagName);
        int sz = l.getLength();
        for (int i = 0; i < sz; i++) {
            Element ce = (Element) l.item(i);
            processTags(ce, addTo);
        }
    }
    
    private void processTags(Element e, List tags) {
        NodeList tagList = e.getElementsByTagName(TAG_TAG);
        int sz = tagList.getLength();
        for (int i = 0; i < sz; i++) {
            Element te = (Element) tagList.item(i);
            processTag(te, tags);
        }
    }
    
    private void processTag(Element e, List tags) {
        String tagName = e.getAttribute(ATTR_NAME);
        String methodPrefix = e.getAttribute(ATTR_METHOD_PREFIX);
        NodeList tagAttrList = e.getElementsByTagName(TAG_ATTRIBUTE);
        int sz = tagAttrList.getLength();
        List attrs = sz == 0 ? Collections.EMPTY_LIST : new ArrayList();
        for (int i = 0; i < sz; i++) {
            Element ae = (Element) tagAttrList.item(i);
            processTagAttribute(ae, attrs); 
        } 
        Collections.sort(attrs, attributeComparator);
        TagDescriptor td = new TagDescriptor(tagName, methodPrefix, attrs);
        tags.add(td);
    }
    
    private void processTagAttribute(Element e, List l) {
        String attrName = e.getAttribute(ATTR_NAME);
        String req = e.getAttribute(ATTR_REQUIRED);
        String type = e.getAttribute(ATTR_TYPE);
        boolean required = req != null ? Boolean.valueOf(req).booleanValue() : false;
        NodeList valueList = e.getElementsByTagName(TAG_VALUE);
        int sz = valueList.getLength();
        List values = sz == 0 ? Collections.EMPTY_LIST : new ArrayList();
        for (int i = 0; i < sz; i++) {
            Element ve = (Element) valueList.item(i);
            values.add(ve.getFirstChild().getNodeValue());
        }
        AttributeDescriptor desc = new AttributeDescriptor(attrName, required, values, type);
        l.add(desc);
    }

    private void initCompletionPrefix() {
        CompletionPrefixSettings s = getCompletionPrefixSettings();
        s.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (CompletionPrefixSettings.PROPERTY_COMPLETION_PREFIX.equals(evt.getPropertyName())) {
                    completionPrefix = (String) evt.getNewValue();
                }
            }
        });
        completionPrefix = s.getCompletionPrefix();
    }
    
    public String getCompletionPrefix() {
        return completionPrefix;
    }
    
    public abstract CompletionPrefixSettings getCompletionPrefixSettings();
    
    public abstract InputStream getDescriptorInputStream();
    
}
