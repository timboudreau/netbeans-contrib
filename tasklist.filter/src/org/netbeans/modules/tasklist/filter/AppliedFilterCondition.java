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

package org.netbeans.modules.tasklist.filter;

/**
 * This class represents a filter condition applied to a property.
 */
public final class AppliedFilterCondition {

    private SuggestionProperty prop;
    private FilterCondition cond;

    public AppliedFilterCondition(SuggestionProperty property, FilterCondition condition) {
        this.prop = property;
        this.cond = condition;
    }

    public Object clone() {
        return new AppliedFilterCondition(prop, (FilterCondition)cond.clone());
    }

    public SuggestionProperty getProperty() { return prop;}
    public FilterCondition getCondition() { return cond;}
    
    /**
     * Tests if the condition is true on the property of task.
     * @param task the object to filter
     * @return true, if value of the property of <code>task</code>
     * defined by getProperty() passed the condition getCondition()
     */
    public boolean isTrue(Object task) {
        return cond.isTrue(prop.getValue(task));
    }
    
    public String toString() {
        return cond.toString() + " applied to " + prop.toString();
    }
    
    /**
     * Checks whether afc is of the same type.
     * This method will be used to replace a condition created with
     * Filter.getConditionsFor(Node.Property) with one contained in a filter.
     * This method should return true also if this and fc have different
     * constants for comparing with property values.
     *
     * @param fc another condition
     * @return true fc is of the same type as this
     */
    public boolean sameType(AppliedFilterCondition afc) {
        return getCondition().sameType(afc.getCondition()) && getProperty().equals(afc.getProperty());
    }
    
}
