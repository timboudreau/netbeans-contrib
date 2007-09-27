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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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