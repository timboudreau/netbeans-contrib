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
package org.netbeans.modules.wsdlextensions.hl7.validator;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.util.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.BindingInput;
import org.netbeans.modules.xml.wsdl.model.BindingFault;
import org.netbeans.modules.xml.wsdl.model.BindingOperation;
import org.netbeans.modules.xml.wsdl.model.BindingOutput;
import org.netbeans.modules.xml.wsdl.model.Definitions;
import org.netbeans.modules.xml.wsdl.model.PortType;
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

import org.netbeans.modules.wsdlextensions.hl7.HL7Component;
import org.netbeans.modules.wsdlextensions.hl7.HL7Operation;
import org.netbeans.modules.wsdlextensions.hl7.HL7Message;
import org.netbeans.modules.wsdlextensions.hl7.HL7Binding;
import org.netbeans.modules.wsdlextensions.hl7.HL7Address;
import org.netbeans.modules.wsdlextensions.hl7.HL7ProtocolProperties;

/**
 * semantic validation, check WSDL elements & attributes values and 
 * any relationship between;
 *
 * @author raghunadh.teegavarapu@sun.com
 */
public class HL7ComponentValidator implements Validator, HL7Component.Visitor {

    private static final String HL7_URL_PREFIX = "hl7://";

    private static final String HL7_URL_LOGIN_HOST_DELIM = "@";

    private static final String HL7_URL_COLON_DELIM = ":";

    private static final String HL7_URL_PATH_DELIM = "/";

    private static final ResourceBundle mMessages = ResourceBundle.getBundle("org.netbeans.modules.wsdlextensions.hl7.validator.Bundle");

    private Validation mValidation;

    private ValidationType mValidationType;

    private ValidationResult mValidationResult;

    public static final ValidationResult EMPTY_RESULT = new ValidationResult(Collections.EMPTY_SET,
            Collections.EMPTY_SET);

    public HL7ComponentValidator() {
    }

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
    public ValidationResult validate(Model model, Validation validation, ValidationType validationType) {
        mValidation = validation;
        mValidationType = validationType;

        HashSet<ResultItem> results = new HashSet<ResultItem>();
        HashSet<Model> models = new HashSet<Model>();
        models.add(model);
        mValidationResult = new ValidationResult(results, models);

        // Traverse the model
        if (model instanceof WSDLModel) {
            WSDLModel wsdlModel = (WSDLModel) model;

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

                int numHL7Bindings = binding.getExtensibilityElements(HL7Binding.class).size();
                if (numHL7Bindings == 0) {
                    continue;
                }

                if (numHL7Bindings > 0 && numHL7Bindings != 1) {
                    results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, binding,
                            mMessages.getString("HL7BindingValidation.ONLY_ONE_HL7_BINDING_ALLOWED")));
                }

