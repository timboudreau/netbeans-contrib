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
 */

package org.netbeans.modules.vcscore;

import java.util.*;
import java.io.*;
import java.text.*;

import org.openide.util.*;
import org.openide.filesystems.FileObject;

import org.netbeans.modules.vcscore.util.*;
//import org.netbeans.modules.vcscore.commands.VcsCommandOrder;

/**
 *
 * @author  Pavel Buzek, Martin Entlicher
 */

public class VcsConfigVariable extends Object implements Cloneable, Serializable, Comparable {

    /**
     * The variable name.
     */
    private String name;          // The variable name
    /**
     * The label of this variable in the Customizer.
     */
    private String label;         // The label of that variable in the Customizer
    /**
     * The mnemonic of this variable label in the Customizer.
     */
    private Character labelMnemonic;
    /**
     * The accessibility name of this variable field in the Customizer.
     */
    private String a11yName;
    /**
     * The accessibility description of this variable field in the Customizer.
     */
    private String a11yDescription;
    /**
     * The value of the variable.
     */
    private String value;         // The value of this variable
    /**
     * Whether this variable is basic or not. Basic variables can be set in the Customizer.
     */
    private boolean basic;       // Whether this variable is basic or not (basic variables can be set in the Customizer)
    /**
     * Whether this variable is a local file. Browse buttom will be created for this variable.
     */
    private boolean localFile;   // Whether this variable is a local file. Browse buttom will be created for this variable.
    /**
     * Whether this variable is a local directory. Browse buttom will be created for this variable.
     */
    private boolean localDir;    // Whether this variable is a local directory. Browse buttom will be created for this variable.
    /**
     * Whether this variable is an executable. Browse button will be created for this variable.
     */
    private boolean executable;
    /**
     * The custom selector for this variable.
     * Select button will be created in the customizer and selector executed on its action.
     */
    private String customSelector;// The custom selector for this variable
    /**
     * The order of the variable in the Customizer.
     */
    private int order;           // The order of the variable in the Customizer

    static final long serialVersionUID =4230769028627379053L;

    /** Creates new VcsConfigVariable with zero order.
    * @param name the variable name
    * @param label the label of that variable in the Customizer
    * @param value the value of this variable
    * @param basic whether this variable is basic or not. Basic variables can be set in the Customizer.
    * @param localFile whether this variable is a local file. Browse buttom will be created for this variable.
    * @param localDir whether this variable is a local directory. Browse buttom will be created for this variable.
    * @param customSelector the custom selector for this variable. It can be a subclass of <code>VcsAdditionalCommand</code> or an executable.
    */
    public VcsConfigVariable(String name, String label, String value,
                             boolean basic, boolean localFile, boolean localDir,
                             String customSelector) {
        this(name, label, value, basic, localFile, localDir, customSelector, 0);
    }

    /** Creates new VcsConfigVariable
    * @param name the variable name
    * @param label the label of that variable in the Customizer
    * @param value the value of this variable
    * @param basic whether this variable is basic or not. Basic variables can be set in the Customizer.
    * @param localFile whether this variable is a local file. Browse buttom will be created for this variable.
    * @param localDir whether this variable is a local directory. Browse buttom will be created for this variable.
    * @param customSelector the custom selector for this variable. It can be a subclass of <code>VcsAdditionalCommand</code> or an executable.
    * @param order the order of this variable in the Customizer.
    */
    public VcsConfigVariable(String name, String label, String value,
                             boolean basic, boolean localFile, boolean localDir,
                             String customSelector, int order) {
        this.name = name;
        this.label = label;
        this.value = value;
        this.basic = basic;
        this.localFile = localFile;
        this.localDir = localDir;
        this.customSelector = customSelector;
        this.order = order;
        this.executable = false;
        this.labelMnemonic = null; // no mnemonics by default
        this.a11yName = null; // no accessibility name by default
        this.a11yDescription = null; // no accessibility description by default
    }

    public String getName () { return name; }
    public void setName (String name) { this.name = name; }
    
    public String getLabel () { return label;  }
    public void setLabel (String label) { this.label = label;  }
    
    public Character getLabelMnemonic() { return labelMnemonic; }
    public void setLabelMnemonic(Character labelMnemonic) { this.labelMnemonic = labelMnemonic; }
    
    public String getA11yName() { return a11yName; }
    public void setA11yName(String a11yName) { this.a11yName = a11yName; }
    
    public String getA11yDescription() { return a11yDescription; }
    public void setA11yDescription(String a11yDescription) { this.a11yDescription = a11yDescription; }
    
    public String getValue () { return value;  }
    public void setValue (String value) { this.value = value;  }
    
    public boolean isBasic () { return basic;  }
    public void setBasic (boolean basic) { this.basic = basic;  }
    
    public boolean isLocalFile() { return localFile;  }
    public void setLocalFile (boolean localFile) { this.localFile = localFile;  }
    
    public boolean isLocalDir() { return localDir;  }
    public void setLocalDir (boolean localDir) { this.localDir = localDir;  }
    
    public boolean isExecutable() { return executable; }
    public void setExecutable(boolean executable) { this.executable = executable; }
    
    public String getCustomSelector () { return customSelector; }
    public void setCustomSelector (String customSelector) { this.customSelector = customSelector; }
    
    public void setOrder(int order) { this.order = order; }
    public int getOrder() { return this.order; }

    public String toString () {
        String strBasic = ""; // NOI18N
        if (isBasic ()) strBasic = "(basic)"; // NOI18N
        if (isLocalFile ()) strBasic += "(local file)"; // NOI18N
        if (isLocalDir ()) strBasic += "(local directory)"; // NOI18N
        if (isExecutable()) strBasic += "(executable)"; // NOI18N
        return name+"("+label+")"+strBasic+"="+value; // NOI18N
    }

    public Object clone () {
        VcsConfigVariable cloneVar = new VcsConfigVariable (name, label, value, basic, localFile, localDir, customSelector, order);
        cloneVar.setExecutable(isExecutable());
        cloneVar.setLabelMnemonic(getLabelMnemonic());
        return cloneVar;
    }

    /**
     * Sort a vector of commands or variables by the orderArr property.
     * @param commands the commands or variables to sort
     * @return new sorted vector of commands or variables
     */
    public static Vector sortVariables(Vector variables) {
        Vector sorted;
        if (variables == null) return variables;
        Object[] vars = null;
        vars = (Object[]) variables.toArray();
        java.util.Arrays.sort(vars, new Comparator() {
                                  public int compare(Object o1, Object o2) {
                                      if (o1 instanceof VcsConfigVariable)
                                          return ((VcsConfigVariable) o1).getOrder() - ((VcsConfigVariable) o2).getOrder();
                                      return 0; // the elements are not known to me
                                  }
                                  public boolean equals(Object o) {
                                      return false;
                                  }
                              });
        sorted = new Vector();
        for(int i = 0; i < vars.length; i++) {
            sorted.addElement(vars[i]);
        }
        return sorted;
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this object is
     * less than, equal to, or greater than the specified object.
     * <p>
     * This implementation makes basic variables smaller than non-basic,
     * otherwise variables are compared by their names.
     */
    public int compareTo(Object o) {
        VcsConfigVariable var = (VcsConfigVariable) o;
        if (isBasic() && !var.isBasic()) return -1;
        if (!isBasic() && var.isBasic()) return +1;
        return getName().compareTo(var.getName());
    }
    
}

