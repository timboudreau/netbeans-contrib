/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Leon Chiver. All Rights Reserved.
 */

package org.netbeans.modules.editor.java.doclet;

import java.util.List;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.modules.editor.java.doclet.ast.Attribute;
import org.netbeans.modules.editor.java.doclet.ast.Javadoc;
import org.netbeans.modules.editor.java.doclet.ast.Tag;

/**
 * @author leon chiver
 */
public interface DocletDescriptor {
    
    void addAttributeValues(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Attribute currentAttribute, Javadoc jd, List result);
    
    void addAttributeValues(
            BaseDocument doc, int offset, ClassDefinition def, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result);
    
    void addAttributeValues(
            BaseDocument doc, int offset, Field field, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result);
    
    void addContents(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Attribute currentAttribute, Javadoc jd, List result);
    
    void addContents(
            BaseDocument doc, int offset, ClassDefinition def, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result);
    
    void addContents(
            BaseDocument doc, int offset, Field field, Tag currentTag, 
            Attribute currentAttribute, Javadoc jd, List result);
    
    void addTags(
            BaseDocument doc, int offset, CallableFeature callableFeat, 
            Tag currentTag, Javadoc jd, List result);
    
    void addTags(
            BaseDocument doc, int offset, ClassDefinition def, Tag currentTag, 
            Javadoc jd, List result);
    
    void addTags(
            BaseDocument doc, int offset, Field field, Tag currentTag, 
            Javadoc jd, List result);

    String getCompletionPrefix();
    
}
