package org.netbeans.modules.tasklist.core;

import org.openide.nodes.PropertySupport;

/** 
 * Class holding column properties.
 * See debuggercore's TreeTableExplorerViewSupport.java 
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
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
}
