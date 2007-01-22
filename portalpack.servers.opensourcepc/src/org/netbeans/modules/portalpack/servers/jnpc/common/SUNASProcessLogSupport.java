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

package org.netbeans.modules.portalpack.servers.jnpc.common;

import org.netbeans.modules.portalpack.servers.core.common.LogSupport;

/**
 * @author Satyaranjan
 */
public class SUNASProcessLogSupport extends LogSupport{
       private String prevMessage;
       private volatile boolean errorOutput = false;
       private volatile boolean isSuccess = false;       
       public LineInfo analyzeLine(String logLine) {
            String path = null;
            int line = -1;
            String message = null;
            boolean error = false;
            boolean accessible = false;
            logLine = logLine.trim();
            int lineLenght = logLine.length();
        
            if(logLine.startsWith("CLI") && logLine.indexOf("failed") != -1)
            {
                error = true;
                errorOutput = true;
            } else if(logLine.indexOf("executed successfully") != -1)
            {
                isSuccess = true;
            }
            // every other message treat as normal info message
            else {
                prevMessage = logLine;
            }
            return new LineInfo(path, line, "PSMGR:::: "+message, error, accessible);
        }
        
        public boolean isErrorInOutput()
        {
            return errorOutput;
        }
        
        public boolean hasError()
        {
            return errorOutput;
        }
        
        public boolean hasSuccess()
        {
            return isSuccess;
        }

    public String getPrevMessage() {
        return prevMessage;
    }
}
