
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */


package com.sun.tthub.data;

import java.util.Collection;
import java.util.Map;
import java.lang.StringBuffer;
/**
 *
 * @author choonyin
 */
public class ReqProcessingResult {

    private Map fieldErrors;
    private Collection globalErrors;
    private String statusMessage;
    private boolean clearTTValueFlag=false;
    
    /** Creates a new instance of ReqProcessingResult */
    public ReqProcessingResult() {
    }

    public Map getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Collection getGlobalErrors() {
        return globalErrors;
    }

    public void setGlobalErrors(Collection globalErrors) {
        this.globalErrors = globalErrors;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean isClearTTValueFlag() {
        return clearTTValueFlag;
    }

    public void setClearTTValueFlag(boolean clearTTValueFlag) {
        this.clearTTValueFlag = clearTTValueFlag;
    }
    
    public String toString(){
      StringBuffer buffer=new StringBuffer("[ReqProcessingResult]");
      buffer.append("\nstatusMessage-").append(this.statusMessage);
      buffer.append("\nfieldErrors-").append(this.fieldErrors);
      buffer.append("\nglobalErrors-").append(this.globalErrors);
      buffer.append("\nisClearTTValueFlag-").append(this.isClearTTValueFlag());
      return buffer.toString();
    }
    
}
