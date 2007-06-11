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

package org.netbeans.modules.wsdlextensions.msmq.impl;

import java.util.Collection;

import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;

import org.netbeans.modules.wsdlextensions.msmq.MSMQMessage;
import org.netbeans.modules.wsdlextensions.msmq.MSMQComponent;
import org.netbeans.modules.wsdlextensions.msmq.MSMQQName;

import org.w3c.dom.Element;

public class MSMQMessageImpl extends MSMQComponentImpl implements MSMQMessage {
    
    public MSMQMessageImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public MSMQMessageImpl(WSDLModel model){
        this(model, createPrefixedElement(MSMQQName.MESSAGE.getQName(), model));
    }
    
    public void accept(MSMQMessage.Visitor visitor) {
        visitor.visit(this);
    }

    public String getConnectionMode() {
        return getAttribute(MSMQAttribute.MSMQ_CONNECTION_MODE);
    }

    public void setConnectionMode(String connMode) {
        setAttribute(MSMQ_CONNECTION_MODE, MSMQAttribute.MSMQ_CONNECTION_MODE, connMode);
    }

    public String getDestination() {
        return getAttribute(MSMQAttribute.MSMQ_DESTINATION);
    }

    public void setDestination(String destination) {
        setAttribute(MSMQ_DESTINATION, MSMQAttribute.MSMQ_DESTINATION, destination);
    }

    public String getShareMode() {
        return getAttribute(MSMQAttribute.MSMQ_SHARE_MODE);
    }

    public void setShareMode(String shareMode) {
        setAttribute(MSMQ_SHARE_MODE, MSMQAttribute.MSMQ_SHARE_MODE, shareMode);
    }

	public String getAccessMode() {
        return getAttribute(MSMQAttribute.MSMQ_ACCESS_MODE);
    }

    public void setAccessMode(String accessMode) {
        setAttribute(MSMQ_ACCESS_MODE, MSMQAttribute.MSMQ_ACCESS_MODE, accessMode);
    }
	
	public long getMessageLookupID() {
        String msgID = getAttribute(MSMQAttribute.MSMQ_MSG_LOOKUP_ID);
        long msgIDVal = 1L;
        if ( msgID != null ) {
            try {
                msgIDVal = Long.getLong(msgID);
            }
            catch (Exception e) {
                // just ignore
            }
        }
        return msgIDVal;
    }

    public void setMessageLookupID(long msgLookupID) {
        setAttribute(MSMQ_MSG_LOOKUP_ID, MSMQAttribute.MSMQ_MSG_LOOKUP_ID, msgLookupID);
    }
	
	public int getReceiveInterval() {
        String interval = getAttribute(MSMQAttribute.MSMQ_RECEIVE_INTERVAL);
        int intervalVal = 1000;
        if ( interval != null ) {
            try {
                intervalVal = Integer.parseInt(interval);
            }
            catch (Exception e) {
                // just ignore
            }
        }
        return intervalVal;
    }

    public void setReceiveInterval(int interval) {
        setAttribute(MSMQ_RECEIVE_INTERVAL, MSMQAttribute.MSMQ_RECEIVE_INTERVAL, "" + interval);
    }

	public int getMessagePriority() {
        String priority = getAttribute(MSMQAttribute.MSMQ_MESSAGE_PRIORITY);
        int priorityVal = 3;
        if ( priority != null ) {
            try {
                priorityVal = Integer.parseInt(priority);
            }
            catch (Exception e) {
                // just ignore
            }
        }
        return priorityVal;
    }

    public void setMessagePriority(int priority) {
        setAttribute(MSMQ_MESSAGE_PRIORITY, MSMQAttribute.MSMQ_MESSAGE_PRIORITY, "" + priority);
    }

	public String getAcknowledgement() {
        return getAttribute(MSMQAttribute.MSMQ_ACKNOWLEDGEMENT);
    }

    public void setAcknowledgement(String ack) {
        setAttribute(MSMQ_ACKNOWLEDGEMENT, MSMQAttribute.MSMQ_ACKNOWLEDGEMENT, ack);
    }

	public String getMessageType() {
        return getAttribute(MSMQAttribute.MSMQ_MESSAGE_TYPE);
    }

    public void setMessageType(String msgType) {
        setAttribute(MSMQ_MESSAGE_TYPE, MSMQAttribute.MSMQ_MESSAGE_TYPE, msgType);
    }

    public String getUse() {
        return getAttribute(MSMQAttribute.MSMQ_USE);
    }

    public void setUse(String use) {
        setAttribute(MSMQ_USE, MSMQAttribute.MSMQ_USE, use);
    }
	
    public String getEncodingStyle() {
        return getAttribute(MSMQAttribute.MSMQ_ENCODING_STYLE);
    }

    public void setEncodingStyle(String style) {
        setAttribute(MSMQ_ENCODING_STYLE, MSMQAttribute.MSMQ_ENCODING_STYLE,style);
	}

    public String getMessagePart() {
        return getAttribute(MSMQAttribute.MSMQ_PART);
    }

    public void setMessagePart(String part) {
        setAttribute(MSMQ_PART, MSMQAttribute.MSMQ_PART, part);
    }

	public String getTransaction() {
        return getAttribute(MSMQAttribute.MSMQ_TRANSACTION);
    }

    public void setTransaction(String transaction) {
        setAttribute(MSMQ_TRANSACTION, MSMQAttribute.MSMQ_TRANSACTION, transaction);
    }
}
