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

package org.netbeans.modules.vcs.advanced.conditioned;

import java.util.Comparator;
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
    
    public static final class IfUnlessComparator extends Object implements Comparator {
        
        public int compare(Object o1, Object o2) {
            IfUnlessCondition c1 = (IfUnlessCondition) o1;
            IfUnlessCondition c2 = (IfUnlessCondition) o2;
            String[] ifUnless1 = retrieveIfUnless(c1); // IF and UNLESS var names
            String[] ifUnless2 = retrieveIfUnless(c2); // IF and UNLESS var names
            if (!ifUnless1[0].equals(ifUnless2[0])) {
                return ifUnless1[0].compareTo(ifUnless2[0]); // Compare the IF attributes first
            } else {
                return ifUnless1[1].compareTo(ifUnless2[1]); // If IF are the same then UNLESS
            }
        }
        
        private static String[] retrieveIfUnless(IfUnlessCondition c) {
            String[] ifUnless = new String[2];
            ifUnless[0] = c.getIf();
            if (ifUnless[0] == null) ifUnless[0] = "";
            ifUnless[1] = c.getUnless();
            if (ifUnless[1] == null) ifUnless[1] = "";
            return ifUnless;
        }
        
    }
}
