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
 * The descriptor of variable input components.
 * @author  Martin Entlicher
 */
public class VariableInputDescriptor extends Object {

    public static final int INPUT_LABEL = 0;
    public static final int INPUT_PROMPT_FIELD = 1;
    public static final int INPUT_PROMPT_AREA = 2;
    public static final int INPUT_ASK = 3;
    public static final int INPUT_SELECT_RADIO = 4;
    public static final int INPUT_RADIO_BTN = 5;
    public static final int INPUT_SELECT_COMBO = 6;
    public static final int INPUT_COMBO_ITEM = 7;
    public static final int INPUT_GLOBAL = 8;
    
    public static final String INPUT_STR_LABEL = "LABEL";
    public static final String INPUT_STR_PROMPT_FIELD = "PROMPT_FOR";
    public static final String INPUT_STR_PROMPT_AREA = "PROMPT_FOR_FILE";
    public static final String INPUT_STR_ASK = "ASK_FOR";
    public static final String INPUT_STR_SELECT_RADIO = "SELECT_RADIO";
    public static final String INPUT_STR_SELECT_COMBO = "SELECT_COMBO";
    public static final String INPUT_STR_GLOBAL_PARAMS = "GLOBAL_PARAMS";
    public static final String INPUT_STR_GLOBAL_ALL_VARS = "ALL_VARIABLES";
    public static final String INPUT_IS_EXPERT = "_EXPERT";
    
    public static final String INPUT_STR_ENABLE = "ENABLE";
    public static final String INPUT_STR_DISABLE = "DISABLE";
    
    public static final String SELECTOR = "SELECTOR_";
    public static final String SELECTOR_FILE = SELECTOR + "FILE";
    public static final String SELECTOR_DIR = SELECTOR + "DIR";
    public static final String SELECTOR_DATE_CVS = SELECTOR + "DATE_CVS";
    public static final String SELECTOR_CMD = SELECTOR + "CMD_";
    
    public static final String IF_VAR_NON_EMPTY_BEGIN = "IF_VAR_NON_EMPTY\"";
    public static final String IF_VAR_NON_EMPTY_END = "\"_";
    public static final String IF_VAR_EMPTY_BEGIN = "IF_VAR_EMPTY\"";
    public static final String IF_VAR_EMPTY_END = "\"_";

    public static final char INPUT_STR_ARG_OPEN = '(';
    public static final char INPUT_STR_ARG_CLOSE = ')';
    public static final char INPUT_SELECT_STR_ARG_OPEN = '[';
    public static final char INPUT_SELECT_STR_ARG_CLOSE = ']';

    private static HashMap inputMap = null;
    private static final Object inputMapLock = new Object();
    
    private String label;
    private ArrayList components = new ArrayList();
    
    /** Creates new VariableInputDescriptor */
    private VariableInputDescriptor() {
        if (inputMap == null) {
            synchronized (inputMapLock) {
                if (inputMap == null) {
                    inputMap = new HashMap();
                    inputMap.put(INPUT_STR_LABEL, new Integer(INPUT_LABEL));
                    inputMap.put(INPUT_STR_PROMPT_FIELD, new Integer(INPUT_PROMPT_FIELD));
                    inputMap.put(INPUT_STR_PROMPT_AREA, new Integer(INPUT_PROMPT_AREA));
                    inputMap.put(INPUT_STR_ASK, new Integer(INPUT_ASK));
                    inputMap.put(INPUT_STR_SELECT_RADIO, new Integer(INPUT_SELECT_RADIO));
                    inputMap.put(INPUT_STR_SELECT_COMBO, new Integer(INPUT_SELECT_COMBO));
                    inputMap.put(INPUT_STR_GLOBAL_PARAMS, new Integer(INPUT_GLOBAL));
                }
            }
        }
    }
    
    /**
     * Create a new VariableInputDescriptor.
     * @param label the label of that descriptor
     * @param components the components which this descriptor consists of
     */
    public static VariableInputDescriptor create(String label, VariableInputComponent[] components) {
        VariableInputDescriptor descriptor = new VariableInputDescriptor();
        descriptor.label = label;
        descriptor.components.addAll(Arrays.asList(components));
        return descriptor;
    }
    
