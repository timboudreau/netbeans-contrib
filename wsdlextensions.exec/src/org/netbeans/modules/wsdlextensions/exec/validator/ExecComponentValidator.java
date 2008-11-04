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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.wsdlextensions.exec.validator;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.netbeans.modules.xml.wsdl.model.BindingOperation;
import org.netbeans.modules.xml.wsdl.model.Operation;
import org.netbeans.modules.xml.wsdl.model.OperationParameter;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.wsdl.model.Part;

import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;
import org.netbeans.modules.xml.xam.Reference;
import org.netbeans.modules.xml.xam.spi.Validation;
import org.netbeans.modules.xml.xam.spi.Validation.ValidationType;
import org.netbeans.modules.xml.xam.spi.ValidationResult;
import org.netbeans.modules.xml.xam.spi.Validator;
import org.netbeans.modules.xml.xam.spi.Validator.ResultItem;

import org.netbeans.modules.wsdlextensions.exec.ExecOperation;
import org.netbeans.modules.wsdlextensions.exec.ExecMessage;
import org.netbeans.modules.wsdlextensions.exec.ExecBinding;
import org.netbeans.modules.wsdlextensions.exec.ExecAddress;
import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.BindingInput;
import org.netbeans.modules.xml.wsdl.model.BindingOutput;
import org.netbeans.modules.xml.wsdl.model.Definitions;
import org.netbeans.modules.xml.wsdl.model.Port;
import org.netbeans.modules.xml.wsdl.model.Service;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;