                Iterator<BindingOperation> bindingOps = binding.getBindingOperations().iterator();
                boolean foundHL7Op = false;
                while (bindingOps.hasNext()) {
                    BindingOperation bindingOp = bindingOps.next();
                    List hl7OpsList = bindingOp.getExtensibilityElements(HL7Operation.class);
                    Iterator<HL7Operation> hl7Ops = hl7OpsList.iterator();

                    while (hl7Ops.hasNext()) {
                        hl7Ops.next().accept(this);
                    }

                    if (hl7OpsList.size() > 0) {
                        foundHL7Op = true;
                        BindingInput bindingInput = bindingOp.getBindingInput();
                        if (bindingInput != null) {
                            int inputMessageCnt = 0;
                            Iterator<HL7Message> hl7Messages = bindingInput.getExtensibilityElements(HL7Message.class).iterator();
                            while (hl7Messages.hasNext()) {
                                inputMessageCnt++;
                                HL7Message hl7Message = hl7Messages.next();
                                hl7Message.accept(this);
                            }
                            if (inputMessageCnt > 1) {
                                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, binding,
                                        mMessages.getString("HL7BindingValidation.ATMOST_ONE_MESSAGE_IN_INPUT")
                                                + inputMessageCnt));
                            }
                            if (inputMessageCnt == 0) {
                                results.add(new Validator.ResultItem(
                                        this,
                                        Validator.ResultType.ERROR,
                                        binding,
                                        mMessages.getString("HL7BindingValidation.NO_EXTENSIBILITY_ELEMENT_FOUND_IN_INPUT")));
                            }
                        }

                    }
                }
                // there is hl7:binding but no hl7:operation
                if (numHL7Bindings > 0 && !foundHL7Op) {
                    results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, binding,
                            mMessages.getString("HL7BindingValidation.MISSING_HL7_OPERATION")));
                }
                // there is no hl7:binding but there are hl7:operation
                if (numHL7Bindings == 0 && foundHL7Op) {
                    results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, binding,
                            mMessages.getString("HL7BindingValidation.HL7_OPERATION_WO_HL7_BINDING")));
                }
            }

            Iterator<Service> services = defs.getServices().iterator();
            while (services.hasNext()) {
                Iterator<Port> ports = services.next().getPorts().iterator();
                while (ports.hasNext()) {
                    Port port = ports.next();
                    if (port.getBinding() != null) {
                        Binding binding = port.getBinding().get();
                        if (binding != null) {
                            int numRelatedHL7Bindings = binding.getExtensibilityElements(HL7Binding.class).size();
                            Iterator<HL7Address> hl7Addresses = port.getExtensibilityElements(HL7Address.class).iterator();
                            if ((numRelatedHL7Bindings > 0) && (!hl7Addresses.hasNext())) {
                                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, port,
                                        mMessages.getString("HL7ExtValidation.MISSING_HL7_ADDRESS")));
                            }

                            if (port.getExtensibilityElements(HL7Address.class).size() > 1) {
                                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, port,
                                        mMessages.getString("HL7ExtValidation.ONLY_ONE_HL7ADDRESS_ALLOWED")));
                            }
                            while (hl7Addresses.hasNext()) {
                                hl7Addresses.next().accept(this);
                            }

                            Iterator<HL7ProtocolProperties> hl7ProtocolProperties = port.getExtensibilityElements(
                                    HL7ProtocolProperties.class).iterator();
                            if ((numRelatedHL7Bindings > 0) && (!hl7ProtocolProperties.hasNext())) {
                                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, port,
                                        mMessages.getString("HL7ExtValidation.MISSING_HL7_PROTOCOLPROPERTIES")));
                            }

                            if (port.getExtensibilityElements(HL7ProtocolProperties.class).size() > 1) {
                                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, port,
                                        mMessages.getString("HL7ExtValidation.ONLY_ONE_PROTOCOLPROPERTIES_ALLOWED")));
                            }
                            while (hl7ProtocolProperties.hasNext()) {
                                hl7ProtocolProperties.next().accept(this);
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

    public void visit(HL7Address target) {
        // validate the following:
        // (1) attribute 'url' has the right syntax: i.e. hl7://[hl7_host]:[hl7_port]
        // 
        Collection<ResultItem> results = mValidationResult.getValidationResult();
        String serverlocation = target.getHL7ServerLocationURL();
        HL7AddressURL url = new HL7AddressURL(serverlocation);

        if (!isAToken(serverlocation, target)) {
            url.parse(results, this, target);
        }

    }

    private boolean isAToken(String name, HL7Component target) {
        Collection<ResultItem> results = mValidationResult.getValidationResult();

        boolean isToken = false;

        if (name != null && name.startsWith("${")) {
            isToken = true;
            if (!name.endsWith("}")) {
                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target, getMessage(
                        "HL7ExtValidation.INVALID_ENVIRONMENT_TOKEN_NAME", new Object[] { name })));
            }
        }

        return isToken;
    }

    public void visit(HL7Binding target) {
        // for hl7 binding tag - nothing to validate at this point
    }

    public void visit(HL7Operation target) {
        // for hl7 operation tag - nothing to validate at this point
    }

    public void visit(HL7Message target) {
        // check the values and relations of/between all the attributes
        Collection<ResultItem> results = mValidationResult.getValidationResult();
        String use = target.getUse();
        if (!nonEmptyString(use) && !use.equals("encoded")) {
            results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                    mMessages.getString("HL7Message.INVALID_HL7_USE")));

        }
        String hl7encoderStyle = target.getEncodingStyle();
        if (hl7encoderStyle == null || hl7encoderStyle.length() < 0 || !hl7encoderStyle.equals("hl7encoder-1.0")) {
            results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                    mMessages.getString("HL7Message.INVALID_HL7_ENCODINGSTYLE")));
        }

    }

    public void visit(HL7ProtocolProperties target) {
        // check the values and relations of/between all the attributes
        Collection<ResultItem> results = mValidationResult.getValidationResult();
        // validation for MSH
        Boolean validateMSH = target.getValidateMSHEnabled();
        if (validateMSH != null) {
            String booleanValue = validateMSH.toString();
            if (nonEmptyString(booleanValue)) {
                if (booleanValue.equals("true")) {
                    if (!nonEmptyString(target.getProcessingID()) || !nonEmptyString(target.getVersionID())) {
                        results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                                mMessages.getString("HL7ProtocolProperties.REQUIRED_HL7_MESSAGE_VALIDATION")));

                    }
                }

            }
        }
        // version 2.1 does not have support for enchanced mode of ack
        String versionID = target.getVersionID();
        String ackMode = target.getAckMode();
        if (nonEmptyString(versionID)) {
			if(versionID.equals("3.0")){
				 results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target, getMessage(
                            "HL7ProtocolProperties.NOT_SUPPORT_VERSION", new Object[] { versionID })));
			}
            if (nonEmptyString(ackMode)) {
                if (versionID.equals("2.1") && ackMode.equals("enhanced")) {
                    results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target, getMessage(
                            "HL7ProtocolProperties.INVALID_ACKCODE_FOR_VERSION", new Object[] { versionID, ackMode })));

                }

            }

        } else {

            results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                    mMessages.getString("HL7ProtocolProperties.INVALID_VERSION")));
        }
		// Validation for sftEnable. sftEnabled is true only with version id 2.5
		Boolean sftEnabled = target.getSFTEnabled();
		if(sftEnabled != null) {
			String booleanValue = sftEnabled.toString();
            if (nonEmptyString(booleanValue)) {
				if (booleanValue.equals("true")) {
						if (nonEmptyString(versionID)) {
                            boolean condition = versionID.equals("2.5") || 
                                                versionID.equals("2.5.1");
							if (!condition) {
								results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target, getMessage(
											"HL7ProtocolProperties.INVALID_VERSION_FOR_SFT", new Object[] { versionID })));
							}
	 					}
                 }
			}
		}
		// Validation for Software Install date.
		String softwareInstallDate = target.getSoftwareInstallDate();
		 if (nonEmptyString(softwareInstallDate)) {
			 Date dt = null;
			 try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				sdf.setLenient(false);
				dt = sdf.parse(softwareInstallDate);
			 } catch(ParseException e){
				results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target, getMessage(
											"HL7ProtocolProperties.INVALID_DATE", new Object[] { dt })));
			 }
	 
		 }
        // Validation for Start block charecter
        Byte startBlockChar = target.getStartBlockChar();
        if (startBlockChar != null) {
            if (startBlockChar.intValue() < 1 || startBlockChar.intValue() > 127) {

                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                        mMessages.getString("HL7ProtocolProperties.INVALID_HL7_STARTBLOCK_CHARACTER")));
            }
        } else {
            results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                    mMessages.getString("HL7ProtocolProperties.INVALID_HL7_STARTBLOCK_CHARACTER")));
        }

        // Validation for End block charecter
        Byte endBlockChar = target.getEndBlockChar();
        if (endBlockChar != null) {
            if (endBlockChar.intValue() < 1 || endBlockChar.intValue() > 127) {

                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                        mMessages.getString("HL7ProtocolProperties.INVALID_HL7_ENDTBLOCK_CHARACTER")));
            }
        } else {
            results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                    mMessages.getString("HL7ProtocolProperties.INVALID_HL7_ENDTBLOCK_CHARACTER")));
        }

        // Validation for End Date charecter
        Byte endDataChar = target.getEndDataChar();
        if (endDataChar != null) {
            if (endDataChar.intValue() < 1 || endDataChar.intValue() > 127) {

                results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                        mMessages.getString("HL7ProtocolProperties.INVALID_HL7_ENDTDATA_CHARACTER")));
            }
        } else {
            results.add(new Validator.ResultItem(this, Validator.ResultType.ERROR, target,
                    mMessages.getString("HL7ProtocolProperties.INVALID_HL7_ENDTDATA_CHARACTER")));
        }

    }

    private boolean nonEmptyString(String strToTest) {
        boolean nonEmpty = false;
        if (strToTest != null && strToTest.length() > 0) {
            nonEmpty = true;
        }
        return nonEmpty;
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
