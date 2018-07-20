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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

/*
 * WS70CommandType.java
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;
import javax.enterprise.deploy.shared.CommandType;

/**
 * CommandType for the Webserver 70 commands like starting and stopping the
 * Instances other than Admin Server
 *
 * @author Mukesh Garg
 */
public class WS70CommandType extends CommandType {
    /**
      * The DeploymentManger action operation being processed is distribute.
      */
     public static final WS70CommandType DISTRIBUTE = new WS70CommandType(0);
     /**
      * The DeploymentManger action operation being processed is start.
      */
     public static final WS70CommandType START = new WS70CommandType(1);
     /**
      * The DeploymentManger action operation being processed is stop.
      */
     public static final WS70CommandType STOP = new WS70CommandType(2);
     /**
      * The DeploymentManger action operation being processed is undeploy.
      */
     public static final WS70CommandType UNDEPLOY = new WS70CommandType(3);
     /**
      * he DeploymentManger action operation being processed is redeploy.
      */
     public static final WS70CommandType REDEPLOY = new WS70CommandType(4);
     
     public static final WS70CommandType STARTTARGET = new WS70CommandType(11);
     public static final WS70CommandType STOPTARGET = new WS70CommandType(12);
     public static final WS70CommandType STARTTARGETDEBUG = new WS70CommandType(13);
    
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
     protected WS70CommandType(int value){
         super(value);
         this.value = value;
     }
}
