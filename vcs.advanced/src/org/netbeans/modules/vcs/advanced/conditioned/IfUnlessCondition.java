/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.conditioned;

import org.netbeans.modules.vcs.advanced.variables.Condition;

/**
 * Representation of pair of conditions: if and unless.
 *
 * @author  Martin Entlicher
 */
public class IfUnlessCondition extends Object {
    
    private String cName = "";
    private Condition c;
    private Condition.Var varIf;
    private Condition.Var varUnless;

    /** Creates a new instance of IfUnlessCondition */
    public IfUnlessCondition(Condition c) {
        this.c = c;
        if (c != null) {
            Condition.Var[] cvars = c.getVars();
            for (int i = 0; i < cvars.length; i++) {
                if (c.isPositiveTest(cvars[i])) { // true when compared to "" => unless
                    varUnless = cvars[i];
                } else { // false when compared to "" => if
                    varIf = cvars[i];
                }
            }
            cName = c.getName();
        }
    }
    
    public Condition getCondition() {
        return c;
    }
    
    public void setConditionName(String cName) {
        this.cName = cName;
    }
    
    public String getIf() {
        return (varIf != null) ? varIf.getName() : "";
    }

    public String getUnless() {
        return (varUnless != null) ? varUnless.getName() : "";
    }
    
    public void setIf(String strIf) {
        if (strIf == null || strIf.equals("")) {
            if (varIf != null) {
                c.removeVar(varIf);
                varIf = null;
                if (varUnless == null) {
                    c = null;
                }
            }
        } else {
            if (varIf != null) {
                varIf.setName(strIf);
            } else {
                if (c == null) {
                    c = new Condition(cName);
                }
                varIf = new Condition.Var(strIf, "", Condition.COMPARE_VALUE_EQUALS);
                c.addVar(varIf, false); // Does not equal to ""
            }
        }
    }
    
    public void setUnless(String strUnless) {
        if (strUnless == null || strUnless.equals("")) {
            if (varUnless != null) {
                c.removeVar(varUnless);
                varUnless = null;
                if (varIf == null) {
                    c = null;
                }
            }
        } else {
            if (varUnless != null) {
                varUnless.setName(strUnless);
            } else {
                if (c == null) {
                    c = new Condition(cName);
                }
                varUnless = new Condition.Var(strUnless, "", Condition.COMPARE_VALUE_EQUALS);
                c.addVar(varUnless, true); // Equals to ""
            }
        }
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof IfUnlessCondition)) return false;
        IfUnlessCondition iuc = (IfUnlessCondition) obj;
        Condition c = iuc.getCondition();
        return (this.c == c || this.c != null && this.c.equals(c));
    }
    
    public int hashCode() {
        if (c == null) return 0;
        else return c.hashCode();
    }

    public String toString() {
        String strIf = (varIf != null) ? varIf.getName() : null;
        String strUnless = (varUnless != null) ? varUnless.getName() : null;
        if (strIf != null && strUnless != null) {
            return strIf + " && !"+strUnless;
        } else if (strIf != null) {
            return strIf;
        } else if (strUnless != null) {
            return "!"+strUnless;
        } else {
            return org.openide.util.NbBundle.getMessage(IfUnlessCondition.class, "IfUnlessCondition.Default");
        }
    }
}
