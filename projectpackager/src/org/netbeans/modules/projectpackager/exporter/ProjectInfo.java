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

package org.netbeans.modules.projectpackager.exporter;

import java.util.HashMap;
import java.util.Vector;

/**
 * Serves as storage of data related to projects
 * @author Roman "Roumen" Strobl
 */
public class ProjectInfo {
    
    private static Vector names;
    private static Vector sourceRootPaths;
    private static Vector selected;
    
    /** Creates a new instance of ProjectInfo */
    private ProjectInfo() {
    }
    
    /**
     * Return project name
     * @param index index of project
     * @return project name
     */
    public static String getName(int index) {
        return (String) names.get(index);
    }

    /**
     * Set project name
     * @param index index of project
     * @param name project name
     */
    public static void setName(int index, String name) {
        names.add(index, name);
    }

    /**
     * Returns source root paths for a project
     * @param index index of project
     * @return source roots
     */
    public static String[] getSourceRootPaths(int index) {
        return (String[]) sourceRootPaths.get(index);
    }

    /**
     * Set source roots of a project
     * @param index index of project
     * @param aSourceRootPaths source roots
     */
    public static void setSourceRootPaths(int index, String[] aSourceRootPaths) {
        sourceRootPaths.add(index, aSourceRootPaths);
    }    

    /**
     * Is a project selected?
     * @param index index of project
     * @return true if selected
     */
    public static boolean isSelected(int index) {
        return ((Boolean) selected.get(index)).booleanValue();
    }

    /**
     * Set a project to be selected
     * @param index index of project
     * @param aSelected true if selected
     */
    public static void setSelected(int index, boolean aSelected) {
        selected.add(index, Boolean.valueOf(aSelected));
    }
    
    /**
     * Return number of projects shown in dialog
     * @return project count
     */
    public static int getProjectCount() {
        return names.size();
    }
    
    /**
     * Initialize projects - create new Vectors with data or empty them if they exist
     */
    public static void initProjects() {
        if (names==null) {
            names = new Vector();
        } else {
            names.clear();
        }
        if (sourceRootPaths==null) {
            sourceRootPaths = new Vector();
        } else {
            sourceRootPaths.clear();
        }
        if (selected==null) {
            selected = new Vector();
        } else {
            selected.clear();
        }
    }
    
}
