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

package org.netbeans.modules.clazz;

import org.netbeans.api.mdr.MDRepository;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.openide.src.*;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Parameter;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;

/** Implementation of the constructor element for class objects.
* It's simple bridge to the java reflection Constructor, delegates
* all tasks to it.
*
* @author Dafe Simonek
*/
class ConstructorElementImpl extends MemberElementImpl
    implements ConstructorElement.Impl {

    /** The array of parameters of this constructor (or method)*/
    private MethodParameter[] parameters;
    /** The array of exceptions which can be thrown */
    private Identifier[] exceptions;
    /** One JavaDoc empty implementation for all objects */
    private static final ClassJavaDocImpl.Method METHOD_JAVADOC_IMPL = new ClassJavaDocImpl.Method();
    
    static final long serialVersionUID =5714347955571851240L;
    
    ConstructorElementImpl (final CallableFeature data ) {
        super(data);
    }

    CallableFeature getBehavioral() {
        return (CallableFeature)data;
    }
    
    public void initializeData() {
        super.initializeData();
        getExceptions();
        getParameters();
    }
    
    /** @return the array specifying the parameters
    */
    public MethodParameter[] getParameters () {
        if (parameters == null) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    parameters = new MethodParameter[0];
                } else {
                    Parameter[] pars = (Parameter[])getBehavioral().getParameters().toArray(new Parameter[0]);
                    MethodParameter[] mp = new MethodParameter[pars.length];
                    for (int i = 0; i < pars.length; i++) {
                        Parameter p=pars[i];
                        mp[i] = new MethodParameter(p.getName(), Util.createType(p.getType()), p.isFinal());
                    }
                    parameters = mp;
                }
            } finally {
                repo.endTrans();
            }
        }
        return parameters;
    }
    
    protected Identifier createName(Object data) {
	if (this instanceof MethodElementImpl) {
	    return super.createName(data);
	}
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Identifier.create(""); // NOI18N
            }
            return Identifier.create(((JavaClass)(getBehavioral().getDeclaringClass())).getSimpleName());
        } finally {
            repo.endTrans();
        }
    }

    /** Unsupported, throws SourceException
    */
    public void setParameters (MethodParameter[] params) throws SourceException {
        throwReadOnlyException();
    }

    /** @return the array of the exceptions throwed by the method.
    */
    public Identifier[] getExceptions () {
        if (exceptions == null) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    return new Identifier[0];
                }
                JavaClass[] reflEx = (JavaClass[])getBehavioral().getExceptions().toArray(new JavaClass[0]);
                exceptions = new Identifier[reflEx.length];
                // build our exception types
                for (int i = 0; i < reflEx.length; i++) {
                    exceptions[i] = Identifier.create(Util.createClassName(reflEx[i].getName()));
                }
            } finally {
                repo.endTrans();
            }
        }
        return exceptions;
    }

    /** Unsupported, throws SourceException
    */
    public void setExceptions (Identifier[] exceptions) throws SourceException {
        throwReadOnlyException();
    }

    /** Unsupported, throws SourceException
    */
    public void setBody (String s) throws SourceException {
        throwReadOnlyException();
    }

    /** Unsupported, always return empty string.
    */
    public String getBody () {
        return ""; // NOI18N
    }

    /** Empty implementation
    * @return Empty JavaDoc implementation.
    */
    public JavaDoc.Method getJavaDoc () {
        return METHOD_JAVADOC_IMPL;
    }

    public Object readResolve() {
        return new ConstructorElement(this, null);
    }
}