/**
 * JMSComponentValidator
 * semantic validation, check WSDL elements & attributes values and 
 * any relationship between;
 *
 * 
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.xml.xam.spi.Validator.class)
public class ExecComponentValidator
        implements Validator {
    
    private static final ResourceBundle mMessages =
            ResourceBundle.getBundle("org.netbeans.modules.wsdlextensions.exec.validator.Bundle");
    
    private Validation mValidation;
    private ValidationType mValidationType;
    private ValidationResult mValidationResult;
    
    public static final ValidationResult EMPTY_RESULT = 
        new ValidationResult( Collections.EMPTY_SET, 
                Collections.EMPTY_SET);
    
    public ExecComponentValidator() {}
    
    /**
     * Returns name of this validation service.
     */
    public String getName() {
        return getClass().getName();
    }
    
    /**
     * Validates given model.
     *
     * @param model model to validate.
     * @param validation reference to the validation context.
     * @param validationType the type of validation to perform
     * @return ValidationResult.
     */
    public ValidationResult validate(Model model, Validation validation,
            ValidationType validationType) {
        mValidation = validation;
        mValidationType = validationType;
        
        HashSet<ResultItem> results = new HashSet<ResultItem>();
        HashSet<Model> models = new HashSet<Model>();
        models.add(model);
        mValidationResult = new ValidationResult(results, models);

        if (model instanceof WSDLModel) {
            WSDLModel wsdlModel = (WSDLModel)model;
            
            if (model.getState() == Model.State.NOT_WELL_FORMED) {
                return EMPTY_RESULT;
            }
            
            Definitions defs = wsdlModel.getDefinitions();
            
            Iterator<Service> services = defs.getServices().iterator();
            while (services.hasNext()) {
                Iterator<Port> ports = services.next().getPorts().iterator();
                while (ports.hasNext()) {
                    Port port = ports.next();
                }
            }            
            
            Iterator<Binding> bindings = defs.getBindings().iterator();            
            while (bindings.hasNext()) {
                Binding binding = bindings.next();
                
                if (binding.getType() == null || binding.getType().get() == null) {
                    continue;
                }
                
                int numExecBindings =
                    binding.getExtensibilityElements(ExecBinding.class).size();
                if (numExecBindings == 0) {
                    continue;
                }
                
                if (numExecBindings > 0 && numExecBindings != 1) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            getMessage("ExecBindingValidation.ONLY_ONE_EXEC_BINDING_ALLOWED",
                                       new Object[]{binding.getName(), 
                                                    new Integer(numExecBindings)})));
                }

                Iterator<BindingOperation> bindingOps =
                        binding.getBindingOperations().iterator();
                boolean foundExecOp = false;
                int msgCnt = 0;
                while (bindingOps.hasNext()) {
                    BindingOperation bindingOp = bindingOps.next();
                    List<ExecOperation> execOpsList = bindingOp.getExtensibilityElements(ExecOperation.class);
                    Iterator<ExecOperation> execOps = execOpsList.iterator();
                    
                    // there should only be one exec:operation for the binding operation
                    if (execOpsList.size() > 1) {
                        results.add(
                                new Validator.ResultItem(this,
                                Validator.ResultType.ERROR,
                                bindingOp,
                                getMessage("ExecBindingValidation.ONLY_ONE_EXEC_OPERATION_ALLOWED",
                                           new Object[]{binding.getName(),
                                                        bindingOp.getName(),
                                                        new Integer(execOpsList.size())})));
                    }
                    
                    // validate all anyways if more than one is found
                    while (execOps.hasNext()) {
                        validate(bindingOp, execOps.next());
                    }
                    
                    if(execOpsList.size() > 0) {
                        foundExecOp = true;
//                        if (bindingOp.getBindingInput() != null
//                                && bindingOp.getBindingOutput() != null) {
//                            results.add(
//                                    new Validator.ResultItem(this,
//                                    Validator.ResultType.ERROR,
//                                    bindingOp,
//                                    getMessage("ExecBindingValidation.MUST_BE_ONEWAY_OPERATION", 
//                                                new Object[] {binding.getName(),
//                                                              bindingOp.getName()})));
//                            continue;
//                        }
                    }
                }
                // there is exec:binding but no exec:operation
                if ( numExecBindings > 0 && !foundExecOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            getMessage("ExecBindingValidation.MISSING_EXEC_OPERATION",
                                       new Object[]{binding.getName()})));
                }
                // there is no exec:binding but there are exec:operation
                if ( numExecBindings == 0 && foundExecOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            getMessage("ExecBindingValidation.EXEC_OPERATION_WO_EXEC_BINDING",
                                       new Object[]{binding.getName()})));
                }
            }
        }
        // Clear out our state
        mValidation = null;
        mValidationType = null;
        

        return mValidationResult;
    }

    private void validate(ExecAddress target) {
        //TODO: validation not implemented
    }

    private void validate(ExecBinding target) {
    }

    private void validate(BindingOperation bindingOp, 
                          ExecOperation target) {
        Collection<ResultItem> results =
                mValidationResult.getValidationResult();
        
        if (target.getCommand() == null || target.getCommand().length() == 0) {
            results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            bindingOp,
                            getMessage("ExecBindingValidation.EXEC_OPERATION_WO_COMMAND",
                                       new Object[]{bindingOp.getName()})));
        }

        BindingInput bindingIn = bindingOp.getBindingInput();
        if (bindingIn != null) {
            List<ExecMessage> msgList = 
                bindingIn.getExtensibilityElements(ExecMessage.class);
            if (msgList.size() == 0) {
                results.add(new Validator.ResultItem(this,
                                Validator.ResultType.ERROR,
                                bindingIn,
                                getMessage("ExecBindingValidation.MISSING_EXEC_MESSAGE",
                                           new Object[]{bindingIn.getName()})));
            }
        }        
        BindingOutput bindingOut = bindingOp.getBindingOutput();
        if (bindingOut != null) {
            List<ExecMessage> msgList = 
                bindingOut.getExtensibilityElements(ExecMessage.class);
            if (msgList.size() == 0) {
                results.add(new Validator.ResultItem(this,
                                Validator.ResultType.ERROR,
                                bindingOut,
                                getMessage("ExecBindingValidation.MISSING_EXEC_MESSAGE",
                                           new Object[]{bindingOut.getName()})));
            }
        }
    }

    private void validate(BindingOperation bindingOp,
                          OperationParameter opParam, 
                          ExecMessage target) {
    }

    private String getMessage(String key, String param) {
        return getMessage(key, new Object[] {param});
    }
    
    private String getMessage(String key, Object[] params) {
        String fmt = mMessages.getString(key);
        if ( params != null ) {
            return MessageFormat.format(fmt, params);
        } else {
            return fmt;
        }
    }
}
