/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */
package org.netbeans.modules.sysprops;

import java.util.*;

import org.openide.nodes.*;

/** Children for a PropertyNode.
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 */
public class PropertyChildren extends Children.Keys {

    /** Optional holder for the keys, to be used when changing them dynamically. */
    Set myKeys;
    
    /** Name of the Property. */
    String property;

    /** Creates new PropertyChildren
     * 
     * @param property the Name of the Property (of the Node).
     */
    public PropertyChildren (String property) {
        this.property = property;
        myKeys = null;
    }

    /**
     * Activates this Children.
     */
    protected void addNotify () {
        if (myKeys != null) return;
        searchKeys();
        if (myKeys.size() > 0) setKeys (myKeys);
    }

    /**
     * Deactivates this Children.
     */
    protected void removeNotify () {
        myKeys = null;
        setKeys (Collections.EMPTY_SET);
    }

    /**
     * Creates a Node of an Object key.
     */
    protected Node[] createNodes (Object key) {
        // interpret your key here...usually one node generated, but could be zero or more
        return new Node[] { new PropertyNode ((String) key) };
    }
    
    /**
     * Refreshs the Set of childrens
     */
    public void refreshChildren() {
        searchKeys();
        setKeys(myKeys);
    }
    
    /**
     * Searchs all subkeys of this Node.
     */
    public void searchKeys() {
        myKeys = new TreeSet();
        Properties p = System.getProperties();
        Enumeration e = p.propertyNames();
        String searchString = property + ".";
        String prop;
        while (e.hasMoreElements()) {
            prop = (String) e.nextElement();
            if (prop.startsWith(searchString)) {
                int i = prop.indexOf('.', searchString.length());
                if (i < 0) {
                    myKeys.add(prop);
                } else {
                    myKeys.add(prop.substring(0, i));
                }
            }
        }
    }
}