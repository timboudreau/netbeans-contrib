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

import java.text.MessageFormat;

import org.openide.util.NbBundle;

/**
 * This class validates the variable input components and provides the result
 * of that validation.
 *
 * @author  Martin Entlicher
 */
public class VariableInputValidator extends Object {

    public static final String VALIDATOR = "VALIDATOR_";
    public static final String VALIDATOR_NON_EMPTY = VALIDATOR + "NON_EMPTY";

    private boolean valid;
    private String message = null;
    private String variable = null;
        
    /**
     * Creates new VariableInputValidator object
     */
    public VariableInputValidator(VariableInputComponent component, String validator) {
        if (validator == null) valid = true;
        else {
            if (VALIDATOR_NON_EMPTY.equals(validator)) {
                validateNonEmpty(component);
            } else {
                valid = false;
                message = g("VariableInputValidator.BadValidator", validator);
            }
        }
    }
    
    private void validateNonEmpty(VariableInputComponent component) {
        String value = component.getValue();
        if (value == null || value.length() == 0) {
            valid = false;
            message = g("VariableInputValidator.NotEmpty", component.getLabel());
            variable = component.getVariable();
        } else {
            valid = true;
        }
    }

    /** Tells whether the validation was successfull.
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Get the validadtion message.
     * @return the localized validation message or null, when no validation is available.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Get the name of variable where validation has failed.
     * @return the name of variable or null, when no variable is available.
     */
    public String getVariable() {
        return variable;
    }
    
    private static String g(String pattern, Object obj) {
        return MessageFormat.format(
                NbBundle.getBundle(VariableInputValidator.class).getString(pattern),
                new Object[] { obj }
            );
    }
}
