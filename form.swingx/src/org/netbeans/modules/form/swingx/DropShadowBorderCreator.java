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
package org.netbeans.modules.form.swingx;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.form.CreationDescriptor;
import org.netbeans.modules.form.CreationDescriptor.Creator;
import org.netbeans.modules.form.FormProperty;
import org.netbeans.modules.form.codestructure.CodeExpression;
import org.netbeans.modules.form.codestructure.CodeExpressionOrigin;

/**
 * Creator for DropShadowBorder.
 *
 * @author Jan Stola
 */
public class DropShadowBorderCreator implements Creator {
    /** Creation descriptor this creator belongs to. */
    private CreationDescriptor cd;
    /** Property names. */
    private String[] propNames;
    /** Parameter types. */
    private Class[] paramTypes;
    
    /**
     * Creates a new creator for DropShadowBorder.
     * 
     * @param cd creation descriptor this creator belongs to.
     * @param propNames property names.
     * @param paramTypes parameter types.
     */
    protected DropShadowBorderCreator(CreationDescriptor cd, String[] propNames, Class[] paramTypes) {
        assert (propNames.length == paramTypes.length);
        this.cd = cd;
        this.propNames = propNames;
        this.paramTypes = paramTypes;
    }

    /**
     * Returns number of parameters of the creator.
     *
     * @return number of parameters of the creator.
     */
    public int getParameterCount() {
        return propNames.length;
    }

    /**
     * Returns parameter types of the creator.
     *
     * @return parameter types of the creator.
     */
    public Class[] getParameterTypes() {
        return paramTypes;
    }

    /**
     * Returns exception types of the creator.
     *
     * @return exception types of the creator.
     */
    public Class[] getExceptionTypes() {
        return new Class[0];
    }

    /**
     * Returns property names of the creator.
     *
     * @return property names of the creator.
     */
    public String[] getPropertyNames() {
        return propNames;
    }

    /**
     * Creates instance according to given properties.
     *
     * @param props properties describing the instance to create.
     * @return instance that reflects values of the given properties.
     */
    public Object createInstance(FormProperty[] props) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object[] values = new Object[propNames.length];
        List<String> namesList = Arrays.asList(propNames);
        for (int i=0; i<props.length; i++) {
            int index = namesList.indexOf(props[i].getName());
            if (index != -1) {
                values[index] = props[i].getRealValue();
            }
        }
        return createInstance(values);
    }

    /**
     * Creates instance according to given parameter values.
     *
     * @param paramValues parameter values describing the instance to create.
     * @return instance that reflects values of the given parameters.
     */
    public Object createInstance(Object[] paramValues) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            Constructor constr = cd.getDescribedClass().getConstructor(paramTypes);
            return constr.newInstance(paramValues);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Returns creation code according to given properties.
     *
     * @param props properties describing the instance whose creation code should be returned.
     * @param expressionType type of the expression to create.
     * @return creation code that reflects values of the given properties.
     */
    public String getJavaCreationCode(FormProperty[] props, Class expressionType, String genericTypes) {
        FormProperty[] neededProps = new FormProperty[propNames.length];
        List<String> namesList = Arrays.asList(propNames);
        for (int i=0; i<props.length; i++) {
            int index = namesList.indexOf(props[i].getName());
            if (index != -1) {
                neededProps[index] = props[i];
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("new "); // NOI18N
        sb.append(cd.getDescribedClass().getName());
        sb.append('(');
        for (int i=0; i<neededProps.length; i++) {
            sb.append(neededProps[i].getJavaInitializationString());
            if (i != neededProps.length-1) {
                sb.append(", "); // NOI18N
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public CodeExpressionOrigin getCodeOrigin(CodeExpression[] params) {
        return null; // PENDING how is this used?
    }

}
