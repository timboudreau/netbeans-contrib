/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

/**
 *
 * @author Satyaranjan
 */
public class MethodInfo {
    
    private String methodName;
    private ParameterInfo[] paramTable;
   
    
    /**
     * Creates a new instance of change parameters refactoring.
     *
     * @param method   i.e. method or constructor
     */
    public MethodInfo(String methodName) {
        this.methodName = methodName;
    }
    
    public String getMethodName()
    {
        return methodName;
    }
    
    /**
     * Getter for  parameters
     * @return array of  parameters
     */
    public ParameterInfo[] getParameterInfo() {
        if(paramTable == null) return new ParameterInfo[0];
        return paramTable;
    }
    
    
    /**
     * Sets  parameters for a method
     * @param paramTable parameters
     */
    public void setParameterInfo(ParameterInfo[] paramTable) {
        this.paramTable = paramTable;
    }

    @Override
    public String toString() {
        StringBuffer signature = new StringBuffer(methodName);
        signature.append("(");
        if(paramTable == null)
            return "methodName()";
        for(int i=0; i < paramTable.length; i++)
        {
            String type = paramTable[i].getType();
            //assert type == null;
            signature.append(paramTable[i].getType());
            signature.append(" ");
            signature.append(paramTable[i].getName());
            if(i != (paramTable.length - 1) )
                signature.append(",");
        }
        signature.append(")");
        return signature.toString();
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
            
        }
        
        
        /**
         * Returns value of the name of parameter. If the name was not
         * changed, returns null.
         *
         * @return  new name for parameter or null in case that it was not changed.
         */
        public String getName() { return name; }

        /**
         * Returns value of the type of parameter. If the name was not
         * changed, returns null.
         *
         * @return new type for parameter or null if it was not changed.
         */
        public String getType() { return type; }

        /**
         * Returns value of the default value in case of the new parameter.
         * Otherwise, it returns null.
         *
         * @return default value for new parameter, otherwise null.
         */
        public String getDefaultValue() { return defaultVal; }
    }  
    
}
