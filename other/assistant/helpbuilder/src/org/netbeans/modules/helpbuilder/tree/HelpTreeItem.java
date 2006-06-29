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


