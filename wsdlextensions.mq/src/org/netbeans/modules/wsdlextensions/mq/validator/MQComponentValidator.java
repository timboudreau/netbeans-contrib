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
package org.netbeans.modules.wsdlextensions.mq.validator;

import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ResourceBundle;

import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.BindingInput;
import org.netbeans.modules.xml.wsdl.model.BindingFault;
import org.netbeans.modules.xml.wsdl.model.BindingOperation;
import org.netbeans.modules.xml.wsdl.model.BindingOutput;
import org.netbeans.modules.xml.wsdl.model.Definitions;
import org.netbeans.modules.xml.wsdl.model.Input;
import org.netbeans.modules.xml.wsdl.model.Output;
import org.netbeans.modules.xml.wsdl.model.OperationParameter;
import org.netbeans.modules.xml.wsdl.model.Port;
import org.netbeans.modules.xml.wsdl.model.Service;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.wsdl.model.Part;

import org.netbeans.modules.xml.xam.Component;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.Model.State;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;
import org.netbeans.modules.xml.xam.spi.Validation;
import org.netbeans.modules.xml.xam.spi.Validation.ValidationType;
import org.netbeans.modules.xml.xam.spi.ValidationResult;
import org.netbeans.modules.xml.xam.spi.Validator;
import org.netbeans.modules.xml.xam.spi.Validator.ResultItem;

import org.netbeans.modules.wsdlextensions.mq.MQComponent;
import org.netbeans.modules.wsdlextensions.mq.MQBinding;
import org.netbeans.modules.wsdlextensions.mq.MQOperation;
import org.netbeans.modules.wsdlextensions.mq.MQAddress;
import org.netbeans.modules.wsdlextensions.mq.MQBody;



/**
 * MQComponentValidator
 * semantic validation, check WSDL elements & attributes values and
 * any relationship between;
 *
 *
 */
