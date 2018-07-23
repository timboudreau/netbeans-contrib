/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.util;

import java.awt.Dimension;
import java.util.*;

import org.netbeans.modules.vcscore.Variables;
import org.openide.ErrorManager;

import javax.swing.*;

/**
 * The representation of component for variable input.
 * @author  Martin Entlicher
 */
public class VariableInputComponent extends Object {

    private int component;
    private boolean expert = false;
    private boolean trivial;
    private String variable;
    private String label;
    private Character labelMnemonic;
    private String a11yName = null;
    private String a11yDescription = null;
    private String value = null;
    private String valueSelected = null;
    private String valueUnselected = null;
    private String defaultValue = null;
    private Dimension dimension = null;
    private boolean multiLine = false;
    private HashSet enable = null;
    private HashSet disable = null;
    private String selector = null;
    private String validator = null;
    private String style = null;
    private ArrayList subComponents = null;
    private String[] varConditions = new String[2];
    private String[] selectorVarConditions = new String[2];
    private ArrayList history = new ArrayList();

    /** Use for INPUT_JCOMPONENT components only. */
    private Class componentClass;

    private NestableInputComponent nestableComponent;

    /** Creates new VariableInputComponent */
    public VariableInputComponent(int component, String variable, String label) {
        this(component, variable, label, null);
    }
    
    /** Creates new VariableInputComponent */
    public VariableInputComponent(int component, String variable, String label,
                                  String labelMnemonic) {
        this.component = component;
        this.variable = variable;
        this.label = label;
        if (labelMnemonic != null && labelMnemonic.length() > 0) {
            this.labelMnemonic = new Character(labelMnemonic.charAt(0));
        } else {
            this.labelMnemonic = null;
        }
    }

    /** Creates new VariableInputComponent */
    public VariableInputComponent(int component, String variable, Class componentClass) {
        variable.hashCode();  // NPE check
        this.component = component;
        this.variable = variable;
        this.componentClass = componentClass;
    }

    public int getComponent() {
        return component;
    }
    
    public String getVariable() {
        return variable;
    }
    
    public void setVarConditions(String[] varConditions) {
        this.varConditions = varConditions;
    }
    
    public String[] getVarConditions() {
        return varConditions;
    }
    
    /** Whether the given conditions are fulfilled for the gived map of variable values.
     * @param varConditions the array of variables for empty and non-empty conditions.
     * @param vars the map of variables and their values
     * @return true when the test was successfull, or no conditions were defined, false otherwise
     */
    public static boolean isVarConditionMatch(String[] varConditions, Map vars) {
        boolean is = true;
        if (vars == null) return true;
        if (varConditions[0] != null) {
            String var = (String) vars.get(varConditions[0]);
            if (var != null) var = Variables.expand(vars, var, false);
            is &= (var == null || var.length() == 0);
        }
        if (varConditions[1] != null) {
            String var = (String) vars.get(varConditions[1]);
            if (var != null) var = Variables.expand(vars, var, false);
            is &= (var != null && var.length() > 0);
        }
        return is;
    }
    
    public void setExpert(boolean expert) {
        assert expert == false || trivial == false;
        this.expert = expert;
    }

    /** If true component is vible in expert mode only. */
    public boolean isExpert() {
        return expert;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabelMnemonic(String mnemonic) {
        if (mnemonic != null && mnemonic.length() > 0) {
            this.labelMnemonic = new Character(mnemonic.charAt(0));
        } else {
            this.labelMnemonic = null;
        }
    }
    
    public Character getLabelMnemonic() {
        return labelMnemonic;
    }
    
    public void setA11yName(String name) {
        this.a11yName = name;
    }
    
    public String getA11yName() {
        return a11yName;
    }
    
    public void setA11yDescription(String description) {
        this.a11yDescription = description;
    }
    
    public String getA11yDescription() {
        return a11yDescription;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
//        if (component == VariableInputDescriptor.INPUT_JCOMPONENT) {
//            return getNestableComponent(false).getValue(getVariable());
//        } else {
            return value;
//        }
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
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * @return false for values referencing to variables.
     */
    private boolean canResetDefaultValue() {
        String value = getDefaultValue();
        if (value == null) return true;
        int index = value.indexOf("${");
        if (index < 0) index = value.indexOf("$[?");
        if (index >= 0 && (index == 0 || value.charAt(index - 1) != '\\')) {
            return false;
        }
        return !needsPreCommandPerform();
    }
    
    public boolean needsPreCommandPerform() {
        String value = getDefaultValue();
        if (value != null &&
            (value.indexOf(org.netbeans.modules.vcscore.commands.PreCommandPerformer.INSERT_OUTPUT) >= 0 ||
             value.indexOf(org.netbeans.modules.vcscore.commands.PreCommandPerformer.INSERT_ERROR) >= 0 ||
             value.indexOf(org.netbeans.modules.vcscore.commands.PreCommandPerformer.FILE_OUTPUT) >= 0)) {
            return true;
        }
        return false;
    }
    
    public boolean isExpandableDefaultValue() {
        return !canResetDefaultValue();
    }
    
    public void setValuesAsDefault() {
        if (canResetDefaultValue()) {
            setDefaultValue(getValue());
        }
        VariableInputComponent[] comps = subComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].setValuesAsDefault();
        }
    }
    
