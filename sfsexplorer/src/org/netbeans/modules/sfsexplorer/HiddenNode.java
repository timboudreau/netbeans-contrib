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
package org.netbeans.modules.sfsexplorer;

import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;


/**
 * Node representing hidden files.
 * @author David Strupl
 */
class HiddenNode extends AbstractNode {
    /**
     * The file displayed by this node.
     */
    private FileObject fileObject;
    
    /**
     * Creates the node.
     * @param fileObject File object that was hidden
     */
    HiddenNode(FileObject fileObject) {
        super(Children.LEAF);
        this.fileObject = fileObject;
        setName(fileObject.getNameExt());
        setIconBaseWithExtension("org/netbeans/modules/sfsexplorer/delete.gif");
        setShortDescription(NbBundle.getMessage(HiddenNode.class, "This_file_has_been_hidden."));
    }
    
    /**
     * Overriden to provide supply properties.
     * @return Initialized sheet.
     */
    @Override protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Sheet.Set set = s.get(Sheet.PROPERTIES);
        set.put(new PropertySupport.ReadOnly<String>("origin", String.class,
            NbBundle.getMessage(HiddenNode.class, "Defined_in"), NbBundle.getMessage(HiddenNode.class, "The_name_of_the_jar_that_defines_this_file/folder")) {
            public String getValue() {
                return XMLFileSystemCache.getInstance().getModuleName(fileObject);
            }
        });
        set.put(new PropertySupport.ReadOnly<String>("hiddenby", 
            String.class, NbBundle.getMessage(HiddenNode.class, "Hidden_by"), NbBundle.getMessage(HiddenNode.class, "The_name_of_the_jar_that_hides_this_file/folder")) {
            public String getValue() {
                return XMLFileSystemCache.getInstance().whoHides(fileObject);
            }
        });
        return s;
    }
}