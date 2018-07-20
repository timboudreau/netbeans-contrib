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
    