    public void setDefaultValues() {
        if (canResetDefaultValue()) {
            setValue(getDefaultValue());
        }
        VariableInputComponent[] comps = subComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].setDefaultValues();
        }
    }
    
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
    
    public Dimension getDimension() {
        return dimension;
    }
    
    public boolean isMultiLine() {
        return multiLine;
    }
    
    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
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
    
    public void setSelectorVarConditions(String[] varConditions) {
        this.selectorVarConditions = varConditions;
    }
    
    public String[] getSelectorVarConditions() {
        return selectorVarConditions;
    }
    
    public void setValidator(String validator) {
        this.validator = validator;
    }
    
    public String getValidator() {
        return validator;
    }
    
    public VariableInputValidator validate() {

        if (component == VariableInputDescriptor.INPUT_JCOMPONENT) {
            return new NestableInputValidator(this);
        } else {
            return new VariableInputValidator(this, validator);
        }
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public String getStyle() {
        return style;
    }
    
    void addValuesToHistory() {
        history.add(value);
        VariableInputComponent[] comps = subComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].addValuesToHistory();
        }
    }
    
    /*
    public List getValueHistory() {
        ArrayList valueHistory = new ArrayList();
        valueHistory.addAll(history);
        return valueHistory;
    }
     */
    public int getHistorySize() {
        return history.size();
    }
    
    public String getHistoryValue(int index) {
        if (index < history.size()) {
            return (String) history.get(index);
        } else {
            return value;
        }
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
    
    public String toString() {
        return toString(false);
    }
    
    /**
     * @param cutCompVar Whether to cut the component name and variable.
     */
    private String toString(boolean cutCompVar) {
        // varConditions, name, expert, variable, label, defaultValue, a11n,
        // validator, selector, dimension, valueSelected, valueUnselected,
        // enable, disable, subComponents
        StringBuffer items = new StringBuffer();
        if (!cutCompVar) {
            putVarConditionsStr(items, varConditions);
            String name = VariableInputDescriptor.getInputIdString(component);
            items.append(name);
            if (expert) {
                items.append(VariableInputDescriptor.INPUT_IS_EXPERT);
            }
            items.append(VariableInputDescriptor.INPUT_STR_ARG_OPEN);
            items.append('\"');
            items.append(variable);
            items.append("\"");
            if (VariableInputDescriptor.INPUT_TEXT == component) {
                Dimension d = getDimension();
                int grid = d.width;
                int gridwidth = d.height;
                if (isMultiLine()) {
                    items.append(", ");
                    items.append(Integer.toString(grid));
                    items.append(", ");
                    items.append(Integer.toString(gridwidth));
                    items.append(", ");
                    items.append("LINE_MULTI");
                } else {
                    if (gridwidth != 3) {
                        items.append(", ");
                        items.append(Integer.toString(grid));
                        items.append(", ");
                        items.append(Integer.toString(gridwidth));
                    } else if (grid != 0) {
                        items.append(", ");
                        items.append(Integer.toString(grid));
                    }
                }
                items.append(")");
                return items.toString();
            }
            items.append(", ");
        }
        items.append('\"');
        items.append(label);
        if (value == null) {
            items.append("\", \"\"");
        } else {
            items.append("\", \"");
            if (VariableInputDescriptor.INPUT_ASK == component) {
                if (value != null && (valueSelected == null && value.equals("true") || value.equals(valueSelected))) {
                    items.append("true");
                } else {
                    items.append("false");
                }
            } else {
                items.append(value);
            }
            items.append('\"');
        }
        if (a11yName != null || a11yDescription != null || labelMnemonic != null) {
            VariableInputDescriptor.appendAccessibility(items, a11yName,
                                                        a11yDescription,
                                                        labelMnemonic);
        }
        if (validator != null) {
            items.append(", \"");
            items.append(validator);
            items.append('\"');
        }
        if (selector != null) {
            items.append(", \"");
            putVarConditionsStr(items, selectorVarConditions);
            items.append(selector);
            items.append('\"');
        }
        if (dimension != null) {
            items.append(", ");
            items.append(dimension.width);
            items.append(", ");
            items.append(dimension.height);
        }
        if (component == VariableInputDescriptor.INPUT_ASK) {
            if (valueSelected != null) {
                items.append(", \"");
                items.append(valueSelected);
                items.append('\"');
                if (valueUnselected != null) {
                    items.append(", \"");
                    items.append(valueUnselected);
                    items.append('\"');
                }
            }
        }
        if (enable != null && !enable.isEmpty()) {
            items.append(", ");
            items.append(VariableInputDescriptor.INPUT_STR_ENABLE +
                         VariableInputDescriptor.INPUT_STR_ARG_OPEN);
            for (Iterator it = enable.iterator(); ; ) {
                String varName = (String) it.next();
                items.append(varName);
                if (it.hasNext()) {
                    items.append(", ");
                } else {
                    break;
                }
            }
            items.append(VariableInputDescriptor.INPUT_STR_ARG_CLOSE);
        }
        if (disable != null && !disable.isEmpty()) {
            items.append(", ");
            items.append(VariableInputDescriptor.INPUT_STR_DISABLE +
                         VariableInputDescriptor.INPUT_STR_ARG_OPEN);
            for (Iterator it = disable.iterator(); ; ) {
                String varName = (String) it.next();
                items.append(varName);
                if (it.hasNext()) {
                    items.append(", ");
                } else {
                    break;
                }
            }
            items.append(VariableInputDescriptor.INPUT_STR_ARG_CLOSE);
        }
        VariableInputComponent[] subComponents = subComponents();
        for (int i = 0; i < subComponents.length; i++) {
            items.append(", [");
            items.append(subComponents[i].toString(true));
            items.append(']');
        }
        if (!cutCompVar) {
            items.append(VariableInputDescriptor.INPUT_STR_ARG_CLOSE);
        }
        return items.toString();
    }
    
    private static void putVarConditionsStr(StringBuffer items, String[] varConditions) {
        if (varConditions[0] != null) {
            items.append(VariableInputDescriptor.IF_VAR_EMPTY_BEGIN);
            items.append(varConditions[0]);
            items.append(VariableInputDescriptor.IF_VAR_EMPTY_END);
        }
        if (varConditions[1] != null) {
            items.append(VariableInputDescriptor.IF_VAR_NON_EMPTY_BEGIN);
            items.append(varConditions[1]);
            items.append(VariableInputDescriptor.IF_VAR_NON_EMPTY_END);
        }
    }

    public void setTrivial(boolean trivial) {
        assert expert == false || trivial == false;
        this.trivial = trivial;
    }

    /** If true commonent can be displayed in trivial mode only. */
    public boolean isTrivial() {
        return trivial;
    }

    /** Instance of JCOMPONENT type of componnet otherwise <code>null</code> */
    public NestableInputComponent getNestableComponent(boolean newInstance) {
        if (nestableComponent == null || newInstance) {
            try {
                Object o = componentClass.newInstance();
                JComponent c = (JComponent) o;
                nestableComponent = (NestableInputComponent) o;
            } catch (InstantiationException e) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(e, "Missing public no arg constructor " + componentClass); // NOI18N
                err.notify(e);
            } catch (IllegalAccessException e) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(e, "Unaccesible public no arg constructor " + componentClass); // NOI18N
                err.notify(e);
            }
        }
        return nestableComponent;
    }
}
