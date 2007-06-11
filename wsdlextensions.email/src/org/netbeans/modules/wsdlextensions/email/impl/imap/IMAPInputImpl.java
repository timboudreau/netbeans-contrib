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

package org.netbeans.modules.wsdlextensions.email.impl.imap;

import java.util.Collection;
import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPComponent;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPInput;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPQName;
import org.netbeans.modules.wsdlextensions.email.impl.EMAILAttribute;
import org.netbeans.modules.xml.wsdl.model.OperationParameter;
import org.w3c.dom.Element;

/**
 * @author Sainath Adiraju
 */
public class IMAPInputImpl extends IMAPComponentImpl implements IMAPInput {
    
    public IMAPInputImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public IMAPInputImpl(WSDLModel model){
        this(model, createPrefixedElement(IMAPQName.INPUT.getQName(), model));
    }
    
    public void accept(IMAPComponent.Visitor visitor) {
        visitor.visit(this);
    }
  
    public String getMessageName() {
		 return getAttribute(EMAILAttribute.EMAIL_MESSAGE_NAME);
	 }

     public void setMessageName() {
		 setAttribute(IMAPInput.EMAIL_MESSAGE, EMAILAttribute.EMAIL_MESSAGE_NAME, "message");
     }
 
     public String getSubjectName() {
		 return getAttribute(EMAILAttribute.EMAIL_SUBJECT_NAME);
	 }

     public void setSubjectName() {
		 setAttribute(IMAPInput.EMAIL_SUBJECT, EMAILAttribute.EMAIL_SUBJECT_NAME, "subject");
     }

	 public String getFrom() {
		 return getAttribute(EMAILAttribute.EMAIL_FROM_NAME);
	 }

     public void setFrom() {
		 setAttribute(IMAPInput.EMAIL_FROM, EMAILAttribute.EMAIL_FROM_NAME, "from");
     }

     public String getCharSet() {
		 return getAttribute(EMAILAttribute.EMAIL_CHARSET_NAME);
	 }

     public void setCharSet() {
		 setAttribute(IMAPInput.EMAIL_CHARSET, EMAILAttribute.EMAIL_CHARSET_NAME, "charset");
     }
     
     public void setTo(){
                 setAttribute(IMAPInput.EMAIL_TO, EMAILAttribute.EMAIL_TO_NAME, "to");
     }

     public  String getTo(){
                 return getAttribute(EMAILAttribute.EMAIL_TO_NAME);
     }
     
     public void setCc(){
                 setAttribute(IMAPInput.EMAIL_CC, EMAILAttribute.EMAIL_CC_NAME, "cc");
     }

     public  String getCc(){
                 return getAttribute(EMAILAttribute.EMAIL_CC_NAME);
     }
     
     public void setBcc(){
                 setAttribute(IMAPInput.EMAIL_BCC, EMAILAttribute.EMAIL_BCC_NAME, "bcc");
     }

     public  String getBcc(){
                 return getAttribute(EMAILAttribute.EMAIL_BCC_NAME);
     }
     
      public  String getUse(){
                 return getAttribute(EMAILAttribute.EMAIL_USE_NAME);
     }
      
     public void setUse(){
                 setAttribute(IMAPInput.EMAIL_USE, EMAILAttribute.EMAIL_USE_NAME, "use");
     }
     
     public  String getEncodingStyle(){
                 return getAttribute(EMAILAttribute.EMAIL_ENCODING_STYLE);
     }
      
     public void setEncodingStyle(){
                 setAttribute(IMAPInput.EMAIL_ENCSTYLE, EMAILAttribute.EMAIL_USE_NAME, "encodingStyle");
     }
     
}
