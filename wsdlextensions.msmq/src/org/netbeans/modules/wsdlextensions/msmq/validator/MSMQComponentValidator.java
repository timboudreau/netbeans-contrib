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
package org.netbeans.modules.wsdlextensions.msmq.validator;

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

import org.netbeans.modules.wsdlextensions.msmq.MSMQComponent;
import org.netbeans.modules.wsdlextensions.msmq.MSMQOperation;
import org.netbeans.modules.wsdlextensions.msmq.MSMQMessage;
import org.netbeans.modules.wsdlextensions.msmq.MSMQBinding;
import org.netbeans.modules.wsdlextensions.msmq.MSMQAddress;

/**
 * semantic validation, check WSDL elements & attributes values and
 * any relationship between;
 *
 * @author Sun Microsystems
 */

@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.xml.xam.spi.Validator.class)
public class MSMQComponentValidator
        implements Validator, MSMQComponent.Visitor {

    private static final ResourceBundle mMessages =
            ResourceBundle.getBundle("org.netbeans.modules.wsdlextensions.msmq.validator.Bundle");

    private Validation mValidation;

    private ValidationType mValidationType;

	private ValidationResult mValidationResult;

    public static final ValidationResult EMPTY_RESULT =
        new ValidationResult( Collections.EMPTY_SET,
                Collections.EMPTY_SET);

    public MSMQComponentValidator() {}

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
							getMessage("MSMQBindingValidation.BINDING_REFERENCES_INVALID_PORTTYPE",
									   new Object[]{binding.getName()})));
					continue;
				}

			*/

                int numMSMQBindings = binding.getExtensibilityElements(MSMQBinding.class).size();

				if (numMSMQBindings == 0) {
					continue;
                }

                if (numMSMQBindings > 0 && numMSMQBindings != 1) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("MSMQBindingValidation.ONLY_ONE_MSMQ_BINDING_ALLOWED")));
                    continue;
                }

                Iterator<BindingOperation> bindingOps =
                        binding.getBindingOperations().iterator();
                boolean foundMSMQOp = false;
                while (bindingOps.hasNext()) {
                    BindingOperation bindingOp = bindingOps.next();
                    List msmqOpsList = bindingOp.getExtensibilityElements(MSMQOperation.class);
                    Iterator<MSMQOperation> msmqOps =
                            msmqOpsList.iterator();

                    while (msmqOps.hasNext()) {
                        msmqOps.next().accept(this);
                    }

                    if(msmqOpsList.size() > 0) {
                        foundMSMQOp = true;
                        BindingInput bindingInput = bindingOp.getBindingInput();
                        if (bindingInput != null) {
                            int inputMessageCnt = 0;
                            Iterator<MSMQMessage> msmqMessages =
                                    bindingInput.getExtensibilityElements(MSMQMessage.class).iterator();
                            while (msmqMessages.hasNext()) {
                                inputMessageCnt++;
                                MSMQMessage msmqMessage = msmqMessages.next();
                                msmqMessage.accept(this);
                            }
                            if ( inputMessageCnt > 1 ) {
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        binding,
                                        mMessages.getString("MSMQBindingValidation.ATMOST_ONE_MESSAGE_IN_INPUT") + " " + inputMessageCnt));
                            }
                        }

                        BindingOutput bindingOutput = bindingOp.getBindingOutput();
                        if (bindingOutput != null) {
                            int outputMessageCnt = 0;
                            Iterator<MSMQMessage> msmqMessages =
                                    bindingOutput.getExtensibilityElements(MSMQMessage.class).iterator();
                            while (msmqMessages.hasNext()) {
                                outputMessageCnt++;
                                MSMQMessage msmqMessage = msmqMessages.next();
                                msmqMessage.accept(this);
                            }
                            if ( outputMessageCnt > 1 ) {
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        binding,
                                        mMessages.getString("MSMQBindingValidation.ATMOST_ONE_MESSAGE_IN_OUTPUT") + " " + outputMessageCnt));
                            }
                        }
                    }
                }
                // there is msmq:binding but no msmq:operation
                if ( numMSMQBindings > 0 && !foundMSMQOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("MSMQBindingValidation.MISSING_MSMQ_OPERATION")));
                }
                // there is no msmq:binding but there are msmq:operation
                if ( numMSMQBindings == 0 && foundMSMQOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("MSMQBindingValidation.MSMQ_OPERATION_WO_MSMQ_BINDING")));
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
                            int numRelatedMSMQBindings = binding.getExtensibilityElements(MSMQBinding.class).size();
                            Iterator<MSMQAddress> msmqAddresses = port.getExtensibilityElements(MSMQAddress.class).iterator();
                            if((numRelatedMSMQBindings > 0) && (!msmqAddresses.hasNext())){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("MSMQExtValidation.MISSING_MSMQ_ADDRESS")));
                            }

                            if(port.getExtensibilityElements(MSMQAddress.class).size() > 1){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("MSMQExtValidation.ONLY_ONE_MSMQADDRESS_ALLOWED")));
                            }
                            while (msmqAddresses.hasNext()) {
                                msmqAddresses.next().accept(this);
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

    public void visit(MSMQAddress target) {
        // for msmq address tag - nothing to validate at this point
    }

	public void visit(MSMQBinding target) {
        // for msmq binding tag - nothing to validate at this point
    }

    public void visit(MSMQOperation target) {
        // for msmq operation tag - nothing to validate at this point
    }

    public void visit(MSMQMessage target) {
        // check the values and relations of/between all the attributes
        Collection<ResultItem> results =
                mValidationResult.getValidationResult();

		//check for http protocol
		String connMode = target.getConnectionMode();
		String access = target.getAccessMode();
		String dest = target.getDestination().toUpperCase();
		String httpurl = "DIRECT=HTTP";
		if(connMode.equals("QueueFormatName") && !(access.equals("SEND_ACCESS"))) {
			if( dest.startsWith(httpurl)){
				results.add(new Validator.ResultItem(this,
						Validator.ResultType.ERROR,
						target,
						mMessages.getString("MSMQMessage.MSMQ_DIRECT_FORMAT_NAME_HTTP_NOT_SUPP")));
			}
		}

		//check messageType
		String messageType=target.getMessageType();
		if(!access.equals("SEND_ACCESS") ){
			if((!isNull(messageType)) && (!messageType.equals("array of bytes"))){
				results.add(new Validator.ResultItem(this,
						Validator.ResultType.WARNING,
						target,
						mMessages.getString("MSMQMessage.MSMQ_MESSAGE_TYPE_NOT_SUPP")));
			}
		}

		//check encodingStyle
		String useType = target.getUse();
		String encodingStyle = target.getEncodingStyle();
		if(useType.equals("encoded")){
			if(isNull(encodingStyle)){
				results.add(new Validator.ResultItem(this,
						Validator.ResultType.ERROR,
						target,
						mMessages.getString("MSMQMessage.MSMQ_ENCODING_STYLE_MISSING")));
			}
		}
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
