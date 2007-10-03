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

import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import org.openide.ErrorManager;

/**
 * This class validates the variable input components and provides the result
 * of that validation.
 *
 * @author  Martin Entlicher
 */
public class VariableInputValidator extends Object {

    public static final String VALIDATOR = "VALIDATOR_";
    public static final String VALIDATOR_NON_EMPTY = VALIDATOR + "NON_EMPTY";
    public static final String VALIDATOR_REGEXP_MATCH = VALIDATOR + "REGEXP_MATCH(";
    public static final String VALIDATOR_REGEXP_UNMATCH = VALIDATOR + "REGEXP_UNMATCH(";
    public static final String VALIDATOR_FILE = VALIDATOR + "FILE";
    public static final String VALIDATOR_FOLDER = VALIDATOR + "FOLDER";
    public static final String VALIDATOR_JAVA = VALIDATOR + "JAVA";

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
            } else if (validator.startsWith(VALIDATOR_REGEXP_MATCH)) {
                validateRegExpMatch(component, validator, true);
            } else if (validator.startsWith(VALIDATOR_REGEXP_UNMATCH)) {
                validateRegExpMatch(component, validator, false);
            } else if (VALIDATOR_FILE.equals(validator)) {
                validateFile(component);
            } else if (VALIDATOR_FOLDER.equals(validator)) {
                validateFolder(component);
            } else if (validator.startsWith(VALIDATOR_JAVA)) {
                String arg = validator.substring(VALIDATOR_JAVA.length());
                validateByJava(component, arg);
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
            if (component.getComponent() == VariableInputDescriptor.INPUT_PROMPT_AREA) {
                valid = checkNonEmptyFile(component);
            } else {
                valid = true;
            }
        }
    }
    
    private boolean checkNonEmptyFile(VariableInputComponent component) {
        String path = component.getValue();
        java.io.File file = new java.io.File(path);
        if (!file.exists()) {
            message = g("VariableInputValidator.FileDoesNotExist", component.getLabel(), file.getPath());
            variable = component.getVariable();
            return false;
        } else {
            java.io.Reader r = null;
            try {
                r = new java.io.FileReader(file);
                if (r.read() >= 0) {
                    return true;
                } else {
                    message = g("VariableInputValidator.NotEmpty", component.getLabel());
                    variable = component.getVariable();
                    return false;
                }
            } catch (java.io.IOException ioex) {
                message = ioex.getLocalizedMessage();
                variable = component.getVariable();
                return false;
            } finally {
                if (r != null) {
                    try {
                        r.close();
                    } catch (java.io.IOException ioex) {}
                }
            }
        }
    }

    /**
     * Using reflection executes custom static method that takes one string
     * and returns string (or null on success).
     */
    private void validateByJava(VariableInputComponent component, String classMethod) {
        String value = component.getValue();

        int index = classMethod.lastIndexOf('.');
        String methodName = classMethod.substring(index+1, classMethod.length()).trim();
        String className = classMethod.substring(0, index).trim();

        String result = null;
        try {
            Lookup lookup = Lookup.getDefault();
            ClassLoader loader = (ClassLoader) lookup.lookup(ClassLoader.class);
            Class validatorClass = Class.forName(className, true, loader);
            Class[] params = new Class[] {String.class};
            Method validatorMethod = validatorClass.getMethod(methodName, params);
            try {
                result = (String) validatorMethod.invoke(null, new Object[] {value});
            } catch (ClassCastException ex) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(ex, "VALIDATOR_JAVA method must be public static String name(String val) "  + classMethod); // NOI18N
                err.notify(ex);
            }
        } catch (ClassNotFoundException e) {
            ErrorManager err = ErrorManager.getDefault();
            err.annotate(e, "Invalid VALIDATOR_JAVA class " + classMethod);  // NOI18N
            err.notify(e);
        } catch (NoSuchMethodException e) {
            ErrorManager err = ErrorManager.getDefault();
            err.annotate(e, "VALIDATOR_JAVA method must be public static String name(String val) "  + classMethod); // NOI18N
            err.notify(e);
        } catch (IllegalAccessException e) {
            ErrorManager err = ErrorManager.getDefault();
            err.notify(e);
        } catch (InvocationTargetException e) {
            ErrorManager err = ErrorManager.getDefault();
            err.annotate(e, "Exception in VALIDATOR_JAVA method " + classMethod);
            err.notify(e);
        }

        if (result != null) {
            valid = false;
            message = result;
            variable = component.getVariable();
        } else {
            valid = true;
        }
    }

    private void validateFile(VariableInputComponent component) {
        String value = component.getValue();
        if (value == null) {
            valid = false;
            message = g("VariableInputValidator.FileDoesNotExist", component.getLabel(), ""+value);
            variable = component.getVariable();
            return ;
        }
        java.io.File file = new java.io.File(value);
        if (!file.isAbsolute() || !file.exists()) {
            valid = false;
            message = g("VariableInputValidator.FileDoesNotExist", component.getLabel(), file.getPath());
            variable = component.getVariable();
        } else {
            valid = true;
        }
    }
    
    private void validateFolder(VariableInputComponent component) {
        String value = component.getValue();
        if (value == null) {
            valid = false;
            message = g("VariableInputValidator.FolderDoesNotExist", component.getLabel(), ""+value);
            variable = component.getVariable();
            return ;
        }
        java.io.File file = new java.io.File(value);
        if (!file.isAbsolute() || !file.isDirectory()) {
            valid = false;
            message = g("VariableInputValidator.FolderDoesNotExist", component.getLabel(), file.getPath());
            variable = component.getVariable();
        } else {
            valid = true;
        }
    }
    
    private void validateRegExpMatch(VariableInputComponent component, String validator, boolean match) {
        int index = validator.lastIndexOf(')');
        if (index < 0) index = validator.length();
        String regExp;
        if (match) {
            regExp = validator.substring(VALIDATOR_REGEXP_MATCH.length(), index);
        } else {
            regExp = validator.substring(VALIDATOR_REGEXP_UNMATCH.length(), index);
        }
        valid = validateRegExpMatch(component, regExp);
        if (message != null) valid = false;
        else if (valid != match) {
            if (match) {
                message = g("VariableInputValidator.RegExpNotMatched", regExp, component.getLabel());
            } else {
                message = g("VariableInputValidator.RegExpNotUnmatched", regExp, component.getLabel());
            }
            variable = component.getVariable();
            valid = false;
        } else {
            valid = true;
        }
    }
    
    private boolean validateRegExpMatch(VariableInputComponent component, String regExpStr) {
        String value = component.getValue();
        if (value == null) value = "";
        try {
            return Pattern.matches(regExpStr, value);
        } catch (PatternSyntaxException exc) {
            message = g("VariableInputValidator.BadRegExp", regExpStr, component.getLabel(), exc.getLocalizedMessage());
            variable = component.getVariable();
            return false;
        }
    }
    
    protected void setValid(boolean valid) {
        this.valid = valid;
    }

    /** Tells whether the validation was successfull.
     */
    public boolean isValid() {
        return valid;
    }
    
    protected void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Get the validadtion message.
     * @return the localized validation message or null, when no validation is available.
     */
    public String getMessage() {
        return message;
    }
    
    protected void setVariable(String variable) {
        this.variable = variable;
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

    private static String g(String pattern, Object obj, Object obj2) {
        return MessageFormat.format(
                NbBundle.getBundle(VariableInputValidator.class).getString(pattern),
                new Object[] { obj, obj2 }
            );
    }

    private static String g(String pattern, Object obj, Object obj2, Object obj3) {
        return MessageFormat.format(
                NbBundle.getBundle(VariableInputValidator.class).getString(pattern),
                new Object[] { obj, obj2, obj3 }
            );
    }
}
