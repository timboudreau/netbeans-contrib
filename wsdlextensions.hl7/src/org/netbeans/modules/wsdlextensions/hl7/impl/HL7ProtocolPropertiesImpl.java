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

package org.netbeans.modules.wsdlextensions.hl7.impl;

import java.util.Collection;
import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.wsdlextensions.hl7.HL7ProtocolProperties;
import org.netbeans.modules.wsdlextensions.hl7.HL7Component;
import org.netbeans.modules.wsdlextensions.hl7.HL7QName;
import org.w3c.dom.Element;

/**
 * @author raghunadh.teegavarapu@sun.com
 */
public class HL7ProtocolPropertiesImpl extends HL7ComponentImpl implements HL7ProtocolProperties {
    
    public HL7ProtocolPropertiesImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public HL7ProtocolPropertiesImpl(WSDLModel model){
        this(model, createPrefixedElement(HL7QName.POTOCOLPROPERTIES.getQName(), model));
    }
    
    public void accept(HL7Component.Visitor visitor) {
        visitor.visit(this);
    }

    public String getAckMode() {
        return getAttribute(HL7Attribute.HL7_ACK_MODE);
    }

    public void setAckMode(String ackMode) {
        setAttribute(HL7ProtocolProperties.HL7_ACK_MODE, HL7Attribute.HL7_ACK_MODE, ackMode);
    }
  
   public String getLLPType() {
        return getAttribute(HL7Attribute.HL7_LLP_TYPE);
    }

    public void setLLPType(String llpType) {
        setAttribute(HL7ProtocolProperties.HL7_LLP_TYPE, HL7Attribute.HL7_LLP_TYPE, llpType);
    }

    public Byte getEndDataChar() {
        String endDataCharStr = getAttribute(HL7Attribute.HL7_END_DATA_CHARACTER);
		Byte endDataChar = null;
		if(nonEmptyString(endDataCharStr)){
		  try {
		  	endDataChar = new Byte(endDataCharStr);
		  }
		  catch (Exception e){
			// Just Ignor
		  } 
		  
		}
		return endDataChar;
    }

    public void setEndDataChar(Byte val) {
        setAttribute(HL7ProtocolProperties.HL7_END_DATA_CHARACTER, HL7Attribute.HL7_END_DATA_CHARACTER, val.toString());
    }

	 public Byte getEndBlockChar() {
        String endBlockCharStr = getAttribute(HL7Attribute.HL7_END_BLOCK_CHARACTER);
		Byte endBlockChar = null;
		if(nonEmptyString(endBlockCharStr)){
		  try {
		  	endBlockChar = new Byte(endBlockCharStr);
		  }
		  catch (Exception e){
			// Just Ignor
		  } 
		  
		}
		return endBlockChar;
    }

    public void setEndBlockChar(Byte val) {
        setAttribute(HL7ProtocolProperties.HL7_END_BLOCK_CHARACTER, HL7Attribute.HL7_END_BLOCK_CHARACTER, val.toString());
    }
	
	public Byte getStartBlockChar() {
        String startBlockCharStr = getAttribute(HL7Attribute.HL7_START_BLOCK_CHARACTER);
		Byte startBlockChar = null;
		if(nonEmptyString(startBlockCharStr)){
		  try {
		  	startBlockChar = new Byte(startBlockCharStr);
		  }
		  catch (Exception e){
			// Just Ignor
		  } 
		  
		}
		return startBlockChar;
    }

    public void setStartBlockChar(Byte val) {
        setAttribute(HL7ProtocolProperties.HL7_START_BLOCK_CHARACTER, HL7Attribute.HL7_START_BLOCK_CHARACTER, val.toString());
    }

	public Boolean getHLLPChkSumEnabled() {
        String hllpCheckSumStr = getAttribute(HL7Attribute.HL7_HLLP_CHECKSUM_ENABLED);
		Boolean hllpCheckSum = null;
		if(nonEmptyString(hllpCheckSumStr)){
		  try {
		  	hllpCheckSum = new Boolean(hllpCheckSumStr);
		  }
		  catch (Exception e){
			// Just Ignor
		  } 
		  
		}
		return hllpCheckSum;
    }

    public void setHLLPChkSumEnabled(Boolean val) {
        setAttribute(HL7ProtocolProperties.HL7_HLLP_CHECKSUM_ENABLED, HL7Attribute.HL7_HLLP_CHECKSUM_ENABLED, val.toString());
    }

	public Boolean getSeqNumEnabled() {
        String seqNumEnabledStr = getAttribute(HL7Attribute.HL7_SEQNUM_ENABLED);
		Boolean seqNumEnabled = false;
		if(nonEmptyString(seqNumEnabledStr)){
		  try {
		  	seqNumEnabled = new Boolean(seqNumEnabledStr);
		  }
		  catch (Exception e){
			// Just Ignor
		  } 
		  
		}
		return seqNumEnabled;
    }

