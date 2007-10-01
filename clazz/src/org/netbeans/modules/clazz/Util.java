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

import java.util.ResourceBundle;
import java.util.*;
import org.netbeans.jmi.javamodel.Array;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.PrimitiveType;
import org.netbeans.jmi.javamodel.PrimitiveTypeKind;
import org.netbeans.jmi.javamodel.PrimitiveTypeKindEnum;
import org.netbeans.jmi.javamodel.Resource;

import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.netbeans.modules.classfile.Method;
import org.openide.src.Type;
import org.openide.src.MethodParameter;
import org.openide.src.Identifier;

/**
 *
 * @author  sdedic
 */
final class Util {

    private static RequestProcessor classProcessor;
        
    static String createClassName(String signature) {
        return signature.replace('/', '.').replace('$', '.');
    }

    static Type createType(org.netbeans.jmi.javamodel.Type t) {
        if (t == null)
            return null;
        if (t instanceof Array) {
            return Type.createArray(createType(((Array)t).getType()));
        } else if (t instanceof JavaClass) {
            JavaClass ct=(JavaClass)t;
                return Type.createClass(
                    Identifier.create(createClassName(ct.getName())));
        } else if (t instanceof PrimitiveType) {
            PrimitiveTypeKind tag = ((PrimitiveType)t).getKind();
            if (tag == PrimitiveTypeKindEnum.BOOLEAN) {
                return Type.BOOLEAN;
            } else if (tag == PrimitiveTypeKindEnum.BYTE) {
                return Type.BYTE;
            } else if (tag == PrimitiveTypeKindEnum.CHAR) {
                return Type.CHAR;
            } else if (tag == PrimitiveTypeKindEnum.DOUBLE) {
                return Type.DOUBLE;
            } else if (tag == PrimitiveTypeKindEnum.FLOAT) {
                return Type.FLOAT;
            } else if (tag == PrimitiveTypeKindEnum.INT) {
                return Type.INT;
            } else if (tag == PrimitiveTypeKindEnum.LONG) {
                return Type.LONG;
            } else if (tag == PrimitiveTypeKindEnum.SHORT) {
                return Type.SHORT;
            } else if (tag == PrimitiveTypeKindEnum.VOID) {
                return Type.VOID;
            }
        }
        throw new IllegalArgumentException("Invalid TypeDescriptor: " + t); // NOI18N
    }
    
    static RequestProcessor getClassProcessor() {
        if (classProcessor==null)
            classProcessor=new RequestProcessor("Clazz",5); // NOI18N
        return classProcessor;
    }
}
