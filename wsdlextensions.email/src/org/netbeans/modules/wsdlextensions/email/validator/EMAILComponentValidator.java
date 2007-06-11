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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.wsdlextensions.email.validator;

import org.netbeans.modules.wsdlextensions.email.imap.IMAPAddress;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPBinding;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPComponent;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPInput;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPOperation;

import org.netbeans.modules.wsdlextensions.email.pop3.POP3Address;
import org.netbeans.modules.wsdlextensions.email.pop3.POP3Binding;
import org.netbeans.modules.wsdlextensions.email.pop3.POP3Component;
import org.netbeans.modules.wsdlextensions.email.pop3.POP3Input;
import org.netbeans.modules.wsdlextensions.email.pop3.POP3Operation;
import java.net.URI;
import java.net.URL;
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
import org.netbeans.modules.xml.wsdl.model.PortType;
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

//import com.sun.jbi.ui.devtool.wsdl.email.EMAILBinding;
import org.netbeans.modules.xml.wsdl.model.OperationParameter;
import org.netbeans.modules.xml.wsdl.model.Part;


/**
 * semantic validation, check WSDL elements & attributes values and 
 * any relationship between;
 *
 * @author Sainath Adiraju
 */
public class EMAILComponentValidator
        implements Validator, IMAPComponent.Visitor , POP3Component.Visitor{
    
  //  private static final String EMAIL_URL_PREFIX = "email://";
  //   private static final String EMAIL_URL_LOGIN_HOST_DELIM = "@";
  //  private static final String EMAIL_URL_COLON_DELIM = ":";
  

    private static final ResourceBundle mMessages =
            ResourceBundle.getBundle("org.netbeans.modules.wsdlextensions.email.validator.Bundle");
    
    private Validation mValidation;
    private ValidationType mValidationType;
    private ValidationResult mValidationResult;
    
    public static final ValidationResult EMPTY_RESULT = 
        new ValidationResult( Collections.EMPTY_SET, 
                Collections.EMPTY_SET);
    
    public EMAILComponentValidator() {}
    
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


                int numIMAPBindings = binding.getExtensibilityElements(IMAPBinding.class).size();

				//if (numIMAPBindings == 0) {
                //    continue;
                //}
                
                int numPOP3Bindings = binding.getExtensibilityElements(POP3Binding.class).size();

				if (numPOP3Bindings == 0 && numIMAPBindings == 0) {
                    continue;
                }
                

                if (numIMAPBindings > 0 && numIMAPBindings != 1) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("EMAILBindingValidation.ONLY_ONE_EMAIL_BINDING_ALLOWED")));
                }
                
                if (numPOP3Bindings > 0 && numPOP3Bindings != 1) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("POP3BindingValidation.ONLY_ONE_EMAIL_BINDING_ALLOWED")));
                }

                Iterator<BindingOperation> bindingOps =
                        binding.getBindingOperations().iterator();
                boolean foundIMAPOp = false;
                boolean foundPOP3Op = false;
                
                while (bindingOps.hasNext()) {
                    BindingOperation bindingOp = bindingOps.next();
                    List imapOpsList = bindingOp.getExtensibilityElements(IMAPOperation.class);
                    List pop3OpsList = bindingOp.getExtensibilityElements(POP3Operation.class);
                    Iterator<IMAPOperation> imapOps = imapOpsList.iterator();
                    Iterator<POP3Operation> pop3Ops = pop3OpsList.iterator();
                    
                    while (imapOps.hasNext()) {
                        imapOps.next().accept(this);
                    }
                    while (pop3Ops.hasNext()){
                        pop3Ops.next().accept(this);
                    }
                    
                    if(imapOpsList.size() > 0 || (pop3OpsList.size()) > 0) {
                        if(imapOpsList.size() > 0)
                    	foundIMAPOp = true;
                        else
                        foundPOP3Op = true;
                        BindingInput bindingInput = bindingOp.getBindingInput();
                        if (bindingInput != null) {
                            int inputMessageCnt = 0;
                            Iterator<IMAPInput> imapInput =
                                    bindingInput.getExtensibilityElements(IMAPInput.class).iterator();
                            Iterator<POP3Input> pop3Input =
                                    bindingInput.getExtensibilityElements(POP3Input.class).iterator();
                            while (imapInput.hasNext()) {
                                inputMessageCnt++;
                                IMAPInput imapInputEle = imapInput.next();
                                imapInputEle.accept(this);
                                validate(imapInputEle, bindingInput.getInput().get());
                                //emailInputEle.accept(this,bindingInput.getInput().get());
                            }
                            while (pop3Input.hasNext()) {
                                inputMessageCnt++;
                                POP3Input pop3InputEle = pop3Input.next();
                                pop3InputEle.accept(this);
                                validate(pop3InputEle, bindingInput.getInput().get());

                            }
                            if ( inputMessageCnt > 1 ) {
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        binding,
                                        mMessages.getString("EMAILBindingValidation.ATMOST_ONE_Message_IN_INPUT") + inputMessageCnt));
                            }
                        }
                        
                        
                    }
                }
                // there is email:binding but no email:operation
                if ( numIMAPBindings > 0 && !foundIMAPOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("EMAILBindingValidation.MISSING_EMAIL_OPERATION")));
                }
                if ( numPOP3Bindings > 0 && !foundPOP3Op ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("EMAILBindingValidation.MISSING_EMAIL_OPERATION")));
                }
                // there is no imap:binding but there are imap:operation
                if ( numIMAPBindings == 0 && foundIMAPOp ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("EMAILBindingValidation.EMAIL_OPERATION_WO_EMAIL_BINDING")));
                }
                if ( numPOP3Bindings == 0 && foundPOP3Op ) {
                    results.add(
                            new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            binding,
                            mMessages.getString("EMAILBindingValidation.EMAIL_OPERATION_WO_EMAIL_BINDING")));
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
                            int numRelatedIMAPBindings = binding.getExtensibilityElements(IMAPBinding.class).size();
                            Iterator<IMAPAddress> imapAddresses = port.getExtensibilityElements(IMAPAddress.class).iterator();
                            
                            int numRelatedPOP3Bindings = binding.getExtensibilityElements(POP3Binding.class).size();
                            Iterator<POP3Address> pop3Addresses = port.getExtensibilityElements(POP3Address.class).iterator();
                            
                            if((numRelatedIMAPBindings > 0) && (!imapAddresses.hasNext())){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("EMAILExtValidation.MISSING_EMAIL_ADDRESS")));
                            }
                            
                            if((numRelatedPOP3Bindings > 0) && (!pop3Addresses.hasNext())){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("EMAILExtValidation.MISSING_EMAIL_ADDRESS")));
                            }

                            if(port.getExtensibilityElements(IMAPAddress.class).size() > 1){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("EMAILExtValidation.ONLY_ONE_EMAILADDRESS_ALLOWED")));
                            }
                            if(port.getExtensibilityElements(POP3Address.class).size() > 1){
                                results.add(
                                        new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        port,
                                        mMessages.getString("EMAILExtValidation.ONLY_ONE_EMAILADDRESS_ALLOWED")));
                            }
                            
                            while (imapAddresses.hasNext()) {
                                imapAddresses.next().accept(this);
                            }
                            while (pop3Addresses.hasNext()){
                                pop3Addresses.next().accept(this);
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

    public void visit(IMAPAddress target) {
        // validate the following:
        // (1) attribute 'url' has the right syntax: i.e. mailto:sainath.adirjau@sun.com
        // 
        //Collection<ResultItem> results =
          //      mValidationResult.getValidationResult();
        
       // EMAILAddressURL url = new EMAILAddressURL();
        //try{
        //url.unmarshal(results, this, target);
       // }catch(Exception ex){
         
    //}
    }
    
    public void visit(POP3Address target) {
     // nothing to validate at this point of time    
    }
    public void visit(IMAPBinding target) {
        // for email binding tag - nothing to validate at this point
   }
    
    public void visit(POP3Binding target) {
        // for email binding tag - nothing to validate at this point
   }

    public void visit(IMAPOperation target) {
        // for email operation tag - nothing to validate at this point
    }
    
    public void visit(POP3Operation target) {
        // for email operation tag - nothing to validate at this point
    }
    
    public void visit(IMAPInput target){
     //nothing to validate
    }
    
    public void visit(POP3Input target){
     //nothing to validate
    }
    private void validate (IMAPInput target,OperationParameter opParam){
    	
    	Collection<ResultItem> results = mValidationResult.getValidationResult();
    	String  from = target.getFrom();

        if(from != null){
	if (from==""){
                results.add(new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        target,
                                        mMessages.getString("EMAILINPUTValidation.INVALID_FROM_ADDRESS")));
		}else if (!referencesValidMessagePart(opParam.getMessage(), from)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));                 
               }
               
        }
		
	String to = target.getTo();
        if(to != null){
	if(to==""){
                results.add(new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        target,
                                        mMessages.getString("EMAILINPUTValidation.INVALID_TO_ADDRESS")));
		}else if (!referencesValidMessagePart(opParam.getMessage(), to)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
        }    
        }
        String cc = target.getCc();
        if(cc != null){
        if(cc==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_CC_ADDRESS")));
	           }else if (!referencesValidMessagePart(opParam.getMessage(), cc)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                   }
        }
        String bcc = target.getBcc();
        if(bcc != null){
        if(bcc==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_BCC_ADDRESS")));
                    }else if (!referencesValidMessagePart(opParam.getMessage(), bcc)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                    }
        }
        String message = target.getMessageName();
        if(message != null){
        if(message==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_BCC_ADDRESS")));
                    }else if (!referencesValidMessagePart(opParam.getMessage(), message)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                    }
        }
        String subject = target.getSubjectName();
        if(subject != null){
        if(subject==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_BCC_ADDRESS")));
                    }else if (!referencesValidMessagePart(opParam.getMessage(), subject)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                    }
        }
        String charset = target.getCharSet();
        if(charset != null){
        if(charset==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_BCC_ADDRESS")));
                    }else if (!referencesValidMessagePart(opParam.getMessage(), charset)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                    }
        }
        
       
        
        
   }
   
   private void validate (POP3Input target,OperationParameter opParam){
    	
    	Collection<ResultItem> results = mValidationResult.getValidationResult();
    	String  from = target.getFrom();

        if(from != null){
	if (from==""){
                results.add(new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        target,
                                        mMessages.getString("EMAILINPUTValidation.INVALID_FROM_ADDRESS")));
		}else if (!referencesValidMessagePart(opParam.getMessage(), from)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));                 
               }
               
        }
		
	String to = target.getTo();
        if(to != null){
	if(to==""){
                results.add(new Validator.ResultItem(this,
                                        Validator.ResultType.ERROR,
                                        target,
                                        mMessages.getString("EMAILINPUTValidation.INVALID_TO_ADDRESS")));
		}else if (!referencesValidMessagePart(opParam.getMessage(), to)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
        }    
        }
        String cc = target.getCc();
        if(cc != null){
        if(cc==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_CC_ADDRESS")));
	           }else if (!referencesValidMessagePart(opParam.getMessage(), cc)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                   }
        }
        String bcc = target.getBcc();
        if(bcc != null){
        if(bcc==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_BCC_ADDRESS")));
                    }else if (!referencesValidMessagePart(opParam.getMessage(), bcc)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                    }
        }
        String message = target.getMessageName();
        if(message != null){
        if(message==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_BCC_ADDRESS")));
                    }else if (!referencesValidMessagePart(opParam.getMessage(), message)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                    }
        }
        String subject = target.getSubjectName();
        if(subject != null){
        if(subject==""){
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_BCC_ADDRESS")));
                    }else if (!referencesValidMessagePart(opParam.getMessage(), subject)) {
                    results.add(new Validator.ResultItem(this,
                            Validator.ResultType.ERROR,
                            target,
                            mMessages.getString("EMAILINPUTValidation.INVALID_PART_NAME")));
                    }
        }
        
       
        
        
   }

        private boolean referencesValidMessagePart (NamedComponentReference<Message> wsdlMessage, 
                                                String partName) {
        
        // Let wsdl validator catch undefined message for operation input or output
        if (wsdlMessage == null || wsdlMessage.get() == null || wsdlMessage.get().getParts() == null) {
            return true;
        }
        
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

	private boolean nonEmptyString(String strToTest) {
        boolean nonEmpty = false;
        if (strToTest != null && strToTest.length() > 0) {
            nonEmpty = true;
        }
        return nonEmpty;
    }
}