public class MQComponentValidator
        implements Validator {
    
    private static final ResourceBundle mMessages =
            ResourceBundle.getBundle("org.netbeans.modules.wsdlextensions.mq.validator.Bundle");
    
    private Validation mValidation;
    private ValidationType mValidationType;
    private ValidationResult mValidationResult;
    
    public static final ValidationResult EMPTY_RESULT =
            new ValidationResult( Collections.EMPTY_SET,
            Collections.EMPTY_SET);
    
    public MQComponentValidator() {}
    
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
                
                int numMQBindings = binding.getExtensibilityElements(MQBinding.class).size();
                if ( numMQBindings == 0 )
                    continue;
                
                if (numMQBindings > 0 && numMQBindings != 1) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            getMessage("MQBindingValidation.ONLY_ONE_MQ_BINDING_ALLOWED",
                            new Object[]{binding.getName(),
                            new Integer(numMQBindings)})));
                }
                
                Iterator<BindingOperation> bindingOps =
                        binding.getBindingOperations().iterator();
                boolean foundMQOp = false;
                int bodyCnt = 0;
                while (bindingOps.hasNext()) {
                    BindingOperation bindingOp = bindingOps.next();
                    List <MQOperation> mqOpsList = bindingOp.getExtensibilityElements(MQOperation.class);
                    Iterator<MQOperation> mqOps =
                            mqOpsList.iterator();
                    
                    while (mqOps.hasNext()) {
                        validate(bindingOp, mqOps.next());
                    }
                    
                    if(mqOpsList.size() > 0) {
                        foundMQOp = true;
                        BindingInput bindingInput = bindingOp.getBindingInput();
                        if (bindingInput != null) {
                            bodyCnt = 0;
                            // assumption:
                            // under <input>, there could be one of the following:
                            // <mq:body>
                            // but only one is allowed;
                            //
                            Iterator<MQBody> mqBodies =
                                    bindingInput.getExtensibilityElements(MQBody.class).iterator();
                            
                            if ( mqBodies != null ) {
                                while (mqBodies.hasNext()) {
                                    bodyCnt++;
                                    MQBody mqBody = mqBodies.next();
                                    validate(bindingOp, bindingInput.getInput().get(), mqBody);
                                }
                                if ( bodyCnt > 1 ) {
                                    results.add(
                                            new Validator.ResultItem(this,
                                            Validator.ResultType.ERROR,
                                            bindingInput,
                                            getMessage("MQBindingValidation.ATMOST_ONE_MQBODY_IN_INPUT",
                                            new Object [] {bindingOp.getName(),
                                            new Integer(bodyCnt),
                                            bindingInput.getName()})));
                                }
                                
                            }
                            
                            
                        }
                        
                        BindingOutput bindingOutput = bindingOp.getBindingOutput();
                          if (bindingOutput != null) {
                             results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        bindingInput,
                                        getMessage("MQBindingValidation.NO_OUPUT_SUPPORTED",
                                                  new Object [] {bindingOp.getName(),
                                                                 bindingOutput.getName()})));
                          }
                        
                    }
                }
                // there is mq:binding but no mp:operation
                if ( numMQBindings > 0 && !foundMQOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            getMessage("MQBindingValidation.MISSING_MQ_OPERATION",
                            new Object[]{binding.getName()})));
                }
                // there is no mp:binding but there are mp:operation
                if ( numMQBindings == 0 && foundMQOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            getMessage("MQBindingValidation.MQ_OPERATION_WO_MQ_BINDING",
                            new Object[]{binding.getName()})));
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
                            int numRelatedMQBindings = binding.getExtensibilityElements(MQBinding.class).size();
                            List <MQAddress> mqAddressList = port.getExtensibilityElements(MQAddress.class);
                            Iterator<MQAddress> mqAddresses = mqAddressList.iterator();
                            if((numRelatedMQBindings > 0) && (mqAddressList.size()==0)){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        getMessage("MQAddressValidation.MISSING_MQ_ADDRESS",
                                        new Object[]{port.getName(),
                                        new Integer(numRelatedMQBindings)})));
                            }
                            
                            if(mqAddressList.size() > 1){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        getMessage("MQAddressValidation.ONLY_ONE_MQ_ADDRESS_ALLOWED",
                                        new Object[]{port.getName(),
                                        new Integer(mqAddressList.size())})));
                            }
                            while (mqAddresses.hasNext()) {
                                validate(mqAddresses.next());
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
    
    private void validate(MQAddress target) {
        // validate mq:address
        Collection<ResultItem> results =
                mValidationResult.getValidationResult();
        String qMgrNm = target.getQueueManagerName();
        if (qMgrNm == null || qMgrNm.length() == 0 || qMgrNm.startsWith("Please specify a value") )
        {
             results.add(new Validator.ResultItem(this,
                    Validator.ResultType.ERROR,
                    target,
                    getMessage("MQAddressValidation.NO_QMGR_SPECIFIED","")));
                    
        
        }
        String hostname = target.getHostName();
        if (hostname == null || hostname.length() == 0 || hostname.startsWith("Please specify a value")){
            results.add(
                    new Validator.ResultItem(this,
                    Validator.ResultType.WARNING,
                    target,
                    getMessage("MQAddressValidation.ADVICE_HOSTNAME",
                    new Object[]{hostname == null?"":hostname})));
            
        }
        
    }
    
    private void validate(MQBinding target) {
        // for mq binding tag - nothing to validate at this point
    }
    
    
    private void validate(BindingOperation bindingOp, MQOperation target) {
        Collection<ResultItem> results =
                mValidationResult.getValidationResult();
        
        // ToDo: validate MQ operation
        String mep = "in-only";
        boolean hasInput = bindingOp.getBindingInput() != null;
        boolean hasOutput = bindingOp.getBindingOutput() != null;
        
        if (hasInput && hasOutput) {
            mep = "in-out";
        }
        
        String queuename = target.getQueueName();
        
        
        if (queuename == null) {
            results.add(new Validator.ResultItem(this,
                    Validator.ResultType.ERROR,
                    target,
                    getMessage("MQOperation.EMPTY_QUEUENAME_EMPTY",
                    new Object[] {bindingOp.getName()})));
        }
        
        String transaction = target.getTransaction();
        if (transaction != null &&
                transaction.equals("XATransaction") && mep.equals("in-out")) {
            results.add(new Validator.ResultItem(this,
                    Validator.ResultType.ERROR,
                    target,
                    getMessage("MQOperation.XA_NOT_SUPPORTED_FOR_IN_OUT_XCHANGE",
                    new Object[] {bindingOp.getName()})));
        }
        
    }
    
    private void validate(BindingOperation bindingOp,
            OperationParameter opParam,
            MQBody target) {
        Collection<ResultItem> results =
                mValidationResult.getValidationResult();
        
        // get mq:body type
        String mqMsgType = target.getMessageType();
        if(mqMsgType == null) {
             results.add(new Validator.ResultItem(this,
                        Validator.ResultType.ERROR,
                        target,
                        getMessage("MQMessage.MESSAGE_TYPE_IS_NOT_SUPPORTED",
                        new Object[] {bindingOp.getName(),
                        (opParam instanceof Input)? "input":"output",
                        "null"})));
             return;
        }
        if (mqMsgType.equals(MQBody.TEXT_MESSAGE)) {
            String MessageBodyPart = target.getMessageBodyPart();
            if (MessageBodyPart == null || MessageBodyPart.length() == 0) {
                results.add(new Validator.ResultItem(this,
                        Validator.ResultType.ERROR,
                        target,
                        getMessage("MQMessage.TEXT_MESSAGE_MESSAGEBODY_NOT_SPECIFIED",
                        new Object[] {bindingOp.getName(),
                        (opParam instanceof Input)? "input":"output",
                        opParam.getName()})));
            } else {
                 // make sure textPart references a vald wsdl message part
                if (!referencesValidMessagePart(opParam.getMessage(), MessageBodyPart)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            getMessage("MQMessage.TEXT_PART_REFERENCES_NON_EXISTENT_PART",
                                       new Object[] {bindingOp.getName(),
                                                     (opParam instanceof Input)? "input":"output",
                                                     opParam.getName(),
                                                     MessageBodyPart,
                                                     opParam.getMessage().getQName()})));                    
                }
                
            }
        }
    }
    
    private boolean referencesValidMessagePart(NamedComponentReference<Message> wsdlMessage,
            String partName) {
        boolean isValdPartReference = false;
        Iterator<Part> partIter = wsdlMessage.get().getParts().iterator();
        while(partIter.hasNext()) {
            Part p = partIter.next();
            if (p.getName().equals(partName)) {
                isValdPartReference = true;
                break;
            }
        }
        return isValdPartReference;
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