    /**
     * Parse the string representation of components into the descriptor object.
     * @params inputItems the string representation of input components
     * @return the descriptor object
     * @throws VariableInputFormatException when the parsed string contains some errors
     */
    public static VariableInputDescriptor parseItems(String inputItems) throws VariableInputFormatException {
        VariableInputDescriptor descriptor = new VariableInputDescriptor();
        int index = 0;
        int begin;
        int end = 0;
        do {
            begin = inputItems.indexOf(INPUT_STR_ARG_OPEN, index);
            if (begin < 0) break;
            end = VcsUtilities.getPairIndex(inputItems, begin + 1,
                                            INPUT_STR_ARG_OPEN, INPUT_STR_ARG_CLOSE);
            if (end < 0) break;
            //System.out.println("inputItems = '"+inputItems+"', index = "+index+", begin = "+begin+", end = "+end);
            String inputStr = inputItems.substring(index, begin).trim();
            boolean expert = inputStr.endsWith(INPUT_IS_EXPERT);
            if (expert) inputStr = inputStr.substring(0, inputStr.length() - INPUT_IS_EXPERT.length());
            String[] varConditions = new String[2];
            inputStr = getVarConditions(inputStr, varConditions);
            int inputId = getInputId(inputStr);
            String inputArg = inputItems.substring(begin + 1, end);
            String[] inputArgs = VcsUtilities.getQuotedStrings(inputArg);
            //System.out.println("parseItems: "+inputStr+": "+inputArg);
            if (inputId == INPUT_LABEL && inputArgs.length > 0) {
                descriptor.label = VcsUtilities.getBundleString(inputArgs[0]);
            } else {
                VariableInputComponent component = parseComponent(inputId, inputArgs, inputArg);
                component.setExpert(expert);
                component.setVarConditions(varConditions);
                descriptor.components.add(component);
            }
            index = end + 1;
            while (index < inputItems.length()
                   && Character.isWhitespace(inputItems.charAt(index))) {
                index++;
            }
        } while (index < inputItems.length());
        if (index < inputItems.length()) {
            if (end >= 0) {
                throw new VariableInputFormatException(
                          g("EXC_UnrecognizedItem", new Integer(index)));
            } else {
                throw new VariableInputFormatException(
                          g("EXC_MissingClosingPar", new Integer(begin)));
            }
        }
        return descriptor;
    }
    
    /**
     * Get the label of this input descriptor.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Get the array of components.
     * @return the array of components.
     */
    public VariableInputComponent[] components() {
        return (VariableInputComponent[]) components.toArray(new VariableInputComponent[0]);
    }
    
    /**
     * Check, that the values of all components are valid.
     * @return the validator object describing the validation
     */
    public VariableInputValidator validate() {
        //boolean valid = true;
        VariableInputValidator validator = null;
        VariableInputComponent[] components = components();
        for (int i = 0; i < components.length; i++) {
            validator = components[i].validate();
            if (!validator.isValid()) break;
        }
        if (validator == null) {
            validator = new VariableInputValidator(null, null);
        }
        return validator;
    }
    
    public void addValuesToHistory() {
        VariableInputComponent[] comps = components();
        for (int i = 0; i < comps.length; i++) {
            comps[i].addValuesToHistory();
        }
    }
    
    public void setValuesAsDefault() {
        VariableInputComponent[] comps = components();
        for (int i = 0; i < comps.length; i++) {
            comps[i].setValuesAsDefault();
        }
    }
    
    public void setDefaultValues() {
        VariableInputComponent[] comps = components();
        for (int i = 0; i < comps.length; i++) {
            comps[i].setDefaultValues();
        }
    }

    /** Get the variables, that will be checked for a presence of the input item.
     * @param inputStr the input item
     * @param varConditions the array, that is filled with variable names
     * @return the name of the original input item
     */
    private static String getVarConditions(String inputStr, String[] varConditions) {
        //System.out.println("getVarConditions("+inputStr+")");
        //System.out.println(inputStr+".startsWith("+IF_VAR_EMPTY_BEGIN+") = "+inputStr.startsWith(IF_VAR_EMPTY_BEGIN));
        if (inputStr.startsWith(IF_VAR_EMPTY_BEGIN)) {
            int i = inputStr.indexOf(IF_VAR_EMPTY_END, IF_VAR_EMPTY_BEGIN.length());
            if (i > 0) {
                varConditions[0] = inputStr.substring(IF_VAR_EMPTY_BEGIN.length(), i);
                inputStr = inputStr.substring(i + IF_VAR_EMPTY_END.length());
            }
        }
        //System.out.println(inputStr+".startsWith("+IF_VAR_NON_EMPTY_BEGIN+") = "+inputStr.startsWith(IF_VAR_NON_EMPTY_BEGIN));
        if (inputStr.startsWith(IF_VAR_NON_EMPTY_BEGIN)) {
            int i = inputStr.indexOf(IF_VAR_NON_EMPTY_END, IF_VAR_NON_EMPTY_BEGIN.length());
            if (i > 0) {
                varConditions[1] = inputStr.substring(IF_VAR_NON_EMPTY_BEGIN.length(), i);
                inputStr = inputStr.substring(i + IF_VAR_NON_EMPTY_END.length());
            }
        }
        return inputStr;
    }

