/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.helpbuilder.tree;

import java.util.Vector;
import java.util.Locale;
import javax.help.Map.ID;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

/**
 * A class for individual tree items.
 *
 * @author Richard Gregor
 * @version   1.1    
 */

public class HelpTreeItem {    
   
    private HelpTreeItem parent = null;
    private Vector children = new Vector();
    protected String name;
    protected Locale locale;
    
    /**
     * Creates item with name
     *
     * @param name The name of item
     */
    public HelpTreeItem(String name){     
        setName(name);
    }
    
    /**
     * Creates empty item
     */
    public HelpTreeItem(){
        this(null);
    }
    
    /**
     * Adds HelpTreeItem as a child.
     *
     * @param item The HelpTreeItem.
     */
    public void add(HelpTreeItem item) {
        item.setParent(this);
        children.add(item);
    }
    
    /**
     * Removes HelpTreeItem from vector of children.
     *
     * @param item The HelpTreeItem to remove.
     */
    public void remove(HelpTreeItem item) {
        item.setParent(null);
        children.remove(item);
    }
    /**
     * Returns parent of HelpTreeItem.
     */
    public HelpTreeItem getParent() {
        return parent;
    }
    
    /**
     * Sets the parent of this item.
     *
     * @param parent The HelpTreeItem.
     */
    public void setParent(HelpTreeItem parent) {
        this.parent = parent;
    }
    
    /**
     * Returns children of this HelpTreeItem.
     */
    public Vector getChildren() {
        return children;
    }
    
    public Object clone(){       
        HelpTreeItem item = new HelpTreeItem(getName());        
        return item;
    }
    /**
     * Sets the name of the item.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Returns the name of the item.
     */
    public String getName() {
	return name;
    }
    
    public void setLocale(){
        this.locale = locale;
    }
    
    /**
    * Returns the locale for the item.
    */
    public Locale getLocale() {
	return locale;
    }

    /**
     * Returns a String used when displaying the object.
     * Used by CellRenderers.
     *
     * @see TOCCellRenderer
     */
    public String toString() {
	return (name);
    }      


}


