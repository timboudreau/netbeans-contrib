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

package org.netbeans.modules.portalpack.servers.core;


import javax.enterprise.deploy.shared.CommandType;

public class PSCommandType extends CommandType {
    /**
      * The DeploymentManger action operation being processed is distribute.
      */
     public static final PSCommandType DISTRIBUTE = new PSCommandType(0);
     /**
      * The DeploymentManger action operation being processed is start.
      */
     public static final PSCommandType START = new PSCommandType(1);
     /**
      * The DeploymentManger action operation being processed is stop.
      */
     public static final PSCommandType STOP = new PSCommandType(2);
     /**
      * The DeploymentManger action operation being processed is undeploy.
      */
     public static final PSCommandType UNDEPLOY = new PSCommandType(3);
     /**
      * he DeploymentManger action operation being processed is redeploy.
      */
     public static final PSCommandType REDEPLOY = new PSCommandType(4);
     
     public static final PSCommandType STARTTARGET = new PSCommandType(11);
     public static final PSCommandType STOPTARGET = new PSCommandType(12);
     public static final PSCommandType STARTTARGETDEBUG = new PSCommandType(13);
    
     private static final CommandType[] enumValueTable = new CommandType[]{
         DISTRIBUTE,
         START,
         STOP,
         UNDEPLOY,
         REDEPLOY,
         STARTTARGET,
         STOPTARGET,
         STARTTARGETDEBUG
     };

     private static final String[] stringTable = new String[]{
         "distribute",
         "start",
         "stop",
         "undeploy",
         "redeploy",
         "starttarget",
         "stoptarget",
         "starttargetdebug"
     };
     private int value;
     protected PSCommandType(int value){
         super(value);
         this.value = value;
     }
}