    private static int getInputId(String inputStr) throws VariableInputFormatException {
        Integer id = (Integer) inputMap.get(inputStr);
        if (id == null) {
            throw new VariableInputFormatException(
                      g("EXC_UnrecognizedComponent", inputStr));
        }
        return id.intValue();
    }
    
    /**
     * Parse the compoent from a string representation.
     * @param id the identification of the component
     * @param inputArgs the array of input arguments 
     * @param inputArg the same input arguments as one string (the array representation may ignore some characters)
     * @throws VariableInputFormatException when the parsed string contains some errors
     */
    private static VariableInputComponent parseComponent(int id, String[] inputArgs, String inputArg) throws VariableInputFormatException {
        //System.out.println("parseComponent("+id+", "+VcsUtilities.arrayToString(inputArgs)+", "+inputArg+")");
        int len = inputArgs.length;
        if (len < 2) {
            throw new VariableInputFormatException(g("EXC_InsufficientArgs"));
        }
        VariableInputComponent component = new VariableInputComponent(id, inputArgs[0], VcsUtilities.getBundleString(inputArgs[1]));
        if (len >= 3) {
            component.setValue(VcsUtilities.getBundleString(inputArgs[2])); // default variable value
        }
        int argNum = 3; // the number of currently processing argument
        if (len > argNum && inputArgs[argNum].indexOf(VariableInputValidator.VALIDATOR) == 0) {
            String validator = inputArgs[argNum];
            component.setValidator(validator);
            argNum++;
        }
        if (len > argNum && inputArgs[argNum].indexOf(SELECTOR) >= 0) {
            String selector = inputArgs[argNum];
            String[] varConditions = new String[2];
            selector = getVarConditions(selector, varConditions);
            if (selector.indexOf(SELECTOR) == 0) {
                component.setSelector(selector);
                component.setSelectorVarConditions(varConditions);
                argNum++;
            }
        }
        if (INPUT_PROMPT_AREA == id && len >= (argNum + 2)) {
            try {
                int x = Integer.parseInt(inputArgs[argNum]);
                int y = Integer.parseInt(inputArgs[argNum + 1]);
                component.setDimension(new Dimension(x, y));
            } catch (NumberFormatException exc) {
                throw new VariableInputFormatException(exc.getMessage());
            }
            argNum += 2;
        }
        if (INPUT_SELECT_RADIO == id || INPUT_SELECT_COMBO == id) {
            String[] inputSelectArgs = getSelectArgs(inputArgs[0], inputArg, inputArgs[0].length() + inputArgs[1].length());
            int subId;
            if (INPUT_SELECT_RADIO == id) {
                subId = INPUT_RADIO_BTN;
            } else {
                subId = INPUT_COMBO_ITEM;
            }
            for (int i = 0; i < inputSelectArgs.length; i++) {
                VariableInputComponent subComponent = parseComponent(subId, VcsUtilities.getQuotedStrings(inputSelectArgs[i]), inputSelectArgs[i]);
                component.addSubComponent(subComponent);
            }
        }
        //int enableIndex = argNum;
        if (INPUT_ASK == id && len > argNum && inputArgs[argNum].indexOf(INPUT_STR_ENABLE) < 0) {
            component.setValueSelected(inputArgs[argNum]);
            argNum++;
        }
        if (INPUT_ASK == id && len > argNum && inputArgs[argNum].indexOf(INPUT_STR_ENABLE) < 0) {
            component.setValueUnselected(inputArgs[argNum]);
            argNum++;
        }
        if (INPUT_ASK == id) {
            String selected = component.getValueSelected();
            if (selected != null) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(component.getValue())) {
                    component.setValue(component.getValueSelected());
                } else {
                    component.setValue(component.getValueUnselected());
                }
            }
        }
        if (len > argNum && inputArgs[argNum].indexOf(INPUT_STR_ENABLE) == 0) {
            argNum = addEnable(inputArgs, argNum, component);
        }
        if (len > argNum && inputArgs[argNum].indexOf(INPUT_STR_DISABLE) == 0) {
            argNum = addDisable(inputArgs, argNum, component);
        }
        // Radio buttons may have sub components.
        if (INPUT_RADIO_BTN == id) {
            if (len > argNum) {
                int index = inputArg.indexOf(inputArgs[argNum]);
                if (index > 0) {
                    String subInputArg = inputArg.substring(index);
                    VariableInputDescriptor subDescriptor = VariableInputDescriptor.parseItems(subInputArg);
                    VariableInputComponent[] subComponents = subDescriptor.components();
                    for (int i = 0; i < subComponents.length; i++) {
                        component.addSubComponent(subComponents[i]);
                    }
                }
                /*
                int begin = inputArgs[argNum].indexOf(INPUT_STR_ARG_OPEN);
                String inputStr = inputArgs[argNum].substring(0, begin).trim();
                boolean expert = inputStr.endsWith(INPUT_IS_EXPERT);
                if (expert) inputStr = inputStr.substring(0, inputStr.length() - INPUT_IS_EXPERT.length());
                int inputId = getInputId(inputStr);
                int i = argNum + 1;
                for (int open = 1; open != 0 && len > i; i++) {
                    open += VcsUtilities.charCount(inputArgs[i], INPUT_STR_ARG_OPEN);
                    open -= VcsUtilities.charCount(inputArgs[i], INPUT_STR_ARG_CLOSE);
                }
                System.out.println("inputArgs = "+VcsUtilities.arrayToString(inputArgs));
                System.out.println("argNum = "+argNum+", i = "+i+", subArgs.length = "+(i - argNum - 1));
                String[] subArgs = new String[i - argNum - 1];
                System.arraycopy(inputArgs, argNum + 1, subArgs, 0, i - argNum - 1);
                VariableInputComponent subComponent = parseComponent(inputId, subArgs, null);
                subComponent.setExpert(expert);
                component.addSubComponent(subComponent);
                argNum = i;
                 */
            }
        }
        return component;
    }
        
    private static int addEnable(String[] inputArgs, int enableIndex, VariableInputComponent component) {
        //System.out.println("addEnable("+VcsUtilities.arrayToString(inputArgs)+")");
        String var = inputArgs[enableIndex].substring(INPUT_STR_ENABLE.length() + 1).trim();
        String endBracket = new Character(INPUT_STR_ARG_CLOSE).toString();
        do {
            if (var.endsWith(endBracket)) {
                var = var.substring(0, var.length() - 1);
                component.addEnable(var.trim());
                //System.out.println("  addEnable: "+var.trim()+", to component "+component.getLabel());
                break;
            } else {
                component.addEnable(var.trim());
                //System.out.println("  addEnable: "+var.trim()+", to component "+component.getLabel());
                ++enableIndex;
                if (enableIndex >= inputArgs.length) break;
                var = inputArgs[enableIndex].trim();
            }
        } while (true);
        return enableIndex + 1;
    }
    
    private static int addDisable(String[] inputArgs, int enableIndex, VariableInputComponent component) {
        //System.out.println("addDisable("+VcsUtilities.arrayToString(inputArgs)+")");
        String var = inputArgs[enableIndex].substring(INPUT_STR_DISABLE.length() + 1).trim();
        String endBracket = new Character(INPUT_STR_ARG_CLOSE).toString();
        do {
            if (var.endsWith(endBracket)) {
                var = var.substring(0, var.length() - 1);
                component.addDisable(var.trim());
                //System.out.println("  addDisable: "+var.trim()+", to component "+component.getLabel());
                break;
            } else {
                component.addDisable(var.trim());
                //System.out.println("  addDisable: "+var.trim()+", to component "+component.getLabel());
                ++enableIndex;
                if (enableIndex >= inputArgs.length) break;
                var = inputArgs[enableIndex].trim();
            }
        } while (true);
        return enableIndex + 1;
    }
    
    /**
     * Get the select component arguments. These arguments begin with '[' and end with ']'.
     */
    private static String[] getSelectArgs(String varName, String inputArg, int begin) {
        /*
        String varName = inputArgs[0];
        StringBuffer inputBuff = new StringBuffer(inputArgs[begin]);
        for (int i = begin + 1; i < inputArgs.length; i++) {
            inputBuff.append(","+inputArgs[i]);
        }
         */
        String input = inputArg;//inputBuff.toString();
        //System.out.println("getSelectedArgs: '"+input+"'");
        int index = 0;
        ArrayList selectArgsList = new ArrayList();
        do {
            while (index < input.length() && input.charAt(index) != INPUT_SELECT_STR_ARG_OPEN) index++;
            //System.out.println("index = "+index);
            if (index >= input.length()) break;
            int end = VcsUtilities.getPairIndex(input, index + 1, INPUT_SELECT_STR_ARG_OPEN, INPUT_SELECT_STR_ARG_CLOSE);
            //System.out.println("end = "+end);
            if (end < 0) break;
            String selectArg = input.substring(index + 1, end);
            //System.out.println("selectArg = "+selectArg);
            //String[] selectArgs = VcsUtilities.getQuotedStrings(selectArg);
            //List selectList = Arrays.asList(selectArgs);
            //selectList.add(0, varName);
            //System.out.println("selectArgs = '"+VcsUtilities.array2string((String[]) selectList.toArray(new String[0]))+"'");
            selectArgsList.add(varName + ", " + selectArg);//selectList.toArray(new String[0]));
            index = end + 1;
        } while (index < input.length());
        return (String[]) selectArgsList.toArray(new String[0]);
    }

    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(VariableInputDescriptor.class).getString(s);
    }
    
    private static String g(String s, Object obj) {
        return java.text.MessageFormat.format(g(s), new Object[] { obj });
    }
    
}
