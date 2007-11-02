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
            levelProp.setDisplayName(NbBundle.getMessage(LoggerNode.class,"Logger_Level_prop"));
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