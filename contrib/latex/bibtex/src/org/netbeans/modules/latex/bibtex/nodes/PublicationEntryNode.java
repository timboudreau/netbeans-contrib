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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.bibtex.nodes;

import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.NbBundle;

/**
 *  @author Jan Lahoda
 */
public class PublicationEntryNode extends BiBEntryNode {
    public PublicationEntryNode(PublicationEntry entry, FileObject source) {
        super(entry, source);
        entry.addPropertyChangeListener(this);
        setName(entry.getTag());
        
        setDisplayName(entry.getTag() + " : " + entry.getTitle());
        setIconBaseWithExtension("org/netbeans/modules/latex/bibtex/resources/pubentry.gif");
    }
    
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Set properties = sheet.get(Sheet.PROPERTIES);
        
        if (properties == null) {
            sheet.put(properties = Sheet.createPropertiesSet());
        }
        
        try {
            Property name = new PropertySupport.Reflection(getEntry(), String.class, "type");//NOI18N
            
            name.setName("type");//NOI18N
            name.setDisplayName(NbBundle.getBundle(PublicationEntryNode.class).getString("LBL_Type"));
            properties.put(name);
            
            Property tag = new PropertySupport.Reflection(getEntry(), String.class, "tag");//NOI18N
            
            tag.setName("tag");//NOI18N
            tag.setDisplayName(NbBundle.getBundle(PublicationEntryNode.class).getString("LBL_Tag"));
            properties.put(tag);

            Property title = new PropertySupport.Reflection(getEntry(), String.class, "title");//NOI18N
            
            title.setName("title");//NOI18N
            title.setDisplayName(NbBundle.getBundle(PublicationEntryNode.class).getString("LBL_Title"));
            properties.put(title);
            
            Property author = new PropertySupport.Reflection(getEntry(), String.class, "author");//NOI18N
            
            author.setName("author");//NOI18N
            author.setDisplayName(NbBundle.getBundle(PublicationEntryNode.class).getString("LBL_Author"));
            properties.put(author);
        }  catch (NoSuchMethodException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return sheet;
    }

}