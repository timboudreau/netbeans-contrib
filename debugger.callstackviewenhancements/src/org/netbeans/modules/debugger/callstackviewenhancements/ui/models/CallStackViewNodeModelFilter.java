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

package org.netbeans.modules.debugger.callstackviewenhancements.ui.models;

import com.sun.jdi.StackFrame;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.NodeModelFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author Sandip V.Chitale (Sandip.Chitale@Sun.Com)
 */
public class CallStackViewNodeModelFilter implements NodeModelFilter {
    private static boolean dontShowCustomIcons = Boolean.getBoolean("org.netbeans.modules.debugger.callstackviewenhancements.dontShowCustomIcons");

    public CallStackViewNodeModelFilter() {

    }

    public String getDisplayName(NodeModel original, Object node) throws UnknownTypeException {
        return original.getDisplayName(node);        
    }

    public String getIconBase(NodeModel original, Object node) throws UnknownTypeException {
        if (!dontShowCustomIcons) {
            if (node instanceof CallStackFrame) {
                com.sun.jdi.Method method = Utils.getMethod((CallStackFrame) node);
                if (method != null) {
                    if (method.isSynthetic()) {
                        return "org/netbeans/modules/java/navigation/resources/methods";
                    } else if (method.isStaticInitializer()) {
                        return "org/netbeans/modules/java/navigation/resources/initializerSt";
                    } else if (method.isStaticInitializer()) {
                        return "org/netbeans/modules/java/navigation/resources/initializer";
                    } else if (method.isConstructor()) {
                        if (method.isPrivate()) {
                            return "org/netbeans/modules/java/navigation/resources/constructorPrivate";
                        } else if (method.isProtected()) {
                            return "org/netbeans/modules/java/navigation/resources/constructorProtected";
                        } else if (method.isPublic()) {
                            return "org/netbeans/modules/java/navigation/resources/constructorPublic";
                        } else {
                            return "org/netbeans/modules/java/navigation/resources/constructorPackage";
                        }
                    } else if (method.isStatic()) {
                        if (method.isPrivate()) {
                            return "org/netbeans/modules/java/navigation/resources/methodStPrivate";
                        } else if (method.isProtected()) {
                            return "org/netbeans/modules/java/navigation/resources/methodStProtected";
                        } else if (method.isPublic()) {
                            return "org/netbeans/modules/java/navigation/resources/methodStPublic";
                        } else {
                            return "org/netbeans/modules/java/navigation/resources/methodStPackage";
                        }
                    } else {
                        if (method.isPrivate()) {
                            return "org/netbeans/modules/java/navigation/resources/methodPrivate";
                        } else if (method.isProtected()) {
                            return "org/netbeans/modules/java/navigation/resources/methodProtected";
                        } else if (method.isPublic()) {
                            return "org/netbeans/modules/java/navigation/resources/methodPublic";
                        } else {
                            return "org/netbeans/modules/java/navigation/resources/methodPackage";
                        }
                    }
                }
            }
        }
        return original.getIconBase(node);
    }

    public String getShortDescription(NodeModel original, Object node) throws UnknownTypeException {
        return original.getShortDescription(node);
    }

    public void addModelListener(ModelListener l) {
    }

    public void removeModelListener(ModelListener l) {
    }   
}