    public void setSeqNumEnabled(Boolean val) {
        setAttribute(HL7ProtocolProperties.HL7_SEQNUM_ENABLED, HL7Attribute.HL7_SEQNUM_ENABLED, val.toString());
    }

	public Boolean getValidateMSHEnabled() {
        String valdateMSHStr = getAttribute(HL7Attribute.HL7_VALIDATE_MSH);
		Boolean valdateMSH = null;
		if(nonEmptyString(valdateMSHStr)){
		  try {
		  	valdateMSH = new Boolean(valdateMSHStr);
		  }
		  catch (Exception e){
			// Just Ignor
		  } 
		  
		}
		return valdateMSH;
    }

    public void setValidateMSHEnabled(Boolean val) {
        setAttribute(HL7ProtocolProperties.HL7_VALIDATE_MSH, HL7Attribute.HL7_VALIDATE_MSH, val.toString());
    }

	public void setProcessingID(String procID) {
        setAttribute(HL7ProtocolProperties.HL7_PROCESSING_ID, HL7Attribute.HL7_PROCESSING_ID, procID);
    }
  
   public String getProcessingID() {
        return getAttribute(HL7Attribute.HL7_PROCESSING_ID);
    }

	public void setVersionID(String verID) {
        setAttribute(HL7ProtocolProperties.HL7_VERSION_ID, HL7Attribute.HL7_VERSION_ID, verID);
    }
  
   public String getVersionID() {
        return getAttribute(HL7Attribute.HL7_VERSION_ID);
    }

	public Boolean getSFTEnabled() {
        String valdateSFTStr = getAttribute(HL7Attribute.HL7_ENABLED_SFT);
		Boolean valdateSFT = null;
		if(nonEmptyString(valdateSFTStr)){
		  try {
		  	valdateSFT = new Boolean(valdateSFTStr);
		  }
		  catch (Exception e){
			// Just Ignor
		  } 
		  
		}
		return valdateSFT;
    }

    public void setSFTEnabled(Boolean val) {
        setAttribute(HL7ProtocolProperties.HL7_ENABLED_SFT, HL7Attribute.HL7_ENABLED_SFT, val.toString());
    }

	public void setSoftwareVendorOrganization(String val) {
        setAttribute(HL7ProtocolProperties.HL7_SOFTWARE_VENDOR_ORGANIZATION, HL7Attribute.HL7_SOFTWARE_VENDOR_ORGANIZATION, val);
    }
  
   public String getSoftwareVendorOrganization() {
        return getAttribute(HL7Attribute.HL7_SOFTWARE_VENDOR_ORGANIZATION);
    }
	public void setSoftwareCertifiedVersionOrReleaseNumber(String val) {
        setAttribute(HL7ProtocolProperties.HL7_SOFTWARE_CERTIFIED_VERSION, HL7Attribute.HL7_SOFTWARE_CERTIFIED_VERSION, val);
    }
  
   public String getSoftwareCertifiedVersionOrReleaseNumber() {
        return getAttribute(HL7Attribute.HL7_SOFTWARE_CERTIFIED_VERSION);
    }

	public void setSoftwareProductName(String val) {
        setAttribute(HL7ProtocolProperties.HL7_SOFTWARE_PRODUCT_NAME, HL7Attribute.HL7_SOFTWARE_PRODUCT_NAME, val);
    }
  
   public String getSoftwareProductName() {
        return getAttribute(HL7Attribute.HL7_SOFTWARE_PRODUCT_NAME);
    }
	
	public void setSoftwareBinaryID(String val) {
        setAttribute(HL7ProtocolProperties.HL7_SOFTWARE_BINARY_ID, HL7Attribute.HL7_SOFTWARE_BINARY_ID, val);
    }
  
   public String getSoftwareBinaryID() {
        return getAttribute(HL7Attribute.HL7_SOFTWARE_BINARY_ID);
    }

	public void setSoftwareProductInformation(String val) {
        setAttribute(HL7ProtocolProperties.HL7_SOFTWARE_PRODUCT_INFORMATION, HL7Attribute.HL7_SOFTWARE_PRODUCT_INFORMATION, val);
    }
  
   public String getSoftwareProductInformation() {
        return getAttribute(HL7Attribute.HL7_SOFTWARE_PRODUCT_INFORMATION);
    }

	public void setSoftwareInstallDate(String val) {
        setAttribute(HL7ProtocolProperties.HL7_SOFTWARE_INSTALL_DATE, HL7Attribute.HL7_SOFTWARE_INSTALL_DATE, val);
    }
  
   public String getSoftwareInstallDate() {
        return getAttribute(HL7Attribute.HL7_SOFTWARE_INSTALL_DATE);
    }

  private boolean nonEmptyString(String strToTest) {
        boolean nonEmpty = false;
        if (strToTest != null && strToTest.length() > 0) {
            nonEmpty = true;
        }
        return nonEmpty;
    }
}

