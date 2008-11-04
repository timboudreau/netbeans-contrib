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
package org.netbeans.modules.wsdlextensions.dcom.validator;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.text.MessageFormat;

import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.BindingInput;
import org.netbeans.modules.xml.wsdl.model.BindingFault;
import org.netbeans.modules.xml.wsdl.model.BindingOperation;
import org.netbeans.modules.xml.wsdl.model.BindingOutput;
import org.netbeans.modules.xml.wsdl.model.Definitions;
import org.netbeans.modules.xml.wsdl.model.Input;
import org.netbeans.modules.xml.wsdl.model.Output;
import org.netbeans.modules.xml.wsdl.model.Port;
import org.netbeans.modules.xml.wsdl.model.Service;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;

import org.netbeans.modules.xml.xam.Component;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.Model.State;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.xam.spi.Validation;
import org.netbeans.modules.xml.xam.spi.Validation.ValidationType;
import org.netbeans.modules.xml.xam.spi.ValidationResult;
import org.netbeans.modules.xml.xam.spi.Validator;
import org.netbeans.modules.xml.xam.spi.Validator.ResultItem;

import org.netbeans.modules.wsdlextensions.dcom.DCOMComponent;
import org.netbeans.modules.wsdlextensions.dcom.DCOMOperation;
import org.netbeans.modules.wsdlextensions.dcom.DCOMMessage;
import org.netbeans.modules.wsdlextensions.dcom.DCOMBinding;
import org.netbeans.modules.wsdlextensions.dcom.DCOMAddress;

/**
 * semantic validation, check WSDL elements & attributes values and
 * any relationship between;
 *
 * @author Chandrakanth Belde
 *
 */

