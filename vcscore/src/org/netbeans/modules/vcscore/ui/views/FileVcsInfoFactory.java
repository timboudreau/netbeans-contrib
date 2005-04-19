/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views;

import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.nodes.*;

import org.netbeans.modules.vcscore.ui.views.actions.*;
import org.netbeans.modules.vcscore.ui.views.types.*;



import java.io.*;
import java.util.*;

/**
 * @author  Milos Kleint
 */
public class FileVcsInfoFactory {
    
    private static WeakSet weakTypeSet = new WeakSet();
    
    /** Creates a new instance of FileVcsInfoFactory */
    private FileVcsInfoFactory() {
    }

    public static FileVcsInfo createBlankFileVcsInfo(String type, File file) {
        return createFileVcsInfo(FileVcsInfo.BLANK + type, file, false);
    }
    
    public static FileVcsInfo createFileVcsInfo(String type, File file, boolean isLeaf) {
        Children children = null;
        if (isLeaf) {
            children = Children.LEAF;
        } else {
            children = new FileVcsInfoChildren();
        }
        FileVcsInfo toReturn = new FileVcsInfo(file, type, children);
        toReturn.setGeneralTypeInfo(getTypeInfo(type));
        return toReturn;
    }
    
    
    private static GeneralTypeInfo getTypeInfo(String type) {
        GeneralTypeInfo found = null;
        synchronized (weakTypeSet) {
            Iterator it = weakTypeSet.iterator();
            while (it.hasNext()) {
                GeneralTypeInfo tp = (GeneralTypeInfo)it.next();
                if (tp.getType().equals(type)) {
                    found = tp;
                    break;
                }
            }
            if (found == null) {
                found = createTypeInfo(type);
                weakTypeSet.add(found);
            }
        }
        return found;
    }
    
    private static GeneralTypeInfo createTypeInfo(String type) {
        GeneralTypeInfo toReturn = null;
        // TODO - rewrite to be general.. some kind of registration maybe..

        if (type.equals(StatusInfoPanel.TYPE)) {
            SystemAction act = (SystemAction)SharedClassObject.findObject(DiffWithRepositoryAction.class, true);
            SystemAction act2 = (SystemAction)SharedClassObject.findObject(SeeTagsAction.class, true);
            
            toReturn = new GeneralTypeInfo(type, new SystemAction[] {act, act2}, null);
        } else {
            toReturn = new GeneralTypeInfo(type, new SystemAction[] {}, null);
        }
        
        return toReturn;
    }
    
    public static class GeneralTypeInfo {
        
        private SystemAction[] actions;
        private String type;
        private String[] dumpableAttributes;
        private List dumpableList;
        
        private GeneralTypeInfo(String type, SystemAction[] actions, String[] attrsToDump) {
            this.actions = actions;
            this.type = type;
            this.dumpableAttributes = attrsToDump;
            if (dumpableAttributes == null) {
                this.dumpableList = Arrays.asList(new String[] {});
            } else {
                this.dumpableList = Arrays.asList(dumpableAttributes);
            }
        }
        
        public SystemAction[] getAdditionalActions() {
            return actions;
        }

        public String[] getAttributesAllowedToDump() {
            return dumpableAttributes;
        }
        
        public boolean isDumpAbleAttribute(String attr) {
            return dumpableList.contains(attr);
        }
        
        public String getType() {
            return type;
        }
    }
}
    
