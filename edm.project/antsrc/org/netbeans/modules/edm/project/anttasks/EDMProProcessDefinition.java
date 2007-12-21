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
package org.netbeans.modules.edm.project.anttasks;

import org.w3c.dom.*;
import java.io.File;

import com.sun.mashup.engine.EDMProcessDefinition;
import com.sun.sql.framework.jdbc.DBConnectionParameters;
import org.netbeans.modules.sql.framework.common.jdbc.SQLDBConnectionDefinition;

/**
 * 
 * This class is responsible for representing the mashup queries, 
 * basically picks up the mashup engine file and sucks in the sqls in the 
 * order. Basically, creates the db links, creates the external tables
 * and the view query sqls. 
 *
 *  <MashupEngine>
    <connectiondef name="IDB_CONN_DEF" driverName="org.axiondb.jdbc.AxionDriver" dbName="Internal" dbUrl="jdbc:axiondb:testdb:C:/test/" userName="sa" password="02C820">
 *  </connectiondef>
 *   <init>
 *        <dbLinks>
 *             <dbLink></dbLink>
 *       </dbLinks>
 *       <VTables>
 *             <VTable name="test" type="WebTable"><dropsql/><createsql/></VTable>
 *       </VTables>
 *   </init>
 *   <process>
 *      <DataMashup name="">
 **          <mashupsql/>
 *       </DataMashup>
 *   </process>
 *  </MashupEngine>
 *   
 *
 * 
 * @author Srinivasan Rengarajan
 * 
 */
public class EDMProProcessDefinition extends EDMProcessDefinition {
    
      
        
        public EDMProProcessDefinition(File file) {
            super(file);
        }
        
        public EDMProProcessDefinition() {
            super();
        }
   
        
        public void setDBConnectionParameters(SQLDBConnectionDefinition connDef) {
            if( connectionDef == null ) {
                connectionDef = new DBConnectionParameters();
            }
            this.connectionDef.setDBType(connDef.getDBType());
            this.connectionDef.setConnectionURL(connDef.getConnectionURL());
            this.connectionDef.setDescription(connDef.getDescription());
            this.connectionDef.setDriverClass(connDef.getDriverClass());
            this.connectionDef.setJNDIPath(connDef.getJNDIPath());
            this.connectionDef.setName(connDef.getName());
            this.connectionDef.setUserName(connDef.getUserName());
            this.connectionDef.setPassword(connDef.getPassword());
            this.connectionDef.setOTDPathName(connDef.getJNDIPath());
        }
        
      
        
        
        public static void main(String[] args) {
            File f = new File(args[0]);
            EDMProcessDefinition processDef = new EDMProcessDefinition(f);            
        }
        
        
}