@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.xml.xam.spi.Validator.class)
public class DCOMComponentValidator
        implements Validator, DCOMComponent.Visitor {

    private static final ResourceBundle mMessages =
            ResourceBundle.getBundle("org.netbeans.modules.wsdlextensions.dcom.validator.Bundle");

    private Validation mValidation;

    private ValidationType mValidationType;

    private ValidationResult mValidationResult;

    public static final ValidationResult EMPTY_RESULT = 
			new ValidationResult( Collections.EMPTY_SET, Collections.EMPTY_SET);

    public DCOMComponentValidator() {}

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

        // Traverse the model
        if (model instanceof WSDLModel) {
            WSDLModel wsdlModel = (WSDLModel)model;

            if (model.getState() == State.NOT_WELL_FORMED) {
                return EMPTY_RESULT;
            }

            Definitions defs = wsdlModel.getDefinitions();
            Iterator<Binding> bindings = defs.getBindings().iterator();

            while (bindings.hasNext()) {
                Binding binding = bindings.next();
				if (binding.getType() == null || binding.getType().get() == null) {
					continue;
                }

                // This validation will be taken care of by the WSDL Editor generically

			/*

				PortType portType = binding.getType().get();

				if (portType == null) {
					results.add(
							new Validator.ResultItem(this,
							Validator.ResultType.ERROR,
							binding,
							getMessage("DCOMBindingValidation.BINDING_REFERENCES_INVALID_PORTTYPE",
									   new Object[]{binding.getName()})));
					continue;
				}

			*/

                int numDCOMBindings = binding.getExtensibilityElements(DCOMBinding.class).size();

				if (numDCOMBindings == 0) {
					continue;
                }

                if (numDCOMBindings > 0 && numDCOMBindings != 1) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("DCOMBindingValidation.ONLY_ONE_DCOM_BINDING_ALLOWED")));
                    continue;
                }

                Iterator<BindingOperation> bindingOps =
                        binding.getBindingOperations().iterator();
                boolean foundDCOMOp = false;
                while (bindingOps.hasNext()) {
                    BindingOperation bindingOp = bindingOps.next();
                    List dcomOpsList = bindingOp.getExtensibilityElements(DCOMOperation.class);
                    Iterator<DCOMOperation> dcomOps = dcomOpsList.iterator();
                    while (dcomOps.hasNext()) {
                        dcomOps.next().accept(this);
                    }

                    if(dcomOpsList.size() > 0) {
                        foundDCOMOp = true;
                        BindingInput bindingInput = bindingOp.getBindingInput();
                        if (bindingInput != null) {
                            int inputMessageCnt = 0;
                            Iterator<DCOMMessage> dcomMessages =
                                    bindingInput.getExtensibilityElements(DCOMMessage.class).iterator();
                            while (dcomMessages.hasNext()) {
                                inputMessageCnt++;
                                DCOMMessage dcomMessage = dcomMessages.next();
                                dcomMessage.accept(this);
                            }
                            if ( inputMessageCnt > 1 ) {
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        binding,
                                        mMessages.getString("DCOMBindingValidation.ATMOST_ONE_MESSAGE_IN_INPUT") + " " + inputMessageCnt));
                            }
                        }

                        BindingOutput bindingOutput = bindingOp.getBindingOutput();
                        if (bindingOutput != null) {
                            int outputMessageCnt = 0;
                            Iterator<DCOMMessage> dcomMessages =
                                    bindingOutput.getExtensibilityElements(DCOMMessage.class).iterator();
                            while (dcomMessages.hasNext()) {
                                outputMessageCnt++;
                                DCOMMessage dcomMessage = dcomMessages.next();
                                dcomMessage.accept(this);
                            }
                            if ( outputMessageCnt > 1 ) {
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        binding,
                                        mMessages.getString("DCOMBindingValidation.ATMOST_ONE_MESSAGE_IN_OUTPUT") + " " + outputMessageCnt));
                            }
                        }
                    }
                }
                // there is dcom:binding but no dcom:operation
                if ( numDCOMBindings > 0 && !foundDCOMOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("DCOMBindingValidation.MISSING_DCOM_OPERATION")));
                }
                // there is no dcom:binding but there are dcom:operation
                if ( numDCOMBindings == 0 && foundDCOMOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("DCOMBindingValidation.DCOM_OPERATION_WO_DCOM_BINDING")));
                }
            }

            Iterator<Service> services = defs.getServices().iterator();
            while (services.hasNext()) {
                Iterator<Port> ports = services.next().getPorts().iterator();
                while (ports.hasNext()) {
                    Port port = ports.next();
                    if(port.getBinding() != null) {
                        Binding binding = port.getBinding().get();
                        if(binding != null) {
                            int numRelatedDCOMBindings = binding.getExtensibilityElements(DCOMBinding.class).size();
                            Iterator<DCOMAddress> dcomAddresses = port.getExtensibilityElements(DCOMAddress.class).iterator();
                            if((numRelatedDCOMBindings > 0) && (!dcomAddresses.hasNext())){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("DCOMExtValidation.MISSING_DCOM_ADDRESS")));
                            }

                            if(port.getExtensibilityElements(DCOMAddress.class).size() > 1){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("DCOMExtValidation.ONLY_ONE_DCOMADDRESS_ALLOWED")));
                            }
                            while (dcomAddresses.hasNext()) {
                                dcomAddresses.next().accept(this);
                            }
                        }
                    }
                }
            }
        }
        // Clear out our state
        mValidation = null;
        mValidationType = null;

        return mValidationResult;
    }

    public void visit(DCOMAddress target) {

		Collection<ResultItem> results =
			mValidationResult.getValidationResult();  

        String domain = target.getDCOMDOMAIN();
        if(domain.contains("provide value for this")){
			results.add(new Validator.ResultItem(this,
							Validator.ResultType.ERROR,
							target,
							mMessages.getString("DCOMAddress.DCOM_DOMAIN_MISSING")));
        }

		String server = target.getDCOMSERVER();
        if(server.contains("provide value for this")){
			results.add(new Validator.ResultItem(this,
							Validator.ResultType.ERROR,
							target,
							mMessages.getString("DCOMAddress.DCOM_SERVER_MISSING")));
        }

        String user = target.getDCOMUSERNAME();
        if(user.contains("provide value for this")){
			results.add(new Validator.ResultItem(this,
							Validator.ResultType.ERROR,
							target,
							mMessages.getString("DCOMAddress.DCOM_USERNAME_MISSING")));
        }

        String password = target.getDCOMPASSWORD();
        if(password.contains("provide value for this")){
			results.add(new Validator.ResultItem(this,
							Validator.ResultType.ERROR,
							target,
							mMessages.getString("DCOMAddress.DCOM_PASSWORD_MISSING")));
        }

    }

    public void visit(DCOMBinding target) {
        Collection<ResultItem> results =
                mValidationResult.getValidationResult();  

        String uuid = target.getDCOMUUID();
        if(uuid.contains("provide value for this")){
			results.add(new Validator.ResultItem(this,
							Validator.ResultType.ERROR,
							target,
							mMessages.getString("DCOMBinding.DCOM_UUID_MISSING")));
        }

    }

    public void visit(DCOMOperation target) {
        Collection<ResultItem> results =
                mValidationResult.getValidationResult();  

        String method = target.getDCOMMETHODNAME();
        if(method.contains("provide value for this")){
			results.add(new Validator.ResultItem(this,
							Validator.ResultType.ERROR,
							target,
							mMessages.getString("DCOMOperation.DCOM_METHOD_MISSING")));
        }
    }

    public void visit(DCOMMessage target) {        
      // for dcom operation tag - nothing to validate at this point
    }

    //check whether it is null or empty
    private boolean isNull(String val){
        if((val == null) || (val.trim().equals(""))){
                return true;
        } else {
                return false;
        }
    }

    private String getMessage(String key, String param) {
            return getMessage(key, new Object[] { param });
    }

    private String getMessage(String key, Object[] params) {
            String fmt = mMessages.getString(key);
            if (params != null) {
                    return MessageFormat.format(fmt, params);
            } else {
                    return fmt;
            }
    }
}
