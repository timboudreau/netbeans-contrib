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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.logmanagement;

import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha G
 */
 class LoggerSheet {

    private Logger logger;
    /* sheet sets/categories */
    private Sheet sheet;
    private Sheet.Set info;

     LoggerSheet(Logger logger) {
        this.logger = logger;
        sheet = Sheet.createDefault();
        info = Sheet.createPropertiesSet();
        createSheet();
    }

    private void createSheet() {
        info.setName("info"); //NOI18N
        info.setDisplayName(NbBundle.getMessage(LoggerNode.class,"Logger_Information"));
        try {

            Property<String> nameProp = new PropertySupport.Reflection<String>(logger, String.class, "getName", null);//NOI18N
            nameProp.setName("name"); //NOI18N
            nameProp.setDisplayName(NbBundle.getMessage(LoggerNode.class,"Logger_Name"));
            info.put(nameProp);
            PropertySupport.Reflection<String> levelProp = new PropertySupport.Reflection<String>(logger, String.class, "getLevel", "setLevel");//NOI18N
            nameProp.setName("level"); //NOI18N
            levelProp.setDisplayName(NbBundle.getMessage(LoggerNode.class,"Logger_Level"));
            levelProp.setPropertyEditorClass(LevelPropertyEditor.class);
            
            info.put(levelProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        sheet.put(info);
    }

    public Sheet getSheet() {
        return sheet;
    }
}