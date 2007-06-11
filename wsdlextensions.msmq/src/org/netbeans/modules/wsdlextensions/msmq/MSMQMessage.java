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

package org.netbeans.modules.wsdlextensions.msmq;

/*
 *
 * @author Sun Microsystems
 */

public interface MSMQMessage extends MSMQComponent {

	public static final String MSMQ_CONNECTION_MODE = "connectionMode";

	public static final String MSMQ_DESTINATION = "destination";
    
	public static final String MSMQ_SHARE_MODE = "shareMode";
    
	public static final String MSMQ_ACCESS_MODE = "accessMode";
    
	public static final String MSMQ_MSG_LOOKUP_ID = "msgLookupID";
    
	public static final String MSMQ_RECEIVE_INTERVAL = "receiveInterval";
    
	public static final String MSMQ_MESSAGE_PRIORITY = "messagePriority";
    
	public static final String MSMQ_ACKNOWLEDGEMENT = "acknowledgement";
	
	public static final String MSMQ_MESSAGE_TYPE = "messageType";
    
	public static final String MSMQ_USE = "use";
    
	public static final String MSMQ_ENCODING_STYLE = "encodingStyle";
    
	public static final String MSMQ_PART = "part";

	public static final String MSMQ_TRANSACTION = "transaction";
    
    //<msmq:message connectionMode="QueueName"
    public String getConnectionMode();

    public void setConnectionMode(String connMode);
    
	//<msmq:message destination="<Queue>"
    public String getDestination();
    
	public void setDestination(String destination);
    
	//<msmq:message shareMode="DENY_NONE"
    public String getShareMode();
    
	public void setShareMode(String sharemode);
    
	//<msmq:message accessMode="SEND_ACCESS"
    public String getAccessMode();
    
	public void setAccessMode(String accessmode);
    
	//<msmq:message msgLookupID="1L"
    public long getMessageLookupID();
    
	public void setMessageLookupID(long id);
    
	//<msmq:message receiveInterval="5000"
    public int getReceiveInterval();
    
	public void setReceiveInterval(int interval);
    
	//<msmq:message messagePriority="7"
    public int getMessagePriority();
    
	public void setMessagePriority(int priority);
    
	//<msmq:message acknowledgement="NO"
    public String getAcknowledgement();
    
	public void setAcknowledgement(String ack);
	
	//<msmq:message messageType="string"
    public String getMessageType();
    
	public void setMessageType(String msgType);
    
	//<msmq:message ="use:literal"
    public String getUse();
    
	public void setUse(String use);
    
	//<msmq:message encodingStyle="encoded"
    public String getEncodingStyle();
    
	public void setEncodingStyle(String style);
    
	//<msmq:message part="part1"
    public String getMessagePart();
    
	public void setMessagePart(String part);

	//<msmq:message transaction="NoTransaction"
	public String getTransaction();
    
	public void setTransaction(String transaction);

}

