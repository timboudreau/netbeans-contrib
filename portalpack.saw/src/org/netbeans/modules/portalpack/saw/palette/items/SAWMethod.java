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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.saw.palette.items;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Vihang
 */
public class SAWMethod {

    public SAWMethod() {
    }
    private String methodName;
    private Vector parameters;
    private List exceptionList;
    private String returnType;

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }
    private String methodBody;
    public List getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(List exceptionList) {
        this.exceptionList = exceptionList;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Vector getParameters() {
        return parameters;
    }

    public void setParameters(Vector parameters) {
        this.parameters = parameters;
    }
    
    
    
}
