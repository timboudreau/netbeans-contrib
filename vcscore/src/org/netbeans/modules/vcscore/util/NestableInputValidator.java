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

package org.netbeans.modules.vcscore.util;

/**
 * Validator implementation delegating to NestableInputComponnet
 *
 * @author Petr Kuzel
 */
final class NestableInputValidator extends VariableInputValidator {

    private final VariableInputComponent nestedComponent;

    public NestableInputValidator(VariableInputComponent component) {
        super(component, "NestableInputValidator.class");  // NOI18N
        nestedComponent = component;
    }

    public String getMessage() {
        String varName = nestedComponent.getVariable();
        NestableInputComponent egg = nestedComponent.getNestableComponent(false);
        return egg.getVerificationMessage(varName);
    }

    public boolean isValid() {
        String varName = nestedComponent.getVariable();
        NestableInputComponent egg = nestedComponent.getNestableComponent(false);
        return egg.getVerificationMessage(varName) == null;
    }
}
