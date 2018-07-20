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
package org.netbeans.modules.portalpack.commons.palette.java;

import java.util.List;

/**
 *
 * @author Vihang
 * @author Satyaranjan
 */
public class JavaMethod {

    private String methodName;
    private List modifiers;
    private List annotationValues;
    private ParameterInfo[] paramTable;
    private List exceptionList;
    private String returnType;

    public JavaMethod(String methodName) {
        this.methodName = methodName;
    }
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

    public ParameterInfo[] getParameters() {
        return paramTable;
    }

    public void setParameters(ParameterInfo[] parameters) {
        this.paramTable = parameters;
    }

    public void setModifiers(List modifiers) {
        this.modifiers = modifiers;
    }

    public List getModifier() {
        return modifiers;
    }

    @Override
    public String toString() {
        StringBuffer signature = new StringBuffer(methodName);
        signature.append("(");
        if (paramTable == null) {
            return "methodName()";
        }
        for (int i = 0; i < paramTable.length; i++) {
            String type = paramTable[i].getType();
            //assert type == null;
            signature.append(paramTable[i].getType());
            signature.append(" ");
            signature.append(paramTable[i].getName());
            if (i != (paramTable.length - 1)) {
                signature.append(",");
            }
        }
        signature.append(")");
        return signature.toString();
    }

    public void setAnnotations(List annotationValues) {
        this.annotationValues = annotationValues;
    }

    public List getAnnotations() {
        return annotationValues;
    }

    public static final class ParameterInfo {

        String name;
        String type;
        String defaultVal;

        /**
         * Creates a new instanceof of ParameterInfo. This constructor can be
         * used for newly added parameters or changed original parameters.
         * When you call method with -1 origIndex, you have to provide not
         * null values in all other pamarameters, otherwise it throws an
         * IllegalArgumentException.
         *
         * @param  origIndex  for newly added parameters, use -1, otherwise
         *                    use index in original parameters list
         * @param  name       parameter name 
         * @param  type       parameter type
         * @param  defaultVal should be provided for the all new parameters.
         *                    For changed parameters, it is ignored.
         */
        public ParameterInfo(String name, String type, String defaultVal) {
            // new parameter
            // if (origIndex == -1 && (name == null || defaultVal == null || type == null || name.length() == 0 || defaultVal.length() == 0)) {
            //    throw new IllegalArgumentException(NbBundle.getMessage(ChangeParameters.class, "ERR_NoValues"));
            // }

            this.name = name;
            this.type = type;
            this.defaultVal = defaultVal;

        }
        
        public ParameterInfo(String name,String type) {
            
            this.name = name;
            this.type = type;
        }

        /**
         * Returns value of the name of parameter. If the name was not
         * changed, returns null.
         *
         * @return  new name for parameter or null in case that it was not changed.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns value of the type of parameter. If the name was not
         * changed, returns null.
         *
         * @return new type for parameter or null if it was not changed.
         */
        public String getType() {
            return type;
        }

        /**
         * Returns value of the default value in case of the new parameter.
         * Otherwise, it returns null.
         *
         * @return default value for new parameter, otherwise null.
         */
        public String getDefaultValue() {
            return defaultVal;
        }
    }
}
