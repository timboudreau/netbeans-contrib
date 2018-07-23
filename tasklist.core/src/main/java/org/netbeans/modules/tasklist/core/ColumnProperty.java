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

package org.netbeans.modules.tasklist.core;

import org.openide.nodes.PropertySupport;

import java.beans.PropertyEditor;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;

/**
 * Class holding column properties.
 * See debuggercore's TreeTableExplorerViewSupport.java
 *
 * @author Tor Norbye
 */
public class ColumnProperty extends PropertySupport.ReadOnly {
    /** Id of the column. Used such that with deserialization,
     * we can tell exactly which column you're referring to,
     * even if we've added and removed columns from the system.
     * (Could also store the column property name, but that's
     * more work and more data). */        
    public int uid; // Used to check equivalence in serialized data,
                    // so I don't have to store whole string names
    public int width;

    private Class propertyEditorClass;

    public ColumnProperty(
			  int uid,
			  SuggestionProperty prop,
			  boolean sortable,
			  boolean defaultVisiblity,
			  int width
			  ) {
      this(uid, prop.getID(), prop.getValueClass(), prop.getName(), prop.getHint(), sortable, defaultVisiblity, width);
    }

    public ColumnProperty(
			  int uid,
			  SuggestionProperty prop,
			  boolean sortable,
			  int width
			  ) {
      this(uid, prop.getID(),prop.getName(), prop.getHint(), sortable, width);
    }
			  


    // Used for non-treetable columns
    /** Construct a new property for a "table column" (e.g. not
     * the leftmost tree column)
     * @param uid UID of this column
     * @param name Property name
     * @param type Type of this property
     * @param displayName Name shown in the display
     * @param hint Tooltip for the property
     * @param sortable Whether or not this column is valid as a sort key
     * @param defaultVisibility Whether or not this column should be shown by
     * @param width Default width for the column
     * default */        
    public ColumnProperty(
        int uid,
        String name,
        Class type,
        String displayName,
        String hint,
        boolean sortable,
        boolean defaultVisibility,
        int width
    ) {
        super(name, type, displayName, hint);
        this.uid = uid;
        this.width = width;
        setValue("suppressCustomEditor", Boolean.TRUE);  // NOI18N
//        setValue("canEditAsText", Boolean.FALSE); // NOI18N
        setValue ("ColumnDescriptionTTV", hint); // NOI18N
        if (sortable) {
            setValue("ComparableColumnTTV", Boolean.TRUE);// NOI18N
        }
        if (!defaultVisibility) {
            setValue("InvisibleInTreeTableView", Boolean.TRUE);// NOI18N
        }
    }

    // Used for the Tree column (column 0)
    /** Construct a column object for the treecolumn (leftmost
     * column).
     * @param uid UID of the column
     * @param name Property name
     * @param displayName Name shown in the display
     * @param hint Tooltip for the property
     * @param sortable Whether or not this column is sortable
     * @param width Default width for the column
     */
    public ColumnProperty (
        int uid,
        String name,
        String displayName,
        String hint,
        boolean sortable,
        int width
    ) {     
        super(name, String.class, displayName, hint);
        this.uid = uid;
        this.width = width;
        setValue( "TreeColumnTTV", Boolean.TRUE );// NOI18N
        setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
        setValue("canEditAsText", Boolean.FALSE); // NOI18N
        if (sortable) {
            setValue ("ComparableColumnTTV", Boolean.TRUE);// NOI18N
        }
    }       

    public Object getValue() {
        return null;
    }

    public int getWidth() {
        return width;
    }

    public final void setPropertyEditorClass(Class peClass) {
        propertyEditorClass = peClass;
    }

    public final PropertyEditor getPropertyEditor() {
        if (propertyEditorClass != null)
            try {
                return (PropertyEditor) propertyEditorClass.newInstance ();
            } catch (InstantiationException ex) {
            } catch (IllegalAccessException iex) {
            }
        return super.getPropertyEditor ();
    }
}
