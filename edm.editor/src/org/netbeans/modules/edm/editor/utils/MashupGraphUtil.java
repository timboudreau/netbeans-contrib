/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.utils;

import java.awt.Image;

import org.openide.util.Utilities;

import org.netbeans.modules.sql.framework.model.SQLConstants;
/**
 *
 * @author karthikeyan s
 */
public class MashupGraphUtil {
    
    private static final Image JOIN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/join_view.png"); // NOI18N
    
    private static final Image TABLE_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/SourceTable.png"); // NOI18N
    
    private static final Image COLUMN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/column.gif"); // NOI18N
    
    private static final Image PRIMARY_COLUMN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/columnPrimary.gif"); // NOI18N
    
    private static final Image FOREIGN_COLUMN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/columnForeign.gif"); // NOI18N    
    
    private static final Image CONDITION_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/condition.png"); // NOI18N
    
    private static final Image PROPERTIES_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/properties.png"); // NOI18N
    
    private static final Image FILTER_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/filter16.gif"); // NOI18N
    
    private static final Image RUNTIME_INPUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/RuntimeInput.png"); // NOI18N
    
    private static final Image RUNTIME_ATTR_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/columnselection.png"); // NOI18N
    
    private static final Image FOREIGN_KEY_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/foreignKey.gif"); // NOI18N     
    
    /** Creates a new instance of MashupGraphUtil */
    private MashupGraphUtil() {
    }
    
    public static Image getImageForObject(int type) {
        switch(type) {
            case SQLConstants.JOIN:
                return JOIN_IMAGE;
            case SQLConstants.RUNTIME_INPUT:
                return RUNTIME_INPUT_IMAGE;
            case SQLConstants.SOURCE_TABLE:
            case SQLConstants.JOIN_TABLE:
                return TABLE_IMAGE;
        }
        return null;
    }
    
    public static Image getColumnImage() {
        return COLUMN_IMAGE;
    }

    public static Image getConditionImage() {
        return CONDITION_IMAGE;
    }
    
    public static Image getPropertiesImage() {
        return PROPERTIES_IMAGE;
    }
    
    public static Image getFilterImage() {
        return FILTER_IMAGE;
    }
    
    public static Image getRuntimeAttributeImage() {
        return RUNTIME_ATTR_IMAGE;
    }
    
    public static Image getPrimaryKeyColumnImage() {
        return PRIMARY_COLUMN_IMAGE;
    }

    public static Image getForeignKeyColumnImage() {
        return FOREIGN_COLUMN_IMAGE;
    }    
    
    public static Image getForeignKeyImage() {
        return FOREIGN_KEY_IMAGE;
    }        
}