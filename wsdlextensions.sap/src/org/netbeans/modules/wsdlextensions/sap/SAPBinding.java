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

 /**
  * @author Sun Microsystems
  */

package org.netbeans.modules.wsdlextensions.sap;

public interface SAPBinding extends SAPComponent {
    public static final String SAPBINDING_TRXMODE = "transactionalMode";
    public static final String SAPBINDING_TRXIDDB = "transactionIDVerificationDatabase";
    public static final String SAPBINDING_MAXTIDDBROWS = "maxTIDDatabaseRows";

    public void setTransactionalMode(TransactionalMode transactionalMode);

    public TransactionalMode getTransactionalMode();
        
    public void setTransactionIdDatabase(String transactionIdDb);

    public String getTransactionIdDatabase();
        
    public void setMaxTransactionIdRows(Long maxTransactionIdRows);

    public Long getMaxTransactionIdRows();
        
    public enum TransactionalMode {
        TRANSACTIONAL    ("Transactional"),    // NO I8N
        NONTRANSACTIONAL("Non-Transactional"); // NO I8N
        
        public static TransactionalMode forDisplayName(String val) {
            for (TransactionalMode t : values()) {
                if (t.toString().equals(val)) {
                    return t;
                }
            }
            return null;
        }
        
        TransactionalMode(String displayName) {
            this.displayName = displayName;
        }
        public String toString() {
            return displayName;
        }
        private final String displayName;
    }
}
