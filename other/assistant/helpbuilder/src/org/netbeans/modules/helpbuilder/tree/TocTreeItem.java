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
import java.io.IOException;
import java.net.URL;

/**
 * A class for individual tree items.
 *
 * @author Richard Gregor
 * @version   1.1    
 */

public class TocTreeItem extends HelpTreeItem {
    
    private HelpTreeItem parent = null;
    private Vector children = new Vector();
    private String url= null;
    private String target = null;    
    private Locale locale = null;
    private String mergeType = null;   
    private boolean homeID = false;

    /**
     * Creates item with name
     *
     * @param name The name of item
     */
    public TocTreeItem(String name){     
        super(name);
    }
    
    /**
     * Creates empty item
     */
    public TocTreeItem(){
        super(null);
    }
    /**
     * Creates HelpTreeItem.
     *
     * @param name The name of item
     * @param target The target of item
     * @param url The external representation of url
     * @param homeID Set true if item is used as home page
     * @param locale The Locale of this item
     */
    public TocTreeItem(String name, String target, String url, boolean homeID, Locale locale){
        this.name = name;
        this.target = target;
        this.url = url;
        this.homeID = homeID;
        this.locale = locale;
    }    
 
    /**
     * Returns the id for this item.
     */
    public String getTarget() {
        return target;
    }
    
    /**
     * Sets the target
     */
    public void setTarget(String target){
        this.target = target;
    }
    
    /**
     *Returns the external representation of url for this item.
     */
    public String getURLSpec(){
        return url;
    }    

    /**
     *Sets the external representation of url for this item.
     */
    public void setURLSpec(String url){
        this.url = url;
    }    
    
    /**
     * Sets the merge type
     */
    public void setMergeType(String mergeType){
        this.mergeType = mergeType;
    }
    
    /**
     * Returns the merge type for the item
     */
    public String getMergeType(){
        return mergeType;
    }
 
    /**
     * Returns whether item is homeID or not
     */
    public boolean isHomeID(){
        return homeID;
    }
    
    /**
     * Sets this item as homeID
     */
    public void setHomeID(boolean value){
        this.homeID = value;
    }

    /**
     * Returns a String used when displaying the object.
     * Used by CellRenderers.
     *
     * @see TOCCellRenderer
     */
    public String toString() {
        if(isHomeID())
            return "<html><font color=green>"+name+"</font><html>";
        else
            return (name);
    }      
}


