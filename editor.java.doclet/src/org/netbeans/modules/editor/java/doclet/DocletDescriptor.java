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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Leon Chiver. All Rights Reserved.
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
