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

package org.netbeans.modules.vcscore.util;

import java.awt.Dimension;
import java.util.*;

/**
 * The representation of component for variable input.
 * @author  Martin Entlicher
 */
public class VariableInputComponent extends Object {

    private int component;
    private boolean expert = false;
    private String variable;
    private String label;
    private String value = null;
    private String valueSelected = null;
    private String valueUnselected = null;
    private Dimension dimension = null;
    private HashSet enable = null;
    private HashSet disable = null;
    private String selector = null;
    private String validator = null;
    private ArrayList subComponents = null;
    
    /** Creates new VariableInputComponent */
    public VariableInputComponent(int component, String variable, String label) {
        this.component = component;
        this.variable = variable;
        this.label = label;
    }
    
    public int getComponent() {
        return component;
    }
    
    public String getVariable() {
        return variable;
    }
    
    public void setExpert(boolean expert) {
        this.expert = expert;
    }
    
    public boolean isExpert() {
        return expert;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValueSelected(String valueSelected) {
        this.valueSelected = valueSelected;
    }
    
    public String getValueSelected() {
        return valueSelected;
    }
    
    public void setValueUnselected(String valueUnselected) {
        this.valueUnselected = valueUnselected;
    }
    
    public String getValueUnselected() {
        return valueUnselected;
    }
    
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
    
    public Dimension getDimension() {
        return dimension;
    }
    
    public void addEnable(String varName) {
        if (enable == null) enable = new HashSet();
        enable.add(varName);
    }
    
    public Set getEnable() {
        if (enable != null) {
            return Collections.unmodifiableSet(enable);
        } else {
            return Collections.EMPTY_SET;
        }
    }
    
    public Set getDisable() {
        if (disable != null) {
            return Collections.unmodifiableSet(disable);
        } else {
            return Collections.EMPTY_SET;
        }
    }
    
    public void addDisable(String varName) {
        if (disable == null) disable = new HashSet();
        disable.add(varName);
    }
    
    public void setSelector(String selector) {
        this.selector = selector;
    }
    
    public String getSelector() {
        return selector;
    }
    
    public void setValidator(String validator) {
        this.validator = validator;
    }
    
    public String getValidator() {
        return validator;
    }
    
    public VariableInputValidator validate() {
        return new VariableInputValidator(this, validator);
    }
    
    public void addSubComponent(VariableInputComponent component) {
        if (subComponents == null) subComponents = new ArrayList();
        subComponents.add(component);
    }
    
    public VariableInputComponent[] subComponents() {
        if (subComponents == null) {
            return new VariableInputComponent[0];
        } else {
            return (VariableInputComponent[]) subComponents.toArray(new VariableInputComponent[0]);
        }
    }
}
