/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
            this.connectionDef.setOTDPathName(connDef.getOTDPathName());
        }
        
      
        
        
        public static void main(String[] args) {
            File f = new File(args[0]);
            EDMProcessDefinition processDef = new EDMProcessDefinition(f);            
        }
        
        
}
