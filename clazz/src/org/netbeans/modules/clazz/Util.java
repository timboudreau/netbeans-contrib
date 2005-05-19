/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
